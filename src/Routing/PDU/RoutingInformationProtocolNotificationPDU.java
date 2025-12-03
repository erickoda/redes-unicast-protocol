package src.Routing.PDU;

public class RoutingInformationProtocolNotificationPDU {
    private String message = "";

    public RoutingInformationProtocolNotificationPDU(short ripNodeA, short ripNodeB, int cost) {
        this.message = "RIPNTF " + ripNodeA + " " + ripNodeB + " " + cost;
    }

    public String getMessage() {
        return message;
    }
}
