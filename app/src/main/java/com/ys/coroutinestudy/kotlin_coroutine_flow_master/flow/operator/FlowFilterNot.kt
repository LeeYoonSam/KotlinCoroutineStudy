package com.ys.coroutinestudy.kotlin_coroutine_flow_master.flow.operator

import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
	// 짝수로 filter 하는 술어를 그대로 사용하고 filterNot으로 교체해서 홀수를 출력
	(1..20).asFlow().filterNot {
		it % 2 == 0
	}.collect(::println)

	// 술어를 변경해서 홀수를 추출
	(1..20).asFlow().filter {
		it % 2 != 0
	}.collect(::println)
}