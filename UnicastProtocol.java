public class UnicastProtocol implements UnicastServerInterface {

    private UnicastServiceUserInterface userService;

    public UnicastProtocol() {
    }

    @Override
    public boolean UPDataReq(short destination, String message) {
        return true;
    }
}
