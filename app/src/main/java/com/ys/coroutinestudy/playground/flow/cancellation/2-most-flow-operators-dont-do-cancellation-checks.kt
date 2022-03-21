package com.ys.coroutinestudy.playground.flow.cancellation

import com.ys.coroutinestudy.util.log
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking

/**
 * 대부분의 다른 flow 연산자는 성능상의 이유로 자체적으로 추가 취소 확인을 수행하지 않습니다.
 */
fun main() = runBlocking {
	(1..5).asFlow().collect { value ->
		if (value == 3) cancel()
		log(value)
	}
}