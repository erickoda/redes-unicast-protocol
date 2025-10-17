package src.Routing;

public class RoutingManagementApplication implements RoutingProtocolManagementServiceUserInterface {
    private RoutingProtocolManagementInterface routingManagement;

    public RoutingManagementApplication(
            RoutingProtocolManagementInterface routingManagement) {
        this.routingManagement = routingManagement;
    }

    public void distanceTableIndication(short node, int[][] table) {
    }

    public void linkCostIndication(short nodeA, short nodeB, int cost) {
    }
}