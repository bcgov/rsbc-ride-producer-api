package bcgov.rsbc.ride.kafka.models;


public class viRequestPayload {

    private String typeofevent;
    private viPayloadRecord viPayload;
    private locationRequestPayload locationRequestPayload;

    public viRequestPayload() {}

    public viRequestPayload(String typeofevent, viPayloadRecord viPayload) {
        this.typeofevent = typeofevent;
        this.viPayload = viPayload;
    }

    public String getTypeofevent() {
        return typeofevent;
    }


    public void setTypeofevent(String value) {
        this.typeofevent = value;
    }


    public viPayloadRecord getViPayload() {
        return viPayload;
    }


    public void setViPayload(viPayloadRecord value) {
        this.viPayload = value;
    }

    public locationRequestPayload getLocationRequestPayload() {
        return locationRequestPayload;
    }

    public void setLocationRequestPayload(locationRequestPayload value) {
        this.locationRequestPayload = value;
    }

    public String toString() {
        return "{\"typeofevent\": \"" + typeofevent + "\", \"viPayload\":" + viPayload + ", \"locationRequestPayload\":" + locationRequestPayload + "}";
    }
}
