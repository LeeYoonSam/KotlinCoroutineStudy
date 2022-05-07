package com.ys.coroutinestudy.playground.kotlin_coroutine_flow_master.flow.operator

import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
	// filter 기능으로 술어를 만족하는 값만 추출
	(1..20).asFlow().filter {
		it % 2 == 0 // 술어, predicate
	}.collect(::println)
}