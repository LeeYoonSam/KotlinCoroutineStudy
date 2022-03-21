package com.ys.coroutinestudy.playground.flow.exceptionhandling

import com.ys.coroutinestudy.util.log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking

private fun simple(): Flow<Int> = flow {
	for (i in 1..3) {
		println("Emitting $i")
		emit(i)
	}
}

/**
 * catch 는 다운스트림 예외를 처리하지는 못한다.
 *
 * 에러 발생
 *  - Exception in thread "main" java.lang.IllegalStateException: Collected 2
 */
fun main() = runBlocking {
	simple()
		.catch { e -> log("Caught $e") } // 다운스트림 예외를 catch하지 않습니다.
		.collect { value ->
			check(value <= 1) { "Collected $value" }
			log(value)
		}
}