package io.civis.blockchain.coop.rest

import io.civis.blockchain.coop.core.FabricChainCodeClient
import io.civis.blockchain.coop.core.model.InvokeArgs
import io.civis.blockchain.coop.rest.config.CoopUserConfig
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/ssm",  produces = [MediaType.APPLICATION_JSON_VALUE])
class CoopController(val fabricClient: FabricChainCodeClient, val coopUserConfig: CoopUserConfig) {

    @PostMapping
    @ResponseBody
    fun command(@RequestBody params: InvokeParam): Mono<String> {
        val user = coopUserConfig.enrollConfiguredUser();
        val invokeArgs = InvokeArgs(params.function, params.args.iterator());
        val future = fabricClient.invoke(user, coopUserConfig.userOrg, params.channel, params.chainid, invokeArgs);
        return Mono.fromFuture(future).map{ it -> it.transactionID }
    }

    @GetMapping
    @ResponseBody
    fun query(channel: String, chainid: String, function: String, args: Array<String>): String {
        val user = coopUserConfig.enrollConfiguredUser();
        return fabricClient.query(user, coopUserConfig.userOrg, channel, chainid, InvokeArgs(function, args.iterator()));
    }

    data class InvokeParam(val channel: String, val chainid: String,
                           val function: String, val args: Array<String>);

}