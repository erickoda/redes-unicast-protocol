package src.Routing;

import src.Unicast.UnicastServerInterface;
import src.Unicast.UnicastServiceUserInterface;

public class RoutingInformationProtocol
        implements UnicastServiceUserInterface, RoutingProtocolManagementInterface {
    private UnicastServerInterface unicastServer;
    private RoutingProtocolManagementServiceUserInterface routingProtocolManagementServiceUserInterface;

    public RoutingInformationProtocol() {
    }

    public UnicastServerInterface getUnicastServer() {
        return unicastServer;
    }

    public void setUnicastServer(UnicastServerInterface unicastServer) {
        this.unicastServer = unicastServer;
    }

    @Override
    public void UPDataInd(short source, String message) {
        System.out.println("[NEW MESSAGE]: You received a new message from " + source + ". Content: " + message);
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
