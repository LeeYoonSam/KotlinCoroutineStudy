package com.ys.coroutinestudy.coroutine_sample.context

import com.ys.coroutinestudy.coroutine_sample.future.await
import com.ys.coroutinestudy.coroutine_sample.future.future
import com.ys.coroutinestudy.coroutine_sample.util.log
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

fun main() = runBlocking(CommonPool) {

	// multithreaded pool
	val n = 4
	val compute = newFixedThreadPoolContext(n, "Compute")
	// start 4 coroutines to do some heavy computation
	val subs = Array(n) { i ->
		future(compute) {
			log("Starting computation #$i")
			delay(1000)
			log("Done computation #$i")
		}
	}
	// await all of them
	subs.forEach { it.await() }
	log("Done all")
}