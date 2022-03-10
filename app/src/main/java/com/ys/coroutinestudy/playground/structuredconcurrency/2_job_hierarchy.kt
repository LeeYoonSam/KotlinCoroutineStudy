package com.ys.coroutinestudy.playground.structuredconcurrency

import com.ys.coroutinestudy.util.log
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
		log("Starting coroutine")
		delay(1000)
	}

	log("passedJob and coroutineJob are references to the same job object: ${passedJob === coroutineJob}")
	log("Is coroutineJob a child of scopeJob? =>${scopeJob.children.contains(coroutineJob)}")
}