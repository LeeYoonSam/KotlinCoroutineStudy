package com.ys.coroutinestudy.playground.flow

import com.ys.coroutinestudy.util.log
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {

	val numbers = (1..3).asFlow()
		.onEach { delay(300) }

	val strings = flowOf("one", "two", "three")
		.onEach { delay(400) }

	val startTime = System.currentTimeMillis() // 시작 시간을 기록
	numbers.combine(strings) { a, b -> "$a -> $b" } // combine 으로 단일 문자열 구성
		.collect { value ->
			log("$value at ${System.currentTimeMillis() - startTime} ms from start")
		}
}