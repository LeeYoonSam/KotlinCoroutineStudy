package com.ys.coroutinestudy.kotlin_coroutine_flow_master.flow.operator

import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
	(1..20).asFlow()
		.transform {
			emit(it)
			emit(someCalc(it))
		}
		// takeWhile 연산자를 통해 조건에 만족하는 결과만 취합니다.
		.takeWhile {
			it < 15
		}
		.collect {
			println(it)
		}


	// 주의: `takeWhile` 의 조건으로 첫번째 값이 만족하지 못하면 뒤따르는 값들은 조건에 만족하더라도 출력이 되지 않습니다.
	(1..20).asFlow()
		.transform {
			emit(it)
			emit(someCalc(it))
		}
		// takeWhile 연산자를 통해 조건에 만족하는 결과만 취합니다.
		.takeWhile {
			it < 1
		}
		.collect {
			println(it)
		}
}