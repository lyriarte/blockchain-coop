package io.civis.blockchain.coop.core;

import io.civis.blockchain.coop.core.model.InvokeArgs;
import org.hyperledger.fabric.sdk.BlockEvent;
import org.hyperledger.fabric.sdk.User;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;

class FabricChainCodeClientTest {
    
    @Test
    void query() throws Exception {
        FabricUserClient client = FabricUserClient.fromConfigFile("client/config.json");
        User user = client.enroll("df82a3b46bda4183fb691fa9b57a39b8", "121a59e3882a7e7344333772a79df5cc", "bclan");

        FabricChainCodeClient chainCodeClient = FabricChainCodeClient.fromConfigFile("client/config.json");
        String value = chainCodeClient.query(user, "bclan", "sandbox", "ssm", InvokeArgs.from("admin", "adam"));

        assertThat(value).isEqualTo("{\"name\":\"adam\",\"pub\":\"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAr9S4z40tHAbiN0gfN0BO5JwtsjsSP/VteNLcse9X2onzTYCJ68kTEV643WQBj53V/qZECBQRx8zbizhkLlay6W51hqGCypRyqopx0hQwf4+vujVbSVsGAG1YuITVKdqmcVErFUbnVmd6FuTMcNH9n40AMe04UzhL+2BncZsgXV1H87rF+W64kBzNWwkXHwZsnnz/uH2hqVun7jXLr18R34s/zsYh/uP5Lei+3V2u2VMVuwMDlya8YMfVeMPfo41T9HnYU5LL26kqrLHw7/9pQ5PHb8IUXlCFlODYBTUmePu7d60fdJ5TQD9MvtposYyng1T2PvfzsbjKAYK2KKQi6wIDAQAB\"}");
    }

    @Test
    void invoke() throws Exception {
        FabricUserClient client = FabricUserClient.fromConfigFile("client/config.json");
        User user = client.enroll("df82a3b46bda4183fb691fa9b57a39b8", "121a59e3882a7e7344333772a79df5cc", "bclan");

        FabricChainCodeClient chainCodeClient = FabricChainCodeClient.fromConfigFile("client/config.json");
        CompletableFuture<BlockEvent.TransactionEvent> value = chainCodeClient.invoke(user, "bclan", "sandbox", "ssm", InvokeArgs.from("register",
                "{\"name\":\"sam\",\"pub\":\"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvZV/7N5lU9LAu6/2AGQ8jRUmWsfJDXRwL7RXj1LK4AidSK+l5TM9TbSRcDXDG7TNj6cKnwwjCbJMG61eYMIVul/1/VnfJBx1+rwglyxE57D5Rj/bR5POAYHx8COnKoDHXjBDg1qQ6wLZ/HySkY7UlEJ0AZsblmNInuV42JNHI+Y1pNnu3kRzH/rMtuwVZ0W6zrr5w2xwqbWI5nnNtc86gbwCX5Y11hya1RZQz275D6is0m3BoiL9Z7FdAor6vX6GmzMd7OuAnvG/raz3re1meTKW5jCP654mL2uq45boUWVbJX6UljmbbNZu/OYrM8b3T+FdGs5FiiLD7kStVbIq9QIDAQAB\"}",
                "adam",
                "ruvlrvL3RwsTDebsI2knoUbRNaZkOmTDU6VN1WLE7lghk+RbYdX96FDwNiHiRhOCb71e80EmIChyPz2Y2AGQvZJ+jNDaM4WMjvIrrQbNe9XhElBoqGydz413O8xZCPmUfmeFU+4655mDMiTTRnvXiR2WqjQHlaMyiUUndMS0ATs+AWvgURK/DK6zNblBwBmRk2R9fmoufigmBtMazNQUxMWdhokBIEPv2Zpxm5khoVbmi3ycDvBzuRb+VCADA1Cqmjrfpjwpo9wVEh0W46ldz4x1e2rXrjwO0h10OtRHtEbSM+ZKH/VfUOijLBiPiog2m8VzZ/VHcklkKztqUBFPMg=="
        ));
        BlockEvent.TransactionEvent transaction = value.get();
        assertThat(transaction.isValid()).isTrue();
    }
}