package src.Routing.PDU;

public class RoutingInformationProtocolRequestPDU {
    /** Mensagem da PDU */
    private String message = "";

    /**
     * Construtor para criar a PDU de requisição
     * 
     * @throws InvalidRIPPDUException
     */
    public RoutingInformationProtocolRequestPDU() throws InvalidRIPPDUException {
        this.message = "RIPRQT";

        this.validatePDU();
    }

    /**
     * Construtor para criar a PDU de requisição a partir da mensagem
     * 
     * @param message
     * @throws InvalidRIPPDUException
     */
    public RoutingInformationProtocolRequestPDU(String message) throws InvalidRIPPDUException {
        this.message = message;
        this.validatePDU();
    }

    /**
     * Valida a PDU
     * 
     * @throws InvalidRIPPDUException
     */
    private void validatePDU() throws InvalidRIPPDUException {
        if (!this.message.startsWith("RIPRQT")) {
            throw new InvalidRIPPDUException("Mensagem não inicia com RIPRQT");
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
     * @return
     */
    public String getMessage() {
        return message;
    }
}
