package com.mgu.reactive.tutorial

import kotlinx.coroutines.delay
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class PersonController {
    val people = mapOf(
            Integer.valueOf(1) to Person(1,"Marc"),
            Integer.valueOf(2) to Person(2,"Eric"),
            Integer.valueOf(3) to Person(3,"Amélie")
    )

    val domains = mapOf(
            Integer.valueOf(1) to "Darts",
            Integer.valueOf(2) to "Spring",
            Integer.valueOf(3) to "Kube"
    )

    @GetMapping("/person")
    fun getPersonIds() = people.keys

    @GetMapping("/person/{id}")
    suspend fun getPerson(@PathVariable id :Integer, @RequestParam(name="delay", required = false) delay: String): Person? {
        if(delay!=null) {
            delay(delay.toLong() * 1000)
        }
        return people.get(id.toInt())
    }

    @GetMapping("/person/{id}/domain")
    suspend fun getPersonDomain(@PathVariable id :Integer, @RequestParam(name="delay", required = false) delay: String): String? {
        if(delay!=null) {
            delay(delay.toLong() * 2000)
        }
        return domains.get(id.toInt())
    }
}

data class Person(val id: Int, val name: String)