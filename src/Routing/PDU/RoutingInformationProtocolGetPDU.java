package src.Routing.PDU;

/**
 * PDU para requisição de custo de link entre dois nós
 */
public class RoutingInformationProtocolGetPDU {

    /** Nó A */
    private short ripNodeA;

    /** Nó B */
    private short ripNodeB;

    /** Mensagem da PDU */
    private String message = "";

    /**
     * Construtor para criar a PDU a partir dos nós
     * 
     * @param ripNodeA Nó A
     * @param ripNodeB Nó B
     */
    public RoutingInformationProtocolGetPDU(short ripNodeA, short ripNodeB) throws InvalidRIPPDUException {
        this.ripNodeA = ripNodeA;
        this.ripNodeB = ripNodeB;
        this.message = "RIPGET " + ripNodeA + " " + ripNodeB;

        this.validatePDU();
    }

    /**
     * Valida a PDU
     * 
     * @throws InvalidRIPPDUException
     */
    private void validatePDU() throws InvalidRIPPDUException {
        if (!this.message.startsWith("RIPGET")) {
            throw new InvalidRIPPDUException("Mensagem não inicia com RIPGET");
        }

        if (this.ripNodeA < 1 || this.ripNodeB < 1 || this.ripNodeA > 15 || this.ripNodeB > 15) {
            throw new InvalidRIPPDUException("Nó A ou Nó B inválido");
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
     * Construtor para criar a PDU a partir da mensagem
     * 
     * @param message Mensagem da PDU
     */
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

    /**
     * Obtém a mensagem da PDU
     * 
     * @return Mensagem da PDU
     */
    public String getMessage() {
        return message;
    }

    /**
     * Obtém o nó A
     * 
     * @return Nó A
     */
    public short getRipNodeA() {
        return ripNodeA;
    }

    /**
     * Obtém o nó B
     * 
     * @return Nó B
     */
    public short getRipNodeB() {
        return ripNodeB;
    }
}
