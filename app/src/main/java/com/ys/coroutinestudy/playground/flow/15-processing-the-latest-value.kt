package com.ys.coroutinestudy.playground.flow

import com.ys.coroutinestudy.util.log
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis

private fun simple(): Flow<Int> = flow {
	for (i in 1..3) {
		delay(100) // 비동기식으로 100ms를 기다린다고 가정
		emit(i) // 다음 값을 내보냅니다
	}
}

fun main() = runBlocking {
	val time = measureTimeMillis {
		simple()
			.collectLatest { value ->
				log("Collection $value")
				delay(300)
				log("Done $value")
			}
	}
	log("Collected in $time ms")
}