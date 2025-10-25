package src.Unicast;

import java.util.regex.Pattern;

import src.Unicast.Exception.InvalidPDUException;
import src.Unicast.Exception.InvalidPDUFormatException;
import src.Unicast.Exception.InvalidPDUSizeException;

/**
 * Implementa o Formato Padrão de Mensagem do Protocolo Unicast.
 * <p>
 * Essa classe é responsável por garantir e padronizar o formato da
 * PDU do {@link UnicastProtocol}.
 *
 * @see UnicastProtocol
 */
public class UnicastPDU {

    /* O tamanho máximo da mensagem */
    private final int sizeInBytes = 1024;

    /* O conteúdo da mensagem */
    private String message;

    /* Mensagem em bytes */
    private byte[] buffer;

    /*
     * Constrói a instância PDU para uma mensagem
     * <p>
     * Tenta formatar a mensagem para o padrão UDP, caso haja algum erro, uma
     * {@link InvalidPDUException} é ativada
     * 
     * @param message - conteúdo da mensagem
     */
    public UnicastPDU(String message) throws InvalidPDUException {
        this.message = message;
        this.buffer = message.getBytes();

        verifyMessage();
    }

    /*
     * Pega a mensagem em bytes
     * 
     * @return mensagem formatada em m array de bytes
     */
    public byte[] getMessageBytes() {
        return buffer;
    }

    /*
     * Pega a mensagem formatada
     * 
     * @return mensagem formatada
     */
    public String getMessage() {
        return message;
    }

    /*
     * Verifica se a mensagem é válida
     * <p>
     * Verifica se a mensagem é nula, tem tamanho inválido ou se houve algum erro na
     * formatação, joga uma {@link InvalidPDUException} diferente para cada erro.
     * 
     * @return mensagem formatada
     */
    public void verifyMessage() throws InvalidPDUException {
        if (this.message == null) {
            throw new InvalidPDUFormatException("[INVALID FORMAT]: PDU IS NULL");
        }

        if (this.message.length() > sizeInBytes) {
            throw new InvalidPDUSizeException(this.message.length());
        }

        if (!Pattern.matches("UPDREQPDU \\d+ .+", this.message)) {
            throw new InvalidPDUFormatException(
                    "[INVALID FORMAT]: PDU SHOULD HAVE DE FORMAT <UPDREQPDU><space><data_size><space><data>");
        }
    }
}
