package com.ys.coroutinestudy.kotlin_coroutine_flow_master.basic.basic5_context_dispatchers

import com.ys.coroutinestudy.kotlin_coroutine_flow_master.getCurrentThreadName
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// 코루틴 스코프, 코루틴 컨텍스트는 구조화되어 있고 부모에게 계층적으로 되어 있습니다.
// 코루틴 컨텍스트의 `Job` 역시 부모에게 의존적입니다.
// 부모를 캔슬했을 때의 영향을 확인해보세요.

fun main() = runBlocking {
	val job = launch {
		launch(Job()) {
			println(coroutineContext[Job])
			println("launch1: ${getCurrentThreadName()}")
			delay(1000L)
			println("3!")
		}

		launch {
			println(coroutineContext[Job])
			println("launch2: ${getCurrentThreadName()}")
			delay(1000L)
			println("1!")
		}
	}

	delay(500L)

	// 이후에 delay 가 1000L 이상이 되면 3! 이 출력됩니다.
	job.cancelAndJoin()

	// delay 가 없으면 그대로 메인함수 종료
	delay(1000L)
}