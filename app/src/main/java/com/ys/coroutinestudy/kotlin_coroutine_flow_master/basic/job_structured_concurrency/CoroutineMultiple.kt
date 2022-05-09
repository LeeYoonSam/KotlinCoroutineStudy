package com.ys.coroutinestudy.kotlin_coroutine_flow_master.basic.job_structured_concurrency

import kotlinx.coroutines.*

/**
 * 코루틴은 협력적으로 동작하기 때문에 여러 코루틴을 만드는 것이 큰 비용이 들지 않습니다.
 * 10만개의 간단한 일을 하는 코루틴도 큰 부담은 아닙니다.
 */
private suspend fun doOneTwoThree() = coroutineScope {
	val job = launch {
		println("launch1: ${Thread.currentThread().name}")
		// `delay` 가 있어도 해당 `launch` 블럭 코드 실행이 끝나기 전까지 대기를 합니다.
		delay(1000L)
		println("3!")
	}
	job.join() // suspension point, 첫번째 런치 블럭이 끝날때까지 잠들었다가 깨어나서 다음 코드를 호출

	launch {
		println("launch2: ${Thread.currentThread().name}")
		println("1!")
	}

	repeat(100000) {
		launch {
			println("launch3: ${Thread.currentThread().name}")
			delay(500L)
			println("2!")
		}
	}
	println("4!")
}

fun main() = runBlocking {
	doOneTwoThree()
	println("runBlocking: ${Thread.currentThread().name}")
	println("5!")
}