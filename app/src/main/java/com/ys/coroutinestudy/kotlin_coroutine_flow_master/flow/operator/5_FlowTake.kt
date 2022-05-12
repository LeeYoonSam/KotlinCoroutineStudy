package com.ys.coroutinestudy.kotlin_coroutine_flow_master.flow.operator

import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
	(1..20).asFlow()
		.transform {
			emit(it)
			emit(someCalc(it))
		}
		// take 연산자를 통해 몇 개의 수행 결과만 취합니다.
		.take(5)
		.collect {
			println(it)
		}
}