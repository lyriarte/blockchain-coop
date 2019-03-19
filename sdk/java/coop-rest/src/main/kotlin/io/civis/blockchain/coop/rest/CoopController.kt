package io.civis.blockchain.coop.rest

import io.civis.blockchain.coop.core.FabricChainCodeClient
import io.civis.blockchain.coop.core.model.InvokeArgs
import io.civis.blockchain.coop.rest.config.CoopConfig
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/",  produces = [MediaType.APPLICATION_JSON_VALUE])
class CoopController(val fabricClient: FabricChainCodeClient, val coopConfig: CoopConfig, val enrolledUserProvider: EnrolledUserProvider) {

    @GetMapping
    fun query(cmd: String, fcn: String, args: Array<String>): Mono<String> {
        val user = enrolledUserProvider.get()
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
        val user = enrolledUserProvider.get()
        val invokeArgs = InvokeArgs(params.fcn, params.args.iterator());
        val future = fabricClient.invoke(coopConfig.getEndorsers(), user, coopConfig.channel, coopConfig.chaincodeId, invokeArgs);
        return Mono.fromFuture(future).map{
            it -> it.transactionID
        }
    }

    @GetMapping("v2")
    fun query(fcn: String, args: Array<String>): String {
        val user = enrolledUserProvider.get()
        return fabricClient.query(coopConfig.getEndorsers(), user, coopConfig.channel, coopConfig.chaincodeId, InvokeArgs(fcn, args.iterator()));
    }

    data class InvokeParam(val fcn: String, val args: Array<String>);

}