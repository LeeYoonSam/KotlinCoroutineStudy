package com.ys.coroutinestudy.kotlin_coroutine_flow_master.flow.buffering

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis

fun simple(): Flow<Int> = flow {
	for (i in 1..3) {
		delay(100L)
		emit(i)
	}
}

// 보내는쪽 받는쪽이 모두 바빠서 delay 만큼 지연
// 버퍼 없이 사용하기 때문에 simple 에서 300ms, main 에서 300*3 = 900ms 합쳐서 1200 ms 정도 지연
fun main() = runBlocking {
	val time = measureTimeMillis {
		simple().collect {
			delay(300L)
			println(it)
		}
	}

	println("Collected in $time ms")
}