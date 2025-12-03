package src.Routing;

/**
 * Estratégia para o Protocolo de Informação de Roteamento.
 * 
 * @see src.Routing.RoutingInformationProtocol
 */
public interface RoutingInformationProtocolStrategy {
    /**
     * Manipula a mensagem recebida de um nó específico ou gerente.
     * 
     * @param source  ID do nó ou gerente que enviou a mensagem.
     * @param message A mensagem recebida.
     */
    public void handleMessage(short source, String message);

    /**
     * Solicita o custo do link entre dois nós.
     * 
     * @param nodeA
     * @param nodeB
     * @return true se a requisição foi enviada com sucesso, false caso contrário.
     */
    default boolean executeGetLinkCost(short nodeA, short nodeB) {
        return false;
    }

    /**
     * Altera o custo do link entre dois nós.
     * 
     * @param nodeA
     * @param nodeB
     * @param cost  custo do enlace
     * @return true se a requisição foi enviada com sucesso, false caso contrário.
     */
    default boolean executeSetLinkCost(short nodeA, short nodeB, int cost) {
        return false;
    }

    /**
     * Solicita a Tabela de Distância de um nó específico.
     * 
     * @param node
     * @return true se a requisição foi enviada com sucesso, false caso contrário.
     */
    default boolean executeGetDistanceTable(short node) {
        return false;
    }
}
