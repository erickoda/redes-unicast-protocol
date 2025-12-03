package src.Routing;

/**
 * Interface para o gerenciamento do protocolo de roteamento.
 */
public interface RoutingProtocolManagementInterface {
    /**
     * Solicita a Tabela de Distância de um nó específico.
     * 
     * @param node Nó
     * 
     * @return true se a requisição foi enviada com sucesso, false caso contrário.
     */
    public boolean getDistanceTable(short node);

    /**
     * Solicita o custo do enlace entre dois nós.
     * 
     * @param nodeA Nó A
     * @param nodeB Nó B
     * 
     * @return true se a requisição foi enviada com sucesso, false caso contrário.
     */
    public boolean getLinkCost(short nodeA, short nodeB);

    /**
     * Altera o custo do enlace entre dois nós.
     * 
     * @param nodeA Nó A
     * @param nodeB Nó B
     * @param cost  Custo do enlace entre A e B
     * 
     * @return true se a requisição foi enviada com sucesso, false caso contrário.
     */
    public boolean setLinkCost(short nodeA, short nodeB, int cost);
}
