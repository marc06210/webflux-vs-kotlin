package com.afklm.teccse.tutorial.reactive

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.*

@RestController
class DemoController {
    private val webClient = WebClient.builder().baseUrl("http://localhost:8082?delay=2").build()

    @GetMapping("/backend/people")
    suspend fun getPersons(): Flow<Person> =
        webClient.get().uri("/person").retrieve().bodyToFlow<Long>()
            .map { getPerson(it) }

    suspend fun getPerson(id :Long): Person = coroutineScope {
        val person: Deferred<Person> = this@coroutineScope.async {
            webClient.get().uri("/person/{id}", id).retrieve().awaitBody<Person>()
        }
        val domain: Deferred<String> = this@coroutineScope.async {
            webClient.get().uri("/person/{id}/domain", id).retrieve().awaitBody<String>()
        }
        Person(person.await().id, person.await().name, domain.await())
    }
}

data class Person(val id: Long, val name: String, val domain: String?)