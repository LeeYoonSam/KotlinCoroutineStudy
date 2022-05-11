package com.ys.coroutinestudy.kotlin_coroutine_flow_master.basic.basic6_ceh_supervisor_job

import com.ys.coroutinestudy.kotlin_coroutine_flow_master.printCurrentThreadName
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.random.Random

// `runBlocking`에서는 CEH를 사용할 수 없습니다.
// `runBlocking`은 자식이 예외로 종료되면 항상 종료되고 CEH를 호출하지 않습니다.

private suspend fun printRandom1(): Int {
	delay(1000L)
	printCurrentThreadName()
	return Random.nextInt(0, 500)
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
	val job = launch (ceh) {
		val a = async { printRandom1() }
		val b = async { printRandom2() }
		println(a.await())
		println(b.await())
	}
	job.join()
}