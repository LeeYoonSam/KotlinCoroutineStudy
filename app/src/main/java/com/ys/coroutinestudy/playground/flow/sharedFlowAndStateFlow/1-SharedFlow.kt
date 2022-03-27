package com.ys.coroutinestudy.playground.flow.sharedFlowAndStateFlow

import com.ys.coroutinestudy.util.log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.random.Random

// return type은 Flow이므로 emit을 호출할 수 없습니다.
// SharedFlow를 ReturnType으로 정의할 수도 있습니다. 그러면 호출 사이트에서 이에 대해 emit()을 호출할 수 있습니다.
private fun sharedFlow():Flow<Int> {
	val sharedFlow = MutableSharedFlow<Int>()

	GlobalScope.launch(Dispatchers.Default) {
		repeat(3) {
			sharedFlow.emit(Random.nextInt(0, 50))
			delay(500)
		}
	}

	return sharedFlow
}

fun main() = runBlocking<Unit> {
	val sharedFlow = sharedFlow()

	// 각 수집기는 동일한 SharedFlow 를 구독하므로 동일한 번호를 얻습니다.

	launch {
		sharedFlow.collect {
			log("First collector: collected: $it")
		}

		log("First collector stopped")
	}

	launch {
		delay(600)

		// 현재 emit 된 데이터부터 수집
		sharedFlow.collect {
			log("Second collector: collected: $it")
		}

		log("Second collector stopped")
	}
}