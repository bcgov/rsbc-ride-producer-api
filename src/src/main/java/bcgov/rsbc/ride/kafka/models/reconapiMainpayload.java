package bcgov.rsbc.ride.kafka.models;

public class reconapiMainpayload {

    private String apipath;

    private Long eventid;
    private String datasource;
    private String event_type;

    private String payloaddata;

    // Getters and setters
    public String getapipath() {
        return apipath;
    }
    public void setapipath(String apipath) {
        this.apipath = apipath;
    }

    public Long geteventid() {
        return eventid;
    }
    public void seteventid(Long eventid) {
        this.eventid = eventid;
    }

    public String getdatasource() {
        return datasource;
    }
    public void setdatasource(String datasource) {
        this.datasource = datasource;
    }

    public String getEventType() {
        return event_type;
    }
    public void setEventType(String event_type) {
        this.event_type = event_type;
    }

    public String getpayloaddata() {
        return payloaddata;
    }
    public void setpayloaddata(String payloaddata) {
        this.payloaddata = payloaddata;
    }
}


