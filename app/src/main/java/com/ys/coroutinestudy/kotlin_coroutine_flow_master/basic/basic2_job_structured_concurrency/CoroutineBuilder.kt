package com.ys.coroutinestudy.kotlin_coroutine_flow_master.basic.basic2_job_structured_concurrency

import kotlinx.coroutines.*

/**
 * 코루틴 빌더는 코루틴 스코프 내에서만 호출해야 합니다.
 */
private suspend fun doOneTwoThree() {
	// launch 부분에 에러 발생 - Suspension functions 은 오직 coroutine body 에서 호출을 해야 합니다.
	// doOneTwoThree 는 코루틴 바디가 없어서 에러가 발생합니다.
	// launch {
	// 	println("launch1: ${Thread.currentThread().name}")
	// 	delay(1000L)
	// 	println("3!")
	// }
	//
	// launch {
	// 	println("launch2: ${Thread.currentThread().name}")
	// 	println("1!")
	// }
	//
	// launch {
	// 	println("launch3: ${Thread.currentThread().name}")
	// 	delay(500L)
	// 	println("2!")
	// }
	println("4!")
}

fun main() = runBlocking {
	doOneTwoThree()
	println("runBlocking: ${Thread.currentThread().name}")
	println("5!")
}