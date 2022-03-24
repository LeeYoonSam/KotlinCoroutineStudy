package com.ys.coroutinestudy.playground.flow.imperativeVsdeclarative

import com.ys.coroutinestudy.util.log
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking

private fun events(): Flow<Int> = (1..3).asFlow().onEach { delay(100) }

fun main() = runBlocking {
	events()
		.onEach { event -> log("Event: $event") }
		.collect() // <--- flow 대기 수집

	log("Done")
}