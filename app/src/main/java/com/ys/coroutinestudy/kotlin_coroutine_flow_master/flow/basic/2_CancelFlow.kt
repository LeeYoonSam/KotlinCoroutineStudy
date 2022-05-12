package com.ys.coroutinestudy.kotlin_coroutine_flow_master.flow.basic

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.random.Random

// 코루틴 시간에 배웠던 `withTimeoutOrNull`을 이용해 간단히 취소할 수 있습니다.

private fun flowSomething(): Flow<Int> = flow {
	repeat(10) {
		emit(Random.nextInt(0, 500))
		delay(100L)
	}
}

fun main() = runBlocking {
	val result = withTimeoutOrNull(500L) {
		flowSomething().collect {
			println(it)
		}
		true
	} ?: false

	if (!result) {
		println("취소되었습니다.")
	}
}