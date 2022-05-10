package com.ys.coroutinestudy.kotlin_coroutine_flow_master.basic.basic4_suspending_function

import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.random.Random
import kotlin.system.measureTimeMillis

private suspend fun getRandom1() : Int {
	delay(1000L)
	return Random.nextInt(0, 500)
}

private suspend fun getRandom2() : Int {
	delay(1000L)
	return Random.nextInt(0, 500)
}

// async 를 이용해 동시 수행하기
fun main() = runBlocking {
	val elapsedTime = measureTimeMillis {

		// async 키워드를 사용하는 순간 코드 블록이 수행을 준비하는데 LAZY 로 인자를 전달하면 원하는 순간 수행을 준비하게 할 수 있습니다.
		val value1 = async(start = CoroutineStart.LAZY) { getRandom1() }
		val value2 = async(start = CoroutineStart.LAZY) { getRandom2() }

		// start 메서드를 통해서 원하는 순간에 수행
		value1.start() // 큐에 수행을 예약 한다.
		value2.start()

		// 수행 결과를 보면 getRandom1과 getRandom2를 같이 수행해서 경과시간이 반으로 줄어들었습니다.
		// await 을 호출하면 잠들었다가 깨어나서 코드를 처리하게 됩니다.
		println("${value1.await()} + ${value2.await()} = ${value1.await() + value2.await()}")
	}

	println(elapsedTime)
}