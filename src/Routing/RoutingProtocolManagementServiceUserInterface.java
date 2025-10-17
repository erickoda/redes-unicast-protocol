package src.Routing;

public interface RoutingProtocolManagementServiceUserInterface {
    public void distanceTableIndication(short node, int[][] table);

    public void linkCostIndication(short nodeA, short nodeB, int cost);
}
