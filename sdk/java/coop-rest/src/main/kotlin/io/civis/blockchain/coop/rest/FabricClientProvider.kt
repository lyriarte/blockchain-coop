package io.civis.blockchain.coop.rest

import io.civis.blockchain.coop.core.FabricUserClient
import io.civis.blockchain.coop.core.factory.FabricClientFactory
import io.civis.blockchain.coop.rest.config.CoopConfig
import org.hyperledger.fabric.sdk.HFClient
import org.hyperledger.fabric.sdk.User
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

@Component
@Scope("singleton")
class FabricClientProvider(val fabricUserClient: FabricUserClient, val coopConfig: CoopConfig, val clientFactoty: FabricClientFactory) {

    private lateinit var client: HFClient;

    fun get() : HFClient {
        if(!::client.isInitialized) {
            val user = fabricUserClient.enroll(coopConfig.userName, coopConfig.userPassword, coopConfig.userOrg)
            client = clientFactoty.getHfClient(user)
        }
        return client;
    }
}

