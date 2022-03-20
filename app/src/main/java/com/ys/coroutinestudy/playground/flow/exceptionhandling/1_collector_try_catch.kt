package com.ys.coroutinestudy.playground.flow.exceptionhandling

import com.ys.coroutinestudy.util.log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking

private fun simple(): Flow<Int> = flow {
	for (i in 1..3) {
		log("Emitting $i")
		emit(i)
	}
}

fun main() = runBlocking {
	try {
		simple().collect { value ->
			log(value)
			check(value <= 1) { "Collected $value" }
		}
	} catch (e: Exception) {
		log("Caught $e")
	}
}