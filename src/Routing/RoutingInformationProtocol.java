package src.Routing;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Optional;

import src.Unicast.UnicastAddress;
import src.Unicast.UnicastAddressSingleton;
import src.Unicast.UnicastPDU;
import src.Unicast.UnicastReceivedMessagesSingleton;
import src.Unicast.UnicastServerInterface;
import src.Unicast.UnicastServiceUserInterface;
import src.Unicast.Exception.InvalidPDUException;

public class RoutingInformationProtocol
        implements UnicastServiceUserInterface, RoutingProtocolManagementInterface, Runnable {
    int port;
    private UnicastServerInterface unicastServer;
    private RoutingProtocolManagementServiceUserInterface routingProtocolManagementServiceUserInterface;

    public RoutingInformationProtocol(short ucsapId) {
        UnicastAddressSingleton unicastAddressSingleton = UnicastAddressSingleton.getInstance();

        Optional<UnicastAddress> unicastAddressOptional = unicastAddressSingleton.getUnicastAddressFrom(ucsapId);

        if (unicastAddressOptional.isEmpty()) {
            System.err.println("[ERROR]: INVALID NODE UCSAP_ID");
            System.exit(1);
        }

        this.port = unicastAddressOptional.get().getPortNumber();
    }

    public UnicastServerInterface getUnicastServer() {
        return unicastServer;
    }

    public void setUnicastServer(UnicastServerInterface unicastServer) {
        this.unicastServer = unicastServer;
    }

    @Override
    public void run() {
        while (true) {
            System.out.println("[LISTENER]: MESSAGE LISTENER READY!");
            this.UDP();
        }
    }

    public void UDP() {
        byte[] buffer;
        DatagramSocket datagramSocket;
        DatagramPacket datagramPacket;
        UnicastAddressSingleton unicastAddressSingleton = UnicastAddressSingleton.getInstance();
        UnicastReceivedMessagesSingleton unicastReceivedMessagesSingleton = UnicastReceivedMessagesSingleton
                .getInstance();

        buffer = new byte[1024];

        try {
            datagramSocket = new DatagramSocket(this.port);
            datagramPacket = new DatagramPacket(buffer, buffer.length);

            datagramSocket.receive(datagramPacket);

            UnicastPDU unicastPDU = new UnicastPDU(new String(datagramPacket.getData()).trim());

            unicastReceivedMessagesSingleton.addMessage(unicastPDU);
            Optional<Short> ucsapIdOptional = unicastAddressSingleton
                    .getUcsapIdFrom(datagramPacket.getAddress().toString(), datagramPacket.getPort());

            if (ucsapIdOptional.isEmpty()) {
                System.err.println("[INVALID ADDRESS]: RECEIVED MESSAGE FROM FOREIGN NODE");
                datagramSocket.close();
                return;
            }

            short ucsapId = ucsapIdOptional.get();

            this.UPDataInd(ucsapId, unicastPDU.getMessage());

            datagramSocket.close();
        } catch (IOException ioException) {
            System.err.println(ioException);
        } catch (InvalidPDUException invalidPDUException) {
            System.err.println(invalidPDUException);
        }

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
