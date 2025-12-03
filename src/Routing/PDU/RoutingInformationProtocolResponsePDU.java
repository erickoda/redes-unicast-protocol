package src.Routing.PDU;

public class RoutingInformationProtocolResponsePDU {
    /** Nó */
    private short ripNode;

    /** Tabela de Distância */
    private int[][] distanceTable;

    /** Mensagem da PDU */
    private String message = "";

    /**
     * Construtor para criar a PDU a partir do nó e da tabela de distância
     * 
     * @param ripNode
     * @param distanceTable
     * @throws InvalidRIPPDUException
     */
    public RoutingInformationProtocolResponsePDU(short ripNode, int[][] distanceTable) throws InvalidRIPPDUException {
        this.ripNode = ripNode;
        this.distanceTable = distanceTable;

        this.message = "RIPRSP " + ripNode + " ";
        for (int i = 0; i < distanceTable.length; i++) {
            for (int j = 0; j < distanceTable[i].length; j++) {
                if (j == 0) {
                    this.message += distanceTable[i][j];
                } else {
                    this.message += ":" + distanceTable[i][j];
                }
            }

            if (i < distanceTable.length - 1) {
                this.message += " ";
            }
        }

        this.validatePDU();
    }

    /**
     * Construtor para criar a PDU a partir da mensagem
     * 
     * @param message
     * @throws InvalidRIPPDUException
     */
    public RoutingInformationProtocolResponsePDU(String message) throws InvalidRIPPDUException {
        this.message = message;
        String[] words = message.split(" ");
        this.ripNode = Short.parseShort(words[1]);
        this.distanceTable = new int[words.length - 2][];

        for (int i = 2; i < words.length; i++) {
            String[] costsString = words[i].split(":");
            int[] distanceVector = new int[costsString.length];

            for (int j = 0; j < costsString.length; j++) {
                distanceVector[j] = Integer.parseInt(costsString[j]);
            }

            this.distanceTable[i - 2] = distanceVector;
        }

        this.validatePDU();
    }

    /**
     * Valida a PDU
     * 
     * @throws InvalidRIPPDUException
     */
    private void validatePDU() throws InvalidRIPPDUException {
        if (!this.message.startsWith("RIPRSP")) {
            throw new InvalidRIPPDUException("Mensagem não inicia com RIPRSP");
        }

        if (this.ripNode < 1 || this.ripNode > 15) {
            throw new InvalidRIPPDUException("Nó " + this.ripNode + " inválido");
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
        return this.message;
    }

    /**
     * Pega o nó
     * 
     * @return
     */
    public short getRipNode() {
        return ripNode;
    }

    /**
     * Pega a tabela de distância
     * 
     * @return
     */
    public int[][] getDistanceTable() {
        return distanceTable;
    }
}
