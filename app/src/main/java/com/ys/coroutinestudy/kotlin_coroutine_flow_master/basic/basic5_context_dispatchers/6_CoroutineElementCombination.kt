package com.ys.coroutinestudy.kotlin_coroutine_flow_master.basic.basic5_context_dispatchers

import com.ys.coroutinestudy.kotlin_coroutine_flow_master.getCurrentThreadName
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// 여러 코루틴 엘리먼트를 한번에 사용할 수 있다. `+` 연산으로 엘리먼트를 합치면 된다.
// 합쳐진 엘리먼트들은 `coroutineContext[XXX]` 로 조회할 수 있다.

@OptIn(ExperimentalStdlibApi::class)
fun main() = runBlocking<Unit> {
	launch {
		launch(Dispatchers.IO + CoroutineName("launch1")) {
			println("launch1: ${getCurrentThreadName()}")
			println(coroutineContext[CoroutineDispatcher])
			println(coroutineContext[CoroutineName])
			delay(5000L)
		}

		launch(Dispatchers.Default + CoroutineName("launch2")) {
			println("launch2: ${getCurrentThreadName()}")
			println(coroutineContext[CoroutineDispatcher])
			println(coroutineContext[CoroutineName])
			delay(10L)
		}
	}
}