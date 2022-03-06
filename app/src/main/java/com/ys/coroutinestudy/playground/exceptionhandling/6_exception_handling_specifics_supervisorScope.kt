package com.ys.coroutinestudy.playground.exceptionhandling

import com.ys.coroutinestudy.playground.fundamentals.coroutine
import com.ys.coroutinestudy.util.logWithThreadName
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

fun main() {
	val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
		logWithThreadName {
			println("Caught $throwable in CoroutineExceptionHandler")
		}
	}

	val scope = CoroutineScope(Job())

	scope.launch(coroutineExceptionHandler) {
		try {
			supervisorScope {
				launch {
					logWithThreadName {
						println("CoroutineExceptionHandler: ${coroutineContext[CoroutineExceptionHandler]}")
					}

					// 여기서 발생한 throw 는 coroutineExceptionHandler 로 처리
					throw RuntimeException()
				}
			}

			doSomeThingSuspendSuccess()

			// 여기서 발생한 throw 는 catch 구문에서 처리
			doSomeThingSuspend()

		} catch (e: Exception) {
			// 여기 catch 는 호출되지 않음
			logWithThreadName {
				println("Caught $e")
			}
		}
	}

	Thread.sleep(1000)
}

private suspend fun doSomeThingSuspendSuccess() {
	coroutineScope {
		launch {
			println("Success")
		}
	}
}

private suspend fun doSomeThingSuspend() {
	coroutineScope {
		launch {
			throw RuntimeException()
		}
	}
}