package io.civis.blockchain.coop.core.factory;

import io.civis.blockchain.coop.core.config.FabricConfig;
import io.civis.blockchain.coop.core.config.OrdererConfig;
import io.civis.blockchain.coop.core.config.OrganisationConfig;
import io.civis.blockchain.coop.core.config.PeerConfig;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.TransactionException;

import java.io.IOException;
import java.util.Collection;

public class FabricChannelFactory {

    private final FabricConfig fabricConfig;

    public FabricChannelFactory(FabricConfig fabricConfig) {
        this.fabricConfig = fabricConfig;
    }

    public static FabricChannelFactory factory(FabricConfig fabricConfig) {
        return new FabricChannelFactory(fabricConfig);
    }

    public Channel getChannel(HFClient client, String orgName, String channelName) throws IOException, InvalidArgumentException, TransactionException {
        OrganisationConfig orgConfig = fabricConfig.getNetwork().getOrganisation(orgName);
        OrdererConfig ordererConfig = fabricConfig.getNetwork().getOrderer();

        Orderer orderer = client.newOrderer(ordererConfig.getServerHostname(), ordererConfig.getUrl(), ordererConfig.getPeerTlsProperties());

        Channel channel = client.newChannel(channelName);
        addPeers(channel, client, orgConfig.getPeers().values());
        return channel
                .addOrderer(orderer)
                .initialize();
    }

    private void addPeers(Channel channel, HFClient client, Collection<PeerConfig> values) throws InvalidArgumentException, IOException {
        for(PeerConfig pc : values) {
            EventHub eventHub = client.newEventHub(pc.getServerHostname(), pc.getEvents());
            Peer peer = client.newPeer(pc.getServerHostname(), pc.getRequests(), pc.getPeerTlsProperties());
            channel.addEventHub(eventHub);
            channel.addPeer(peer);
        }
    }
}
