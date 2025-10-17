import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UnicastPDU {

    final int sizeInBytes = 1024;
    String data;

    public UnicastPDU(String data) {
        this.data = data;
    }

    public boolean isValid() {
        if (this.data == null) {
            return false;
        }

        if (this.data.length() > sizeInBytes) {
            return false;
        }

        if (!Pattern.matches("UPDREQPDU \\d+ .+", this.data)) {
            return false;
        }

        return true;
    }
}
