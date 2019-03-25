package io.civis.blockchain.coop.core;

import org.hyperledger.fabric.sdk.User;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static io.civis.blockchain.coop.core.FabricChainCodeClientTest.*;
import static org.assertj.core.api.Assertions.assertThat;

class FabricUserClientTest {

    @Test
    void register() throws Exception {
        FabricUserClient client = FabricUserClient.fromConfigFile(CLIENT_CONFIG, CRYPTO_CONFIG);
        String userName = "Adrien"+ UUID.randomUUID().toString();
        String val = client.register(USER_NAME, USER_PASSWORD, "bclan", userName, "adrienpass");
        assertThat(val).isEqualTo("adrienpass");
    }

    @Test
    void enroll() throws Exception {
        FabricUserClient client = FabricUserClient.fromConfigFile(CLIENT_CONFIG, CRYPTO_CONFIG);
        User user = client.enroll(USER_NAME, USER_PASSWORD, "bclan");
        assertThat(user).isNotNull();
        assertThat(user.getName()).isEqualTo(USER_NAME);
    }
}