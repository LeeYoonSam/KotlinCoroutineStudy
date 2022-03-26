package com.ys.coroutinestudy.playground.flow.retry

import com.ys.coroutinestudy.util.log
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.runBlocking
import kotlin.random.Random

fun main() = runBlocking {
	getRandom()
		.retry(3)
		.collect {
			// 4회 방출, 초기 1회, retry 3회
			log("Collected: $it")
		}
}

private fun getRandom() = flow {
	repeat(3) {
		delay(100)
		emit(Random.nextInt())
		// 무조건 에러 발생
		check(it < 0)
	}
}