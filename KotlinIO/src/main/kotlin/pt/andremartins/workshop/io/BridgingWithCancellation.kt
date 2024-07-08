package pt.andremartins.workshop.io

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.suspendCancellableCoroutine
import mu.KotlinLogging
import java.util.*
import kotlin.concurrent.thread
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

private val logger = KotlinLogging.logger {}

data class Response(val data: String, val status: Int)

class MyAPI {
    fun registerCallback(callback: (Exception?, Response) -> Unit): UUID {
        thread {
            logger.debug { "Sleeping!" }
            Thread.sleep(3_000)
            callback(null, Response("Some data", 200))
        }
        return UUID.randomUUID()
    }

    fun unregisterCallback(uuid: UUID) {
        logger.info { "Unregistered" }
    }
}

suspend fun MyAPI.requestIt(): Response = suspendCancellableCoroutine { continuation ->
    val uuid = registerCallback { ex, response ->
        if (ex != null) {
            continuation.resumeWithException(ex)
        } else {
            continuation.resume(response)
        }
    }
    continuation.invokeOnCancellation {
        unregisterCallback(uuid)
    }
}

suspend fun main() {
    logger.info { "Hello World!" }

    coroutineScope {
        val myApi = MyAPI()

        val response = async { myApi.requestIt() }

        logger.info { "Requested $response" }

//        launch {
//            delay(2_000)
//            logger.info { "Waited too long need to cancel" }
//            response.cancel()
//        }

        logger.info { "Awaiting for response" }
        try {
            val data = response.await()
            logger.info { "Got data: $data" }
        } catch (ex: CancellationException) {
            logger.error { "Was cancelled" }
        }
    }

    logger.info { "Bye World!" }
}