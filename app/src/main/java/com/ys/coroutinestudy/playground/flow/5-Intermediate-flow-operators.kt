package com.ys.coroutinestudy.playground.flow

import com.ys.coroutinestudy.util.log
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

private suspend fun performRequest(request: Int): String {
	delay(1000) // 장기 실행 비동기 작업으로 가정
	return "response $request"
}

fun main() = runBlocking {
	(1..3).asFlow() // 요청의 흐름
		.map { request -> performRequest(request) }
		.collect { response -> log(response) }
}