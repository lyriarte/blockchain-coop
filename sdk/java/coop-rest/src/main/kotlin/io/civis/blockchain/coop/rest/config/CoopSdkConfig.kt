package io.civis.blockchain.coop.rest.config

import io.civis.blockchain.coop.core.FabricChainCodeClient
import io.civis.blockchain.coop.core.FabricUserClient
import io.civis.blockchain.coop.core.config.FabricConfig
import io.civis.blockchain.coop.core.factory.FabricChannelFactory
import io.civis.blockchain.coop.core.factory.FabricClientFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CoopSdkConfig {

    @Bean
    fun getFabricConfig(): FabricConfig = FabricConfig.loadFromFile("config.json")

    @Bean
    fun getFabricClientFactory(fabricConfig: FabricConfig): FabricClientFactory
            = FabricClientFactory.factory(fabricConfig)

    @Bean
    fun getFabricChannelFactory(fabricConfig: FabricConfig): FabricChannelFactory
            = FabricChannelFactory.factory(fabricConfig)

    @Bean
    fun getFabricChainCodeClient(fabricClientFactory: FabricClientFactory, fabricChannelFactory: FabricChannelFactory): FabricChainCodeClient
            = FabricChainCodeClient(fabricClientFactory, fabricChannelFactory)

    @Bean
    fun getFabricUserClient(fabricConfig: FabricConfig, fabricClientFactory: FabricClientFactory): FabricUserClient
            = FabricUserClient(fabricConfig, fabricClientFactory)


}