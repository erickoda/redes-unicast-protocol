package src.Unicast;

import java.util.ArrayList;

public final class UnicastReceivedMessagesSingleton {
    private static UnicastReceivedMessagesSingleton instance = null;
    private ArrayList<UnicastPDU> unicastMessages = new ArrayList<UnicastPDU>();

    private UnicastReceivedMessagesSingleton() {
    }

    public void addMessage(UnicastPDU unicastPDU) {
        this.unicastMessages.add(unicastPDU);
    }

    public synchronized static UnicastReceivedMessagesSingleton getInstance() {
        if (instance == null) {
            instance = new UnicastReceivedMessagesSingleton();
        }

        return instance;
    }

    public ArrayList<UnicastPDU> getMessages() {
        return this.unicastMessages;
    }
}
