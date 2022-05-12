package com.ys.coroutinestudy.kotlin_coroutine_flow_master.flow.operator

import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.reduce
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
	val value = (1..10)
		.asFlow()
		// 첫번째 값을 결과에 넣은 후 각 값을 가져와 누진적으로 계산합니다.
		.reduce { accumulator, value ->
			accumulator + value
		}

	println(value)
}