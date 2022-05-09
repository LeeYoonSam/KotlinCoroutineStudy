package com.ys.coroutinestudy.kotlin_coroutine_flow_master.flow.flattening

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking

/**
 * flatMapLatest 는 다음 요소의 플래트닝을 시작하며 이전에 진행 중이던 플래트닝을 취소합니다.
 */

private fun requestFlow(i: Int): Flow<String> = flow {
	emit("$i: First")
	delay(500L) // wait 500 ms
	emit("$i: Second")
}

fun main() = runBlocking<Unit> {
	val startTime = System.currentTimeMillis()
	(1..3).asFlow().onEach { delay(100L) }
		.flatMapLatest {
			requestFlow(it)
		}
		.collect {
			println("$it at ${System.currentTimeMillis() - startTime} ms from start")
		}
}