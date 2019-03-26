package io.civis.blockchain.coop.core;

import io.civis.blockchain.coop.core.config.FabricConfig;
import io.civis.blockchain.coop.core.exception.InvokeException;
import io.civis.blockchain.coop.core.factory.FabricChannelFactory;
import io.civis.blockchain.coop.core.model.Endorser;
import io.civis.blockchain.coop.core.model.InvokeArgs;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class FabricChainCodeClient {

    public static FabricChainCodeClient fromConfigFile(String filename, String cryptoConfigBase) throws IOException {
        FabricConfig fabricConfig = FabricConfig.loadFromFile(filename);
        FabricChannelFactory channelFactory = FabricChannelFactory.factory(fabricConfig, cryptoConfigBase);

        return new FabricChainCodeClient(channelFactory);
    }

    private final FabricChannelFactory channelFactory;


    public FabricChainCodeClient(FabricChannelFactory channelFactory) {
        this.channelFactory = channelFactory;
    }

    public CompletableFuture<BlockEvent.TransactionEvent> invoke(List<Endorser> endorsers, HFClient client, String channelName, String chainId, InvokeArgs invokeArgs) throws Exception {
        Channel channel = channelFactory.getChannel(endorsers, client, channelName);
        ChaincodeID chanCodeId = ChaincodeID.newBuilder().setName(chainId).build();
        return invokeBlockChain(client, channel, chanCodeId, invokeArgs);
    }

    public String query(List<Endorser> endorsers, HFClient client, String channelName, String chainId, InvokeArgs invokeArgs) throws Exception {
        Channel channel = channelFactory.getChannel(endorsers, client, channelName);
        ChaincodeID chanCodeId = ChaincodeID.newBuilder().setName(chainId).build();
        return queryBlockChain(client, channel, chanCodeId, invokeArgs);
    }

    private CompletableFuture<BlockEvent.TransactionEvent> invokeBlockChain(HFClient client, Channel channel, ChaincodeID chanCodeId, InvokeArgs invokeArgs) throws InvokeException {
        try {
            TransactionProposalRequest qpr = buildTransactionProposalRequest(client, chanCodeId, invokeArgs);
            Collection<ProposalResponse> responses = channel.sendTransactionProposal(qpr, channel.getPeers());
            List<String> errors = checkProposals(responses);
            if(errors.size() >= responses.size()) {
                throw new InvokeException(errors);
            }
            return channel.sendTransaction(responses);
        } catch (ProposalException e) {
            throw new InvokeException(e);
        } catch (InvalidArgumentException e) {
            throw new InvokeException(e);
        }
    }

    private TransactionProposalRequest buildTransactionProposalRequest(HFClient client, ChaincodeID chanCodeId, InvokeArgs invokeArgs) {
        TransactionProposalRequest qpr = client.newTransactionProposalRequest();
        qpr.setChaincodeID(chanCodeId);
        qpr.setFcn(invokeArgs.getFunction());
        qpr.setArgs(invokeArgs.getValues());
        return qpr;
    }

    private List<String> checkProposals(Collection<ProposalResponse> responses) throws InvokeException {
        List<String> errors = new ArrayList<>();
        for(ProposalResponse res : responses) {
            if(res.isInvalid()){
                errors.add(res.getMessage());
            }
        }
        return errors;
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
