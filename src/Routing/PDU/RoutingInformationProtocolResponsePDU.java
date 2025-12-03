package src.Routing.PDU;

public class RoutingInformationProtocolResponsePDU {
    private short ripNode;
    private int[][] distanceTable;
    private String message = "";

    public RoutingInformationProtocolResponsePDU(short ripNode, int[][] distanceTable) {
        this.ripNode = ripNode;
        this.distanceTable = distanceTable;

        this.message = "RIPRSP " + ripNode + " ";
        for (int i = 0; i < distanceTable.length; i++) {
            for (int j = 0; j < distanceTable[i].length; j++) {
                if (i == 0) {
                    this.message += distanceTable[i];
                } else {
                    this.message += ":" + distanceTable[i];
                }
            }

            if (i < distanceTable.length - 1) {
                this.message += " ";
            }
        }
    }

    public RoutingInformationProtocolResponsePDU(String message) {
        this.message = message;
        String[] words = message.split(" ");
        this.distanceTable = new int[words.length - 2][];

        for (int i = 2; i < words.length; i++) {
            String[] costsString = words[i].split(":");
            int[] distanceVector = new int[costsString.length];

            for (int j = 0; j < costsString.length; j++) {
                distanceVector[j] = Integer.parseInt(costsString[j]);
            }

            this.distanceTable[i - 2] = distanceVector;
        }
    }

    public String getMessage() {
        return this.message;
    }

    public short getRipNode() {
        return ripNode;
    }

    public int[][] getDistanceTable() {
        return distanceTable;
    }
}
