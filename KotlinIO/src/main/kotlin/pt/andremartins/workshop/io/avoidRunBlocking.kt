package pt.andremartins.workshop.io

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

fun main() {
    logger.info { "Hello World!" }
    runBlocking {
        launch {
            logger.info { "Starting A" }
            delay(2_000)
            logger.info { "Finished A" }
        }

        launch {
            logger.info { "Starting B" }
            delay(1_000)
            logger.info { "Finished B" }
        }
    }
    logger.info { "Bye World!" }
}