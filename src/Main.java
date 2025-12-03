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
        RoutingManagementApplication routingManagementApplication;

        for (UnicastAddress address : unicastAddressSingleton.getUnicastAddresses()) {
            RoutingInformationProtocol rip = new RoutingInformationProtocol(address.getUcsapId(), 5);
            if (address.getUcsapId() == 0) {
                routingManagementApplication = new RoutingManagementApplication(rip);
            } else {
                nodes.add(rip);
            }
            Thread.sleep(500);
        }
    }
}
