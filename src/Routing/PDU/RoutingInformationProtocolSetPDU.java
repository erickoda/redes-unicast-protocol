package src.Routing.PDU;

/**
 * PDU para definir o custo do enlace entre dois nós.
 * 
 * @see src.Routing.RoutingInformationProtocol
 */
public class RoutingInformationProtocolSetPDU {
    /** Node A */
    private short ripNodeA;

    /** Node B */
    private short ripNodeB;

    /** Custo do Enlace entre os Nodes */
    private int cost;

    /** Mensage da PDU */
    private String message = "";

    /**
     * Construtor para criar a PDU a partir dos nós e do custo
     * 
     * @param ripNodeA Nó A
     * @param ripNodeB Nó B
     * @param cost     Custo do Enlace entre A e B
     * 
     * @throws InvalidRIPPDUException Exceção de PDU Inválida
     */
    public RoutingInformationProtocolSetPDU(short ripNodeA, short ripNodeB, int cost) throws InvalidRIPPDUException {
        this.ripNodeA = ripNodeA;
        this.ripNodeB = ripNodeB;
        this.cost = cost;
        this.message = "RIPSET " + ripNodeA + " " + ripNodeB + " " + cost;

        this.validatePDU();
    }

    /**
     * Construtor para criar a PDU a partir da mensagem
     * 
     * @param ripset mensagem da PDU
     * 
     * @throws InvalidRIPPDUException Exceção de PDU Inválida
     */
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

    /**
     * Pega a mensagem da PDU
     * 
     * @return Mensagem da PDU
     */
    public String getMessage() {
        return message;
    }

    /**
     * Pega o nó A
     * 
     * @return Nó A
     */
    public short getRipNodeA() {
        return ripNodeA;
    }

    /**
     * Pega o nó B
     * 
     * @return Nó B
     */
    public short getRipNodeB() {
        return ripNodeB;
    }

    /**
     * Pega o custo do enlace entre os nodes
     * 
     * @return custo do enlace
     */
    public int getCost() {
        return cost;
    }
}
