package bcgov.rsbc.ride.kafka;

import bcgov.rsbc.ride.kafka.models.*;
import bcgov.rsbc.ride.kafka.services.ReconService;

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

import java.time.LocalDateTime;
import java.time.ZoneOffset;


@Path("/accidentevents")
public class AccidentProducer {

    public static final String ACCIDENT_GEOLOCATION_PATH = "/accidentgeolocationsubmitted";
    private final static String ACCIDENT_V_2_EVENTS = "/accidentevents";
    private final static String ACCIDENT_V_2 = "accident";
    private final static Logger logger = LoggerFactory.getLogger(dfV2Producer.class);
    private final static String GIS_GEOLOCATION = "gis_geolocation";
    private static final String PRODUCER_API = "producer_api";

    @ConfigProperty(name = "recon.api.host")
    String reconApiHost;

    @Inject
    @Channel("outgoing-gis-geolocation")
    MutinyEmitter<Record<Long, geolocationRecord>> emitterGeolocationEvent;



    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path(ACCIDENT_GEOLOCATION_PATH)
    public Response publishAccidentLocationEvent(@HeaderParam("ride-api-key") String apiKey, accidentGeoLocationRequestPayload accidentGeoLocationRequestPayload) {
      
        if(Commons.checkAuthKey(apiKey)){

            String apiPath = ACCIDENT_V_2_EVENTS + ACCIDENT_GEOLOCATION_PATH;
            logger.info("[RIDE]: Publish app accepted [accident gelocation number: {}] to kafka.", accidentGeoLocationRequestPayload.getAccidentNumber());

            Long uid = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);

            try {
               
                geolocationRecord geoLocation = getAccidentGeolocationRecord(accidentGeoLocationRequestPayload.getAccidentNumber(), accidentGeoLocationRequestPayload.getLocationRequestPayload());
                Commons.sendGeolocationEvent(geoLocation, uid,  ACCIDENT_V_2, apiPath, reconApiHost, emitterGeolocationEvent);
                return Response.ok().entity("{\"status\":\"sent to kafka\",\"event_id\":\""+uid+"\"}").build();
            } catch (Exception e) {
                return handleException(e, apiPath, accidentGeoLocationRequestPayload.toString(), GIS_GEOLOCATION, uid);
            }
        }
        return Response.serverError().status(401).entity("Auth Error").build();
    }

     private static geolocationRecord getAccidentGeolocationRecord(String accidentNumber, locationRequestPayload locationRequestPayload) {
       

        geolocationRecord geoLocation = new geolocationRecord();
        geoLocation.setBusinessProgram("TAS");
        geoLocation.setBusinessType("accident");
        geoLocation.setBusinessId(accidentNumber);
        geoLocation.setLat(String.valueOf(locationRequestPayload.getLatitude()));
        geoLocation.setLong$(String.valueOf(locationRequestPayload.getLongitude()));
        geoLocation.setRequestedAddress(locationRequestPayload.getRequestedAddress());
        geoLocation.setSubmittedAddress(locationRequestPayload.getRequestedAddress());
        geoLocation.setFullAddress(locationRequestPayload.getFullAddress());
        geoLocation.setDatabcLat("NA");
        geoLocation.setDatabcLong("NA");
        geoLocation.setDatabcScore("NA");
        return geoLocation;
    }

    
      private Response handleException(Exception e, String apiPath, String eventObj, String eventType, Long uid) {
        logger.error("[RIDE]: Exception occurred while sending accident geolocation event, exception details: {}", e.toString() + "; " + e.getMessage());
        ReconService reconObj = new ReconService();
        boolean reconResp = reconObj.saveToErrStaging(apiPath, eventObj, ACCIDENT_V_2, eventType, reconApiHost, PRODUCER_API, e.toString(), uid);
        if (!reconResp) {
            logger.error("[RIDE]: Exception occurred while saving to err staging table");
        }
        return Response.serverError().entity("Failed sending  event to kafka").build();
    }



    
}
