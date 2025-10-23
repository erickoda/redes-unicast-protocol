package src.Unicast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Optional;

import src.Unicast.Exception.InvalidPDUException;
import src.Utils.Format;

public class UnicastProtocol implements UnicastServerInterface, Runnable {

    private int port;
    private UnicastServiceUserInterface userService;
    private DatagramSocket datagramSocket;

    public UnicastProtocol(short ucsapId) {
        UnicastAddressSingleton unicastAddressSingleton = UnicastAddressSingleton.getInstance();
        Optional<UnicastAddress> unicastAddressOptional = unicastAddressSingleton.getUnicastAddressFrom(ucsapId);

        if (unicastAddressOptional.isEmpty()) {
            System.err.println("[ERROR]: INVALID NODE UCSAP_ID");
            System.exit(1);
        }

        this.port = unicastAddressOptional.get().getPortNumber();

        try {
            this.datagramSocket = new DatagramSocket(this.port);
        } catch (IOException ioException) {
            System.err.println("[ERROR]: failed to create socket");
            System.exit(1);
        }
    }

    public UnicastServiceUserInterface getUserService() {
        return userService;
    }

    @Override
    public void run() {
        while (true) {
            this.listenForMessage();
        }
    }

    public void closeSocket() {
        this.datagramSocket.close();
    }

    public void setUserService(UnicastServiceUserInterface userService) {
        this.userService = userService;
    }

    public void listenForMessage() {
        byte[] buffer;
        short ucsapId;
        UnicastPDU unicastPDU;
        DatagramPacket datagramPacket;
        Optional<Short> ucsapIdOptional;
        UnicastAddressSingleton unicastAddressSingleton;
        UnicastReceivedMessagesSingleton unicastReceivedMessagesSingleton;

        buffer = new byte[1024];
        unicastAddressSingleton = UnicastAddressSingleton.getInstance();
        unicastReceivedMessagesSingleton = UnicastReceivedMessagesSingleton
                .getInstance();

        try {
            datagramPacket = new DatagramPacket(buffer, buffer.length);

            this.datagramSocket.receive(datagramPacket);

            unicastPDU = new UnicastPDU(new String(datagramPacket.getData()).trim());

            unicastReceivedMessagesSingleton.addMessage(unicastPDU);
            ucsapIdOptional = unicastAddressSingleton
                    .getUcsapIdFrom(datagramPacket.getAddress(),
                            datagramPacket.getPort());

            if (ucsapIdOptional.isEmpty()) {
                System.err.println("[INVALID ADDRESS]: RECEIVED MESSAGE FROM FOREIGN NODE. IP = "
                        + datagramPacket.getAddress().toString() + " Port = " +
                        datagramPacket.getPort());
                return;
            }

            ucsapId = ucsapIdOptional.get();

            this.userService.UPDataInd(ucsapId, unicastPDU.getMessage());

        } catch (IOException ioException) {
            System.err.println(ioException);
        } catch (InvalidPDUException invalidPDUException) {
            System.err.println(invalidPDUException);
        }

    }

    @Override
    public boolean UPDataReq(short destination, String message) {
        int portNumber;
        UnicastPDU unicastPDU;
        InetAddress inetAddress;
        DatagramPacket datagramPacket;
        UnicastAddress destinationAddress;
        Optional<UnicastAddress> destinationAddressOptional;

        UnicastAddressSingleton unicastAddressSingleton = UnicastAddressSingleton.getInstance();
        destinationAddressOptional = unicastAddressSingleton
                .getUnicastAddressFrom(destination);

        if (destinationAddressOptional.isEmpty()) {
            System.err.println("[ERROR]: INVALID DESTINATION ADDRESS");
            return false;
        }

        destinationAddress = destinationAddressOptional.get();

        try {
            unicastPDU = new UnicastPDU(Format.message(message));
            inetAddress = destinationAddress.getInetAddress();
            portNumber = destinationAddress.getPortNumber();

            System.out.println("[SENDING...]: sending message to " + inetAddress.toString() + ":" + portNumber
                    + ". Content: " + unicastPDU.getMessage());

            datagramPacket = new DatagramPacket(
                    unicastPDU.getMessageBytes(),
                    unicastPDU.getMessageBytes().length,
                    inetAddress,
                    portNumber);

            datagramSocket.send(datagramPacket);

        } catch (IOException ioe) {
            System.err.println(ioe);
            return false;
        } catch (InvalidPDUException iPdu) {
            System.err.println(iPdu);
            return false;
        }

        return true;
    }
}
