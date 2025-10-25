package src.Unicast;

import java.util.ArrayList;

/**
 * Implementa um Singleton que armazena as mensagens recebidas.
 * <p>
 * Essa classe é responsável armazenar os {@link UnicastPDU} recebidos pela
 * entidade do {@link UnicastProtocol}. em execução
 *
 * @see UnicastProtocol
 * @see UnicastPDU
 */
public final class UnicastReceivedMessagesSingleton {
    /* Referência para a única instância do Singleton. */
    private static UnicastReceivedMessagesSingleton instance = null;

    /* Lista de mensagens recebidas. */
    private ArrayList<UnicastPDU> unicastMessages = new ArrayList<UnicastPDU>();

    /*
     * Constrói a instância do singleton.
     */
    private UnicastReceivedMessagesSingleton() {
    }

    /*
     * Adiciona um mensagem para o singleton
     */
    public void addMessage(UnicastPDU unicastPDU) {
        this.unicastMessages.add(unicastPDU);
    }

    /*
     * Pega a instância do singleton.
     * <p>
     * Retorna a instância do singleton. Cria a instância se não existir.
     * 
     * @return retorna a instância do singleton
     */
    public synchronized static UnicastReceivedMessagesSingleton getInstance() {
        if (instance == null) {
            instance = new UnicastReceivedMessagesSingleton();
        }

        return instance;
    }

    /*
     * Pega as mensagens.
     * 
     * @return retorna as mensagens recebidas
     */
    public ArrayList<UnicastPDU> getMessages() {
        return this.unicastMessages;
    }
}
