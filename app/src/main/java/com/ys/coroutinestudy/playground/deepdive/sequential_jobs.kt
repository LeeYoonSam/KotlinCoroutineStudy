package com.ys.coroutinestudy.playground.deepdive

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis

/**
 * 두 개의 코루틴을 병렬로 호출하고 세 번째 코루틴을 순차적으로 호출하기 전에 완료될 때까지 기다리는 방법을 살펴보겠습니다.
 */
fun main() {
	sequentialJobsDemo()
}

var seqResult1: String = ""
var seqResult2: String = ""

fun sequentialJobsDemo() {
	val totalTime = measureTimeMillis {
		runBlocking {
			println("CoroutineDemo - Parallel Coroutine Demo Started on ${Thread.currentThread().name}")
			val job1 = launch { coroutineDemoSequenceVoidReturnApiCall1() }
			val job2 = launch { coroutineDemoSequenceVoidReturnApiCall2() }
			job1.join()
			job2.join()
			launch { coroutineDemoSequenceVoidReturnApiCall3() }
		}
	}
	println("CoroutineDemo - Time to finish coroutineDemo: $totalTime")
}
suspend fun coroutineDemoSequenceVoidReturnApiCall1() {
	val time2 = measureTimeMillis {
		println("CoroutineDemo - Coroutine Demo Api Call 1 Started on ${Thread.currentThread().name}")
		delay(700)
		seqResult1 = "Coroutine 1 Executed"
		println("CoroutineDemo - Coroutine Demo Api Call 1 Finished")
	}
	println("CoroutineDemo - Time to finish coroutineDemoApiCall1: $time2")
}

suspend fun coroutineDemoSequenceVoidReturnApiCall2() {
	val time2 = measureTimeMillis {
		println("CoroutineDemo - Coroutine Demo Api Call 2 Started on ${Thread.currentThread().name}")
		delay(500)
		seqResult2 = "Coroutine 2 Executed"
		println("CoroutineDemo - Coroutine Demo Api Call 2 Finished")
	}
	println("CoroutineDemo - Time to finish coroutineDemoApiCall2: $time2")
}

suspend fun coroutineDemoSequenceVoidReturnApiCall3() {
	val time3 = measureTimeMillis {
		println("CoroutineDemo - Coroutine Demo Api Call 3 Started on ${Thread.currentThread().name}")
		delay(500)
		println("CoroutineDemo - Coroutine 3 Executed After $seqResult1 and $seqResult2")
		println("CoroutineDemo - Coroutine Demo Api Call 3 Finished")
	}
	println("CoroutineDemo - Time to finish coroutineDemoApiCall3: $time3")
}