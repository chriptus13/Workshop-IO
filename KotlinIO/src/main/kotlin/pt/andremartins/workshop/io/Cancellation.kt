package pt.andremartins.workshop.io

import kotlinx.coroutines.*
import mu.KotlinLogging
import java.util.concurrent.Executors

private val logger = KotlinLogging.logger {}

suspend fun main() {
    val myDispatcher = Executors
        .newSingleThreadExecutor()
        .asCoroutineDispatcher()

    logger.info { "Hello World!" }


    logger.warn { "Cancelling a launch example" }
    coroutineScope {
        val job = launch {
            logger.info { "Starting job" }
            try {
                delay(3_000)
            } catch (ex: CancellationException) {
                logger.error { "Job cancelled" }
            }
            logger.info { "Coroutine active: $isActive" }
            try {
                delay(5_000)
            } catch (ex: CancellationException) {
                logger.error { "Man! Job is cancelled!" }
            }
        }

        delay(1_000)
        logger.info { "Cancelling job" }
        job.cancel()
    }

    readln()

    logger.warn { "Cancelling an async example" }
    coroutineScope {
        val result = async {
            logger.info { "Starting async" }
            try {
                delay(3_000)
            } catch (ex: CancellationException) {
                logger.error { "Async cancelled" }
            }
            logger.info { "Responding with data" }
            "Some data"
        }

        launch {
            delay(1_500)
            logger.info { "Taking too long, cancelling async!" }
            result.cancel()
        }

        try {
            logger.info { "Waiting for data" }
            val data = result.await()
            logger.info { "Got data: $data" }
        } catch (ex: CancellationException) {
            logger.error { "Await cancelled" }
        }
    }


    logger.info { "Bye World!" }
    myDispatcher.close()
}