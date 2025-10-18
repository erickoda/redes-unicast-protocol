package src.Unicast;

import java.util.regex.Pattern;

import src.Unicast.Exception.InvalidPDUException;
import src.Unicast.Exception.InvalidPDUFormatException;
import src.Unicast.Exception.InvalidPDUSizeException;

public class UnicastPDU {

    private final int sizeInBytes = 1024;
    private String message;
    private byte[] buffer;

    public UnicastPDU(String message) throws InvalidPDUException {
        this.message = message;
        this.buffer = message.getBytes();

        verifyMessage();
    }

    public byte[] getMessageBytes() {
        return buffer;
    }

    public String getMessage() {
        return message;
    }

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
