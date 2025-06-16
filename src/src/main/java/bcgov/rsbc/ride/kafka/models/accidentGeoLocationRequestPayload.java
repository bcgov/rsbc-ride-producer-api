package bcgov.rsbc.ride.kafka.models;


public class accidentGeoLocationRequestPayload {

    private java.lang.String typeofevent;
    private String accidentNumber;
    private locationRequestPayload locationRequestPayload;

    public accidentGeoLocationRequestPayload () {}

    public accidentGeoLocationRequestPayload(java.lang.String typeofevent, String accidentNumber) {
        this.typeofevent = typeofevent;
        this.accidentNumber = accidentNumber;
    }

    public java.lang.String getTypeofevent() {
        return typeofevent;
    }


    public void setTypeofevent(java.lang.String value) {
        this.typeofevent = value;
    }


    public String getAccidentNumber() {
        return accidentNumber;
    }


    public void setAccidentNumber(String accidentNumber) {
        this.accidentNumber = accidentNumber;
    }

    public locationRequestPayload getLocationRequestPayload() {
        return locationRequestPayload;
    }

    public void setLocationRequestPayload(locationRequestPayload value) {
        this.locationRequestPayload = value;
    }

    public String toString() {
        return "{\"typeofevent\": \"" + typeofevent + "\", \"accidentNumber\":" + accidentNumber + ", \"locationRequestPayload\":" + locationRequestPayload + "}";
    }
}
