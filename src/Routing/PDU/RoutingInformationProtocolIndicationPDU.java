package src.Routing.PDU;

public class RoutingInformationProtocolIndicationPDU {
    private int ripNode;
    private int[] distanceVector;
    private String message = "";

    public RoutingInformationProtocolIndicationPDU(short ripNode, int[] distanceVector) {
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
    }

    public RoutingInformationProtocolIndicationPDU(String message) throws InvalidRIPPDUException {
        this.message = message;

        String[] parts = message.trim().split(" ");

        // Validação básica
        if (parts.length < 3 || !parts[0].equals("RIPIND")) {
            throw new InvalidRIPPDUException("Mensagem inválida para RIPIND: " + message);
        }

        // 2. Extrai o ID do nó
        this.ripNode = Integer.parseInt(parts[1]);

        // 3. Extrai e processa o vetor de distância
        // Quebra a parte do vetor pelos dois pontos ":"
        String[] vectorParts = parts[2].split(":");

        this.distanceVector = new int[vectorParts.length];

        for (int i = 0; i < vectorParts.length; i++) {
            try {
                this.distanceVector[i] = Integer.parseInt(vectorParts[i]);
            } catch (NumberFormatException e) {
                // Se houver algum erro de formatação, define como infinito
                this.distanceVector[i] = -1;
            }
        }
    }

    public String getMessage() {
        return message;
    }

    public int[] getDistanceVector() {
        return distanceVector;
    }

    public int getRipNode() {
        return ripNode;
    }
}
