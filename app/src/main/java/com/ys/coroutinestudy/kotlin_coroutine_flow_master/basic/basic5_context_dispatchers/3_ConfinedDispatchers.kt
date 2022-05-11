package com.ys.coroutinestudy.kotlin_coroutine_flow_master.basic.basic5_context_dispatchers

import com.ys.coroutinestudy.kotlin_coroutine_flow_master.getCurrentThreadName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

// `Confined`는 처음에는 부모의 스레드에서 수행됩니다. 하지만 한번 중단점(suspension point)에 오면 바뀌게 됩니다.

fun main() = runBlocking<Unit> {

	// `Confined` 는 중단점 이후 어느 디스패처에서 수행될지 예측하기 어렵습니다. 가능하면 확실한 디스패처를 사용합시다.
	async(Dispatchers.Unconfined) {
		println("Unconfined / ${getCurrentThreadName()}")
		delay(1000L)
		println("Unconfined / ${getCurrentThreadName()}")
	}
}