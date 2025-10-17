public class RoutingInformationProtocol implements UnicastServiceUserInterface, RoutingProtocolManagementInterface {
    private UnicastServerInterface unicastServer;
    private RoutingProtocolManagementServiceUserInterface routingProtocolManagementServiceUserInterface;

    public RoutingInformationProtocol(
            UnicastServerInterface unicastServer,
            RoutingProtocolManagementServiceUserInterface routingProtocolManagementServiceUserInterface) {
        this.unicastServer = unicastServer;
        this.routingProtocolManagementServiceUserInterface = routingProtocolManagementServiceUserInterface;
    }

    public void UPDataInd(short source, String message) {
    }

    public boolean getDistanceTable(short i) {
        return true;
    }

    public boolean getLinkCost(short i, short j) {
        return true;
    }

    public boolean setLinkCost(short i, short j, int cost) {
        return true;
    }
}
