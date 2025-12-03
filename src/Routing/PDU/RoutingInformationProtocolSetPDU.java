package src.Routing.PDU;

public class RoutingInformationProtocolSetPDU {
    private short ripNodeA;
    private short ripNodeB;
    private int cost;
    private String message = "";

    public RoutingInformationProtocolSetPDU(short ripNodeA, short ripNodeB, int cost) {
        this.ripNodeA = ripNodeA;
        this.ripNodeB = ripNodeB;
        this.cost = cost;
        this.message = "RIPSET " + ripNodeA + " " + ripNodeB + " " + cost;
    }

    public RoutingInformationProtocolSetPDU(String ripset) {
        String[] words = ripset.split(" ");
        this.message = ripset;

        try {
            this.ripNodeA = Short.parseShort(words[1]);
            this.ripNodeB = Short.parseShort(words[2]);
            this.cost = Integer.parseInt(words[3]);
        } catch (NumberFormatException numberFormatException) {
            System.err.println("[ERRO]: RIPSET com formato inválido de PDU");
        }
    }

    public String getMessage() {
        return message;
    }

    public short getRipNodeA() {
        return ripNodeA;
    }

    public short getRipNodeB() {
        return ripNodeB;
    }

    public int getCost() {
        return cost;
    }
}
