package com.ys.coroutinestudy.playground.flow.retry

import com.ys.coroutinestudy.util.log
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.runBlocking
import kotlin.random.Random

fun main() = runBlocking {
	simple()
		.retryWhen { _: Throwable, attempt: Long ->

			// attempt 0부터 시작, 실패시 attempt 증가
			log("attempt: $attempt")

			// 람다 식이 true를 반환하면 흐름이 재시도됩니다.
			// 람다 표현식이 false를 반환하면 예외가 수집기로 진행됩니다.
			attempt < 2

			// 특정 예외에 대해서만 재시도 할 수 있습니다.
		}
		.collect {
			// 3회 방출 - 초기 1회, 2회 retry
			log("Collected: $it")

			// retry 가 되면 처음부터 다시 방출
		}
}

private fun simple() = flow {
	for (i in 1..3) {
		delay(100)
		emit(i)
		// 무조건 에러 발생
		check(i < 0)
	}
}