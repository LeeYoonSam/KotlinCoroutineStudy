package com.ys.coroutinestudy.kotlin_coroutine_flow_master.flow.operator

import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
	val value = (1..10)
		.asFlow()
		// 술어를 만족하는 자료의 갯수를 셉니다
		.count {
			it % 2 == 0
		}

	println(value)
}