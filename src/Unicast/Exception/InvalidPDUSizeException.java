package src.Unicast.Exception;

/**
 * Exceção lançada para indicar que uma PDU tem Tamanho inválido.
 *
 * @see InvalidPDUException
 */
public class InvalidPDUSizeException extends InvalidPDUException {
    /**
     * Constrói uma nova {@code InvalidPDUFormatException} com a mensagem de erro
     * especificada.
     *
     * @param size - O tamanho inválido da PDU
     */
    public InvalidPDUSizeException(int size) {
        super("[INVALID PDU SIZE]: PDU SHOULD HAS A SIZE BETWEEN 0 AND 1024. YOUR PDU HAS A SIZE: " + size);
    }
}