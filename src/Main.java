package src;

import java.util.ArrayList;
import java.util.Scanner;

import src.Routing.RoutingInformationProtocol;
import src.Routing.RoutingManagementApplication;
import src.Routing.RoutingProtocolManagementInterface;
import src.Unicast.UnicastAddress;
import src.Unicast.UnicastAddressSingleton;

public class Main {

    public static void main(String[] args) {
        try {
            System.out.println("=== Inicializando Sistema de Roteamento ===");

            // Carrega endereços
            UnicastAddressSingleton unicastAddressSingleton = UnicastAddressSingleton.getInstance();
            ArrayList<RoutingInformationProtocol> nodes = new ArrayList<>();
            RoutingManagementApplication app = null;

            // Instancia e inicia todos os nós e o gerente
            for (UnicastAddress address : unicastAddressSingleton.getUnicastAddresses()) {
                short id = address.getUcsapId();
                System.out.println("Iniciando entidade ID: " + id);

                RoutingInformationProtocol rip = new RoutingInformationProtocol(id, 10);

                if (id == 0) {
                    // Configura o Gerente e a Aplicação de Gerência
                    app = new RoutingManagementApplication(rip);
                    rip.setManagementInterface(app);
                } else {
                    nodes.add(rip);
                }

                Thread.sleep(200);
            }

            if (app == null) {
                System.err.println("[ERRO] Gerente (ID 0) não encontrado no arquivo de configuração!");
                System.exit(1);
            }

            // Interface Interativa (CLI)
            runInteractiveCLI(app);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Menu interativo para testar as funcionalidades do Gerente.
     */
    private static void runInteractiveCLI(RoutingManagementApplication app) {
        Scanner scanner = new Scanner(System.in);
        RoutingProtocolManagementInterface manager = app.getRoutingManagement();

        System.out.println("\n=== Sistema Pronto ===");
        System.out.println("Aguarde alguns segundos para a convergência inicial da rede...");
        printHelp();

        while (true) {
            System.out.print("\nComando> ");
            String input = scanner.nextLine().trim();
            String[] parts = input.split("\\s+");

            if (parts.length == 0 || parts[0].isEmpty())
                continue;

            String command = parts[0].toLowerCase();

            try {
                switch (command) {
                    case "table":
                    case "t":
                        executeGetTable(parts, manager);
                        break;

                    case "cost":
                    case "c":
                        executeGetCost(parts, manager);
                        break;

                    case "set":
                    case "s":
                        executeSetCost(parts, manager);
                        break;

                    case "help":
                    case "h":
                        printHelp();
                        break;

                    case "exit":
                    case "quit":
                        exitProgram();
                        break;

                    default:
                        System.out.println("Comando desconhecido. Digite 'help'.");
                }
            } catch (NumberFormatException e) {
                System.out.println("[ERRO] Argumentos devem ser números inteiros.");
            }
        }
    }

    private static void printHelp() {
        System.out.println("\n--- Comandos Disponíveis ---");
        System.out.println("  table <id>           : Solicita a Tabela de Distância do nó (Ex: table 1)");
        System.out.println("  cost <A> <B>         : Solicita o custo do link entre A e B (Ex: cost 1 2)");
        System.out.println("  set <A> <B> <custo>  : Altera o custo entre A e B (Ex: set 1 2 10)");
        System.out.println("  exit                 : Sair do programa");
        System.out.println("----------------------------");
    }

    private static void executeGetTable(String[] parts, RoutingProtocolManagementInterface manager)
            throws NumberFormatException {
        if (parts.length < 2) {
            System.out.println("Uso: table <node_id>");
        } else {
            short nodeId = Short.parseShort(parts[1]);
            System.out.println("Solicitando Tabela de Distância do Nó " + nodeId + "...");
            boolean ok = manager.getDistanceTable(nodeId);
            if (!ok)
                System.out
                        .println("[ERRO] Falha ao enviar requisição (Gerente ocupado ou ID inválido).");
        }
    }

    private static void executeGetCost(String[] parts, RoutingProtocolManagementInterface manager)
            throws NumberFormatException {
        if (parts.length < 3) {
            System.out.println("Uso: cost <nodeA> <nodeB>");
        } else {
            short a = Short.parseShort(parts[1]);
            short b = Short.parseShort(parts[2]);
            System.out.println("Solicitando custo entre " + a + " e " + b + "...");
            manager.getLinkCost(a, b);
        }
    }

    private static void executeSetCost(String[] parts, RoutingProtocolManagementInterface manager)
            throws NumberFormatException {
        if (parts.length < 4) {
            System.out.println("Uso: set <nodeA> <nodeB> <custo>");
        } else {
            short a = Short.parseShort(parts[1]);
            short b = Short.parseShort(parts[2]);
            int cost = Integer.parseInt(parts[3]);
            System.out.println("Definindo custo entre " + a + " e " + b + " para " + cost + "...");
            manager.setLinkCost(a, b, cost);
        }
    }

    private static void exitProgram() {
        System.out.println("Encerrando o sistema...");
        System.exit(0);
    }
}