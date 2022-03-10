package com.ys.coroutinestudy.playground.structuredconcurrency

import com.ys.coroutinestudy.util.logWithThreadName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun main() {

	val scopeJob = Job()
	val scope = CoroutineScope(Dispatchers.Default + scopeJob)

	val passedJob = Job()
	val coroutineJob = scope.launch(passedJob) {
		println("Starting coroutine")
		delay(1000)
	}

	logWithThreadName {
		println("passedJob and coroutineJob are references to the same job object: ${passedJob === coroutineJob}")
	}

	logWithThreadName {
		println("Is coroutineJob a child of scopeJob? =>${scopeJob.children.contains(coroutineJob)}")
	}
}