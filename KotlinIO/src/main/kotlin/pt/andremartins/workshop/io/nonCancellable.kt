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

    coroutineScope {
        val jobA = launch(start = CoroutineStart.LAZY, context = myDispatcher) {
            withContext(NonCancellable) {
                logger.info { "Starting job A" }
                delay(3_000)
                logger.info { "Finished job A" }
            }
        }

        val jobB = launch(start = CoroutineStart.LAZY, context = myDispatcher) {
            logger.info { "Starting job B" }
            try {
                delay(3_000)
            } catch (ex: CancellationException) {
                logger.error(ex) { "Job B was cancelled" }
            }
            logger.info { "Finished job B" }
        }

        jobA.start()
        jobB.start()
        delay(1_000)
        logger.info { "Cancelling job A" }
        jobA.cancel()
    }

    logger.info { "Bye World!" }
    myDispatcher.close()
}