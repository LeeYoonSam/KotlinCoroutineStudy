package com.ys.coroutinestudy.playground.flow.multipleCollectors

import com.ys.coroutinestudy.util.log
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking

private fun simple(): Flow<Int> = flow {
	log("Flow started")
	for (i in 1..3) {
		delay(100)
		emit(i)
	}
}

fun main() = runBlocking {
	log("Calling simple function...")
	val flow = simple()
	log("Calling collect...")
	flow.collect { value -> log(value) }
	log("Calling collect again...")
	flow.collect { value -> log(value) }
}