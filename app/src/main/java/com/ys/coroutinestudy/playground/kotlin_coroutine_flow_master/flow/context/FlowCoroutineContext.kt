package com.ys.coroutinestudy.playground.kotlin_coroutine_flow_master.flow.context

import com.ys.coroutinestudy.playground.kotlin_coroutine_flow_master.log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun simple(): Flow<Int> = flow {
	log("flow를 시작합니다.")
	for (i in 1..10) {
		emit(i)
	}
}

/**
 * <Unit> 을 붙이지 않으면 실행이 안됩니다. main 은 Unit 을 return 해야 합니다.
 * launch 가 Job 을 반환하기 때문에 main 함수로 인식하지 않고 일반 함수로 인식하게 되어 runBlocking<Unit> 을 사용
 */
fun main() = runBlocking<Unit> {
	launch(Dispatchers.IO) {
		// 플로우는 현재 코루틴 컨텍스트에서 호출 -> Dispatchers.IO
		simple()
			.collect {
				log("${it}를 받음.")
			}
	}
}