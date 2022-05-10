package com.ys.coroutinestudy.kotlin_coroutine_flow_master.basic.basic3_cancel_timeout

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// 취소가 불가능한 Job
// launch(Dispatchers.Default) 는 그 다음 코드 블록을 다른 스레드에서 수행을 시킬 것입니다.
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
	// 취소가 되지 않았다.
	job1.cancel()

	// job1 이 취소돈 종료든 다 끝난 이후에 doCount Done! 을 출력하고 싶다. -> cancel 과 join 활용
	println("doCount Done!")
}

fun main() = runBlocking {
	doCount()
}