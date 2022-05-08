package com.ys.coroutinestudy.playground.kotlin_coroutine_flow_master.flow.flattening

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking

private fun requestFlow(i: Int): Flow<String> = flow {
	emit("$i: First")
	delay(500) // wait 500 ms
	emit("$i: Second")
}

/**
 * flatMapMerge
 *
 * 첫 요소의 플래트닝을 시작하며 이어 다음 요소의 플래티넘을 시작합니다.
 */
fun main() = runBlocking<Unit> {
	val startTime = System.currentTimeMillis() // 시작시간 기억
	(1..3).asFlow().onEach { delay(100L) }
		.flatMapMerge {
			requestFlow(it)
		}
		.collect {
			println("$it at ${System.currentTimeMillis() - startTime} ms from start")
		}
}