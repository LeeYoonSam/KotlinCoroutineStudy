package com.ys.coroutinestudy.playground.flow

import com.ys.coroutinestudy.util.log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.runBlocking

fun numbers(): Flow<Int> = flow {
	try {
		emit(1)
		emit(2)
		println("This line will not execute")
		emit(3)
	} catch (e: Exception) {
		println(e) // kotlinx.coroutines.flow.internal.AbortFlowException: Flow was aborted, no more elements needed
	} finally {
		println("Finally in numbers")
	}
}

fun main() = runBlocking {
	numbers()
		.take(2) // 첫번재 요소를 기준으로 2개만 가져옴
		.collect { value -> log(value) }
}