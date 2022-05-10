package com.ys.coroutinestudy.kotlin_coroutine_flow_master.basic.basic3_cancel_timeout

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

private suspend fun doCount() = coroutineScope {
	val job1 = launch(Dispatchers.Default) {
		var i = 1
		var nextTime = System.currentTimeMillis() + 100L

		while (i <= 10) {
			val currentTime = System.currentTimeMillis()
			if (currentTime >= nextTime) {
				println(i)
				nextTime = currentTime + 100L
				i++
			}
		}
	}

	delay(200L)

	// job1.cancel()
	// job1.join() // cancel 을 하고 실제 cancel 이 될때까지 대기
	// job1 이 취소돈 종료든 다 끝난 이후에 doCount Done! 을 출력하고 싶다. -> cancel 과 join 활용

	// `cancel`을 하고 `join`을 하는 일은 자주 일어나는 일이기 때문에 한번에 하는 `cancelAndJoin`이 준비되어 있습니다.
	job1.cancelAndJoin()
	println("doCount Done!")
}

fun main() = runBlocking {
	doCount()
}