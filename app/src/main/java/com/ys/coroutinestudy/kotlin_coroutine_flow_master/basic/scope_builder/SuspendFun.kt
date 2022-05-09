package com.ys.coroutinestudy.kotlin_coroutine_flow_master.basic.scope_builder

import com.ys.coroutinestudy.kotlin_coroutine_flow_master.getCurrentThreadName
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

suspend fun doThree() {
	println("launch1: ${getCurrentThreadName()}")
	delay(1000L)
	println("3!")
}

suspend fun doTwo() {
	println("runBlocking: ${Thread.currentThread().name}")
	delay(500L)
	println("2!")
}

fun doOne() {
	println("launch1: ${Thread.currentThread().name}")
	println("1!")
}

fun main() = runBlocking {
	launch {
		doThree()
	}

	launch {
		doOne()
	}

	doTwo()
}