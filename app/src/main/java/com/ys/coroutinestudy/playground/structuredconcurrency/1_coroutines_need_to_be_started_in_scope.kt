package com.ys.coroutinestudy.playground.structuredconcurrency

import com.ys.coroutinestudy.util.logWithThreadName
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

val scope = CoroutineScope(Dispatchers.Default)

fun main() = runBlocking {
	val job = scope.launch {
		delay(100)
		println("Coroutine completed")
	}

	job.invokeOnCompletion { throwable ->
		println(throwable)
		if (throwable is CancellationException) {
			logWithThreadName {
				println("Coroutine was cancelled")
			}
		}
	}

	delay(50)
	onDestroy()

	delay(1000)
}

fun onDestroy() {
	logWithThreadName {
		println("life-time of scope ends")
	}
	scope.cancel()
}