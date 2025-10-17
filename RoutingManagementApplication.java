public class RoutingManagementApplication implements RoutingProtocolManagementServiceUserInterface {
    private RoutingProtocolManagementInterface routingManagement;

    public RoutingManagementApplication(
            RoutingProtocolManagementInterface routingManagement) {
        this.routingManagement = routingManagement;
    }

    public void distanceTableIndication(short i, int[][] matrix) {
    }

    public void linkCostIndication(short i, short j, int cost) {
    }
}