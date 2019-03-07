package io.civis.blockchain.coop.core;

import org.hyperledger.fabric.sdk.User;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class FabricUserClientTest {

    @Test
    void register() throws Exception {
        FabricUserClient client = FabricUserClient.fromConfigFile("client/config.json");
        String userName = "Adrien"+ UUID.randomUUID().toString();
        String val = client.register("df82a3b46bda4183fb691fa9b57a39b8", "121a59e3882a7e7344333772a79df5cc", "bclan", userName, "adrienpass");
        assertThat(val).isEqualTo("adrienpass");
    }

    @Test
    void enroll() throws Exception {
        FabricUserClient client = FabricUserClient.fromConfigFile("client/config.json");
        User user = client.enroll("df82a3b46bda4183fb691fa9b57a39b8", "121a59e3882a7e7344333772a79df5cc", "bclan");
        assertThat(user).isNotNull();
        assertThat(user.getName()).isEqualTo("df82a3b46bda4183fb691fa9b57a39b8");
    }
}