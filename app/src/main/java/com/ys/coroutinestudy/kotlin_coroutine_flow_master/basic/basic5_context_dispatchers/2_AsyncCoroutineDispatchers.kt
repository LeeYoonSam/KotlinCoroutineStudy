package com.ys.coroutinestudy.kotlin_coroutine_flow_master.basic.basic5_context_dispatchers

import com.ys.coroutinestudy.kotlin_coroutine_flow_master.getCurrentThreadName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking

// `launch`외에 `async`, `withContext`등의 코루틴 빌더에도 디스패처를 사용할 수 있습니다.

fun main() = runBlocking<Unit> {

	async {
		println("부모의 컨텍스트 / ${getCurrentThreadName()}")
	}

	async(Dispatchers.Default) {
		println("Default / ${getCurrentThreadName()}")
	}

	async(Dispatchers.IO) {
		println("IO / ${getCurrentThreadName()}")
	}

	// 한번이라도 잠이 들었다가 깨어나면 어디서 수행할지 모르는것이  Unconfied 입니다.
	async(Dispatchers.Unconfined) {
		println("Unconfined / ${getCurrentThreadName()}")
	}

	async(newSingleThreadContext("Test Dispatcher")) {
		println("newSingleThreadContext / ${getCurrentThreadName()}")
	}
}