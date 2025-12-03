package src.Routing;

import src.Node;
import src.Unicast.UnicastProtocol;
import src.Unicast.UnicastServiceInterface;
import src.Unicast.UnicastServiceUserInterface;
import src.Manager;

/**
 * Implementa a lógica principal para o Protocolo de Troca de Informações de
 * Roteamento.
 * <p>
 * Essa classe é responsável pela troca de informações de roteamento.
 * Ela atua como um contexto que delega a lógica para uma estratégia específica
 * (Nó ou Gerente) baseada no ID de inicialização.
 * </p>
 */
public class RoutingInformationProtocol
        implements UnicastServiceUserInterface, RoutingProtocolManagementInterface {

    /** O serviço de transferência Unicast */
    private UnicastServiceInterface unicastService;

    /** A interface para notificar a aplicação de gerenciamento */
    private RoutingProtocolManagementServiceUserInterface managementInterface;

    /** A estratégia ativa (Lógica de Nó ou Lógica de Gerente) */
    private final RoutingInformationProtocolStrategy strategy;

    /**
     * Construtor principal.
     * 
     * @param nodeId         O ID deste nó (0 = Gerente, 1-15 = Nó).
     * @param timeoutSeconds O valor do timeout (propagação ou retransmissão).
     */
    public RoutingInformationProtocol(short nodeId, int timeoutSeconds) {
        UnicastProtocol unicastProtocol = new UnicastProtocol(nodeId);
        unicastProtocol.setUserService(this);
        new Thread(unicastProtocol).start();
        this.unicastService = unicastProtocol;

        if (nodeId == 0) {
            this.strategy = new Manager(this, timeoutSeconds);
        } else {
            this.strategy = new Node(this, nodeId, timeoutSeconds);
        }
    }

    /**
     * Retorna o serviço de unicast associado.
     * 
     * @return O serviço de unicast.
     */
    public UnicastServiceInterface getUnicastService() {
        return unicastService;
    }

    /**
     * Define a interface de gerenciamento para enviar notificações à
     * aplicação.
     * 
     * @param managementInterface A interface para enviar notificações.
     */
    public void setManagementInterface(RoutingProtocolManagementServiceUserInterface managementInterface) {
        this.managementInterface = managementInterface;
    }

    /**
     * Método auxiliar para a estratégia notificar a aplicação sobre Distance Table.
     * 
     * @param node  O nó que enviou a tabela.
     * @param table A matriz de distâncias.
     */
    public void notifyDistanceTableIndication(short node, int[][] table) {
        if (this.managementInterface != null) {
            this.managementInterface.distanceTableIndication(node, table);
        }
    }

    /**
     * Método auxiliar para a estratégia notificar a aplicação sobre Link Cost.
     * 
     * @param nodeA Nó origem.
     * @param nodeB Nó destino.
     * @param cost  Custo do link.
     */
    public void notifyLinkCostIndication(short nodeA, short nodeB, int cost) {
        if (this.managementInterface != null) {
            this.managementInterface.linkCostIndication(nodeA, nodeB, cost);
        }
    }

    /**
     * Notifica o usuário do recebimento de uma nova mensagem.
     * 
     * @param source  - o UCSAP ID da entidade que enviou a mensagem
     * @param message - a mensagem recebida
     */
    @Override
    public void UPDataInd(short source, String message) {
        strategy.handleMessage(source, message);
    }

    /**
     * Solicita a Tabela de Distância de um nó específico.
     * 
     * @param node O ID do nó.
     * @return true se a requisição foi enviada com sucesso, false caso contrário.
     */
    @Override
    public boolean getDistanceTable(short node) {
        return strategy.executeGetDistanceTable(node);
    }

    /**
     * Solicita o custo do link entre dois nós.
     * 
     * @param nodeA O ID do nó A.
     * @param nodeB O ID do nó B.
     * 
     * @return true se a requisição foi enviada com sucesso, false caso contrário.
     */
    @Override
    public boolean getLinkCost(short nodeA, short nodeB) {
        return strategy.executeGetLinkCost(nodeA, nodeB);
    }

    /**
     * Altera o custo do link entre dois nós.
     * 
     * @param nodeA O ID do nó A.
     * @param nodeB O ID do nó B.
     * @param cost  O novo custo do link.
     * 
     * @return true se a requisição foi enviada com sucesso, false caso contrário.
     */
    @Override
    public boolean setLinkCost(short nodeA, short nodeB, int cost) {
        return strategy.executeSetLinkCost(nodeA, nodeB, cost);
    }
}