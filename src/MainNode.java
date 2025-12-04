package src;

import src.Routing.RoutingInformationProtocol;
import src.Unicast.UnicastAddress;
import src.Unicast.UnicastAddressSingleton;

/**
 * Classe principal para iniciar um Nó na rede.
 */
public class MainNode {

    /**
     * Carrega um Nó especificado por parâmetro.
     * 
     * @param args argumento por linha de comando: node_id.
     */
    public static void main(String[] args) {

        if (args.length != 1) {
            System.err.println("[ERRO] Uso: java -jar Node.jar <node_id>");
            System.exit(1);
        }

        System.out.println("=== Inicializando Nó ===");

        // Armazena o ID do nó a ser iniciado
        Short nodeId = Short.parseShort(args[0]);

        // Carrega endereços
        UnicastAddressSingleton unicastAddressSingleton = UnicastAddressSingleton.getInstance();
        RoutingInformationProtocol node = null;

        // Carrega o nó solicitado
        for (UnicastAddress address : unicastAddressSingleton.getUnicastAddresses()) {
            short id = address.getUcsapId();

            if (id == nodeId) {
                System.out.println("Iniciando entidade ID: " + id);
                RoutingInformationProtocol rip = new RoutingInformationProtocol(id, 10);
                node = rip;
            }
        }

        if (node == null) {
            System.err.println("[ERRO] Nó " + nodeId + " não encontrado no arquivo de configuração!");
            System.exit(1);
        }
    }

}
