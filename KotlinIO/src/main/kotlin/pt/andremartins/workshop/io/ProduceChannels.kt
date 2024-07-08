package pt.andremartins.workshop.io

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.produce
import mu.KotlinLogging
import java.util.concurrent.Executors
import kotlin.random.Random

private val logger = KotlinLogging.logger {}

@OptIn(ExperimentalCoroutinesApi::class)
suspend fun main() {
    val myDispatcher = Executors
        .newSingleThreadExecutor()
        .asCoroutineDispatcher()

    logger.info { "Hello World!" }

    coroutineScope {
        val channel = produce {
            for (i in 1..10) {
                delay(Random.nextLong(from = 500, until = 1500))
                logger.info { "Emitting $i" }
                send(i)
            }
        }

        launch {
            for (elm in channel) {
                logger.info { "Received $elm" }
            }
        }
    }

    logger.info { "Bye World!" }
    myDispatcher.close()
}