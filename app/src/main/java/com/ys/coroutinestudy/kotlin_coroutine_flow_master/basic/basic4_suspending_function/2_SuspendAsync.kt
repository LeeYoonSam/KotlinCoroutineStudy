package com.ys.coroutinestudy.kotlin_coroutine_flow_master.basic.basic4_suspending_function

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
		/**
		 * - async 키워드를 사용하면 동시에 다른 블록을 수행할 수 있습니다.
		 * - launch 와 비슷하게 보이지만 수행 결과를 await 키워드를 통해 받을 수 있다는 차이가 있습니다.
		 * - await 키워드를 만나면 async 블록이 수행이 끝났는지 확인하고 아직 끝나지 않았다면
		 *  suspend 되었다 나중에 다시 깨어나고 반환값을 받아옵니다.
		 * - await 는 job.join() + 결과도 가져온다고 볼 수 있습니다.
		 */
		val value1 = async { getRandom1() }
		val value2 = async { getRandom2() }

		// 수행 결과를 보면 getRandom1과 getRandom2를 같이 수행해서 경과시간이 반으로 줄어들었습니다.
		// await 을 호출하면 잠들었다가 깨어나서 코드를 처리하게 됩니다.
		println("${value1.await()} + ${value2.await()} = ${value1.await() + value2.await()}")
	}

	println(elapsedTime)
}