package src.Routing.PDU;

public class RoutingInformationProtocolGetPDU {

    private short ripNodeA;
    private short ripNodeB;
    private String message = "";

    public RoutingInformationProtocolGetPDU(short ripNodeA, short ripNodeB) {
        this.ripNodeA = ripNodeA;
        this.ripNodeB = ripNodeB;
        this.message = "RIPGET " + ripNodeA + " " + ripNodeB;
    }

    public RoutingInformationProtocolGetPDU(String message) {
        this.message = message;
        String[] words = message.split(" ");

        try {
            this.ripNodeA = Short.parseShort(words[1]);
            this.ripNodeB = Short.parseShort(words[2]);
        } catch (NumberFormatException numberFormatException) {
            System.err.println("[ERRO]: Formato inválido para RIPGET PDU");
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
}
