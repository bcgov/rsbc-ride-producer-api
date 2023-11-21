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
        "ticket_number",
        "count_number",
        "dispute_action_date",
        "dispute_type_code",
        "count_act_regulation",
        "compressed_section",
        "event"
})

public class evtdisputeevent {

    @JsonProperty("event_id")
    private String eventId;
    @JsonProperty("ticket_number")
    private String ticketNumber;
    @JsonProperty("count_number")
    private Integer countNumber;
    @JsonProperty("dispute_action_date")
    private String disputeActionDate;
    @JsonProperty("dispute_type_code")
    private String disputeTypeCode;
    @JsonProperty("count_act_regulation")
    private String countActRegulation;
    @JsonProperty("compressed_section")
    private String compressedSection;
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

    @JsonProperty("count_number")
    public Integer getCountNumber() {
        return countNumber;
    }

    @JsonProperty("count_number")
    public void setCountNumber(Integer countNumber) {
        this.countNumber = countNumber;
    }

    @JsonProperty("dispute_action_date")
    public String getDisputeActionDate() {
        return disputeActionDate;
    }

    @JsonProperty("dispute_action_date")
    public void setDisputeActionDate(String disputeActionDate) {
        this.disputeActionDate = disputeActionDate;
    }

    @JsonProperty("dispute_type_code")
    public String getDisputeTypeCode() {
        return disputeTypeCode;
    }

    @JsonProperty("dispute_type_code")
    public void setDisputeTypeCode(String disputeTypeCode) {
        this.disputeTypeCode = disputeTypeCode;
    }

    @JsonProperty("count_act_regulation")
    public String getCountActRegulation() {
        return countActRegulation;
    }

    @JsonProperty("count_act_regulation")
    public void setCountActRegulation(String countActRegulation) {
        this.countActRegulation = countActRegulation;
    }

    @JsonProperty("compressed_section")
    public String getCompressedSection() {
        return compressedSection;
    }

    @JsonProperty("compressed_section")
    public void setCompressedSection(String compressedSection) {
        this.compressedSection = compressedSection;
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
        sb.append(evtdisputeevent.class.getName()).append('@')
                .append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("eventId");
        sb.append('=');
        sb.append(((this.eventId == null) ? "<null>" : this.eventId));
        sb.append(',');
        sb.append("ticketNumber");
        sb.append('=');
        sb.append(((this.ticketNumber == null) ? "<null>" : this.ticketNumber));
        sb.append(',');
        sb.append("countNumber");
        sb.append('=');
        sb.append(((this.countNumber == null) ? "<null>" : this.countNumber));
        sb.append(',');
        sb.append("disputeActionDate");
        sb.append('=');
        sb.append(((this.disputeActionDate == null) ? "<null>" : this.disputeActionDate));
        sb.append(',');
        sb.append("disputeTypeCode");
        sb.append('=');
        sb.append(((this.disputeTypeCode == null) ? "<null>" : this.disputeTypeCode));
        sb.append(',');
        sb.append("countActRegulation");
        sb.append('=');
        sb.append(((this.countActRegulation == null) ? "<null>" : this.countActRegulation));
        sb.append(',');
        sb.append("compressedSection");
        sb.append('=');
        sb.append(((this.compressedSection == null) ? "<null>" : this.compressedSection));
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