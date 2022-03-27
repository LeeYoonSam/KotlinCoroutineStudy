package com.ys.coroutinestudy.playground.flow.sharedFlowAndStateFlow

import com.ys.coroutinestudy.util.log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

private fun replaySharedFlow(): Flow<Int> {
	val sharedFlow = MutableSharedFlow<Int>(replay = 2)

	GlobalScope.launch {
		repeat(5) {
			log("Emitted: $it")
			sharedFlow.emit(it)
			delay(500)
		}
	}

	return sharedFlow
}

fun main() = runBlocking<Unit> {
	val replaySharedFlow = replaySharedFlow()

	launch {
		replaySharedFlow.collect {
			log("First Collector collected: $it")
		}

		log("First Collector finished collection.")
	}

	launch {
		replaySharedFlow.collect {
			log("Second Collector collected: $it")
		}

		log("Second Collector finished collection.")
	}
}