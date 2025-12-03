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
 * </p>
 *
 * @see UnicastProtocol
 */
public class UnicastPDU {

    /** O tamanho máximo da mensagem */
    private final int sizeInBytes = 1024;

    /** O conteúdo da mensagem */
    private String message;

    /** Mensagem em bytes */
    private byte[] buffer;

    /**
     * Constrói a instância PDU para uma mensagem
     * <p>
     * Tenta formatar a mensagem para o padrão UDP, caso haja algum erro, uma
     * {@link InvalidPDUException} é ativada
     * </p>
     * 
     * @param message - conteúdo da mensagem
     * 
     * @throws InvalidPDUException lança uma exceção se o formato da PDU for
     *                             inválido.
     */
    public UnicastPDU(String message) throws InvalidPDUException {
        this.message = message;
        this.buffer = message.getBytes();

        verifyMessage();
    }

    /**
     * Pega a mensagem em bytes
     * 
     * @return mensagem formatada em m array de bytes
     */
    public byte[] getMessageBytes() {
        return buffer;
    }

    /**
     * Pega a mensagem formatada
     * 
     * @return mensagem formatada
     */
    public String getMessage() {
        return message;
    }

    /**
     * Pega a mensagem sem os cabeçalhos
     * <p>
     * Retorna a mensagem sem os cabeçalhos de controle (UPDREQPDU e tamanho dos
     * dados)
     * </p>
     * 
     * @return mensagem sem os cabeçalhos
     */
    public String getMessageWithoutHeaders() {
        String[] parts = this.message.split(" ", 3);
        return parts[2];
    }

    /**
     * Verifica se a mensagem é válida
     * <p>
     * Verifica se a mensagem é nula, tem tamanho inválido ou se houve algum erro na
     * formatação, joga uma {@link InvalidPDUException} diferente para cada erro.
     * 
     * @throws InvalidPDUException - Lança um exceção se o formato da PDU for
     *                             inválido
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
