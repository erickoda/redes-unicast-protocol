package src.Routing;

public interface RoutingProtocolManagementInterface {
    public boolean getDistanceTable(short node);

    public boolean getLinkCost(short nodeA, short nodeB);

    public boolean setLinkCost(short nodeA, short nodeB, int cost);
}
