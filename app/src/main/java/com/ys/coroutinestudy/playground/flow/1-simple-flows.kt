package com.ys.coroutinestudy.playground.flow

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// 동기적으로 계산된 값의 시퀀스
// 비동기적으로 계산된 값에 대한 흐름

// flow()는 사용할 코루틴을 설정하므로 직접 수행하는 것에 대해 걱정할 필요가 없습니다.
private fun simple(): Flow<Int> = flow { // flow builder
	for (i in 1..3) {
		delay(100) // 여기서 유용한 일을 한다고 가정
		emit(i) // 다음 값을 내보냅니다
	}
}

// 람다 식을 종료하면 flow 가 닫힌 것으로 간주됩니다.

fun main() = runBlocking {
	// 메인 스레드가 차단되었는지 확인하기 위해 동시에 코루틴을 시작합니다.
	launch {
		for (k in 1..3) {
			println("I'm not blocked $k")
			delay(100)
		}
	}
	// flow 수집
	simple().collect { value -> println(value) }
}
