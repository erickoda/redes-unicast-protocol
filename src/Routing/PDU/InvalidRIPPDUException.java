package src.Routing.PDU;

/**
 * Exceção lançada para indicar que uma PDU do RIP
 * é inválida.
 *
 * @see java.lang.Exception
 */
public class InvalidRIPPDUException extends Exception {
    /**
     * Constrói uma nova {@code InvalidRIPPDUException} com a mensagem de erro
     * especificada.
     *
     * @param errorMessage a mensagem de detalhe que descreve o erro.
     */
    public InvalidRIPPDUException(String errorMessage) {
        super(errorMessage);
    }
}
