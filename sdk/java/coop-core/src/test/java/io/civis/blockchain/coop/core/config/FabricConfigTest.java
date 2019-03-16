package io.civis.blockchain.coop.core.config;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;


public class FabricConfigTest {

    @Test
    public void testLoadFromFile() throws IOException {
        FabricConfig config = FabricConfig.loadFromFile("configuration/config.json");
        assertThat(config.getNetwork()).isNotNull();
        assertThat(config.getNetwork().getOrderer().getServerHostname()).isEqualTo("orderer0");
        assertThat(config.getNetwork().getOrderer().getUrl()).isEqualTo("grpcs://orderer0.or-bc0.thingagora.org:7050");
        assertThat(config.getNetwork().getOrderer().getTlsCacerts()).isEqualTo("crypto-config/ordererOrganizations/or-bc0.thingagora.org/orderers/orderer0.or-bc0.thingagora.org/msp/tlscacerts/tlsca.or-bc0.thingagora.org-cert.pem");

        assertThat(config.getNetwork().getOrganisations()).containsKeys("bc0");
        assertThat(config.getNetwork().getOrganisation("bc0").getMspid()).isEqualTo("ThingagoraBC0PeerMSP");
        assertThat(config.getNetwork().getOrganisation("bc0").getName()).isEqualTo("ThingagoraBC0Peer");

    }

}
