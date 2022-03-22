package com.ys.coroutinestudy.playground.flow.cancellation

import com.ys.coroutinestudy.util.log
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking

/**
 * .onEach { currentCoroutineContext().ensureActive() }를
 * 추가할 수 있지만 이를 위해 제공되는 취소 가능한 연산자를 사용할 준비가 되어 있습니다.
 */
fun main() = runBlocking {
	(1..5).asFlow()
		.cancellable() // 취소 가능한 연산자를 사용할 준비가 되었습니다.
		.collect { value ->
			if (value == 3) cancel()
			log(value)
		}
}