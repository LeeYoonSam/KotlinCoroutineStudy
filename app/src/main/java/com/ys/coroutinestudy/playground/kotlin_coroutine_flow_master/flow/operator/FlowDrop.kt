package com.ys.coroutinestudy.playground.kotlin_coroutine_flow_master.flow.operator

import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.dropWhile
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
	(1..20).asFlow()
		.transform {
			emit(it)
			emit(someCalc(it))
		}
		// drop 연산자는 처음 몇개의 결과를 버립니다
		.drop(5)
		.collect {
			println(it)
		}

	(1..20).asFlow()
		.transform {
			emit(it)
			emit(someCalc(it))
		}
		// dropWhile 은 주어진 조건자를 만족하는 첫 번째 요소를 제외한 모든 요소를 포함하는 흐름을 반환합니다.
		.dropWhile {
			it < 15
		}
		.collect {
			println(it)
		}
}