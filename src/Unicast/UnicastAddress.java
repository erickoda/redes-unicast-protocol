package src.Unicast;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Implementa o Formato Padrão de Endereços de Entidades do Protocolo Unicast
 * <p>
 * Essa classe é responsável por validar e padronizar o formato dos
 * endereços do {@link UnicastProtocol}.
 *
 * @see UnicastProtocol
 */
public class UnicastAddress {
    /** Unicast Service Access Point - UCSAP */
    private short ucsapId;

    /** O endereço IP base */
    private InetAddress inetAddress;

    /** Porta utilizada pelo socket */
    private int portNumber;

    /**
     * Constrói a instância do Endereço
     * <p>
     * Extrai os endereços de um String, formata para os tipos apropriados e joga
     * uma {@link UnknownHostException} caso o endereço IP base seja inválido.
     * </p>
     * 
     * @param line - uma String com os dados do endereço.
     * 
     * @throws UnknownHostException - lança uma exceção caso o Ip seja desconhecido.
     */
    public UnicastAddress(String line) throws UnknownHostException {
        String[] words = line.split(" ");

        this.ucsapId = Short.parseShort(words[0]);
        this.inetAddress = InetAddress.getByName(words[1]);
        this.portNumber = Integer.parseInt(words[2]);
    }

    /**
     * Retorna o UCSAP
     * 
     * @return o identificador únicas UCSAP
     */
    public short getUcsapId() {
        return ucsapId;
    }

    /**
     * Retorna o endereço IP base
     * 
     * @return endereço IP
     */
    public InetAddress getInetAddress() {
        return inetAddress;
    }

    /**
     * Retorna a porta do endereço
     * 
     * @return porta utilizada no endereço
     */
    public int getPortNumber() {
        return portNumber;
    }
}
