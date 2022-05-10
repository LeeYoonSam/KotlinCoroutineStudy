package com.ys.coroutinestudy.kotlin_coroutine_flow_master.basic.cancel_timeout

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

private suspend fun doCount() = coroutineScope {
	val job1 = launch(Dispatchers.Default) {
		var i = 1
		var nextTime = System.currentTimeMillis() + 100L

		while (i <= 10 && isActive) {
			val currentTime = System.currentTimeMillis()
			if (currentTime >= nextTime) {
				println(i)
				nextTime = currentTime + 100L
				i++
			}
		}
	}

	delay(200L)
	job1.cancelAndJoin()
	println("doCount Done!")
}

fun main() = runBlocking {
	doCount()
}