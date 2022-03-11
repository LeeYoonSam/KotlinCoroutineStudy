package com.ys.coroutinestudy.playground.structuredconcurrency

import com.ys.coroutinestudy.util.log
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {

	log("Job of GlobalScope: ${GlobalScope.coroutineContext[Job]}")

	val exceptionHandler = CoroutineExceptionHandler { _, exception ->
		log("Caught exception $exception")
	}

	val job = GlobalScope.launch(exceptionHandler) {
		launch {
			delay(50)
			throw RuntimeException()
			log("Still running")
			delay(50)
			log("Still running")
			delay(50)
			log("Still running")
			delay(50)
			log("Still running")
		}
	}

	delay(100)
	job.cancel()
	delay(300)
}