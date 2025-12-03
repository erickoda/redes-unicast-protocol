package src.Routing.PDU;

public class RoutingInformationProtocolNotificationPDU {
    private short ripNodeA;
    private short ripNodeB;
    private int cost;
    private String message = "";

    public RoutingInformationProtocolNotificationPDU(short ripNodeA, short ripNodeB, int cost) {
        this.ripNodeA = ripNodeA;
        this.ripNodeB = ripNodeB;
        this.cost = cost;
        this.message = "RIPNTF " + ripNodeA + " " + ripNodeB + " " + cost;
    }

    public String getMessage() {
        return message;
    }
}
