package bcgov.rsbc.ride.kafka.models;

import com.fasterxml.jackson.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "event_id",
        "ticket_number",
        "event"
})

public class evtpaymentqueryeevent {

    @JsonProperty("event_id")
    private String eventId;
    @JsonProperty("ticket_number")
    private String ticketNumber;
    @JsonProperty("event")
    private Event event;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    @JsonProperty("event_id")
    public String getEventId() {
        return eventId;
    }

    @JsonProperty("event_id")
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    @JsonProperty("ticket_number")
    public String getTicketNumber() {
        return ticketNumber;
    }

    @JsonProperty("ticket_number")
    public void setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    @JsonProperty("event")
    public Event getEvent() {
        return event;
    }

    @JsonProperty("event")
    public void setEvent(Event event) {
        this.event = event;
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
        sb.append(evtpaymentqueryeevent.class.getName()).append('@')
                .append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("eventId");
        sb.append('=');
        sb.append(((this.eventId == null) ? "<null>" : this.eventId));
        sb.append(',');
        sb.append("ticketNumber");
        sb.append('=');
        sb.append(((this.ticketNumber == null) ? "<null>" : this.ticketNumber));
        sb.append(',');
        sb.append("event");
        sb.append('=');
        sb.append(((this.event == null) ? "<null>" : this.event));
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