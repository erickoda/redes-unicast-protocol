package src.Unicast;

import java.util.Optional;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Implementa um Singleton que armazena os dados dos nós da rede.
 * <p>
 * Essa classe é responsável por validar e armazenar os {@link UnicastAddress}
 * das Entidades do {@link UnicastProtocol}.
 * </p>
 *
 * @see UnicastProtocol
 * @see UnicastAddress
 */
public final class UnicastAddressSingleton {
    /** Referência para a única instância do Singleton. */
    private static UnicastAddressSingleton instance = null;

    /** Lista de endereços das entidades. */
    private UnicastAddress[] unicastAddresses = null;

    /**
     * Constrói a instância do singleton.
     * 
     * @param unicastAddresses - lista de endereço de entidades.
     */
    private UnicastAddressSingleton(UnicastAddress[] unicastAddresses) {
        this.unicastAddresses = unicastAddresses;
    }

    /**
     * Pega a instância do singleton.
     * <p>
     * Retorna a instância do singleton. Cria a instância se não existir.
     * </p>
     * 
     * @return retorna a instância do singleton
     */
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
            } catch (UnknownHostException unknownHostException) {
                System.err.println("[ERROR]: UNKNOWN ADDRESS!");
                System.exit(1);
            }

            instance = new UnicastAddressSingleton(conf.toArray(new UnicastAddress[0]));
        }

        return instance;
    }

    /**
     * Busca o Endereço de uma entidade.
     * <p>
     * Busca o endereço de uma Entidade do Protocolo Unicast através de seu id;
     * </p>
     * 
     * @param id - ucsap_id da entidade.
     * 
     * @return retorna um Optional com o objeto do endereço caso seja encontrado ou
     *         None se o endereço não existir.
     */
    public synchronized Optional<UnicastAddress> getUnicastAddressFrom(short id) {
        if (unicastAddresses == null) {
            return Optional.empty();
        }

        return Arrays.stream(unicastAddresses)
                .filter(address -> address.getUcsapId() == id)
                .findFirst();
    }

    /**
     * Busca o UCSAP ID.
     * <p>
     * Retorna o UCSAP ID de uma entidade dado o endereço ip e a porta.
     * </p>
     * 
     * @param ip   - o endereço o IP.
     * 
     * @param port - a porta do endereço.
     * 
     * @return retorna um Optional com o valor do UCSAP ID caso seja encontrado ou
     *         None se o endereço não existir.
     */
    public synchronized Optional<Short> getUcsapIdFrom(InetAddress ip, int port) {
        if (unicastAddresses == null) {
            return Optional.empty();
        }

        Optional<UnicastAddress> unicastAddressOptional = Arrays.stream(unicastAddresses)
                .filter(address -> address.getInetAddress().equals(ip)
                        &&
                        address.getPortNumber() == port)
                .findFirst();

        if (unicastAddressOptional.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of((short) unicastAddressOptional.get().getUcsapId());
    }

    /**
     * Retorna a lista de endereços das entidades.
     * 
     * @return UnicastAddress[] - lista de endereços das entidades.
     */
    public synchronized UnicastAddress[] getUnicastAddresses() {
        return this.unicastAddresses;
    }
}
