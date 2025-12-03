package src;

import java.util.ArrayList;

import src.Routing.RoutingInformationProtocol;
import src.Routing.RoutingManagementApplication;
import src.Unicast.UnicastAddress;
import src.Unicast.UnicastAddressSingleton;

class Main {

    public static void main(String[] args) throws Exception {
        UnicastAddressSingleton unicastAddressSingleton = UnicastAddressSingleton.getInstance();
        ArrayList<RoutingInformationProtocol> nodes = new ArrayList<RoutingInformationProtocol>();
        RoutingManagementApplication routingManagementApplication = null;

        for (UnicastAddress address : unicastAddressSingleton.getUnicastAddresses()) {
            RoutingInformationProtocol rip = new RoutingInformationProtocol(address.getUcsapId(), 10);
            if (address.getUcsapId() == 0) {
                routingManagementApplication = new RoutingManagementApplication(rip);
                rip.setManagementInterface(routingManagementApplication);
            } else {
                nodes.add(rip);
            }
            Thread.sleep(500);
        }

        if (routingManagementApplication != null) {
            // routingManagementApplication.getRoutingManagement().setLinkCost((short) 1,
            // (short) 2, 10);
            // routingManagementApplication.getRoutingManagement().getLinkCost((short) 2,
            // (short) 3);
            routingManagementApplication.getRoutingManagement().getDistanceTable((short) 1);
        }
    }
}
