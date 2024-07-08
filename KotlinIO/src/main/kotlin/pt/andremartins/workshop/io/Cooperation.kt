package pt.andremartins.workshop.io

import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mu.KotlinLogging
import java.util.concurrent.Executors

private val logger = KotlinLogging.logger {}

suspend fun main() {
    val myDispatcher = Executors
        .newSingleThreadExecutor()
        .asCoroutineDispatcher()

    logger.info { "Hello World" }
    coroutineScope {
        logger.info { "Launching A" }
        launch(myDispatcher) {
            while (true) {
                logger.info { "Sleeping A" }
//                Thread.sleep(1_000)
                delay(1_000)
            }
        }
        logger.info { "Launching B" }
        launch(myDispatcher) {
            while (true) {
                logger.info { "Sleeping B" }
                delay(500)
            }
        }
        logger.info { "Launches done" }
    }
    logger.info { "Bye World!" }
}
