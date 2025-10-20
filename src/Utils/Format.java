package src.Utils;

public class Format {
    public static String message(String message) {
        return "UPDREQPDU " + message.length() + " " + message;
    }
}
