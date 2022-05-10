package com.ys.coroutinestudy.kotlin_coroutine_flow_master.basic.basic3_cancel_timeout

import com.ys.coroutinestudy.kotlin_coroutine_flow_master.getCurrentThreadName
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

suspend fun doOneTowThree() = coroutineScope {
	val job1 = launch {
		println("launch1: ${getCurrentThreadName()}")
		delay(1000L)
		println("3!")
	}

	val job2 = launch {
		println("launch2: ${getCurrentThreadName()}")
		println("1!")
	}

	val job3 = launch {
		println("launch3: ${getCurrentThreadName()}")
		delay(500L)
		println("2!")
	}

	delay(800L)
	// cancel 은 더이상 작업을 하지말라는 뜻과 동일합니다.
	job1.cancel()
	job2.cancel()
	job3.cancel()
	println("4!")
}

fun main() = runBlocking {
	doOneTowThree()
	println("runBlocking: ${Thread.currentThread().name}")
	println("5!")
}