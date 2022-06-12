package com.ys.coroutinestudy.playground.deepdive

import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis

fun main() {
	sequentialJobsAsyncDemo()
}

fun sequentialJobsAsyncDemo() {
	val totalTime = measureTimeMillis {
		runBlocking {
			println("CoroutineDemo - Parallel Coroutine Async Demo Started on ${Thread.currentThread().name}")
			val job1 = async { coroutineDemoSequenceAsyncReturnApiCall1() }.await()
			val job2 = async { coroutineDemoSequenceAsyncReturnApiCall2() }.await()
			launch { coroutineDemoSequenceAsyncReturnApiCall3(job1, job2) }
		}
	}
	println("CoroutineDemo - Time to finish coroutineDemo: $totalTime")
}
suspend fun coroutineDemoSequenceAsyncReturnApiCall1(): String {
	val time1 = measureTimeMillis {
		println("CoroutineDemo - Coroutine Demo Api Call 1 Started on ${Thread.currentThread().name}")
		delay(700)
		println("CoroutineDemo - Coroutine Demo Api Call 1 Finished")
	}
	println("CoroutineDemo - Time to finish coroutineDemoApiCall1: $time1")
	return "Coroutine 1 Executed"
}

suspend fun coroutineDemoSequenceAsyncReturnApiCall2(): String {
	val time2 = measureTimeMillis {
		println("CoroutineDemo - Coroutine Demo Api Call 2 Started on ${Thread.currentThread().name}")
		delay(500)
		println("CoroutineDemo - Coroutine Demo Api Call 2 Finished")
	}
	println("CoroutineDemo - Time to finish coroutineDemoApiCall2: $time2")
	return "Coroutine 2 Executed"
}

suspend fun coroutineDemoSequenceAsyncReturnApiCall3(s1: String, s2: String):String {
	val time3 = measureTimeMillis {
		println("CoroutineDemo - Coroutine Demo Api Call 3 Started on ${Thread.currentThread().name}")
		delay(500)
		println("CoroutineDemo - Coroutine 3 Executed After $seqResult1 and $seqResult2")
		println("CoroutineDemo - Coroutine Demo Api Call 3 Finished")
	}
	println("CoroutineDemo - Time to finish coroutineDemoApiCall3: $time3")
	return "Coroutine 2 Executed After $s1 and $s2"
}