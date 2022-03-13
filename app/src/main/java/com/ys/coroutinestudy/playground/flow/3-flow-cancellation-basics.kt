package com.ys.coroutinestudy.playground.flow

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull

private fun simple(): Flow<Int> = flow {
	for (i in 1..3) {
		delay(100)
		println("Emitting $i")
		emit(i)
	}
}

fun main() = runBlocking {
	withTimeoutOrNull(250) { // 250ms 후에 타임아웃
		simple().collect { value -> println(value) }
	}
	println("Done")
}