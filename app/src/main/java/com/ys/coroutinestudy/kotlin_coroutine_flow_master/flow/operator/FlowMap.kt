package com.ys.coroutinestudy.kotlin_coroutine_flow_master.flow.operator

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlin.random.Random

fun flowSomeThing(): Flow<Int> = flow {
	repeat(10) {
		emit(Random.nextInt(0, 500))
		delay(10L)
	}
}

fun main() = runBlocking {
	// map 연산을 통해 데이터를 가공
	flowSomeThing().map {
		"$it $it"
	}.collect {
		println(it)
	}
}