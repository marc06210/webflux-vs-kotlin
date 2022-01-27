package com.afklm.teccse.tutorial.reactive

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ReactiveTestAppKotlinApplication

fun main(args: Array<String>) {
    runApplication<ReactiveTestAppKotlinApplication>(*args)
}
