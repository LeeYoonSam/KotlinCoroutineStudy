package com.ys.coroutinestudy.kotlin_coroutine_flow_master.basic.basic7_share_object

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicInteger

// AtomicInteger 와 같은 스레드 안전한 자료구조를 사용하는 방법이 있습니다.
private val counter = AtomicInteger()

fun main() = runBlocking {
	withContext(Dispatchers.Default) {

		massiveRun {
			// 값을 증가시키고 현재 가지고있는 값을 리턴
			// 다른 스레드가 값을 변경할수 없게 합니다.
			// AtomicInteger 가 이 문제에는 적합한데 항상 정답은 아닙니다.
			counter.incrementAndGet()
		}
	}

	println("Counter = $counter")
}