package com.ys.coroutinestudy.playground.cancellation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException

fun main() = runBlocking {

	val job = launch(Dispatchers.Default) {
		repeat(10) { index ->
			if (isActive) {
				println("operation number $index")
				Thread.sleep(100)
			} else {
				// 취소 시 일부 정리를 수행
				withContext(NonCancellable) {
					delay(100)
					println("Clean up done!")
				}
				throw CancellationException()
			}
		}
	}

	delay(250)
	println("Cancelling Coroutine")
	job.cancel()

	val globalCoroutineJob = GlobalScope.launch {
		repeat(10) {
			println("$it")
			delay(100)
		}
	}

	delay(250)
	globalCoroutineJob.cancel()
	delay(1000)
}