public interface RoutingProtocolManagementServiceUserInterface {
    public void distanceTableIndication(short i, int[][] matrix);

    public void linkCostIndication(short i, short j, int cost);
}
