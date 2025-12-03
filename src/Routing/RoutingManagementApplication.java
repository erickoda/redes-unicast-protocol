package src.Routing;

/**
 * Aplicação de Gerenciamento de Roteamento que implementa a interface
 * de usuário do serviço de gerenciamento de protocolo de roteamento.
 *
 * @see src.Routing.RoutingProtocolManagementServiceUserInterface
 */
public class RoutingManagementApplication implements RoutingProtocolManagementServiceUserInterface {
    /** Protocolo de Roteamento */
    private RoutingProtocolManagementInterface routingManagement;

    /**
     * Construtor principal.
     * 
     * @param routingManagement
     */
    public RoutingManagementApplication(
            RoutingProtocolManagementInterface routingManagement) {
        this.routingManagement = routingManagement;
    }

    /**
     * Imprime a Tabela de Distância de um nó específico.
     */
    public void distanceTableIndication(short node, int[][] table) {
        System.out.println();
        System.out.println("Tabela de Distância do Nó: " + node);

        for (int[] row : table) {
            for (int element : row) {
                System.out.print(element + " ");
            }
            System.out.println();
        }

        System.out.println();
    }

    /**
     * Imprime o custo do enlace entre dois nós.
     */
    public void linkCostIndication(short nodeA, short nodeB, int cost) {
        System.out.println();

        System.out.println("Custo do nó " + nodeA + " para o nó " + nodeB + " é " + cost);

        System.out.println();
    }

    /**
     * Retorna a interface de gerenciamento de roteamento.
     * 
     * @return A interface de gerenciamento de roteamento.
     */
    public RoutingProtocolManagementInterface getRoutingManagement() {
        return routingManagement;
    }
}