package com.ys.coroutinestudy.playground.flow.exceptionhandling

import com.ys.coroutinestudy.util.log
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking
import java.lang.IllegalStateException

fun main() = runBlocking<Unit> {

	// launchIn은 새로운 코루틴을 생성하므로 CoroutineExceptionHandler 사용하여 예외를 처리할 수 있다.
	val coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
		log("CoroutineExceptionHandler Caught: $throwable")
	}

	val someScope = CoroutineScope(Dispatchers.Default + coroutineExceptionHandler)

	var job: Job? = null

	try {
		job = someFlow.onEach {
			log("onEach: $it")
		}.launchIn(someScope) // launchIn은 새로운 코루틴을 생성하므로 try-catch를 사용하여 예외를 처리할 수 없습니다.
	} catch (e: Exception) {
		log("Caught: $e")
	}

	job?.join()
}

private val someFlow = flow {
	emit(1)
	emit(2)
	throw IllegalStateException()
}