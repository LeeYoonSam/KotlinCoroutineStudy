package com.ys.coroutinestudy.playground.exceptionhandling

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

fun main() {

	val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
		println("Caught $throwable in CoroutineExceptionHandler")
	}

	val scope = CoroutineScope(Job())

	scope.launch(exceptionHandler) {
		launch {
			functionThatThrowsIt()
		}
	}

	Thread.sleep(100)
}