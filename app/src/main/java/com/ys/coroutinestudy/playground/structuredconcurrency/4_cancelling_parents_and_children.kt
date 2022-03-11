package com.ys.coroutinestudy.playground.structuredconcurrency

import com.ys.coroutinestudy.util.log
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {

	val scope = CoroutineScope(Dispatchers.Default)

	scope.coroutineContext[Job]!!.invokeOnCompletion { throwable ->
		if (throwable is CancellationException) {
			log("Parent job was cancelled")
		}
	}

	val childCoroutine1Job = scope.launch {
		delay(1000)
		log("Coroutine 1 completed")
	}
	childCoroutine1Job.invokeOnCompletion { throwable ->
		if (throwable is CancellationException) {
			log("Coroutine 1 was cancelled!")
		}
	}

	scope.launch {
		delay(1000)
		log("Coroutine 2 completed")
	}.invokeOnCompletion { throwable ->
		if (throwable is CancellationException) {
			log("Coroutine 2 was cancelled!")
		}
	}

	childCoroutine1Job.cancelAndJoin()
	// scope.coroutineContext[Job]!!.cancelAndJoin()

	delay(2000)
}