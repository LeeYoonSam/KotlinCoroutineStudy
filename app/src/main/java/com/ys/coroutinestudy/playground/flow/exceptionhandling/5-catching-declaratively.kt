package com.ys.coroutinestudy.playground.flow.exceptionhandling

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking

private fun simple(): Flow<Int> = flow {
	for (i in 1..3) {
		println("Emitting $i")
		emit(i)
	}
}

/**
 * onEach
 * - 업스트림 흐름의 각 값이 다운스트림으로 내보내지기전에 지정된 [action]을 호출하는 flow 를 반환합니다.
 */
fun main() = runBlocking {
	simple()
		.onEach { value ->
			check(value <= 1) { "Collected $value" }
			println(value)
		}
		.catch { e -> println("Caught $e") } // catch 가 onEach 보다 위에 위치하면 에러 catch 불가능
		.collect()
}