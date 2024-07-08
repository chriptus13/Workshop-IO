package pt.andremartins.workshop.io

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

suspend fun main() {
    coroutineScope {
        val a = async {
            logger.info { "Starting A" }
            delay(1_000)
            logger.info { "End A" }
            "A"
        }
        val b = async {
            logger.info { "Starting B" }
            delay(500)
            logger.info { "End B" }
            "B"
        }

        logger.info { "Waiting for A." }
        val aa = a.await()
        logger.info { "GOT: $aa" }
        logger.info { "Waiting for B." }
        val bb = b.await()
        logger.info { "GOT: $bb" }
    }
}