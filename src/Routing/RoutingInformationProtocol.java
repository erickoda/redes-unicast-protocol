package src.Routing;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Optional;

import src.Unicast.UnicastAddress;
import src.Unicast.UnicastAddressSingleton;
import src.Unicast.UnicastPDU;
import src.Unicast.UnicastServerInterface;
import src.Unicast.UnicastServiceUserInterface;
import src.Unicast.Exception.InvalidPDUException;

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
        int port;
        byte[] buffer;
        DatagramSocket datagramSocket;
        DatagramPacket datagramPacket;
        Optional<UnicastAddress> unicastAddressOptional;
        UnicastAddress unicastAddress;

        unicastAddressOptional = UnicastAddressSingleton.getUnicastAddressFrom(source);

        if (unicastAddressOptional.isEmpty()) {
            System.err.println("[FATAL ERROR]: PROVIDED ID FOR ADDRESS IS INVALID");
            System.exit(1);
        }

        unicastAddress = unicastAddressOptional.get();
        port = unicastAddress.getPortNumber();
        buffer = new byte[1024];

        try {
            datagramSocket = new DatagramSocket(port);
            datagramPacket = new DatagramPacket(buffer, buffer.length);

            datagramSocket.receive(datagramPacket);

            UnicastPDU unicastPDU = new UnicastPDU(new String(datagramPacket.getData()).trim());

            datagramSocket.close();
        } catch (IOException ioException) {
            System.err.println(ioException);
        } catch (InvalidPDUException invalidPDUException) {
            System.err.println(invalidPDUException);
        }

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
