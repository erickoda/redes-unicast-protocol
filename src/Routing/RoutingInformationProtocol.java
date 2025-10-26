package src.Routing;

import src.Unicast.UnicastServiceInterface;
import src.Unicast.UnicastServiceUserInterface;

/**
 * Implementa a lógica principal para o Protocolo de Troca de Informações de
 * Roteamento.
 * <p>
 * Essa classe é responsável pela troca de informações de roteamento.
 * </p>
 * 
 */
public class RoutingInformationProtocol
        implements UnicastServiceUserInterface, RoutingProtocolManagementInterface {
    /** O serviço de transferência Unicast */
    private UnicastServiceInterface unicastService;
    private RoutingProtocolManagementServiceUserInterface routingProtocolManagementServiceUserInterface;

    public RoutingInformationProtocol() {
    }

    public UnicastServiceInterface getUnicastService() {
        return unicastService;
    }

    /**
     * Define o serviço de transferência Unicast
     * 
     * @param unicastService - O serviço de transferência Unicast
     */
    public void setUnicastService(UnicastServiceInterface unicastService) {
        this.unicastService = unicastService;
    }

    /**
     * Notifica o usuário do recebimento de uma nova mensagem.
     * 
     * @param source  - o UCSAP ID da entidade que enviou a mensagem
     * 
     * @param message - a mensagem recebida
     */
    @Override
    public void UPDataInd(short source, String message) {
        System.out.println("[NEW MESSAGE]: You received a new message from " + source + ". Content: " + message);
    }

    public boolean getDistanceTable(short node) {
        return true;
    }

    public boolean getLinkCost(short nodeA, short nodeB) {
        return true;
    }

    public boolean setLinkCost(short nodeA, short nodeB, int cost) {
        return true;
    }
}
