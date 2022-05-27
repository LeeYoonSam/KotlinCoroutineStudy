package com.ys.coroutinestudy.coroutine_sample.delay

import java.util.concurrent.*
import kotlin.coroutines.*

private val executor = Executors.newSingleThreadScheduledExecutor {
	Thread(it, "scheduler").apply { isDaemon = true }
}

suspend fun delay(time: Long, unit: TimeUnit = TimeUnit.MICROSECONDS): Unit = suspendCoroutine { continuation ->
	executor.schedule({ continuation.resume(Unit) }, time, unit)
}