package com.ys.coroutinestudy.playground.flow.completion

import com.ys.coroutinestudy.util.log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.runBlocking

private fun simple(): Flow<Int> = flow {
	emit(1)
	throw RuntimeException()
}

fun main() = runBlocking {
	simple()
		//.onCompletion{ }은 .catch{ }보다 먼저 호출됩니다.
		//.onCompletion{ }은 예외를 처리하지 않으며 예외는 여전히 다운스트림으로 흐릅니다.
		.onCompletion { cause -> if (cause != null) log("Flow completed exceptionally") }
		.catch { log("Caught exception") }
		.collect { value -> log(value) }
}