package src.Routing;

import src.Unicast.UnicastServerInterface;
import src.Unicast.UnicastServiceUserInterface;

/**
 * Implementa a lógica principal para o Protocolo de Troca de Informações de
 * Roteamento.
 * <p>
 * Essa classe é responsável pela troca de informações de roteamento.
 * 
 */
public class RoutingInformationProtocol
        implements UnicastServiceUserInterface, RoutingProtocolManagementInterface {
    /* O serviço de transferência Unicast */
    private UnicastServerInterface unicastServer;
    private RoutingProtocolManagementServiceUserInterface routingProtocolManagementServiceUserInterface;

    public RoutingInformationProtocol() {
    }

    public UnicastServerInterface getUnicastServer() {
        return unicastServer;
    }

    public void setUnicastServer(UnicastServerInterface unicastServer) {
        this.unicastServer = unicastServer;
    }

    /*
     * Notifica o usuário do recebimento de uma nova mensagem.
     * 
     * @param source - o UCSAP ID da entidade que enviou a mensagem
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
