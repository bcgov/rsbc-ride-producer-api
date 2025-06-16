package bcgov.rsbc.ride.kafka.models;


public class twentyFourHoursRequestPayload {

    private String typeofevent;
    private twentyFourHoursPayloadRecord twentyFourHoursPayload;
    private locationRequestPayload locationRequestPayload;

    public twentyFourHoursRequestPayload() {}

    public twentyFourHoursRequestPayload(String typeofevent, twentyFourHoursPayloadRecord twentyFourHoursPayload) {
        this.typeofevent = typeofevent;
        this.twentyFourHoursPayload = twentyFourHoursPayload;
    }

    public String getTypeofevent() {
        return typeofevent;
    }


    public void setTypeofevent(String value) {
        this.typeofevent = value;
    }


    public twentyFourHoursPayloadRecord getTwentyFourHoursPayload() {
        return twentyFourHoursPayload;
    }


    public void setTwentyFourHoursPayload(twentyFourHoursPayloadRecord value) {
        this.twentyFourHoursPayload = value;
    }

    public locationRequestPayload getLocationRequestPayload() {
        return locationRequestPayload;
    }

    public void setLocationRequestPayload(locationRequestPayload value) {
        this.locationRequestPayload = value;
    }

    public String toString() {
        return "{\"typeofevent\": \"" + typeofevent + "\", \"twentyFourHoursPayload\":" + twentyFourHoursPayload + ", \"locationRequestPayload\":" + locationRequestPayload + "}";
    }
}
