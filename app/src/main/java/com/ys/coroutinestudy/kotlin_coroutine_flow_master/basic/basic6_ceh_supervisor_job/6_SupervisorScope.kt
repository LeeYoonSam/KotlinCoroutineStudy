package com.ys.coroutinestudy.kotlin_coroutine_flow_master.basic.basic6_ceh_supervisor_job

import com.ys.coroutinestudy.kotlin_coroutine_flow_master.printCurrentThreadName
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
import kotlin.random.Random

// 슈퍼 바이저 잡은 예외에 의한 취소를 아래쪽으로 내려가게 한다.
private suspend fun printRandom1() {
	delay(1000L)
	printCurrentThreadName()
	println(Random.nextInt(0, 500))
}

private suspend fun printRandom2(): Int {
	delay(500L)
	printCurrentThreadName()
	throw ArithmeticException()
}

// 슈퍼바이저 스코프를 사용할 때 주의점은 무조건 자식 수준에서 예외를 핸들링 해야한다는 것입니다.
// 자식의 실패가 부모에게 전달되지 않기 때문에 자식 수준에서 예외를 처리해야합니다.
suspend fun supervisordFunc() = supervisorScope {
	launch { printRandom1() }
	launch(ceh) { printRandom2() }
}

private val ceh = CoroutineExceptionHandler { _, exception ->
	println("SomeThing Happend: $exception")
}

fun main() = runBlocking {
	val scope = CoroutineScope(Dispatchers.IO)

	val job = scope.launch {
		supervisordFunc()
	}
	job.join()
}