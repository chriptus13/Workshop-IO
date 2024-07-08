package pt.andremartins.workshop.io

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.await
import kotlinx.coroutines.future.future
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.mono
import reactor.core.publisher.Mono
import java.util.concurrent.CompletableFuture
import kotlin.concurrent.thread
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.time.Duration
import kotlin.time.toJavaDuration

/* Callbacks */

fun <T> fetchWithCallback(value: T, delay: Duration = Duration.ZERO, callback: (T) -> Unit) {
    thread {
        Thread.sleep(delay.toJavaDuration())
        callback(value)
    }
}

suspend fun <T> callbackToSuspend(value: T, delay: Duration = Duration.ZERO): T =
    suspendCoroutine { cont ->
        fetchWithCallback(value, delay) {
            cont.resume(it)
        }
    }

/* Futures */

fun <T> fetchWithCompletableFuture(value: T, delay: Duration = Duration.ZERO): CompletableFuture<T> =
    CompletableFuture.supplyAsync {
        Thread.sleep(delay.toJavaDuration())
        value
    }

// from kotlinx-coroutines-jvm
suspend fun <T> completableFutureToSuspend(value: T, delay: Duration = Duration.ZERO): T =
    fetchWithCompletableFuture(value, delay).await()

// from kotlinx-coroutines-jdk8
fun <T> suspendToCompletableFuture(value: T, delay: Duration = Duration.ZERO): CompletableFuture<T> =
    CoroutineScope(Dispatchers.Default)
        .future { completableFutureToSuspend(value, delay) }

/* REACTIVE */

fun <T> fetchWithMono(value: T, delay: Duration = Duration.ZERO): Mono<T> = Mono.create {
    thread {
        Thread.sleep(delay.toJavaDuration())
        it.success(value)
    }
}

// from kotlinx-coroutines-reactor
suspend fun <T> monoToSuspend(value: T, delay: Duration = Duration.ZERO) = fetchWithMono(value, delay).awaitSingle()

// from kotlinx-coroutines-reactor
fun <T> suspendToMono(value: T, delay: Duration = Duration.ZERO): Mono<T> = mono { monoToSuspend(value, delay) }