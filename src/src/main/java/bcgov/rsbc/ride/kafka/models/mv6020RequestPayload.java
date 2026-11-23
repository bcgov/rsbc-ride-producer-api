package bcgov.rsbc.ride.kafka.models;


public class mv6020RequestPayload {

    private String typeofevent;
    private mv6020PayloadRecord mv6020Payload;
    private locationRequestPayload locationRequestPayload;

    public mv6020RequestPayload() {}

    public mv6020RequestPayload(String typeofevent, mv6020PayloadRecord mv6020Payload) {
        this.typeofevent = typeofevent;
        this.mv6020Payload = mv6020Payload;
    }

    public String getTypeofevent() {
        return typeofevent;
    }


    public void setTypeofevent(String value) {
        this.typeofevent = value;
    }


    public mv6020PayloadRecord getMv6020Payload() {
        return mv6020Payload;
    }


    public void setMv6020Payload(mv6020PayloadRecord value) {
        this.mv6020Payload = value;
    }

    public locationRequestPayload getLocationRequestPayload() {
        return locationRequestPayload;
    }

    public void setLocationRequestPayload(locationRequestPayload value) {
        this.locationRequestPayload = value;
    }

    public String toString() {
        return "{\"typeofevent\": \"" + typeofevent + "\", \"mv6020Payload\":" + mv6020Payload + ", \"locationRequestPayload\":" + locationRequestPayload + "}";
    }
}
