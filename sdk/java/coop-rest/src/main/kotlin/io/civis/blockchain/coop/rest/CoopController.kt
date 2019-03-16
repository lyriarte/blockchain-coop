package io.civis.blockchain.coop.rest

import io.civis.blockchain.coop.core.FabricChainCodeClient
import io.civis.blockchain.coop.core.FabricUserClient
import io.civis.blockchain.coop.core.model.InvokeArgs
import io.civis.blockchain.coop.rest.config.CoopConfig
import org.hyperledger.fabric.sdk.User
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/",  produces = [MediaType.APPLICATION_JSON_VALUE])
class CoopController(val fabricClient: FabricChainCodeClient, val fabricUserClient: FabricUserClient, val coopConfig: CoopConfig) {

    @GetMapping
    fun query(cmd: String, fcn: String, args: Array<String>): Mono<String> {
        val user = enrollConfiguredUser();
        if("invoke".equals(cmd)) {
            val future =  fabricClient.invoke(coopConfig.getEndorsers(), user, coopConfig.channel, coopConfig.chaincodeId, InvokeArgs(fcn, args.iterator()))
            return Mono.fromFuture(future).map{ it -> it.transactionID }
        } else {
            val value = fabricClient.query(coopConfig.getEndorsers(), user, coopConfig.channel, coopConfig.chaincodeId, InvokeArgs(fcn, args.iterator()));
            return Mono.just(value);
        }
    }


    @PostMapping("v2")
    fun command(@RequestBody params: InvokeParam): Mono<String> {
        val user = enrollConfiguredUser();
        val invokeArgs = InvokeArgs(params.fcn, params.args.iterator());
        val future = fabricClient.invoke(coopConfig.getEndorsers(), user, coopConfig.channel, coopConfig.chaincodeId, invokeArgs);
        return Mono.fromFuture(future).map{
            it -> it.transactionID
        }
    }

    @GetMapping("v2")
    fun query(fcn: String, args: Array<String>): String {
        val user = enrollConfiguredUser();
        return fabricClient.query(coopConfig.getEndorsers(), user, coopConfig.channel, coopConfig.chaincodeId, InvokeArgs(fcn, args.iterator()));
    }

    fun enrollConfiguredUser(): User = fabricUserClient.enroll(coopConfig.userName, coopConfig.userPassword, coopConfig.userOrg)

    data class InvokeParam(val fcn: String, val args: Array<String>);

}