package src.Unicast;

/*
 * Define o serviço de Unicast server para implementação do protocolo Unicast.
 * <p>
 * Essa interface especifica o contrato para um usuário que deseja enviar dados
 * para outro nó na rede Unicast
 */
public interface UnicastServerInterface {
    /*
     * Utiliza o Protocolo UDP para enviar uma mensagem
     * <p>
     * 
     * @param destination - O ucsap_id do nó destino da mensagem
     * 
     * @param message - A mensagem a ser enviada para o nó destino
     * 
     * @return um valor boolean. {@code true} se a mensagem foi enviada ou {@code
     * false} caso contrário
     */
    public boolean UPDataReq(short destination, String message);
}
