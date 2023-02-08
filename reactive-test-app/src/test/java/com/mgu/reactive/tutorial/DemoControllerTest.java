package com.mgu.reactive.tutorial;

import mockwebserver3.MockResponse;
import mockwebserver3.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DemoControllerTest {

    private static MockWebServer mockApiServer;

    @Autowired
    private WebTestClient webTestClient;

    @BeforeAll
    public static void startMockServer() throws IOException {
        mockApiServer = new MockWebServer();
        mockApiServer.start(8082);
        System.out.println("server started");
    }

    @AfterAll
    public static void stopServer() throws IOException {
        mockApiServer.close();
        mockApiServer = null;
    }

    @Test
    @DisplayName("Test getting people ids")
    public void testGettingPeopleIds() {
        mockApiServer.enqueue(new MockResponse().setBody("[1,2,3]").setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).setResponseCode(200));
        webTestClient.get().uri("/people/ids")
                .exchange()
                .expectStatus().isOk()
                .expectBody().json("[1,3,2]");
    }

    @Test
    @DisplayName("Test getting everything")
    public void testGettingEverything() {
        mockApiServer.enqueue(new MockResponse().setBody("[1,2,3]").setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).setResponseCode(200));
        mockApiServer.enqueue(new MockResponse().setBody("fieldA").setResponseCode(200));
        mockApiServer.enqueue(new MockResponse().setBody("fieldB").setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).setResponseCode(200));
        mockApiServer.enqueue(new MockResponse().setBody("fieldC").setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).setResponseCode(200));
        webTestClient.get().uri("/people/reactive")
                .exchange()
                .expectStatus().isOk()
                .expectBody().json("[{\"id\":1,\"name\":\"Marc\",\"domain\":\"fieldA\"},{\"id\":2,\"name\":\"Eric\",\"domain\":\"fieldB\"},{\"id\":3,\"name\":\"Amélie\",\"domain\":\"fieldC\"}]");
        // the json test works because reactor decided to manage everything in a single thread, otherwise the domains
        // could have been randomly allocated, this is the limit of such tests
    }
}