package src.Unicast;

import java.util.ArrayList;

public class UnicastReceivedMessagesSingleton {
    private static ArrayList<UnicastPDU> unicastMessages = new ArrayList<UnicastPDU>();

    private UnicastReceivedMessagesSingleton() {
    }

    public static void addMessage(UnicastPDU unicastPDU) {
        UnicastReceivedMessagesSingleton.unicastMessages.add(unicastPDU);
    }

    public static ArrayList<UnicastPDU> getMessages() {
        return UnicastReceivedMessagesSingleton.unicastMessages;
    }
}
