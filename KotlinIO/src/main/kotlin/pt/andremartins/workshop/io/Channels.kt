package pt.andremartins.workshop.io

import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mu.KotlinLogging
import java.util.concurrent.Executors
import kotlin.random.Random

private val logger = KotlinLogging.logger {}

/*

Hot Stream - This means that the data is produced regardless of
whether there are any subscribers to receive it. If no one is receiving
the data, it gets buffered or lost depending on the channel configuration.

*/

suspend fun main() {
    val myDispatcher = Executors
        .newSingleThreadExecutor()
        .asCoroutineDispatcher()

    logger.info { "Hello World!" }

    coroutineScope {
        val channel = Channel<Int>()

        launch {
            for (i in 1..10) {
                delay(Random.nextLong(from = 500, until = 1500))
                logger.info { "Emitting $i" }
                channel.send(i)
            }
            channel.close()
        }

        launch {
            for (elm in channel) {
                logger.info { "Received $elm" }
            }
        }
    }

    logger.info { "Bye World!" }
    myDispatcher.close()
}