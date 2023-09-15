package bcgov.rsbc.ride.kafka;

public enum EventType {
    ISSUANCE(Issuance.EVENT_TYPE, Issuance.ENDPOINT),
    DISPUTE_UPDATE(DisputeUpdate.EVENT_TYPE, DisputeUpdate.ENDPOINT),
    DISPUTE(Dispute.EVENT_TYPE, Dispute.ENDPOINT),
    PAYMENT(Payment.EVENT_TYPE, Payment.ENDPOINT),
    VIOLATIONS(Violations.EVENT_TYPE, Violations.ENDPOINT),
    PAYMENT_QUERY(PaymentQuery.EVENT_TYPE, PaymentQuery.ENDPOINT);

    private final String eventType;
    private final String endpoint;

    EventType(String eventType, String endpoint) {
        this.eventType = eventType;
        this.endpoint = endpoint;
    }
    public String getEventType() {
        return eventType;
    }
    public String getEndpoint() {
        return endpoint;
    }

    public static class Issuance {
        public static final String EVENT_TYPE = "etk_issuance";
        public static final String ENDPOINT = "/issuance";
    }

    public static class DisputeUpdate {
        public static final String EVENT_TYPE = "etk_disputeupdate";
        public static final String ENDPOINT = "/disputeupdate";
    }

    public static class Dispute {
        public static final String EVENT_TYPE = "etk_dispute";
        public static final String ENDPOINT = "/dispute";
    }

    public static class Payment {
        public static final String EVENT_TYPE = "etk_payment";
        public static final String ENDPOINT = "/payment";
    }

    public static class Violations {
        public static final String EVENT_TYPE = "etk_violations";
        public static final String ENDPOINT = "/violations";
    }

    public static class PaymentQuery {
        public static final String EVENT_TYPE = "payment_query";
        public static final String ENDPOINT = "/payquery";
    }

}