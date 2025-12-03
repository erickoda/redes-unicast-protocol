package src.Unicast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Optional;

import src.Unicast.Exception.InvalidPDUException;

/**
 * Implementa a lógica principal para o Protocolo de Comunicação Unicast que usa
 * UDP como base.
 * <p>
 * Essa classe é responsável por enviar e receber {@link UnicastPDU}.
 * Ela executa sua própria thread para continuamente ouvir mensagens e
 * enviá-las para a {@link UnicastServiceUserInterface}.
 *
 * @see UnicastPDU
 * @see UnicastAddressSingleton
 */
public class UnicastProtocol implements UnicastServiceInterface, Runnable {

    /**
     * O serviço de usuário do Unicast responsável para notificar a chegada de novas
     * mensagens
     */
    private UnicastServiceUserInterface userService;

    /** O socket do Protocolo UDP */
    private DatagramSocket datagramSocket;

    private volatile boolean running = true;

    private short ucsapId;

    /**
     * Constrói a instância unicast para um dado Node
     * <p>
     * Utiliza o {@link UnicastAddressSingleton} e o ucsapId para adquirir a porta
     * na qual esta instância do Nó usará para se comunicar com os demais nós.
     * </p>
     * 
     * @param ucsapId é um identificador único - Unicast Service Access Point -
     *                UCSAP.
     */
    public UnicastProtocol(short ucsapId) {
        UnicastAddressSingleton unicastAddressSingleton = UnicastAddressSingleton.getInstance();
        Optional<UnicastAddress> unicastAddressOptional = unicastAddressSingleton.getUnicastAddressFrom(ucsapId);
        this.ucsapId = ucsapId;

        if (unicastAddressOptional.isEmpty()) {
            System.err.println("[ERROR]: INVALID NODE UCSAP_ID");
            System.exit(1);
        }

        try {
            this.datagramSocket = new DatagramSocket(unicastAddressOptional.get().getPortNumber());
        } catch (IOException ioException) {
            System.err.println(ioException);
            System.err.println("[ERROR]: failed to create socket (id: " + ucsapId + ", address: "
                    + unicastAddressOptional.get().getInetAddress() + ":"
                    + unicastAddressOptional.get().getPortNumber());
            System.exit(1);
        }
    }

    /**
     * Escuta por novas mensagens
     * <p>
     * Continuamente chama o método {@link #listenForMessage()}.
     * </p>
     * 
     */
    @Override
    public void run() {
        while (running) {
            this.listenForMessage();
        }
    }

    /**
     * Fecha o socket
     */
    public void closeSocket() {
        this.running = false;
        this.datagramSocket.close();
    }

    /**
     * Define o serviço de usuário
     * 
     * @param userService - serviço de usuário
     */
    public void setUserService(UnicastServiceUserInterface userService) {
        this.userService = userService;
    }

    /**
     * Utiliza o Protocolo UDP para receber mensagens
     * <p>
     * Recebe a mensagem do Protocolo UDP, em seguida utiliza o {@link
     * UnicastAddressSingleton} para buscar o ucsap_id do Nó que enviou a mensagem e
     * armazena a nova mensagem usando {@link UnicastReceivedMessagesSingleton}. Por
     * fim, notifica o usuário usando {@link UnicastServiceUserInterface}
     * </p>
     * 
     */
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

            this.userService.UPDataInd(ucsapId, unicastPDU.getMessageWithoutHeaders());

        } catch (IOException ioException) {
            if (running) {
                // Erro Inesperado
                System.err.println(ioException);
            } else {
                // Erro esperado devido ao fechamento do programa
                return;
            }
        } catch (InvalidPDUException invalidPDUException) {
            System.err.println(invalidPDUException);
        }

    }

    /**
     * Utiliza o Protocolo UDP para enviar uma mensagem
     * 
     * @param destination - O ucsap_id do nó destino da mensagem
     * 
     * @param message     - A mensagem a ser enviada para o nó destino
     * 
     * @return um valor boolean. {@code true} se a mensagem foi enviada ou {@code
     * false} caso contrário
     */
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
            unicastPDU = new UnicastPDU(this.formatMessaage(message));
            inetAddress = destinationAddress.getInetAddress();
            portNumber = destinationAddress.getPortNumber();

            System.out.println("[SENDING...]: from " + this.ucsapId + " sending message to " + inetAddress.toString()
                    + ":" + portNumber
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

    /**
     * Formata uma mensagem de usuário para o formato de mensagem da PDU do Unicast
     * <p>
     * Formata uma mensagem para o padrão:
     * {@code UPDREQPDU <tamanho_dados> <dados>}
     * </p>
     * 
     * @param message - mensagem do usuário
     * 
     * @return mensagem no padrão da PDU do Unicast
     */
    private String formatMessaage(String message) {
        return "UPDREQPDU " + message.length() + " " + message;
    }
}
