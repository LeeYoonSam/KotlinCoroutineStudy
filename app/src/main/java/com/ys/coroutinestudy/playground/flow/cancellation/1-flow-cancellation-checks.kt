package com.ys.coroutinestudy.playground.flow.cancellation

import com.ys.coroutinestudy.util.log
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking

/**
 * 편의를 위해 flow 빌더는 방출된 각 값에 대한 취소에 대한 활성 확인을 추가로 수행합니다.
 */
fun foo(): Flow<Int> = flow {
	for (i in 1..5) {
		log("Emitting $i")
		emit(i)
	}
}

fun main() = runBlocking {
	foo().collect { value ->
		if (value == 3) cancel()
		log(value)
	}
}