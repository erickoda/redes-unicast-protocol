package src.Routing;

public class RoutingManagementApplication implements RoutingProtocolManagementServiceUserInterface {
    private RoutingProtocolManagementInterface routingManagement;

    public RoutingManagementApplication(
            RoutingProtocolManagementInterface routingManagement) {
        this.routingManagement = routingManagement;
    }

    public void distanceTableIndication(short node, int[][] table) {
        System.out.println();
        System.out.println("Tabela de Distância do Nó: " + node);

        for (int[] row : table) {
            for (int element : row) {
                System.out.print(element + " ");
            }
            System.out.println();
        }

        System.out.println();
    }

    public void linkCostIndication(short nodeA, short nodeB, int cost) {
        System.out.println();

        System.out.println("Custo do nó " + nodeA + " para o nó " + nodeB + " é " + cost);

        System.out.println();
    }

    public RoutingProtocolManagementInterface getRoutingManagement() {
        return routingManagement;
    }
}