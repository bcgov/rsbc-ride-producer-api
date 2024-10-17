package bcgov.rsbc.ride.kafka.models;


public class twelveHoursRequestPayload {

    private java.lang.String typeofevent;
    private twelveHoursPayloadRecord twelveHoursPayload;
    private locationRequestPayload locationRequestPayload;

    public twelveHoursRequestPayload() {}

    public twelveHoursRequestPayload(java.lang.String typeofevent, twelveHoursPayloadRecord twelveHoursPayload) {
        this.typeofevent = typeofevent;
        this.twelveHoursPayload = twelveHoursPayload;
    }

    public java.lang.String getTypeofevent() {
        return typeofevent;
    }


    public void setTypeofevent(java.lang.String value) {
        this.typeofevent = value;
    }


    public twelveHoursPayloadRecord getTwelveHoursPayload() {
        return twelveHoursPayload;
    }


    public void setTwelveHoursPayload(twelveHoursPayloadRecord value) {
        this.twelveHoursPayload = value;
    }

    public locationRequestPayload getLocationRequestPayload() {
        return locationRequestPayload;
    }

    public void setLocationRequestPayload(locationRequestPayload value) {
        this.locationRequestPayload = value;
    }

    public String toString() {
        return "{\"typeofevent\": \"" + typeofevent + "\", \"twelveHoursPayload\":" + twelveHoursPayload + ", \"locationRequestPayload\":" + locationRequestPayload + "}";
    }
}
