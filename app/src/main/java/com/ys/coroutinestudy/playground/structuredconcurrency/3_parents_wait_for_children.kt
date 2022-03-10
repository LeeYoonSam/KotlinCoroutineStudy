package com.ys.coroutinestudy.playground.structuredconcurrency

import com.ys.coroutinestudy.util.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {

	val scope = CoroutineScope(Dispatchers.Default)

	val parentCoroutineJob = scope.launch {
		launch {
			delay(1000)
			log("Child Coroutine 1 has completed!")
		}

		launch {
			delay(1000)
			log("Child Coroutine 2 has completed!")
		}
	}

	parentCoroutineJob.join()
	log("Parent Coroutine has completed!")
}