package src.Unicast;

/**
 * Define o serviço para implementação do Serviço de Usuário do Protocolo
 * Unicast
 * <p>
 * Essa interface define um contrato para notificar o Nó sobre o recebimento de
 * uma nova mensagem
 * </p>
 * 
 */
public interface UnicastServiceUserInterface {
    /**
     * Notifica o nó que uma nova mensagem chegou
     * 
     * @param source  - O ucsap_id do nó que enviou a mensagem
     * 
     * @param message - A mensagem recebida
     */
    public void UPDataInd(short source, String message);
}
