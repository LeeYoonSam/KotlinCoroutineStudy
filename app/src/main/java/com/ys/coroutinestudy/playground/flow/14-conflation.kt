package com.ys.coroutinestudy.playground.flow

import com.ys.coroutinestudy.util.log
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis

private fun simple(): Flow<Int> = flow {
	for (i in 1..5) {
		delay(1000) // 비동기식으로 100ms를 기다린다고 가정
		emit(i) // 다음 값을 내보냅니다
	}
}

fun main() = runBlocking {
	val time = measureTimeMillis {
		simple()
			.conflate() // emissions 을 각각 처리하지 않고 합쳐서 처리.
			.collect { value ->
				delay(3000) // 300ms 동안 처리한다고 가정
				log(value)
			}
	}
	log("Collected in $time ms")
}