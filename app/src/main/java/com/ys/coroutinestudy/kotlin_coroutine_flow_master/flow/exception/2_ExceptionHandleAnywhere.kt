package com.ys.coroutinestudy.kotlin_coroutine_flow_master.flow.exception

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

// 어느 곳에서 발생한 예외라도 처리가 가능합니다.

private fun simple(): Flow<String> = flow {
	for (i in 1..3) {
		println("Emitting $i")
		emit(i) // emit next value
	}
}.map {
	check(it <= 1) { "Crashed on $it" }
	"string $it"
}

fun main() = runBlocking {
	try {
		simple().collect { println(it) }
	} catch (e: Throwable) {
		println("Caught $e")
	}
}