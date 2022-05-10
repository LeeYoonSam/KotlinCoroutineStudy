package com.ys.coroutinestudy.kotlin_coroutine_flow_master.basic.basic4_suspending_function

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.random.Random

// async 를 사용한 구조적인 동시성

private suspend fun getRandom1(): Int {
	try {
		delay(1000L)
		return Random.nextInt(0, 500)
	} finally {
		println("getRandom1 is cancelled.")
	}
}

private suspend fun getRandom2(): Int {
	delay(500L)
	throw IllegalStateException()
}

suspend fun doSomething() = coroutineScope { // 부모 코루틴 // 문제로 인해 캔슬
	val value1 = async { getRandom1() } // 자식 코루틴 // 문제로 인해 캔슬
	val value2 = async { getRandom2() } // 자식 코루틴 // 문제 발생

	try {
		println("${value1.await()} + ${value2.await()} = ${value1.await() + value2.await()}")
	} finally {
		println("doSomething is cancelled.")
	}
}

fun main() = runBlocking {
	try {
		doSomething()
	} catch (e: IllegalStateException) {
		// getRandom2 가 오류가 발생해서 getRandom1, doSomething 은 취소됩니다.
		println("doSomething failed: $e")
	}
}