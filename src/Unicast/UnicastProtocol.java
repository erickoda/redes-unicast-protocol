package src.Unicast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Optional;

import src.Unicast.Exception.InvalidPDUException;

public class UnicastProtocol implements UnicastServerInterface {

    private UnicastServiceUserInterface userService;

    public UnicastProtocol() {
    }

    @Override
    public boolean UPDataReq(short destination, String message) {
        int portNumber;
        UnicastPDU unicastPDU;
        InetAddress inetAddress;
        DatagramSocket datagramSocket;
        DatagramPacket datagramPacket;
        UnicastAddress destinationAddress;
        Optional<UnicastAddress> destinationAddressOptional;

        destinationAddressOptional = UnicastAddressSingleton
                .getUnicastAddressFrom(destination);

        if (destinationAddressOptional.isEmpty()) {
            System.err.println("[ERROR]: INVALID DESTINATION ADDRESS");
            return false;
        }

        destinationAddress = destinationAddressOptional.get();

        try {
            unicastPDU = new UnicastPDU(message);
            datagramSocket = new DatagramSocket();
            inetAddress = InetAddress.getByName(destinationAddress.getHostName());
            portNumber = destinationAddress.getPortNumber();

            datagramPacket = new DatagramPacket(
                    unicastPDU.getMessageBytes(),
                    unicastPDU.getMessageBytes().length,
                    inetAddress,
                    portNumber);

            datagramSocket.send(datagramPacket);

            datagramSocket.close();
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
