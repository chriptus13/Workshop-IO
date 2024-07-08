package pt.andremartins.workshop.io

import kotlinx.coroutines.*
import mu.KotlinLogging
import java.util.concurrent.Executors

private val logger = KotlinLogging.logger {}

suspend fun main() {
    val myDispatcher = Executors
        .newSingleThreadExecutor()
        .asCoroutineDispatcher()

    Dispatchers.Unconfined

    logger.info { "Hello World" }
    coroutineScope {
        val jobA = launch(start = CoroutineStart.LAZY, context = myDispatcher) {
            logger.info { "Hey my name is A, what's your name?" }
            yield()
            logger.info { "Nice to meet you B, how are you doing today?" }
            yield()
            logger.info { "Same! Thanks!" }
        }

        val jobB = launch(start = CoroutineStart.LAZY, context = myDispatcher) {
            logger.info { "Hello A, my name is B!" }
            yield()
            logger.info { "Fine! What about you?" }
            yield()
            logger.info { "Awesome!" }
        }
        jobA.start()
        jobB.start()
    }
    logger.info { "Bye World!" }
    myDispatcher.close()
}
