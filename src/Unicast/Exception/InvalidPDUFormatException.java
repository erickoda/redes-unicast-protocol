package src.Unicast.Exception;

/**
 * Exceção lançada para indicar que uma PDU tem formato inválido.
 *
 * @see InvalidPDUException
 */
public class InvalidPDUFormatException extends InvalidPDUException {
    /**
     * Constrói uma nova {@code InvalidPDUFormatException} com a mensagem de erro
     * especificada.
     *
     * @param errorMessage a mensagem de detalhe que descreve o erro.
     */
    public InvalidPDUFormatException(String errorMessage) {
        super(errorMessage);
    }
}
