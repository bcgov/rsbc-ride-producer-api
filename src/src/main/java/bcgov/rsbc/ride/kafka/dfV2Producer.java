package bcgov.rsbc.ride.kafka;

import bcgov.rsbc.ride.kafka.models.*;
import bcgov.rsbc.ride.kafka.Commons;
import bcgov.rsbc.ride.kafka.services.ReconService;
import io.quarkus.mongodb.panache.PanacheQuery;
import io.smallrye.reactive.messaging.MutinyEmitter;
import io.smallrye.reactive.messaging.kafka.Record;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.avro.specific.SpecificRecordBase;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;



@Path("/dfV2events")
public class dfV2Producer {
    private final static String DF_V_2 = "df";
    private final static String DF_V_2_EVENTS = "/dfV2events";
    private final static String TWELVE_HR_EVENT = "12hr_submitted";
    private final static String TWENTY_FOUR_HR_EVENT = "24hr_submitted";
    private final static String VI_EVENT = "vi_submitted";
    private final static String GIS_GEOLOCATION = "gis_geolocation";
    private static final String PRODUCER_API = "producer_api";
    public static final String TWENTY_FOUR_HR_PATH = "/24hrsubmitted";
    public static final String TWELVE_HR_PATH = "/12hrsubmitted";
    public static final String VI_PATH = "/visubmitted";
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

    @Inject
    @Channel("outgoing-gis-geolocation")
    MutinyEmitter<Record<Long, geolocationRecord>> emitterGeolocationEvent;

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
    @Path(TWELVE_HR_PATH)
    public Response publishTwelveHourEvent(@HeaderParam("ride-api-key") String apiKey, twelveHoursRequestPayload twelveHoursRequestPayload) {
        if(Commons.checkAuthKey(apiKey)){
            String apiPath = DF_V_2_EVENTS + TWELVE_HR_PATH;
            logger.info("[RIDE]: Publish app accepted [payload: {}] to kafka.", twelveHoursRequestPayload);
            logger.info("{}",twelveHoursRequestPayload.getTypeofevent());

            Long uid = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);

            try {
                twelveHoursPayloadRecord payloadData = twelveHoursRequestPayload.getTwelveHoursPayload();
                twelveHoursEvent twelveHoursEvent = new twelveHoursEvent(
                        twelveHoursRequestPayload.getTypeofevent(),
                        Collections.singletonList(payloadData)
                );
                geolocationRecord geoLocation = getTwelveHourGeolocationRecord(twelveHoursRequestPayload);

                saveEventToMainStaging(twelveHoursEvent, uid, TWELVE_HR_EVENT, apiPath);
                logger.info("[RIDE]: Kafka event UID: {}", uid);
                emitterTwelveHourEvent.send(Record.of(uid, payloadData)).await().atMost(Duration.ofSeconds(5));

                Commons.sendGeolocationEvent(geoLocation, uid, DF_V_2, apiPath, reconApiHost, emitterGeolocationEvent);
                

                return Response.ok().entity("{\"status\":\"sent to kafka\",\"event_id\":\""+uid+"\"}").build();
            } catch (Exception e) {
                return handleException(e, apiPath, twelveHoursRequestPayload.toString(), TWELVE_HR_EVENT, uid);
            }
        }
        return Response.serverError().status(401).entity("Auth Error").build();
    }

   

    private static geolocationRecord getTwelveHourGeolocationRecord(twelveHoursRequestPayload payloadData) {
        if (payloadData.getLocationRequestPayload() == null) {
            return emptyGeolocationRecord(
                    payloadData.getTypeofevent(),
                    String.valueOf(payloadData.getTwelveHoursPayload().getTwelveHourNumber()),
                    payloadData.getTwelveHoursPayload().getAddressOfOffence() +", " + payloadData.getTwelveHoursPayload().getOffenceCity());
        }

        geolocationRecord geoLocation = new geolocationRecord();
        geoLocation.setBusinessProgram("DF");
        geoLocation.setBusinessType(payloadData.getTypeofevent());
        geoLocation.setBusinessId(String.valueOf(payloadData.getTwelveHoursPayload().getTwelveHourNumber()));
        locationRequestPayload locationRequestPayload = payloadData.getLocationRequestPayload();
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

    private static geolocationRecord emptyGeolocationRecord(String eventType, String businessId, String address) {
        geolocationRecord geoLocation = new geolocationRecord();
        geoLocation.setBusinessProgram("DF");
        geoLocation.setBusinessType(eventType);
        geoLocation.setBusinessId(businessId);
        geoLocation.setLat("");
        geoLocation.setLong$("");
        geoLocation.setRequestedAddress(address);
        geoLocation.setSubmittedAddress(address);
        geoLocation.setFullAddress("NA");
        geoLocation.setDatabcLat("NA");
        geoLocation.setDatabcLong("NA");
        geoLocation.setDatabcScore("NA");
        return geoLocation;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path(TWENTY_FOUR_HR_PATH)
    public Response publishTwentyFourHourEvent(@HeaderParam("ride-api-key") String apiKey, twentyFourHoursRequestPayload twentyFourHoursPayload) {
        if(Commons.checkAuthKey(apiKey)){
            String apiPath = DF_V_2_EVENTS + TWENTY_FOUR_HR_PATH;
            logger.info("[RIDE]: Publish app accepted [payload: {}] to kafka.", twentyFourHoursPayload);
            logger.info("{}",twentyFourHoursPayload.getTypeofevent());

            Long uid = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
            try {
                twentyFourHoursPayloadRecord payloadData = twentyFourHoursPayload.getTwentyFourHoursPayload();
                twentyFourHoursEvent twentyFourHoursEvent = new twentyFourHoursEvent(
                        twentyFourHoursPayload.getTypeofevent(),
                        Collections.singletonList(payloadData)
                );
                geolocationRecord geoLocation = getTwentyFourHourGeolocationRecord(twentyFourHoursPayload);

                saveEventToMainStaging(twentyFourHoursEvent, uid, TWENTY_FOUR_HR_EVENT, apiPath);
                logger.info("[RIDE]: Kafka event UID: {}", uid);
                emitterTwentyFourHourEvent.send(Record.of(uid, payloadData)).await().atMost(Duration.ofSeconds(5));

                Commons.sendGeolocationEvent(geoLocation, uid, DF_V_2, apiPath, reconApiHost, emitterGeolocationEvent);
                

                return Response.ok().entity("{\"status\":\"sent to kafka\",\"event_id\":\""+uid+"\"}").build();
            } catch (Exception e) {
                return handleException(e, apiPath, twentyFourHoursPayload.toString(), TWENTY_FOUR_HR_EVENT, uid);
            }
        }
        return Response.serverError().status(401).entity("Auth Error").build();
    }

    private static geolocationRecord getTwentyFourHourGeolocationRecord(twentyFourHoursRequestPayload twentyFourHoursPayload) {
        if (twentyFourHoursPayload.getLocationRequestPayload() == null) {
            return emptyGeolocationRecord(
                    twentyFourHoursPayload.getTypeofevent(),
                    String.valueOf(twentyFourHoursPayload.getTwentyFourHoursPayload().getTwentyFourHrNo()),
                    twentyFourHoursPayload.getTwentyFourHoursPayload().getAddressOfOffence() +", " + twentyFourHoursPayload.getTwentyFourHoursPayload().getOffenceCity());
        }

        geolocationRecord geoLocation = new geolocationRecord();
        geoLocation.setBusinessProgram("DF");
        geoLocation.setBusinessType(twentyFourHoursPayload.getTypeofevent());
        geoLocation.setBusinessId(String.valueOf(twentyFourHoursPayload.getTwentyFourHoursPayload().getTwentyFourHrNo()));
        locationRequestPayload locationRequestPayload = twentyFourHoursPayload.getLocationRequestPayload();
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

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path(VI_PATH)
    public Response publishViEvent(@HeaderParam("ride-api-key") String apiKey, viRequestPayload viRequestPayload) {
        if(Commons.checkAuthKey(apiKey)){
            String apiPath = DF_V_2_EVENTS + VI_PATH;
            logger.info("[RIDE]: Publish app accepted [payload: {}] to kafka.", viRequestPayload);
            logger.info("{}",viRequestPayload.getTypeofevent());

            Long uid = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
            try {
                viPayloadRecord payloadData = viRequestPayload.getViPayload();
                viEvent viEvent = new viEvent(
                        viRequestPayload.getTypeofevent(),
                        Collections.singletonList(payloadData)
                );
                geolocationRecord geoLocation = getViGeolocationRecord(viRequestPayload);

                saveEventToMainStaging(viEvent, uid, VI_EVENT, apiPath);
                logger.info("[RIDE]: Kafka event UID: {}", uid);
                emitterViEvent.send(Record.of(uid, payloadData)).await().atMost(Duration.ofSeconds(5));

                Commons.sendGeolocationEvent(geoLocation, uid, DF_V_2, apiPath, reconApiHost, emitterGeolocationEvent);
                

                return Response.ok().entity("{\"status\":\"sent to kafka\",\"event_id\":\""+uid+"\"}").build();
            } catch (Exception e) {
                return handleException(e, apiPath, viRequestPayload.toString(), VI_EVENT, uid);
            }
        }
        return Response.serverError().status(401).entity("Auth Error").build();
    }

    private geolocationRecord getViGeolocationRecord(viRequestPayload viRequestPayload) {
        if (viRequestPayload.getLocationRequestPayload() == null) {
            return emptyGeolocationRecord(
                    viRequestPayload.getTypeofevent(),
                    String.valueOf(viRequestPayload.getViPayload().getViNumber()),
                    viRequestPayload.getViPayload().getAddressOfOffence() +", " + viRequestPayload.getViPayload().getOffenceCity());
        }

        geolocationRecord geoLocation = new geolocationRecord();
        geoLocation.setBusinessProgram("DF");
        geoLocation.setBusinessType(viRequestPayload.getTypeofevent());
        geoLocation.setBusinessId(String.valueOf(viRequestPayload.getViPayload().getViNumber()));
        locationRequestPayload locationRequestPayload = viRequestPayload.getLocationRequestPayload();
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
        logger.error("[RIDE]: Exception occurred while sending 12hr_event event, exception details: {}", e.toString() + "; " + e.getMessage());
        ReconService reconObj = new ReconService();
        boolean reconResp = reconObj.saveToErrStaging(apiPath, eventObj, DF_V_2, eventType, reconApiHost, PRODUCER_API, e.toString(), uid);
        if (!reconResp) {
            logger.error("[RIDE]: Exception occurred while saving to err staging table");
        }
        return Response.serverError().entity("Failed sending  event to kafka").build();
    }

    private void saveEventToMainStaging(SpecificRecordBase eventObj, Long key, String eventType, String apiPath) {
        ReconService reconObj = new ReconService();
        boolean reconResp = reconObj.saveTomainStaging(apiPath, eventObj.toString(), DF_V_2, eventType, reconApiHost, key);
        logger.debug("[RIDE]: reconRest {}", reconResp);
        if(!reconResp){
            logger.error("[RIDE]: Exception occurred while saving to main staging table");
        }
    }

 
}
