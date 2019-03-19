package io.civis.blockchain.coop.rest

import io.civis.blockchain.coop.core.FabricUserClient
import io.civis.blockchain.coop.rest.config.CoopConfig
import org.hyperledger.fabric.sdk.User
import org.springframework.stereotype.Component

@Component
class EnrolledUserProvider(val fabricUserClient: FabricUserClient, val coopConfig: CoopConfig) {

    private lateinit var user: User;

    fun get() : User {
        if(!::user.isInitialized) {
            user = fabricUserClient.enroll(coopConfig.userName, coopConfig.userPassword, coopConfig.userOrg)
        }
        return user;
    }
}

