package src.Routing.PDU;

public class RoutingInformationProtocolRequestPDU {
    private String message = "";

    public RoutingInformationProtocolRequestPDU() {
        this.message = "RIPRQT";
    }

    public RoutingInformationProtocolRequestPDU(String message) throws InvalidRIPPDUException {
        if (message.equals("RIPRQT")) {
            this.message = message;
        } else {
            throw new InvalidRIPPDUException("[ERRO]: Formato Inválido para RIPRQT");
        }
    }

    public String getMessage() {
        return message;
    }
}
