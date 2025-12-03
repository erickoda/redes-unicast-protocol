package src.Routing;

public interface RoutingInformationProtocolStrategy {
    public void handleMessage(short source, String message);

    // Métodos opcionais (lançam exceção ou retornam false se não implementados)
    default boolean executeGetLinkCost(short nodeA, short nodeB) {
        return false;
    }

    default boolean executeSetLinkCost(short nodeA, short nodeB, int cost) {
        return false;
    }

    default boolean executeGetDistanceTable(short node) {
        return false;
    }
}
