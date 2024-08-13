package bcgov.rsbc.ride.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.mongodb.panache.PanacheQuery;
import io.smallrye.reactive.messaging.MutinyEmitter;
import io.smallrye.reactive.messaging.kafka.Record;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.time.Duration;

import bcgov.rsbc.ride.kafka.models.evtdisputeupdateevent;
import bcgov.rsbc.ride.kafka.models.evtissuanceeevent;
import bcgov.rsbc.ride.kafka.models.evtpaymenteevent;
import bcgov.rsbc.ride.kafka.models.evtdisputeevent;
import bcgov.rsbc.ride.kafka.models.evtcontraventionseevent;
import bcgov.rsbc.ride.kafka.models.evtpaymentqueryeevent;
import bcgov.rsbc.ride.kafka.models.geolocation;

import bcgov.rsbc.ride.kafka.services.ReconService;


@Path("/etkevents")
public class eTkProducer {

    private final static Logger logger = LoggerFactory.getLogger(eTkProducer.class);

    @Inject
    @Channel("outgoing-issuance")
    MutinyEmitter<Record<Long, evtissuanceeevent>> emitterIssuanceEvent;

    @Inject
    @Channel("outgoing-payment")
    MutinyEmitter<Record<Long, evtpaymenteevent>> emitterPaymentEvent;

    @Inject
    @Channel("outgoing-disputeupdate")
    MutinyEmitter<Record<Long, evtdisputeupdateevent>> emitterDisputeUpdateEvent;

    @Inject
    @Channel("outgoing-dispute")
    MutinyEmitter<Record<Long, evtdisputeevent>> emitterDisputeEvent;

    @Inject
    @Channel("outgoing-violations")
    MutinyEmitter<Record<Long, evtcontraventionseevent>> emitterContraventionsEvent;


    @Inject
    @Channel("outgoing-payquery")
    MutinyEmitter<Record<Long, evtpaymentqueryeevent>> emitterPayQueryEvent;

    @Inject
    @Channel("outgoing-geolocation")
    MutinyEmitter<Record<Long, geolocation>> geoLocationEvent;

    @ConfigProperty(name = "recon.api.host")
    String reconapihost;

    @GET
    @Path("/ping")
    @Produces(MediaType.APPLICATION_JSON)
    public Response etkping() {
        return Response.ok().entity("{\"status\":\"working\"}").build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path(EventType.Issuance.ENDPOINT)
    public Response publishIssuanceEvent(@HeaderParam("ride-api-key") String apiKey, evtissuanceeevent eventobj) {
        return processEvent(apiKey, EventType.ISSUANCE, eventobj, emitterIssuanceEvent);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path(EventType.DisputeUpdate.ENDPOINT)
    public Response publishDisputeUpdateEvent(@HeaderParam("ride-api-key") String apiKey, evtdisputeupdateevent eventobj) {
        return processEvent(apiKey, EventType.DISPUTE_UPDATE, eventobj, emitterDisputeUpdateEvent);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path(EventType.Dispute.ENDPOINT)
    public Response publishDisputeEvent(@HeaderParam("ride-api-key") String apiKey, evtdisputeevent eventobj) {
        return processEvent(apiKey, EventType.DISPUTE, eventobj, emitterDisputeEvent);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path(EventType.Payment.ENDPOINT)
    public Response publishPaymentEvent(@HeaderParam("ride-api-key") String apiKey, evtpaymenteevent eventobj) {
        return processEvent(apiKey, EventType.PAYMENT, eventobj, emitterPaymentEvent);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path(EventType.Violations.ENDPOINT)
    public Response publishContraventionsEvent(@HeaderParam("ride-api-key") String apiKey, evtcontraventionseevent eventobj) {
        return processEvent(apiKey, EventType.VIOLATIONS, eventobj, emitterContraventionsEvent);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path(EventType.PaymentQuery.ENDPOINT)
    public Response publishPaymentQueryEvent(@HeaderParam("ride-api-key") String apiKey, evtpaymentqueryeevent eventobj) {
        return processEvent(apiKey, EventType.PAYMENT_QUERY, eventobj, emitterPayQueryEvent);
    }
    private static long previousTimeMillis = System.currentTimeMillis();
    private static long counter = 0L;

    public static synchronized long nextID() {
        long currentTimeMillis = System.currentTimeMillis();
        counter = (currentTimeMillis == previousTimeMillis) ? (counter + 1L) & 1048575L : 0L;
        previousTimeMillis = currentTimeMillis;
        long timeComponent = (currentTimeMillis & 8796093022207L) << 20;
        return timeComponent | counter;
    }

    private <T> Response processEvent(String apiKey, EventType eventTypeEnum, T eventobj,
                                      MutinyEmitter<Record<Long, T>> mutinyEmitter) {
        if(apiKey == null || apiKeys.find("apikeyval", apiKey).count() == 0) {
            return Response.serverError().status(401).entity("Auth Error").build();
        }

        String eventType = eventTypeEnum.getEventType();
        String reconEndpoint = eventTypeEnum.getEndpoint();

        logger.info("[RIDE]: Publish {} [payload: {}] to kafka.", eventType, eventobj.toString());

        String jsonPayload = null;
        try {
            jsonPayload = new ObjectMapper().writeValueAsString(eventobj);
        } catch (Exception e) {
            return Response.serverError().status(422).entity("Unprocessable Entity").build();
        }

        long uid = nextID();
        try {
            ReconService reconObj = new ReconService();
            boolean reconResp = reconObj.saveTomainStaging("/etkevents" + reconEndpoint, jsonPayload,"etk", eventType,
                    reconapihost, uid);
            if(!reconResp) {
                logger.error("[RIDE]: Exception occurred while saving to main staging table");
            }
            logger.info("[RIDE]: Kafka event UID: {}", uid);
            mutinyEmitter.send(Record.of(uid, eventobj)).await().atMost(Duration.ofSeconds(5));
            return Response.ok().entity("{\"status\":\"sent to kafka\",\"event_id\":\"" + uid + "\"}").build();
        } catch (Exception e) {
            logger.error("[RIDE]: Exception occurred while sending {} event, exception details: {}",
                    eventType, e.toString() + "; " + e.getMessage());

            ReconService reconObj = new ReconService();
            boolean reconResp = reconObj.saveToErrStaging("/etkevents" + reconEndpoint, jsonPayload,"etk",eventType, reconapihost,
                    "producer_api",e.toString(),uid);
            if(!reconResp) {
                logger.error("[RIDE]: Exception occurred while saving to err staging table");
            }

            return Response.serverError().entity("Failed sending event to kafka").build();
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/geolocation")
    // input of array geolocationn
    public Response publishGeolocationEvent(@HeaderParam("ride-api-key") String apiKey, List<geolocation> eventobj ) {
        if(apiKey== null){
            return Response.serverError().status(401).entity("Auth Error").build();
        }
        PanacheQuery<apiKeys> queryKeys = apiKeys.find("apikeyval", apiKey);
        List<apiKeys> foundKeys = queryKeys.list();
        long foundKeyCount=queryKeys.count();
        Boolean errFlg=false;

        if(foundKeyCount==0){
            return Response.serverError().status(401).entity("Auth Error").build();
        }else{
            
            for (geolocation geoObj:eventobj) {     
                logger.info("[RIDE]: Publish geolocation [payload: {}] to kafka.", geoObj);
                long uid = nextID();
                try {
                    String jsonPayload = new ObjectMapper().writeValueAsString(geoObj);
                    //DONE: Prep payload for recon api save master
                    ReconService reconObj=new ReconService();
                    Boolean reconResp= reconObj.saveTomainStaging("/etkevents/geolocation",jsonPayload,"etk","geolocation",reconapihost,uid);
                    if(!reconResp){
                        logger.error("[RIDE]: Exception occurred while saving to main staging table");
                    }
                    logger.info("[RIDE]: Kafka event UID: {}", uid);
                    geoLocationEvent.send(Record.of(uid, geoObj)).await().atMost(Duration.ofSeconds(5));
                    
                } catch (Exception e) {
                    errFlg=true;
                    logger.error("[RIDE]: Exception occurred while sending geolocation event, exception details: {}", e.toString() + "; " + e.getMessage());
                    try {
                        String jsonPayload = new ObjectMapper().writeValueAsString(geoObj);
                        ReconService reconObj=new ReconService();
                        Boolean reconResp= reconObj.saveToErrStaging("/etkevents/geolocation",jsonPayload,"etk","geolocation",reconapihost,"producer_api",e.toString(),uid);
                        if(!reconResp){
                            logger.error("[RIDE]: Exception occurred while saving to err staging table");
                        }
                    } catch (JsonProcessingException ex) {
                        logger.error("[RIDE]: Exception occurred while saving to err staging table");
                    }

                }
            }

        if(Boolean.TRUE.equals(errFlg)){
            return Response.serverError().entity("One of the events Failed sending to kafka").build();
        }else{
            return Response.ok().entity("{\"status\":\"sent to kafka\"}").build();
        }

        }

    }


//    @POST
//    @Produces(MediaType.APPLICATION_JSON)
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Path("/etktestevent")
//    public Response etktestevent(String eventobj) {
//        logger.info(eventobj);
//        Long uid = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
//        emitterTestEvt.send(Record.of(uid.toString(), eventobj)).await().atMost(Duration.ofSeconds(5));
//
//        return Response.ok().entity("{\"status\":\"working\"}").build();
//    }



}