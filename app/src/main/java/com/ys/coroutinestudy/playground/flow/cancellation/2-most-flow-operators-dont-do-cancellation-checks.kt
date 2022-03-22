package com.ys.coroutinestudy.playground.flow.cancellation

import com.ys.coroutinestudy.util.log
import kotlinx.coroutines.cancel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking

/**
 * 대부분의 다른 flow 연산자는 성능상의 이유로 자체적으로 추가 취소 확인을 수행하지 않습니다.
 */
fun main() = runBlocking {
	(1..5).asFlow()
		// ensureActive 로 현재 컨텍스트의 작업이 활성 상태인지 확인합니다.
		// .onEach { currentCoroutineContext().ensureActive() }
		.collect { value ->
		if (value == 3) cancel()
		log(value)
	}
}