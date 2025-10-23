package src.Unicast;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class UnicastAddress {
    private short ucsapId;
    private InetAddress inetAddress;
    private int portNumber;

    public UnicastAddress(String line) throws UnknownHostException {
        String[] words = line.split(" ");

        this.ucsapId = Short.parseShort(words[0]);
        this.inetAddress = InetAddress.getByName(words[1]);
        this.portNumber = Integer.parseInt(words[2]);
    }

    public short getUcsapId() {
        return ucsapId;
    }

    public InetAddress getInetAddress() {
        return inetAddress;
    }

    public int getPortNumber() {
        return portNumber;
    }
}
