package src;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.Scanner;

import src.Routing.RoutingInformationProtocol;
import src.Unicast.UnicastAddress;
import src.Unicast.UnicastAddressSingleton;
import src.Unicast.UnicastPDU;
import src.Unicast.UnicastProtocol;
import src.Unicast.UnicastReceivedMessagesSingleton;

class Main {

    public static void main(String[] args) {
        Scanner sc;
        String message;
        String command;
        short nodeUcsapId = 0;
        short destination;

        if (args.length == 0) {
            System.err.println("[ERROR]: SPECIFY THE UCSAP_ID BY COMMAND LINE");
            System.exit(1);
        }

        try {
            nodeUcsapId = Short.parseShort(args[0]);
        } catch (NumberFormatException numberFormatException) {
            System.err.println("[ERROR]: INVALID UCSAP_ID FORMAT! IT IS NOT A VALID SHORT NUMBER");
            System.exit(1);
        }

        command = "";
        sc = new Scanner(System.in);

        // Inicialização dos endereços da rede unicast
        UnicastAddressSingleton unicastAddresses = UnicastAddressSingleton.getInstance();
        Optional<UnicastAddress> unicastAddressOptional = unicastAddresses.getUnicastAddressFrom(nodeUcsapId);

        if (unicastAddressOptional.isEmpty()) {
            System.err.println("[ERROR]: NODE NOT FOUND ON ENTITY CONFIGURATION FILE");
            System.exit(1);
        }

        UnicastAddress unicastAddress = unicastAddressOptional.get();
        System.out.println(unicastAddress.getUcsapId() + " " + unicastAddress.getHostName() + ":"
                + unicastAddress.getPortNumber());

        // Inicialização dos objetos de protocolo e roteamento
        UnicastProtocol unicastProtocol = new UnicastProtocol();
        RoutingInformationProtocol routingInformationProtocol = new RoutingInformationProtocol(nodeUcsapId);

        unicastProtocol.setUserService(routingInformationProtocol);
        routingInformationProtocol.setUnicastServer(unicastProtocol);

        UnicastReceivedMessagesSingleton unicastReceivedMessagesSingleton = UnicastReceivedMessagesSingleton
                .getInstance();

        new Thread(routingInformationProtocol).start();

        while (!command.equals("exit")) {

            // Imprime lista de comandos na tela
            System.out.println("=========================================================");
            System.out.println("| Commands:                                             |");
            System.out.println("| Send a message: send<space><ucsap_id><space><message> |");
            System.out.println("| Read Messages: read                                   |");
            System.out.println("| Exit: exit                                            |");
            System.out.println("=========================================================");

            // Lê o comando
            command = sc.nextLine();
            String[] commandWords = command.split(" ");

            switch (commandWords[0].toLowerCase()) {
                case "send":

                    if (commandWords.length < 3) {
                        System.err.println(
                                "[ERROR]: INVALID COMMAND FORMAT. Use send<space><ucsap_id><space><message>. Example: send 0 Hello! How are you?");
                    }

                    try {
                        destination = Short.parseShort(commandWords[1]);
                        message = String.join(" ",
                                Arrays.copyOfRange(commandWords, 2, commandWords.length));

                        System.out.println("[SENDING...]: " + message + " to destination " + destination);

                        unicastProtocol.UPDataReq(destination, message);
                    } catch (NumberFormatException numberFormatException) {
                        System.out.println(
                                "[ERROR]: The ucsap_id '" + commandWords[1] + "' is not a valid short number.");
                    }

                    break;
                case "read":
                    ArrayList<UnicastPDU> pduMessages = unicastReceivedMessagesSingleton.getMessages();

                    for (UnicastPDU pduMessage : pduMessages) {
                        System.out.println(pduMessage.getMessage());
                    }

                    break;
                case "exit":
                    break;
                default:
                    System.out.println("[ERROR]: INVALID COMMAND");
                    break;
            }
        }

        sc.close();
    }
}
