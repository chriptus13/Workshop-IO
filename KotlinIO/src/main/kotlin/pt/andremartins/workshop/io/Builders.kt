package pt.andremartins.workshop.io

import kotlinx.coroutines.*
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

fun main() {
    val scope = CoroutineScope(Dispatchers.Default)

    val job: Job = scope.launch {
        logger.info { "Launch and forget" }
        delay(2_000)
        logger.info { "Job finished" }
    }

    val result: Deferred<String> = scope.async {
        logger.info { "Launch for result" }
        delay(1_000)
        logger.info { "Completing result" }
        "Some result"
    }

    logger.info { "Blocking main thread to wait for coroutines to finish" }
    runBlocking {
        job.join()
        logger.info { "Job finished" }

        val str = result.await()
        logger.info { "Finished with $str" }
    }

    // runTest for tests
}