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


    logger.warn { "Starting coroutineScope" }
    try {
        coroutineScope {
            val jobA = launch(start = CoroutineStart.LAZY, context = myDispatcher) {
                logger.info { "Started job A" }
                delay(1_500)
                logger.info { "Throwing" }
                throw Exception("error")
            }

            val jobB = launch(start = CoroutineStart.LAZY, context = myDispatcher) {
                logger.info { "Starting job B" }
                delay(3_000)
                logger.info { "Finished job B" }
            }

            jobA.start()
            jobB.start()
        }
    } catch (ex: Exception) {
        logger.error(ex) { "Error in coroutineScope" }
    }
    logger.warn { "Finished coroutineScope" }

    readln()

    logger.warn { "Starting supervisorScope" }
    supervisorScope {
        val jobA = launch(start = CoroutineStart.LAZY, context = myDispatcher) {
            logger.info { "Started job A" }
            delay(1_500)
            logger.info { "Throwing" }
            throw Exception("error")
        }

        val jobB = launch(start = CoroutineStart.LAZY, context = myDispatcher) {
            logger.info { "Starting job B" }
            delay(3_000)
            logger.info { "Finished job B" }
        }

        jobA.start()
        jobB.start()
    }
    logger.warn { "Finished supervisorScope" }

    readln()

    val coexh = CoroutineExceptionHandler { ctx, throwable ->
        logger.error(throwable) { "Got an unhandled exception" }
    }

    logger.warn { "Starting supervisorScope with CoroutineExceptionHandler" }
    supervisorScope {
        val jobA = launch(start = CoroutineStart.LAZY, context = myDispatcher + coexh) {
            logger.info { "Started job A" }
            delay(1_500)
            logger.info { "Throwing" }
            throw Exception("error")
        }

        val jobB = launch(start = CoroutineStart.LAZY, context = myDispatcher) {
            logger.info { "Starting job B" }
            delay(3_000)
            logger.info { "Finished job B" }
        }

        jobA.start()
        jobB.start()
    }
    logger.warn { "Finished supervisorScope with CoroutineExceptionHandler" }


    logger.info { "Bye World!" }
    myDispatcher.close()
}