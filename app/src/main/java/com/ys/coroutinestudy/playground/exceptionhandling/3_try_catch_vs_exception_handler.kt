package com.ys.coroutinestudy.playground.exceptionhandling

import com.ys.coroutinestudy.util.logWithThreadName
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun main() {

	val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
		logWithThreadName {
			println("Caught exception: $throwable coroutineContext: $coroutineContext")
		}
	}

	val scope = CoroutineScope(Job())

	scope.launch(exceptionHandler) {
		launch {
			println("Starting coroutine 1")
			delay(100)
			throw RuntimeException()
		}

		launch {
			println("Starting coroutine 2")
			delay(3000)
			println("Coroutine 2 completed")
		}
	}

	Thread.sleep(5000)
}