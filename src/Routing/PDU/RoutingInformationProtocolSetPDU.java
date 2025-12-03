package src.Routing.PDU;

public class RoutingInformationProtocolSetPDU {
    private short ripNodeA;
    private short ripNodeB;
    private int cost;
    private String message = "";

    public RoutingInformationProtocolSetPDU(short ripNodeA, short ripNodeB, int cost) throws InvalidRIPPDUException {
        this.ripNodeA = ripNodeA;
        this.ripNodeB = ripNodeB;
        this.cost = cost;
        this.message = "RIPSET " + ripNodeA + " " + ripNodeB + " " + cost;

        this.validatePDU();
    }

    public RoutingInformationProtocolSetPDU(String ripset) throws InvalidRIPPDUException {
        String[] words = ripset.split(" ");
        this.message = ripset;

        try {
            this.ripNodeA = Short.parseShort(words[1]);
            this.ripNodeB = Short.parseShort(words[2]);
            this.cost = Integer.parseInt(words[3]);
        } catch (NumberFormatException numberFormatException) {
            System.err.println("[ERRO]: RIPSET com formato inválido de PDU");
        }

        this.validatePDU();
    }

    /**
     * Valida a PDU
     * 
     * @throws InvalidRIPPDUException
     */
    private void validatePDU() throws InvalidRIPPDUException {
        if (!this.message.startsWith("RIPSET")) {
            throw new InvalidRIPPDUException("Mensagem não inicia com RIPSET");
        }

        if (this.ripNodeA < 1 || this.ripNodeB < 1 || this.ripNodeA > 15 || this.ripNodeB > 15) {
            throw new InvalidRIPPDUException("Nó A ou Nó B inválido");
        }

        if (this.cost < 1 || this.cost > 16) {
            throw new InvalidRIPPDUException("Custo inválido");
        }

        if (!isByteSizeValid()) {
            throw new InvalidRIPPDUException("Tamanho da PDU excede 512 bytes");
        }
    }

    /**
     * Verifica se o tamanho da PDU em bytes é válido
     * 
     * @return se o tamanho é válido
     */
    private boolean isByteSizeValid() {
        byte[] bytes = this.message.getBytes();

        return bytes.length <= 512;
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
