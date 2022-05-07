package com.ys.coroutinestudy.playground.kotlin_coroutine_flow_master.flow.operator

import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.fold
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
	val value = (1..10)
		.asFlow()
		// 첫번째 값을 결과에 넣은 후 각 값을 가져와 누진적으로 계산합니다.
		.fold(10) { accumulator, value ->
			accumulator + value
		}

	println(value)
}