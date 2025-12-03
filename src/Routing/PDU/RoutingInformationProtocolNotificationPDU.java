package src.Routing.PDU;

/**
 * PDU de Notificação do Protocolo de Informação de Roteamento (RIP).
 * Notifica a confirmação de mudança de custo do enlace entre dois nós.
 * 
 * @see src.Routing.RoutingInformationProtocol
 */
public class RoutingInformationProtocolNotificationPDU {
    /** Mensage da PDU */
    private String message = "";

    /**
     * Construtor para criar a PDU a partir dos nós e do custo
     * 
     * @param ripNodeA Nó A
     * @param ripNodeB Nó B
     * @param cost     Custo do Enlace entre A e B
     * @throws InvalidRIPPDUException Exceção de PDU Inválida
     */
    public RoutingInformationProtocolNotificationPDU(short ripNodeA, short ripNodeB, int cost)
            throws InvalidRIPPDUException {
        this.message = "RIPNTF " + ripNodeA + " " + ripNodeB + " " + cost;

        this.validatePDU();
    }

    /**
     * Valida a PDU
     * 
     * @throws InvalidRIPPDUException
     */
    private void validatePDU() throws InvalidRIPPDUException {
        if (!this.message.startsWith("RIPNTF")) {
            throw new InvalidRIPPDUException("Mensagem não inicia com RIPNTF");
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
     * @return mensagem da PDU
     */
    public String getMessage() {
        return message;
    }
}
