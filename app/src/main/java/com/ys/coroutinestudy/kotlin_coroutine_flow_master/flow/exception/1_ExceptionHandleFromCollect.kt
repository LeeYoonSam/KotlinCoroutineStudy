package com.ys.coroutinestudy.kotlin_coroutine_flow_master.flow.exception

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking

private fun simple(): Flow<Int> = flow {
	for (i in 1..3) {
		println("Emitting $i")
		emit(i) // emit next value
	}
}

// 예외는 `collect`을 하는 수집기 측에서도 try-catch 식을 이용 할 수 있습니다.
fun main() = runBlocking {
	try {
		simple().collect {
			println(it)
			check(it <= 1) { "Crashed on $it" }
		}
	} catch (e: Throwable) {
		println("Caught $e")
	}
}