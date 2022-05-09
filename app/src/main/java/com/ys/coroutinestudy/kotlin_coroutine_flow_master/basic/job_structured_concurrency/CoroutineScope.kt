package com.ys.coroutinestudy.kotlin_coroutine_flow_master.basic.job_structured_concurrency

import kotlinx.coroutines.*

/**
 * 코루틴 스코프를 만드는 다른 방법은 스코프 빌더를 이용하는 것입니다.
 *
 * coroutineScope
 *  - CoroutineScope를 만들고 이 범위로 지정된 일시 중단 블록을 호출합니다.
 *  - 제공된 범위는 외부 범위에서 coroutineContext를 상속하지만 컨텍스트의 작업을 재정의합니다.
 *  - 코루틴 스코프는 runBlocking 썼을 때와 모양이 거의 비슷합니다.
 *  - 둘의 차이는 runBlocking 은 현재 스레드를 멈추게 만들고 기다리지만, coroutineScope 는 현재 스레드를 멈추게 하지 않습니다.
 *      호출한 쪽이 suspend 되고 시간이 되면 다시 활동하게 됩니다.
 *  - withContext, runBlocking 은 일이 끝날때까지 스레드를 멈추게 만듭니다.
 */
private suspend fun doOneTwoThree() = coroutineScope {
	launch {
		println("launch1: ${Thread.currentThread().name}")
		delay(1000L)
		println("3!")
	}

	launch {
		println("launch2: ${Thread.currentThread().name}")
		println("1!")
	}

	launch {
		println("launch3: ${Thread.currentThread().name}")
		delay(500L)
		println("2!")
	}
	println("4!")
}

fun main() = runBlocking {
	doOneTwoThree()
	println("runBlocking: ${Thread.currentThread().name}")
	println("5!")
}