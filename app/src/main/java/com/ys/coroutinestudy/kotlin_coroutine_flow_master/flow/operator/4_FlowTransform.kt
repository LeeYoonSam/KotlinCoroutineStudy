package com.ys.coroutinestudy.kotlin_coroutine_flow_master.flow.operator

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.runBlocking

suspend fun someCalc(i: Int): Int {
	delay(10L)
	return i * 2
}

/**
 * transform 연산자를 이용해서 스트림을 변형
 */
fun main() = runBlocking {
	// transform 연산자를 통해서 하나의 값에 2번의 emit 으로 변형
	(1..20).asFlow().transform {
		emit(it)
		emit(someCalc(it))
	}.collect {
		println(it)
	}
}