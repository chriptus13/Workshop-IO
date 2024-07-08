package pt.andremartins.workshop.io

import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeout
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

suspend fun main() {
    logger.info { "Hello World!" }
    try {
        withTimeout(2_000) {
            repeat(10) {
                logger.info { "Sleeping $it" }
                delay(300)
            }
        }
    } catch (ex: TimeoutCancellationException) {
        logger.error { "Timed out!" }
    }
    logger.info { "Bye World!" }
}