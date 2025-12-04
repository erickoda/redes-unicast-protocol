package src.Routing.PDU;

/**
 * PDU de Indicação do Protocolo de Informação de Roteamento (RIP).
 * Envia o vetor de distância de um nó.
 * 
 * @see src.Routing.RoutingInformationProtocol
 */
public class RoutingInformationProtocolIndicationPDU {
    /** Nó */
    private short ripNode;

    /** Vetor de Distância */
    private int[] distanceVector;

    /** Mensagem da PDU */
    private String message = "";

    /**
     * Construtor para criar a PDU a partir do nó e do vetor de distância
     * 
     * @param ripNode        Nó
     * @param distanceVector Vetor de distância
     * @throws InvalidRIPPDUException Exceção de PDU Inválida
     */
    public RoutingInformationProtocolIndicationPDU(short ripNode, int[] distanceVector) throws InvalidRIPPDUException {
        this.ripNode = ripNode;
        this.distanceVector = distanceVector;

        this.message = "RIPIND " + ripNode + " ";
        for (int i = 0; i < distanceVector.length; i++) {
            if (i == 0) {
                this.message += distanceVector[i];
            } else {
                this.message += ":" + distanceVector[i];
            }
        }

        this.validatePDU();
    }

    /**
     * Valida a PDU
     * 
     * @throws InvalidRIPPDUException
     */
    private void validatePDU() throws InvalidRIPPDUException {
        if (!this.message.startsWith("RIPIND")) {
            throw new InvalidRIPPDUException("Mensagem não inicia com RIPIND");
        }

        if (this.ripNode < 1 || this.ripNode > 15) {
            throw new InvalidRIPPDUException("Nó inválido");
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
     * 
     * @throws InvalidRIPPDUException Exceção de PDU Inválida
     */
    public RoutingInformationProtocolIndicationPDU(String message) throws InvalidRIPPDUException {
        this.message = message;

        String[] parts = message.trim().split(" ");

        // Validação básica
        if (parts.length < 3 || !parts[0].equals("RIPIND")) {
            throw new InvalidRIPPDUException("Mensagem inválida para RIPIND: " + message);
        }

        // Extrai o ID do nó
        this.ripNode = Short.parseShort(parts[1]);

        // Extrai e processa o vetor de distância
        String[] vectorParts = parts[2].split(":");

        this.distanceVector = new int[vectorParts.length];

        for (int i = 0; i < vectorParts.length; i++) {
            try {
                this.distanceVector[i] = Integer.parseInt(vectorParts[i]);
            } catch (NumberFormatException e) {
                this.distanceVector[i] = -1;
            }
        }
    }

    /**
     * Pega a mensagem da PDU
     * 
     * @return mensagem
     */
    public String getMessage() {
        return message;
    }

    /**
     * Pega o vetor de distância
     * 
     * @return vetor de distância
     */
    public int[] getDistanceVector() {
        return distanceVector;
    }

    /**
     * Pega o nó
     * 
     * @return nó
     */
    public int getRipNode() {
        return ripNode;
    }
}
