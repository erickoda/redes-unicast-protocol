package src.Unicast;

public class UnicastAddress {
    int ucsapId;
    String hostName;
    int portNumber;

    public UnicastAddress(
            int ucsapId,
            String hostName,
            int portNumber) {
        this.ucsapId = ucsapId;
        this.hostName = hostName;
        this.portNumber = portNumber;
    }
}
