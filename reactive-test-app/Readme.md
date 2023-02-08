# Getting Started
Sample application to show how easy it is to use the Spring WebClient approach to make HTTP calls.

## Tests

The UserControllerTest shows how to create test based on @WebMvcTest.

The DemoControllerTest shows how to use MockWebServer in order to mock a server, in conjonction with SpringBootTest.
Anyway, it has limitations regarding asynchronous network calls but as long as the data remains small then we
should not face any synchronization issues.