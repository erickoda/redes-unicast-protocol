package src.Unicast.Exception;

public class InvalidPDUSizeException extends InvalidPDUException {
    public InvalidPDUSizeException(int size) {
        super("[INVALID PDU SIZE]: PDU SHOULD HAS A SIZE BETWEEN 0 AND 1024. YOUR PDU HAS A SIZE: " + size);
    }
}