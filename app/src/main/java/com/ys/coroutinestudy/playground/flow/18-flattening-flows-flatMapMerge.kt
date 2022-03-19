package com.ys.coroutinestudy.playground.flow

import com.ys.coroutinestudy.util.log
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking

private fun requestFlow(i: Int): Flow<String> = flow {
	emit("$i: First")
	delay(500)
	emit("$i: Second")
}

fun main() = runBlocking {
	val startTime = System.currentTimeMillis()

	(1..3).asFlow()
		// 업스트림 flow의 각 값이 다운스트림으로 내보내지기전에 지정된 [작업]을 호출하는 흐름을 반환합니다.
		.onEach {
			delay(100)
			log("now: $it")
		}
		.flatMapMerge { requestFlow(it) }
		.collect { value ->
			log("$value at ${System.currentTimeMillis() - startTime} ms from start")
		}
}