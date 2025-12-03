package src.Routing;

/**
 * Interface de Usuário do Serviço de Gerenciamento do Protocolo de Roteamento.
 */
public interface RoutingProtocolManagementServiceUserInterface {
    /**
     * Imprime a Tabela de Distância de um nó específico.
     * 
     * @param node
     * @param table A tabela de distâncias.
     */
    public void distanceTableIndication(short node, int[][] table);

    /**
     * Imprime o custo do enlace entre dois nós.
     * 
     * @param nodeA
     * @param nodeB
     * @param cost  Custo do enlace.
     */
    public void linkCostIndication(short nodeA, short nodeB, int cost);
}
