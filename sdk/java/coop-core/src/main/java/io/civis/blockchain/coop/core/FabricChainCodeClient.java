package io.civis.blockchain.coop.core;

import io.civis.blockchain.coop.core.config.FabricConfig;
import io.civis.blockchain.coop.core.factory.FabricChannelFactory;
import io.civis.blockchain.coop.core.factory.FabricClientFactory;
import io.civis.blockchain.coop.core.model.InvokeArgs;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class FabricChainCodeClient {

    public static FabricChainCodeClient fromConfigFile(String filename) throws IOException {
        FabricConfig fabricConfig = FabricConfig.loadFromFile(filename);
        FabricClientFactory clientFactoty = FabricClientFactory.factory(fabricConfig);
        FabricChannelFactory channelFactory = FabricChannelFactory.factory(fabricConfig);
        return new FabricChainCodeClient(clientFactoty, channelFactory);
    }

    private final FabricClientFactory clientFactoty;
    private final FabricChannelFactory channelFactory;


    public FabricChainCodeClient(FabricClientFactory clientFactoty, FabricChannelFactory channelFactory) {
        this.clientFactoty = clientFactoty;
        this.channelFactory = channelFactory;
    }

    public CompletableFuture<BlockEvent.TransactionEvent> invoke(User user, String orgName, String channelName, String chainId, InvokeArgs invokeArgs) throws Exception {
        HFClient client = clientFactoty.getHfClient(user);
        Channel channel = channelFactory.getChannel(client, orgName, channelName);
        ChaincodeID chanCodeId = ChaincodeID.newBuilder().setName(chainId).build();
        return invokeBlockChain(client, channel, chanCodeId, invokeArgs);
    }

    public String query(User user, String orgName, String channelName, String chainId, InvokeArgs invokeArgs) throws Exception {
        HFClient client = clientFactoty.getHfClient(user);
        Channel channel = channelFactory.getChannel(client, orgName, channelName);
        ChaincodeID chanCodeId = ChaincodeID.newBuilder().setName(chainId).build();
        return queryBlockChain(client, channel, chanCodeId, invokeArgs);
    }

    private CompletableFuture<BlockEvent.TransactionEvent> invokeBlockChain(HFClient client, Channel channel, ChaincodeID chanCodeId, InvokeArgs invokeArgs) throws ProposalException, InvalidArgumentException {
        TransactionProposalRequest qpr = client.newTransactionProposalRequest();
        qpr.setChaincodeID(chanCodeId);
        qpr.setFcn(invokeArgs.getFunction());
        qpr.setArgs(invokeArgs.getValues());
        Collection<ProposalResponse> res = channel.sendTransactionProposal(qpr, channel.getPeers());

        return channel.sendTransaction(res);
    }

    private String queryBlockChain(HFClient client, Channel channel, ChaincodeID chanCodeId, InvokeArgs invokeArgs) throws ProposalException, InvalidArgumentException {
        QueryByChaincodeRequest qpr = client.newQueryProposalRequest();
        qpr.setChaincodeID(chanCodeId);
        qpr.setFcn(invokeArgs.getFunction());
        qpr.setArgs(invokeArgs.getValues());
        Collection<ProposalResponse> res = channel.queryByChaincode(qpr);
        return new String(res.iterator().next().getChaincodeActionResponsePayload());
    }

}
