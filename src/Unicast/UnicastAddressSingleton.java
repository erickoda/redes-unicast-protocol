package src.Unicast;

import java.util.Optional;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;

public final class UnicastAddressSingleton {
    private static UnicastAddressSingleton instance = null;
    private UnicastAddress[] unicastAddresses = null;

    private UnicastAddressSingleton(UnicastAddress[] unicastAddresses) {
        this.unicastAddresses = unicastAddresses;
    }

    public static synchronized UnicastAddressSingleton getInstance() {
        if (instance == null) {
            ArrayList<UnicastAddress> conf = new ArrayList<UnicastAddress>();
            File entityConfFile = new File("./src/data/entity_configuration.txt");

            try (Scanner sc = new Scanner(entityConfFile)) {
                while (sc.hasNextLine()) {
                    conf.add(new UnicastAddress(sc.nextLine()));
                }
            } catch (FileNotFoundException fileNotFoundException) {
                System.err.println("[ERROR]: ENTITY CONFIGURATION FILE WAS NOT FOUND!");
                System.exit(1);
            }

            instance = new UnicastAddressSingleton(conf.toArray(new UnicastAddress[0]));
        }

        return instance;
    }

    public synchronized Optional<UnicastAddress> getUnicastAddressFrom(short id) {
        if (unicastAddresses == null) {
            return Optional.empty();
        }

        return Arrays.stream(unicastAddresses)
                .filter(address -> address.getUcsapId() == id)
                .findFirst();
    }

    public synchronized Optional<Short> getUcsapIdFrom(String ip, int port) {
        if (unicastAddresses == null) {
            return Optional.empty();
        }

        Optional<UnicastAddress> unicastAddressOptional = Arrays.stream(unicastAddresses)
                .filter(address -> address.getHostName().equals(ip) && address.getPortNumber() == port)
                .findFirst();

        if (unicastAddressOptional.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of((short) unicastAddressOptional.get().getUcsapId());
    }
}
