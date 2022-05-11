package com.ys.coroutinestudy.kotlin_coroutine_flow_master.basic.basic6_ceh_supervisor_job

import com.ys.coroutinestudy.kotlin_coroutine_flow_master.printCurrentThreadName
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.random.Random

// 예외를 가장 체계적으로 다루는 방법은 CEH (Coroutine Exception Handler, 코루틴 익셉션 핸들러)를 사용하는 것입니다.
// `CoroutineExceptionHandler`를 이용해서 우리만의 CEH를 만든 다음 상위 코루틴 빌더의 컨텍스트에 등록합니다.

private suspend fun printRandom1() {
	delay(1000L)
	printCurrentThreadName()
	println(Random.nextInt(0, 500))
}

private suspend fun printRandom2() {
	delay(500L)
	throw ArithmeticException()
}

// `CoroutineExceptionHandler` 에 등록하는 람다에서 첫 인자는 `CoroutineContext` 두 번째 인자는 `Exception`입니다.
// 대부분의 경우에는 `Exception`만 사용하고 나머지는 `_`로 남겨둡니다.
private val ceh = CoroutineExceptionHandler { _, exception ->
	println("Something happend: $exception")
}

fun main() = runBlocking {

	val scope = CoroutineScope(Dispatchers.IO)
	val job = scope.launch(ceh) {
		launch { printRandom1() }
		launch { printRandom2() }
	}
	job.join()
}