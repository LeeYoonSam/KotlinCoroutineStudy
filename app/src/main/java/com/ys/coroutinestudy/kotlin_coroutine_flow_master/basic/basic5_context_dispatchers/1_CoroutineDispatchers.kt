package com.ys.coroutinestudy.kotlin_coroutine_flow_master.basic.basic5_context_dispatchers

import com.ys.coroutinestudy.kotlin_coroutine_flow_master.getCurrentThreadName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking

// 코루틴의 여러 디스패처 `Default`, `IO`, `Unconfined`, `newSingleThreadContext`을 사용해봅시다.

fun main() = runBlocking<Unit> {

	launch {
		println("부모의 컨텍스트 / ${getCurrentThreadName()}")
	}

	// 1. `Default`는 코어 수에 비례하는 스레드 풀에서 수행합니다.
	launch(Dispatchers.Default) {
		println("Default / ${getCurrentThreadName()}")
	}

	// 2. `IO`는 코어 수 보다 훨씬 많은 스레드를 가지는 스레드 풀입니다. IO 작업은 CPU를 덜 소모하기 때문입니다.
	launch(Dispatchers.IO) {
		println("IO / ${getCurrentThreadName()}")
	}

	// 3. `Unconfined`는 어디에도 속하지 않습니다. 지금 시점에는 부모의 스레드에서 수행될 것입니다.
	launch(Dispatchers.Unconfined) {
		println("Unconfined / ${getCurrentThreadName()}")
	}

	// 4. `newSingleThreadContext`는 항상 새로운 스레드를 만듭니다.
	launch(newSingleThreadContext("Test Dispatcher")) {
		println("newSingleThreadContext / ${getCurrentThreadName()}")
	}
}