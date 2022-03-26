package com.ys.coroutinestudy.playground.flow.operators

import com.ys.coroutinestudy.util.log
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.runBlocking

private val fastFlowEmittingFlow = flow {
	repeat(10) {
		emit(it)
		delay(110)
	}
}

fun main() = runBlocking {
	// 주어진 샘플링 기간 동안 원래 플로우에서 방출한 최신 값만 방출하는 플로우를 리턴합니다.
	// 출력: 1,3,5,6,7
	fastFlowEmittingFlow.sample(200).collect {
		log("$it")
	}
}