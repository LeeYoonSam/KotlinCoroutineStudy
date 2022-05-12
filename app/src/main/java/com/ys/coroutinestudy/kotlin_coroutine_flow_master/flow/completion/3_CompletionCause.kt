package com.ys.coroutinestudy.kotlin_coroutine_flow_master.flow.completion

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.runBlocking

// onCompletion 의 장점으로 종료 처리를 할 때 예외가 발생되었는지 여부를 알 수 있습니다.

private fun simple(): Flow<Int> = flow {
	emit(1)
	throw RuntimeException()
}

fun main() = runBlocking {

	// finally 에서는 문제가 발생했는지 알수 없지만 onCompletion 에서는 cause 의 값에 따라 에러 발생 여부를 알수 있는것이 가장 큰 장점입니다.

	simple()
		.onCompletion { cause ->
			if (cause != null) {
				println("Flow completed exceptionally")
			}

			println("Done")
		}
		// catch 가 onCompletion 보다 업스트림에 있으면 onCompletion 에서는 cause 가 null 이 된다.
		.catch { cause -> println("Caught exception: $cause") }
		.collect { println(it) }
}