package com.ys.coroutinestudy.kotlin_coroutine_flow_master.basic.basic6_ceh_supervisor_job

import com.ys.coroutinestudy.kotlin_coroutine_flow_master.printCurrentThreadName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.random.Random

// 어디에도 속하지 않지만 원래부터 존재하는 전역 `GlobalScope`가 있습니다.
// 이 전역 스코프를 이용하면 코루틴을 쉽게 수행할 수 있습니다.
// 프로그래밍에서 전역 객체를 잘 사용하지 않는 것 처럼 `GlobalScope`도 잘 사용하지 않습니다.

private suspend fun printRandom() {
	delay(500L)
	printCurrentThreadName()
	println(Random.nextInt(0, 500))
}

fun main() = runBlocking {

	// `GlobalScope`는 어떤 계층에도 속하지 않고 영원히 동작하게 된다는 문제점이 있습니다.
	val job = GlobalScope.launch(Dispatchers.IO) {
		launch { printRandom() }
	}

	// `Thread.sleep(1000L)`를 쓴 까닭은 `main`이 `runBlocking`이 아니기 때문입니다. `delay` 메서드를 수행할 수 없습니다.
	Thread.sleep(1000L)
}