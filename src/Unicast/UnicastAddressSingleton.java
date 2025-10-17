package src.Unicast;

import java.util.Optional;
import java.util.Arrays;

public class UnicastAddressSingleton {
    private static UnicastAddress[] unicastAddresses = null;

    private UnicastAddressSingleton() {
    }

    public static void setAddresses(UnicastAddress[] unicastAddresses) {
        UnicastAddressSingleton.unicastAddresses = unicastAddresses;
    }

    public static Optional<UnicastAddress> getUnicastAddressFrom(short id) {
        if (unicastAddresses == null) {
            return Optional.empty();
        }

        return Arrays.stream(UnicastAddressSingleton.unicastAddresses)
                .filter(address -> address.getUcsapId() == id)
                .findFirst();
    }
}
