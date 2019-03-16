package io.civis.blockchain.coop.rest.config

import com.google.common.base.Splitter
import io.civis.blockchain.coop.core.FabricUserClient
import io.civis.blockchain.coop.core.model.Endorser
import org.hyperledger.fabric.sdk.User
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class CoopConfig {

    @Value("\${coop.endorsers}")
    lateinit var endorsers: String

    @Value("\${coop.channel}")
    lateinit var channel: String

    @Value("\${coop.ccid}")
    lateinit var chaincodeId: String

    @Value("\${coop.user.name}")
    lateinit var userName: String

    @Value("\${coop.user.password}")
    lateinit var userPassword: String

    @Value("\${coop.user.org}")
    lateinit var userOrg: String

    @Value("\${coop.config.file}")
    lateinit var configFile: String

    @Value("\${coop.config.crypto}")
    lateinit var configCryptoBase: String

    fun getEndorsers(): List<Endorser> {
        return Splitter.on(",").split(endorsers).map {
            Endorser.fromStringPair(it)
        }
    }

}