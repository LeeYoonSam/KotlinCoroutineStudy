package com.ys.coroutinestudy.kotlin_coroutine_flow_master.flow.context

import com.ys.coroutinestudy.kotlin_coroutine_flow_master.log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

fun simple2(): Flow<Int> = flow {
	// flow 빌더 내에서는 컨텍스트를 바꿀 수 없기 때문에 IllegalStateException 이 발생합니다.
	withContext(Dispatchers.Default) {
		for (i in 1..10) {
			delay(100L)
			emit(i)
		}
	}
}

fun main() = runBlocking<Unit> {
	launch(Dispatchers.IO) {
		simple2()
			.collect {
				log("${it}를 받음.")
			}
	}
}

