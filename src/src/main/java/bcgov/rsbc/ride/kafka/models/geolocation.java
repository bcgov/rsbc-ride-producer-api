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
        "ticket_number",
        "server_code",
        "x_value",
        "y_value",
        "event"
})


public class geolocation {

    @JsonProperty("ticket_number")
    private String ticketNumber;
    @JsonProperty("server_code")
    private String serverCode;
    @JsonProperty("x_value")
    private String xValue;
    @JsonProperty("y_value")
    private String yValue;

    @JsonProperty("event")
    private Event event;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    @JsonProperty("ticket_number")
    public String getTicketNumber() {
        return ticketNumber;
    }

    @JsonProperty("ticket_number")
    public void setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    @JsonProperty("server_code")
    public String getServerCode() {
        return serverCode;
    }

    @JsonProperty("server_code")
    public void setServerCode(String serverCode) {
        this.serverCode = serverCode;
    }

    @JsonProperty("x_value")
    public String getXValue() {
        return xValue;
    }

    @JsonProperty("x_value")
    public void setXValue(String xValue) {
        this.xValue = xValue;
    }

    @JsonProperty("y_value")
    public String getYValue() {
        return yValue;
    }

    @JsonProperty("y_value")
    public void setYValue(String yValue) {
        this.yValue = yValue;
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
        sb.append(geolocation.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("ticketNumber");
        sb.append('=');
        sb.append(((this.ticketNumber == null)?"<null>":this.ticketNumber));
        sb.append(',');
        sb.append("serverCode");
        sb.append('=');
        sb.append(((this.serverCode == null)?"<null>":this.serverCode));
        sb.append(',');
        sb.append("xValue");
        sb.append('=');
        sb.append(((this.xValue == null)?"<null>":this.xValue));
        sb.append(',');
        sb.append("yValue");
        sb.append('=');
        sb.append(((this.yValue == null)?"<null>":this.yValue));
        sb.append(',');
        sb.append("event");
        sb.append('=');
        sb.append(((this.event == null) ? "<null>" : this.event));
        sb.append(',');
        sb.append("additionalProperties");
        sb.append('=');
        sb.append(((this.additionalProperties == null)?"<null>":this.additionalProperties));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}