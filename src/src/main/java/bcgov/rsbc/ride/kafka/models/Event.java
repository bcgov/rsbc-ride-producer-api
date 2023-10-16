package bcgov.rsbc.ride.kafka.models;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "event_id",
        "event_version",
        "event_date_time"
})
@Generated("jsonschema2pojo")
public class Event {

    @JsonProperty("event_id")
    private Integer eventId;
    @JsonProperty("event_version")
    private Integer eventVersion;
    @JsonProperty("event_date_time")
    private Integer eventDateTime;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    @JsonProperty("event_id")
    public Integer getEventId() {
        return eventId;
    }

    @JsonProperty("event_id")
    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    @JsonProperty("event_version")
    public Integer getEventVersion() {
        return eventVersion;
    }

    @JsonProperty("event_version")
    public void setEventVersion(Integer eventVersion) {
        this.eventVersion = eventVersion;
    }

    @JsonProperty("event_date_time")
    public Integer getEventDateTime() {
        return eventDateTime;
    }

    @JsonProperty("event_date_time")
    public void setEventDateTime(Integer eventDateTime) {
        this.eventDateTime = eventDateTime;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Event.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this)))
                .append('[');
        sb.append("eventId");
        sb.append('=');
        sb.append(((this.eventId == null) ? "<null>" : this.eventId));
        sb.append(',');
        sb.append("eventVersion");
        sb.append('=');
        sb.append(((this.eventVersion == null) ? "<null>" : this.eventVersion));
        sb.append(',');
        sb.append("eventDateTime");
        sb.append('=');
        sb.append(((this.eventDateTime == null) ? "<null>" : this.eventDateTime));
        sb.append(',');
        sb.append("additionalProperties");
        sb.append('=');
        sb.append(((this.additionalProperties == null) ? "<null>" : this.additionalProperties));
        sb.append(',');
        if (sb.charAt((sb.length() - 1)) == ',') {
            sb.setCharAt((sb.length() - 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}