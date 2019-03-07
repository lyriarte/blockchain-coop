package io.civis.blockchain.coop.rest.config

import io.civis.blockchain.coop.core.FabricUserClient
import org.hyperledger.fabric.sdk.User
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class CoopUserConfig(val fabricUserClient: FabricUserClient) {

    @Value("\${coop.user.name}")
   lateinit var userName: String

    @Value("\${coop.user.password}")
    lateinit var userPassword: String

    @Value("\${coop.user.org}")
    lateinit var userOrg: String

    fun enrollConfiguredUser(): User
            = fabricUserClient.enroll(userName, userPassword,userOrg)


}