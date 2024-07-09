package pt.andremartins.workshop.io

import kotlinx.coroutines.*
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

suspend fun foo(): String {
    delay(2_000)
    return "Some data"
}

@OptIn(DelicateCoroutinesApi::class)
fun main() {
    // foo() // compilation error
    // suspend functions can only be called
    // from coroutines or other suspend functions

    // Non-blocking code is contagious unless we block

    // Using a scope we can launch or async
    // GlobalScope is global and shares Dispatchers.Default
    // It's a DelicateCoroutinesApi - "its use requires care"
    val globalJob = GlobalScope.launch {
        logger.info { "Global launch start" }
        delay(2_000)
        logger.info { "Global launch finish" }
    }

    logger.info { "Blocking main thread to wait for global job to finish" }
    runBlocking {
        globalJob.join()
        logger.info { "Global job finished" }
    }

    readln()

    // We can create our own using custom Dispatcher
    val scope = CoroutineScope(Dispatchers.Default)

    val job: Job = scope.launch {
        logger.info { "Launch and forget" }
        delay(2_000)
        logger.info { "Job finished" }
    }

    logger.info { "Blocking main thread to wait for our job to finish" }
    runBlocking {
        job.join()
        logger.info { "Our job finished" }
    }

    readln()

    val result: Deferred<String> = scope.async {
        logger.info { "Launch for result" }
        delay(1_000)
        logger.info { "Completing result" }
        foo()
    }

    logger.info { "Blocking main thread to wait for async to finish" }
    runBlocking {
        val str = result.await()
        logger.info { "Finished with $str" }
    }

    // runTest for tests
}