package com.mgu.reactive.tutorial

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ReactiveTestAppKotlinApplication

fun main(args: Array<String>) {
    runApplication<ReactiveTestAppKotlinApplication>(*args)
}
