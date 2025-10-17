package src.Routing;

import src.Unicast.UnicastServerInterface;
import src.Unicast.UnicastServiceUserInterface;

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

    public boolean getDistanceTable(short node) {
        return true;
    }

    public boolean getLinkCost(short nodeA, short nodeB) {
        return true;
    }

    public boolean setLinkCost(short nodeA, short nodeB, int cost) {
        return true;
    }
}
