package com.ys.coroutinestudy.kotlin_coroutine_flow_master.flow.completion

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.runBlocking

private fun simple(): Flow<Int> = (1..3).asFlow()

private fun simpleError(): Flow<Int> = flow {
	repeat(3) {
		emit(it)
		delay(100L)
		throw IllegalArgumentException()
	}
}

fun main() = runBlocking {
	simple()
		.onCompletion { println("Done") }
		.collect { println(it) }

	// 중간에 예외가 발생하더라도 onCompletion 은 작동합니다.
	simpleError()
		.onCompletion { println("DoneError") }
		.collect { println(it) }
}