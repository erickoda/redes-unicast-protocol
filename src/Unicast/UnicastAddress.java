package src.Unicast;

public class UnicastAddress {
    private short ucsapId;
    private String hostName;
    private int portNumber;

    public UnicastAddress(
            short ucsapId,
            String hostName,
            int portNumber) {
        this.ucsapId = ucsapId;
        this.hostName = hostName;
        this.portNumber = portNumber;
    }

    public short getUcsapId() {
        return ucsapId;
    }

    public String getHostName() {
        return hostName;
    }

    public int getPortNumber() {
        return portNumber;
    }
}
