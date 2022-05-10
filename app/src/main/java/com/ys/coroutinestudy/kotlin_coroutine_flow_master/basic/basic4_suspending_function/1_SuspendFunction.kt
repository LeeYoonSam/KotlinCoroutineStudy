package com.ys.coroutinestudy.kotlin_coroutine_flow_master.basic.basic4_suspending_function

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.random.Random
import kotlin.system.measureTimeMillis

private suspend fun getRandom1(): Int {
	delay(1000L)
	return Random.nextInt(0, 500)
}

private suspend fun getRandom2(): Int {
	delay(1000L)
	return Random.nextInt(0, 500)
}

fun main() = runBlocking {

	// 순차적으로 suspend 함수를 수행
	val elapsedTime = measureTimeMillis {

		// getRandom1 이 1000ms 정도 소비
		val value1 = getRandom1()
		// getRandom2 가 1000ms 정도 소비
		val value2 = getRandom2()
		println("$value1 + $value2 = ${value1 + value2}")
	}

	println(elapsedTime)
}