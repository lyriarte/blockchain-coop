package io.civis.blockchain.coop.rest

import io.civis.blockchain.coop.core.FabricChainCodeClient
import io.civis.blockchain.coop.core.model.InvokeArgs
import io.civis.blockchain.coop.rest.config.CoopConfig
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
class CoopController(val fabricClient: FabricChainCodeClient, val coopConfig: CoopConfig, val fabricClientProvider: FabricClientProvider) {

    @GetMapping("/", params = ["cmd=invoke"])
    fun invoke(fcn: String, args: Array<String>): Mono<InvokeReturn> = invoke(InvokeArgs(fcn, args.iterator()))

    @GetMapping("/", params = ["cmd=query"])
    fun queryGet(fcn: String, args: Array<String>): String = query(fcn, args)

    @PostMapping("/v2")
    fun commandInvoke(@RequestBody params: InvokeParam): Mono<InvokeReturn> = invoke(InvokeArgs(params.fcn, params.args.iterator()))

    @GetMapping("/v2")
    fun commandQuery(fcn: String, args: Array<String>): String = query(fcn, args)


    private fun query(fcn: String, args: Array<String>): String {
        val client = fabricClientProvider.get()
        return fabricClient.query(coopConfig.getEndorsers(), client, coopConfig.channel, coopConfig.chaincodeId, InvokeArgs(fcn, args.iterator()));
    }

    private fun invoke(invokeArgs: InvokeArgs): Mono<InvokeReturn> {
        val client = fabricClientProvider.get()
        val future = fabricClient.invoke(coopConfig.getEndorsers(), client, coopConfig.channel, coopConfig.chaincodeId, invokeArgs);
        return Mono.fromFuture(future).map {
            InvokeReturn("SUCCESS", "", it.transactionID)
        }
    }

    data class InvokeParam(val fcn: String, val args: Array<String>);
    data class InvokeReturn(val status: String, val info: String, val transactionId: String);

}