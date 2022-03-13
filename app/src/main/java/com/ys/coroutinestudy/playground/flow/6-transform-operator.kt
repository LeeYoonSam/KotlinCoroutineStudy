package com.ys.coroutinestudy.playground.flow

import com.ys.coroutinestudy.util.log
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.runBlocking

private suspend fun performRequest(request: Int): String {
	delay(1000) // 오래걸리는 작업으로 가정
	return "response $request"
}

fun main() = runBlocking {
	(1..3).asFlow()
		.transform { request ->
			// emit 을 여러번 호출해도 하나의 flow 에 소속, 2개를 묶어서 방출(방출후에 다음 flow 진행)
			emit("Making request $request")
			emit(performRequest(request))
		}
		.collect { response -> log(response) }
}