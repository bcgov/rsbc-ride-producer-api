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
import org.apache.avro.specific.SpecificRecordBase;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Path("/dfV2events")
public class dfV2Producer {
    private final static String DF_V_2 = "df";
    private final static String DF_V_2_EVENTS = "/dfV2events";
    private final static String TWELVE_HR_EVENT = "12hr_submitted";
    private final static String TWENTY_FOUR_HR_EVENT = "24hr_submitted";
    private final static String VI_EVENT = "vi_submitted";
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
    public Response publishTwelveHourEvent(@HeaderParam("ride-api-key") String apiKey, twelveHoursEvent twelveHoursEvent) {
        if(checkAuthKey(apiKey)){
            String apiPath = DF_V_2_EVENTS + TWELVE_HR_PATH;
            logger.info("[RIDE]: Publish app accepted [payload: {}] to kafka.", twelveHoursEvent.getTwelveHoursPayload());
            logger.info("{}",twelveHoursEvent.getTypeofevent());

            twelveHoursPayloadRecord payloadData = twelveHoursEvent.getTwelveHoursPayload().get(0);
            Long uid = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);

            try {
                saveEventToMainStaging(twelveHoursEvent, uid, TWELVE_HR_EVENT, apiPath);

                logger.info("[RIDE]: Kafka event UID: {}", uid);
                emitterTwelveHourEvent.send(Record.of(uid, payloadData)).await().atMost(Duration.ofSeconds(5));

                return Response.ok().entity("{\"status\":\"sent to kafka\",\"event_id\":\""+uid+"\"}").build();
            } catch (Exception e) {
                return handleException(e, apiPath, twelveHoursEvent, TWELVE_HR_EVENT, uid);
            }
        }
        return Response.serverError().status(401).entity("Auth Error").build();
    }
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path(TWENTY_FOUR_HR_PATH)
    public Response publishTwentyFourHourEvent(@HeaderParam("ride-api-key") String apiKey, twentyFourHoursEvent twentyFourHoursEvent) {
        if(checkAuthKey(apiKey)){
            String apiPath = DF_V_2_EVENTS + TWENTY_FOUR_HR_PATH;
            logger.info("[RIDE]: Publish app accepted [payload: {}] to kafka.", twentyFourHoursEvent.getTwentyFourHoursPayload());
            logger.info("{}",twentyFourHoursEvent.getTypeofevent());

            twentyFourHoursPayloadRecord payloadData = twentyFourHoursEvent.getTwentyFourHoursPayload().get(0);
            Long uid = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);

            try {
                saveEventToMainStaging(twentyFourHoursEvent, uid, TWENTY_FOUR_HR_EVENT, apiPath);

                logger.info("[RIDE]: Kafka event UID: {}", uid);
                emitterTwentyFourHourEvent.send(Record.of(uid, payloadData)).await().atMost(Duration.ofSeconds(5));

                return Response.ok().entity("{\"status\":\"sent to kafka\",\"event_id\":\""+uid+"\"}").build();
            } catch (Exception e) {
                return handleException(e, apiPath, twentyFourHoursEvent, TWENTY_FOUR_HR_EVENT, uid);
            }
        }
        return Response.serverError().status(401).entity("Auth Error").build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path(VI_PATH)
    public Response publishViEvent(@HeaderParam("ride-api-key") String apiKey, viEvent viEventObj) {
        if(checkAuthKey(apiKey)){
            String apiPath = DF_V_2_EVENTS + VI_PATH;
            logger.info("[RIDE]: Publish app accepted [payload: {}] to kafka.", viEventObj.getViPayload());
            logger.info("{}",viEventObj.getTypeofevent());

            viPayloadRecord payloadData = viEventObj.getViPayload().get(0);
            Long uid = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);

            try {
                saveEventToMainStaging(viEventObj, uid, VI_EVENT, apiPath);

                logger.info("[RIDE]: Kafka event UID: {}", uid);
                emitterViEvent.send(Record.of(uid, payloadData)).await().atMost(Duration.ofSeconds(5));

                return Response.ok().entity("{\"status\":\"sent to kafka\",\"event_id\":\""+uid+"\"}").build();
            } catch (Exception e) {
                return handleException(e, apiPath, viEventObj, VI_EVENT, uid);
            }
        }
        return Response.serverError().status(401).entity("Auth Error").build();
    }

    private Response handleException(Exception e, String apiPath, SpecificRecordBase eventObj, String viEvent, Long uid) {
        logger.error("[RIDE]: Exception occurred while sending 12hr_event event, exception details: {}", e.toString() + "; " + e.getMessage());
        ReconService reconObj = new ReconService();
        boolean reconResp = reconObj.saveToErrStaging(apiPath, eventObj.toString(), DF_V_2, viEvent, reconApiHost, PRODUCER_API, e.toString(), uid);
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

    private boolean checkAuthKey(String apiKey) {
        if (apiKey == null) {
            return false;
        }

        PanacheQuery<apiKeys> queryKeys = apiKeys.find("apikeyval", apiKey);
        return queryKeys.count() > 0;
    }
}
