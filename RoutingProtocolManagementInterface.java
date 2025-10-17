public interface RoutingProtocolManagementInterface {
    public boolean getDistanceTable(short i);

    public boolean getLinkCost(short i, short j);

    public boolean setLinkCost(short i, short j, int cost);
}
