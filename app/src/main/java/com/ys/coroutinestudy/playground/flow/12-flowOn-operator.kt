package com.ys.coroutinestudy.playground.flow

import com.ys.coroutinestudy.util.log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.runBlocking

private fun simple(): Flow<Int> = flow {
    for (i in 1..3) {
        Thread.sleep(100) // CPU를 소비하는 방식으로 계산한다고 가정합니다.
        log("Emitting $i")
        emit(i) // 다음 값을 내보냅니다
    }
}.flowOn(Dispatchers.Default) // flow 빌더에서 CPU 소비 코드에 대한 컨텍스트를 변경하는 올바른 방법

// flowOn은 컨텍스트에서 CoroutineDispatcher를 변경해야 할 때 업스트림 flow에 대한 또 다른 코루틴을 생성합니다.

// flow 는 {} 백그라운드 스레드에서 작동하는 반면 수집은 기본 스레드에서 발생합니다.

// 여기서 관찰해야 할 또 다른 사항은 flowOn 연산자가 flow의 기본 순차 특성을 변경했다는 것입니다.
// 이제 하나의 코루틴에서 수집이 발생하고 수집 코루틴과 동시에 다른 스레드에서 실행 중인 다른 코루틴에서 방출이 발생합니다.

fun main() = runBlocking<Unit> {
    simple().collect { value ->
        log("Collected $value")
    }
}