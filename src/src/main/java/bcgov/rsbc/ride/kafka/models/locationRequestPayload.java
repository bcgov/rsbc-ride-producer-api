package bcgov.rsbc.ride.kafka.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class locationRequestPayload {
    private float latitude;
    private float longitude;
    private java.lang.String requestedAddress;
    private java.lang.String fullAddress;


    public locationRequestPayload() {}


    public locationRequestPayload(java.lang.Float latitude, java.lang.Float longitude, java.lang.String requestedAddress, java.lang.String fullAddress) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.requestedAddress = requestedAddress;
        this.fullAddress = fullAddress;
    }


    public float getLatitude() {
        return latitude;
    }


    public void setLatitude(float value) {
        this.latitude = value;
    }


    public float getLongitude() {
        return longitude;
    }


    public void setLongitude(float value) {
        this.longitude = value;
    }


    public java.lang.String getRequestedAddress() {
        return requestedAddress;
    }


    public void setRequestedAddress(java.lang.String value) {
        this.requestedAddress = value;
    }

    public java.lang.String getFullAddress() {
        return fullAddress;
    }


    public void setFullAddress(java.lang.String value) {
        this.fullAddress = value;
    }

    public String toString() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
