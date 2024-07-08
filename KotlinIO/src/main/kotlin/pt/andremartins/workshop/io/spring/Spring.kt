package pt.andremartins.workshop.io.spring

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import mu.KotlinLogging
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.ResponseEntity
import org.springframework.r2dbc.core.*
import org.springframework.web.bind.annotation.*
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import java.net.URI
import java.time.LocalDate

@RestController
class HelloController {
    private val logger = KotlinLogging.logger {}

    @GetMapping("/hello")
    suspend fun delayedHelloWorld(
        @RequestParam timeout: Long = 1_000,
    ): String {
        logger.info { "Sleeping for $timeout" }
        delay(timeout)
        logger.info { "Responding" }
        return "Hello World!"
    }
}

@RestController
class HttpBinController(
    private val webClient: WebClient,
) {
    private val logger = KotlinLogging.logger {}

    @GetMapping("/ip")
    suspend fun httpBinMirror(): ResponseEntity<String> {
        val response = webClient.get()
            .uri("https://httpbin.org/get")
            .retrieve()
            .awaitBody<HttpBinResponse>()

        logger.info { "Response: $response" }

        return ResponseEntity.ok(response.origin)
    }
}

@RestController
class DbController(
    private val dbClient: DatabaseClient,
) {
    private val logger = KotlinLogging.logger {}

    @GetMapping("/users/{id}")
    suspend fun getUser(@PathVariable id: Int): User =
        dbClient.sql("SELECT * FROM users WHERE id = :id")
            .bind("id", id)
            .map<User>()
            .awaitOne()
            .also {
                logger.info { "Got user: $it" }
            }

    @FlowPreview
    @GetMapping("/users")
    suspend fun getUsers(): Flow<User> =
        dbClient.sql("SELECT * FROM users")
            .map<User>()
            .flow()

    @PostMapping("/users")
    suspend fun createUser(@RequestBody user: UserDto): ResponseEntity<Unit> {
        val id = dbClient.sql("INSERT INTO users (name, lastName, birthday) VALUES (:name, :lastName, :birthday)")
            .bindProperties(user)
            // https://docs.spring.io/spring-framework/reference/data-access/r2dbc.html#r2dbc-auto-generated-keys
            .filter { st -> st.returnGeneratedValues("id") }
            .map { row -> row["id"] as Int }
            .awaitSingle()

        logger.info { "Created user: $id" }

        return ResponseEntity.created(URI("http://localhost:8080/users/$id"))
            .build()
    }
}

@Configuration
class Configuration {
    @Bean
    fun webClient() = WebClient.create()
}

data class HttpBinResponse(
    val origin: String,
    val url: String,
    val headers: Map<String, String>,
    val args: Map<String, String>,
)

data class User(
    val id: Int,
    val name: String,
    val lastName: String,
    val birthday: LocalDate,
)

data class UserDto(
    val name: String,
    val lastName: String,
    val birthday: LocalDate,
)

@SpringBootApplication
class Spring

fun main(args: Array<String>) {
    runApplication<Spring>(*args)
}

inline fun <reified T> DatabaseClient.GenericExecuteSpec.map(): RowsFetchSpec<T> = mapProperties(T::class.java)