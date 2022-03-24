package com.ys.coroutinestudy.playground.flow.completion

import com.ys.coroutinestudy.util.log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.runBlocking

private fun simple(): Flow<Int> = (1..3).asFlow()

fun main() = runBlocking {
	simple()
		.onCompletion { cause -> log("Flow completed with $cause") }
		.collect { value ->
			check(value <= 1) { "Collected $value" }
			log(value)
		}
}