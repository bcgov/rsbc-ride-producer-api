package bcgov.rsbc.ride.kafka.models;


public class irpRequestPayload {

    private String typeofevent;
    private irpPayloadRecord irpPayload;
    private locationRequestPayload locationRequestPayload;

    public irpRequestPayload() {}

    public irpRequestPayload(String typeofevent, irpPayloadRecord irpPayload) {
        this.typeofevent = typeofevent;
        this.irpPayload = irpPayload;
    }

    public String getTypeofevent() {
        return typeofevent;
    }


    public void setTypeofevent(String value) {
        this.typeofevent = value;
    }


    public irpPayloadRecord getIrpPayload() {
        return irpPayload;
    }


    public void setIrpPayload(irpPayloadRecord value) {
        this.irpPayload = value;
    }

    public locationRequestPayload getLocationRequestPayload() {
        return locationRequestPayload;
    }

    public void setLocationRequestPayload(locationRequestPayload value) {
        this.locationRequestPayload = value;
    }

    public String toString() {
        return "{\"typeofevent\": \"" + typeofevent + "\", \"irpPayload\":" + irpPayload + ", \"locationRequestPayload\":" + locationRequestPayload + "}";
    }
}
