package pt.andremartins.workshop.io

import kotlinx.coroutines.*
import mu.KotlinLogging
import java.util.concurrent.Executors

private val logger = KotlinLogging.logger {}

suspend fun main() {
    val myDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

    logger.info { "Hello World!" }
    coroutineScope {
        logger.info { "Starting coroutineScope" }
        withContext(Dispatchers.Unconfined) {
            logger.info { "Unconfined" }
            delay(1_000)
            logger.info { "Unconfined still?" }
        }

        withContext(myDispatcher) {
            logger.info { "My dispatcher" }
            delay(1_000)
            logger.info { "My dispatcher still?" }
        }

        withContext(Dispatchers.IO) {
            logger.info { "IO" }
            delay(1_000)
            logger.info { "IO still?" }
        }

        withContext(Dispatchers.Default) {
            logger.info { "Default" }
            delay(1_000)
            logger.info { "Default still?" }
        }
        logger.info { "Finishing coroutineScope" }
    }
    logger.info { "Bye World!" }
    myDispatcher.close()
}