package com.ys.coroutinestudy.kotlin_coroutine_flow_master.basic.basic7_share_object

import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

private var counter = 0
val counterContext = newSingleThreadContext("CounterContext")

fun main() = runBlocking {

	// 항상 같은 스레드를 사용하는것이 보장이 됩니다.
	withContext(counterContext) {
		massiveRun {
			counter++
		}
	}

	// 얼마만큼 한정지을 것인지는 자유롭게 정해보세요.
	// 전체 코루틴에서 사용
	// 코루틴 스코프내에서 사용
	// withContext(Dispatchers.Default) { // 전체 코드를 하나의 스레드에서
	// 	massiveRun {
	// 		withContext(counterContext) { // 더하는 코드를 하나의 스레드에서
	// 			counter++
	// 		}
	// 	}
	// }

	println("Counter = $counter")
}