package com.ys.coroutinestudy.playground.flow.exceptionhandling

import com.ys.coroutinestudy.util.log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

private fun simple(): Flow<String> =
	flow {
		for (i in 1..3) {
			println("Emitting $i")
			emit(i) // emit next value
		}
	}.map { value ->
		check(value <= 1) { "Crashed on $value" }
		"string $value"
	}

fun main() = runBlocking {
	simple()
		.catch { e -> emit("Caught $e") } // 예외시 방출
		.collect { value -> log(value) }
}