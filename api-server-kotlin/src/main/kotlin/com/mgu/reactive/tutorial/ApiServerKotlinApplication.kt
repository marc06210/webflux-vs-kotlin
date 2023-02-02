package com.mgu.reactive.tutorial

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ApiServerKotlinApplication

fun main(args: Array<String>) {
	runApplication<ApiServerKotlinApplication>(*args)
}
