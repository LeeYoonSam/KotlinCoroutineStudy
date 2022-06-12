package com.ys.coroutinestudy.playground.deepdive

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis

fun main() = runBlocking {
	singleCoroutineDemo()
}

suspend fun singleCoroutineDemo() {
	val totalTime = measureTimeMillis {
		println("CoroutineDemo - Coroutine Demo Started on ${Thread.currentThread().name}")
		delay(1000)
		coroutineDemoApiCall1()
		coroutineDemoApiCall2()
	}
	println("CoroutineDemo - Time to finish coroutineDemo: $totalTime")
}

suspend fun coroutineDemoApiCall1() {
	val time1 = measureTimeMillis {
		println("CoroutineDemo - Coroutine Demo Api Call 1 Started on ${Thread.currentThread().name}")
		delay(2000)
		println("CoroutineDemo - Coroutine Demo Api Call 1 Finished")
	}
	println("CoroutineDemo - Time to finish coroutineDemoApiCall1: $time1")

}

suspend fun coroutineDemoApiCall2() {
	val time2 = measureTimeMillis {
		println("CoroutineDemo - Coroutine Demo Api Call 2 Started on ${Thread.currentThread().name}")
		delay(500)
		println("CoroutineDemo - Coroutine Demo Api Call 2 Finished")
	}
	println("CoroutineDemo - Time to finish coroutineDemoApiCall2: $time2")
}