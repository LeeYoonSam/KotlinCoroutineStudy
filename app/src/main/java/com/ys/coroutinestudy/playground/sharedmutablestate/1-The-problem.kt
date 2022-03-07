package com.ys.coroutinestudy.playground.sharedmutablestate

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.system.measureTimeMillis

suspend fun massiveRun(action: suspend () -> Unit) {

	val n = 100 // 실행할 코루틴 수
	val k = 1000 // 각 코루틴에 의해 동작이 반복되는 횟수
	val time = measureTimeMillis {
		coroutineScope { // 코루틴의 범위
			repeat(n) {
				launch {
					repeat(k) { action() }
				}
			}
		}
	}

	println("Completed ${n * k} actions in $time ms")
}

var counter = 0

fun main() = runBlocking {
	withContext(Dispatchers.Default) {
		massiveRun {
			counter++
		}
	}
	println("Counter = $counter")
}