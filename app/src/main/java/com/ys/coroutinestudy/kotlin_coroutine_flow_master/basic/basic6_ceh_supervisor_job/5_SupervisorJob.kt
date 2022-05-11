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

private val ceh = CoroutineExceptionHandler { _, exception ->
	println("SomeThing Happend: $exception")
}

fun main() = runBlocking {
	val scope = CoroutineScope(Dispatchers.IO + SupervisorJob() + ceh)

	// `printRandom2`가 실패했지만 `printRandom1`은 제대로 수행된다.
	val job1 = scope.launch { printRandom1() }
	val job2 = scope.launch { printRandom2() }

	// `joinAll`은 복수개의 `Job`에 대해 `join`를 수행하여 완전히 종료될 때까지 기다린다.
	joinAll(job1, job2)
}