package com.ys.coroutinestudy.playground.structuredconcurrency

import com.ys.coroutinestudy.util.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun main() {

	val scope = CoroutineScope(Job())

	scope.launch {
		doSomeTask()

		launch {
			log("Starting Task 3")
			delay(300)
			log("Task 3 completed")
		}
	}

	Thread.sleep(1000)
}

suspend fun doSomeTask() = coroutineScope {
	launch {
		log("Starting Task 1")
		delay(100)
		log("Task 1 completed")
	}

	launch {
		log("Starting Task 2")
		delay(200)
		log("Task 2 completed")
	}
}