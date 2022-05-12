package com.ys.coroutinestudy.kotlin_coroutine_flow_master.flow.flattening

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking

private fun requestFlow(i: Int): Flow<String> = flow {
	emit("$i: First")
	delay(500L)
	emit("$i: Second")
}

/**
 * flatMapConcat 는 첫번째 요소에 대해서 플래트닝을 하고 나서 두번째 요소를 합니다.
 */
fun main() = runBlocking<Unit> {
	val startTime = System.currentTimeMillis() // 첫번째 시간 기억
	(1..3).asFlow()
		.onEach { delay(100L) }
		.flatMapConcat {
			requestFlow(it)
		}
		.collect {
			println("$it at ${System.currentTimeMillis() - startTime} ms from start")
		}
}