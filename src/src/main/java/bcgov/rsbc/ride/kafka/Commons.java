package bcgov.rsbc.ride.kafka;

import java.time.Duration;

import org.apache.avro.specific.SpecificRecordBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.mongodb.panache.PanacheQuery;
import io.smallrye.reactive.messaging.MutinyEmitter;
import io.smallrye.reactive.messaging.kafka.Record;
import jakarta.ws.rs.core.Response;
import bcgov.rsbc.ride.kafka.models.*;
import bcgov.rsbc.ride.kafka.services.ReconService;


public class Commons {

    private final static String GIS_GEOLOCATION = "gis_geolocation";
    private final static Logger logger = LoggerFactory.getLogger(dfV2Producer.class);

    
    public static boolean checkAuthKey(String apiKey) {
        if (apiKey == null) {
            return false;
        }

        PanacheQuery<apiKeys> queryKeys = apiKeys.find("apikeyval", apiKey);
        return queryKeys.count() > 0;
    }

     public static  void sendGeolocationEvent(geolocationRecord geoLocation, Long uid, String eventKey, String apiPath, String  reconApiHost, MutinyEmitter<Record<Long, geolocationRecord>> emitterGeolocationEvent) {
        if (geoLocation != null) {
            gisGeolocationEvent gisGeolocationEvent = new gisGeolocationEvent(GIS_GEOLOCATION, geoLocation);
            saveEventToMainStaging(gisGeolocationEvent, uid, GIS_GEOLOCATION, eventKey, apiPath, reconApiHost);
            logger.info("[RIDE]: Kafka geolocation event UID: {}", uid);
            emitterGeolocationEvent.send(Record.of(uid, geoLocation)).await().atMost(Duration.ofSeconds(5));
        }
    }


    //private final static String DF_V_2 = "df";
     private static void saveEventToMainStaging(SpecificRecordBase eventObj, Long key, String eventType, String eventKey, String apiPath, String  reconApiHost) {
        ReconService reconObj = new ReconService();
        boolean reconResp = reconObj.saveTomainStaging(apiPath, eventObj.toString(), eventKey, eventType, reconApiHost, key);
        logger.debug("[RIDE]: reconRest {}", reconResp);
        if(!reconResp){
            logger.error("[RIDE]: Exception occurred while saving to main staging table");
        }
    }

    
}
