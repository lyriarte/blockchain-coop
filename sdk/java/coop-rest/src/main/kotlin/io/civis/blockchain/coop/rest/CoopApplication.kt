package io.civis.blockchain.coop.rest

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackageClasses = [SsmApplication::class] )
class SsmApplication

fun main(args: Array<String>) {
	runApplication<SsmApplication>(*args)
}