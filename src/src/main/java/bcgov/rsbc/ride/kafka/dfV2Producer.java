package bcgov.rsbc.ride.kafka;

import bcgov.rsbc.ride.kafka.models.*;
import bcgov.rsbc.ride.kafka.services.ReconService;
import io.quarkus.mongodb.panache.PanacheQuery;
import io.smallrye.reactive.messaging.MutinyEmitter;
import io.smallrye.reactive.messaging.kafka.Record;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;


@Path("/dfV2events")
public class dfV2Producer {
    private final static String DF_V_2 = "dfV2";
    private final static String DF_V_2_EVENTS = "/dfV2events";
    private final static String TWELVE_HR_EVENT = "12hr_event";
    private final static String VI_EVENT = "vi_event";
    private static final String PRODUCER_API = "producer_api";
    private final static Logger logger = LoggerFactory.getLogger(dfV2Producer.class);

    @Inject
    @Channel("outgoing-twelvehrevent")
    MutinyEmitter<Record<Long, twelveHoursPayloadRecord>> emitterTwelveHourEvent;

    @Inject
    @Channel("outgoing-twentyfourhrevent")
    MutinyEmitter<Record<Long, twentyFourHoursPayloadRecord>> emitterTwentyFourHourEvent;

    @Inject
    @Channel("outgoing-vievent")
    MutinyEmitter<Record<Long, viPayloadRecord>> emitterViEvent;

    @GET
    @Path("/ping")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dfV2ping() {
        return Response.ok().entity("{\"status\":\"working\"}").build();
    }

    @ConfigProperty(name = "recon.api.host")
    String reconApiHost;


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/12hrsubmitted")
    public Response publishTwelveHourEvent(@HeaderParam("ride-api-key") String apiKey, twelveHoursEvent eventobj) {
        if(!checkAuthKey(apiKey)){
            return Response.serverError().status(401).entity("Auth Error").build();
        } else {
            String apiPath = String.format("%s/12hrsubmitted", DF_V_2_EVENTS);
            logger.info("[RIDE]: Publish app accepted [payload: {}] to kafka.", eventobj.getTwelveHoursPayload());
            logger.info("{}",eventobj.getTypeofevent());

            twelveHoursPayloadRecord payloadData = eventobj.getTwelveHoursPayload().get(0);
            Long uid = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);

            try {
                ReconService reconObj = new ReconService();
                boolean reconResp = reconObj.saveTomainStaging(apiPath,eventobj.toString(), DF_V_2, TWELVE_HR_EVENT, reconApiHost,uid);
                logger.debug("[RIDE]: reconRest {}", reconResp);
                if(!reconResp){
                    logger.error("[RIDE]: Exception occurred while saving to main staging table");
                }

                logger.info("[RIDE]: Kafka event UID: {}", uid);
                emitterTwelveHourEvent.send(Record.of(uid, payloadData)).await().atMost(Duration.ofSeconds(5));

                return Response.ok().entity("{\"status\":\"sent to kafka\",\"event_id\":\""+uid+"\"}").build();
            } catch (Exception e) {
                logger.error("[RIDE]: Exception occurred while sending 12hr_event event, exception details: {}", e.toString() + "; " + e.getMessage());
                ReconService reconObj = new ReconService();
                boolean reconResp = reconObj.saveToErrStaging(apiPath,eventobj.toString(), DF_V_2, TWELVE_HR_EVENT, reconApiHost, PRODUCER_API, e.toString(), uid);
                if(!reconResp){
                    logger.error("[RIDE]: Exception occurred while saving to err staging table");
                }
                return Response.serverError().entity("Failed sending  event to kafka").build();
            }
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/visubmitted")
    public Response publishViEvent(@HeaderParam("ride-api-key") String apiKey, viEvent eventobj) {
        if(!checkAuthKey(apiKey)){
            return Response.serverError().status(401).entity("Auth Error").build();
        } else {
            String apiPath = String.format("%s/visubmitted", DF_V_2_EVENTS);
            logger.info("[RIDE]: Publish app accepted [payload: {}] to kafka.", eventobj.getViPayload());
            logger.info("{}",eventobj.getTypeofevent());

            viPayloadRecord payloadData = eventobj.getViPayload().get(0);
            Long uid = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);

            try {
                ReconService reconObj = new ReconService();
                boolean reconResp = reconObj.saveTomainStaging(apiPath,eventobj.toString(), DF_V_2, VI_EVENT, reconApiHost,uid);
                logger.debug("[RIDE]: reconRest {}", reconResp);
                if(!reconResp){
                    logger.error("[RIDE]: Exception occurred while saving to main staging table");
                }

                logger.info("[RIDE]: Kafka event UID: {}", uid);
                emitterViEvent.send(Record.of(uid, payloadData)).await().atMost(Duration.ofSeconds(5));

                return Response.ok().entity("{\"status\":\"sent to kafka\",\"event_id\":\""+uid+"\"}").build();
            } catch (Exception e) {
                logger.error("[RIDE]: Exception occurred while sending 12hr_event event, exception details: {}", e.toString() + "; " + e.getMessage());
                ReconService reconObj = new ReconService();
                boolean reconResp = reconObj.saveToErrStaging(apiPath,eventobj.toString(), DF_V_2, VI_EVENT, reconApiHost, PRODUCER_API, e.toString(), uid);
                if(!reconResp){
                    logger.error("[RIDE]: Exception occurred while saving to err staging table");
                }
                return Response.serverError().entity("Failed sending  event to kafka").build();
            }
        }
    }

    private boolean checkAuthKey(String apiKey) {
        if (apiKey == null) {
            return false;
        }

        PanacheQuery<apiKeys> queryKeys = apiKeys.find("apikeyval", apiKey);
        return queryKeys.count() > 0;
    }
}
