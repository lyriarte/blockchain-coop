package io.civis.blockchain.coop.core.factory;

import io.civis.blockchain.coop.core.config.FabricConfig;
import io.civis.blockchain.coop.core.config.OrdererConfig;
import io.civis.blockchain.coop.core.config.OrganisationConfig;
import io.civis.blockchain.coop.core.config.PeerConfig;
import io.civis.blockchain.coop.core.model.Endorser;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.TransactionException;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

public class FabricChannelFactory {

    private String cryptoConfigBase;
    private final FabricConfig fabricConfig;

    public FabricChannelFactory(FabricConfig fabricConfig, String cryptoConfigBase) {
        this.cryptoConfigBase = cryptoConfigBase;
        this.fabricConfig = fabricConfig;
    }

    public static FabricChannelFactory factory(FabricConfig fabricConfig, String cryptoConfigBase) {
        return new FabricChannelFactory(fabricConfig, cryptoConfigBase);
    }

    public Channel getChannel(List<Endorser> endorsers, HFClient client, String channelName) throws IOException, InvalidArgumentException, TransactionException {
        OrdererConfig ordererConfig = fabricConfig.getNetwork().getOrderer();

        Orderer orderer = client.newOrderer(ordererConfig.getServerHostname(), ordererConfig.getUrl(), ordererConfig.getPeerTlsProperties(cryptoConfigBase));

        Channel channel = client.newChannel(channelName);
        addPeers(endorsers, channel, client);
        return channel
                .addOrderer(orderer)
                .initialize();
    }

    private void addPeers(List<Endorser> endorsers, Channel channel, HFClient client) throws InvalidArgumentException, IOException {
        for(Endorser endorser : endorsers) {
            OrganisationConfig orgConfig = fabricConfig.getNetwork().getOrganisation(endorser.getOrganisation());
            PeerConfig peerConfig = orgConfig.getPeers().get(endorser.getPeer());

//            EventHub eventHub = client.newEventHub(peerConfig.getServerHostname(), peerConfig.getEvents());
            Peer peer = client.newPeer(peerConfig.getServerHostname(), peerConfig.getRequests(), peerConfig.getPeerTlsProperties(cryptoConfigBase));
//            channel.addEventHub(eventHub);
            channel.addPeer(peer);
        }
    }
}
