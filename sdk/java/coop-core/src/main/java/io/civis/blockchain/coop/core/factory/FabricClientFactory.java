package io.civis.blockchain.coop.core.factory;

import io.civis.blockchain.coop.core.config.FabricConfig;
import io.civis.blockchain.coop.core.config.OrganisationConfig;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric_ca.sdk.HFCAClient;

public class FabricClientFactory {

    private static FabricConfig fabricConfig;

    public FabricClientFactory(FabricConfig fabricConfig) {
        this.fabricConfig = fabricConfig;
    }

    public static FabricClientFactory factory(FabricConfig fabricConfig) {
        return new FabricClientFactory(fabricConfig);
    }

    public HFClient getHfClient() throws Exception {
        CryptoSuite cryptoSuite = CryptoSuite.Factory.getCryptoSuite();
        HFClient client = HFClient.createNewInstance();
        client.setCryptoSuite(cryptoSuite);
        return client;
    }

    public HFCAClient getHfCaClient(String orgName) throws Exception {
        OrganisationConfig config = fabricConfig.getNetwork().getOrganisation(orgName);
        CryptoSuite cryptoSuite = CryptoSuite.Factory.getCryptoSuite();
        HFCAClient caClient = HFCAClient.createNewInstance(config.getCa().getUrl(), config.getCa().getPeerTlsProperties());
        caClient.setCryptoSuite(cryptoSuite);
        return caClient;
    }

    public HFClient getHfClient(User admin) throws Exception {
        HFClient client = getHfClient();
        client.setUserContext(admin);
        return client;
    }

}
