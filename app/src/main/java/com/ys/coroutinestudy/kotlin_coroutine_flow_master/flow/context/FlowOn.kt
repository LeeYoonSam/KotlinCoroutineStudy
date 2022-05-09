package com.ys.coroutinestudy.kotlin_coroutine_flow_master.flow.context

import com.ys.coroutinestudy.util.log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// flowOn 연산자를 통해 컨텍스트를 올바르게 변경 할 수 있습니다.
// 업스트림 대상을 어떤 컨텍스트에서 호출할지 결정 합니다.
fun simple3(): Flow<Int> = flow {
	for (i in 1..10) {
		delay(100L)
		log("값 ${i}를 emit 합니다.")
		emit(i)
	} // 업스트림
}.flowOn(Dispatchers.Default) // 위치 (업스트림을 Dispatchers.Default 로 실행)

fun main() = runBlocking<Unit> {
	launch {
		simple3()
			.collect { // 다운스트림
				log("${it}를 받음")
			}
	}
}