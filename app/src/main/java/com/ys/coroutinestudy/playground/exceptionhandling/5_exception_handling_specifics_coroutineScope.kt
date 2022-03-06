package com.ys.coroutinestudy.playground.exceptionhandling

import com.ys.coroutinestudy.util.logWithThreadName
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {

	try {
		doSomeThingSuspend()
	} catch (e: Exception) {
		logWithThreadName {
			println("Caught $e")
		}
	}
}

private suspend fun doSomeThingSuspend() {
	coroutineScope {
		launch {
			throw RuntimeException()
		}
	}
}