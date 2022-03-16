package com.ys.coroutinestudy.playground.flow

import com.ys.coroutinestudy.util.log
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis

private fun simple(): Flow<Int> = flow {
	for (i in 1..3) {
		delay(100) // 비동기식으로 100ms를 기다린다고 가정
		emit(i)
	}
}

fun main() = runBlocking {
	val time = measureTimeMillis {
		simple()
			.buffer() // 버퍼가 없으면 코드를 완료하는 데 1235ms가 걸리고 그렇지 않으면 액 1000ms 가 걸립니다.
			.collect { value ->
				delay(300) // 300ms 동안 처리한다고 가정
				log(value)
			}
	}

	println("Collected in $time ms")
}

// 처리 파이프라인을 효과적으로 생성했습니다.