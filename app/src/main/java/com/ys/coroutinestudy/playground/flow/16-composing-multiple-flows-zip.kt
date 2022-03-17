package com.ys.coroutinestudy.playground.flow

import com.ys.coroutinestudy.util.log
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {

	val numbers = (1..3).asFlow()
	val strings = flowOf("one", "two", "three")
	numbers.zip(strings) { a, b -> "$a -> $b"} // 단일 문자로 구성
		.collect { log(it) } // 수집 및 인쇄
}