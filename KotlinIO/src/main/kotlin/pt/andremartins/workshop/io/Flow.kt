package pt.andremartins.workshop.io

import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import mu.KotlinLogging
import java.util.concurrent.Executors

private val logger = KotlinLogging.logger {}

/*

Cold Stream - meaning the data is produced only when there is a subscriber.
Each time a flow is collected, a new stream is started.

*/

suspend fun main() {
    val myDispatcher = Executors
        .newSingleThreadExecutor()
        .asCoroutineDispatcher()

    logger.info { "Hello World!" }

    coroutineScope {
        val flow = flow {
            for (i in 1..5) {
                delay(1000)
                emit(i)
            }
        }

        flow.collect {
            logger.info { "Collected $it" }
        }
    }

    logger.info { "Bye World!" }
    myDispatcher.close()
}