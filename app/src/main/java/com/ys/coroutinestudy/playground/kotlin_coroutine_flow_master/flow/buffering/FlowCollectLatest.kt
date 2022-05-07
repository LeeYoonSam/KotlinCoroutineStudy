package com.ys.coroutinestudy.playground.kotlin_coroutine_flow_master.flow.buffering

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis

fun main() = runBlocking {

	/**
	 * collectLatest 동작
	 *  1. 첫번째 값을 받고 처리하는 과정중에 두번째 값이 오면 리셋
	 *  2. 두번째 값을 받고 처리하는 과정중에 세번째 값이 오면 다시 리셋
	 */
	val time = measureTimeMillis {
		simple().collectLatest {
			println("값 ${it}를 처리하기 시작합니다.")
			delay(300)
			println(it)
			println("처리를 완료하였습니다.")
		}
	}

	println("Collected in $time ms")
}