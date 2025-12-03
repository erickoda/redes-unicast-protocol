package src.Unicast.Exception;

/**
 * Exceção lançada para indicar que uma PDU do Protocolo Unicast
 * é inválida.
 *
 * @see java.lang.Exception
 */
public class InvalidPDUException extends Exception {
    /**
     * Constrói uma nova {@code InvalidPDUException} com a mensagem de erro
     * especificada.
     *
     * @param errorMessage a mensagem de detalhe que descreve o erro.
     */
    public InvalidPDUException(String errorMessage) {
        super(errorMessage);
    }
}
