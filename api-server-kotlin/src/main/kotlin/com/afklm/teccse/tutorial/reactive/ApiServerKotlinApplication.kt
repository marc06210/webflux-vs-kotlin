package com.afklm.teccse.tutorial.reactive

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ApiServerKotlinApplication

fun main(args: Array<String>) {
	runApplication<ApiServerKotlinApplication>(*args)
}
