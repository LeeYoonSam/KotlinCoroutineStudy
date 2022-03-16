package com.ys.coroutinestudy.playground.flow

import com.ys.coroutinestudy.util.log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

private fun simple(): Flow<Int> = flow {
	log("Started simple flow")
	for (i in 1..3) {
		emit(i)
	}
}

// 컨텍스트 보존
// flow 수집은 항상 호출 코루틴의 컨텍스트에서 발생합니다.
fun main() = runBlocking<Unit> {
	// main 에서 실행
	simple().collect { value -> log("Collected $value") }

	// DefaultDispatcher-worker 에서 실행
	launch(Dispatchers.Default) {
		simple().collect { value -> log("Collected $value") }
	}
}

