package com.ys.coroutinestudy.playground.exceptionhandling

import com.ys.coroutinestudy.util.logWithThreadName
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

suspend fun main() {

	val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
		logWithThreadName {
			println("Caught $throwable in CoroutineExceptionHandler")
		}
	}

	val scope = CoroutineScope(Job() + exceptionHandler)

	scope.launch {
		val deferred = async {
			delay(200)
			throw RuntimeException()
		}

	}

	Thread.sleep(1000)
}