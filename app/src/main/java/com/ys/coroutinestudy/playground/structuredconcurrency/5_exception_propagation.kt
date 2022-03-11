package com.ys.coroutinestudy.playground.structuredconcurrency

import com.ys.coroutinestudy.util.log
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

fun main() {

	// 작업 실패 처리
	val exceptionHandler = CoroutineExceptionHandler { _, exception ->
		log("Caught exception $exception")
	}

	// SupervisorJob 의 하위 항목은 서로 독립적으로 실패할 수 있습니다.
	val scope = CoroutineScope(SupervisorJob() + exceptionHandler)

	scope.launch {
		log("Coroutine 1 starts")
		delay(50)
		log("Coroutine 1 fails")

		// exceptionHandler 에서 에러 처리
		throw RuntimeException()
	}

	scope.launch {
		log("Coroutine 2 starts")
		delay(500)

		// Coroutine 1 에서 throw 가 발생했지만 SupervisorJob 으로 독립적으로 실패처리가 가능하기 때문에 문제없이 실행
		log("Coroutine 2 completed")
	}.invokeOnCompletion { throwable ->
		if (throwable is CancellationException) {
			log("Coroutine 2 got cancelled!")
		}
	}

	Thread.sleep(1000)

	log("Scope got cancelled: ${!scope.isActive}")
}