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

        return data != null && this.data.length() > 0;
    }
}
