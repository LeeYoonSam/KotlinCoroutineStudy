package com.ys.coroutinestudy.playground.flow

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

// flow 는 항상 수집된 컨텍스트에서 방출해야 합니다.

private fun simple(): Flow<Int> = flow {
    // flow 빌더에서 CPU 소비 코드에 대한 컨텍스트를 변경하는 잘못된 방법
    withContext(Dispatchers.Default) {
        for (i in 1..3) {
            Thread.sleep(100) // CPU를 소비하는 방식으로 계산한다고 가정합니다.
            emit(i) // 다음 값을 내보냅니다
        }
    }
}

/**
 * Exception 발생
 *
 * Exception in thread "main" java.lang.IllegalStateException:
 * Flow invariant is violated: Flow was collected in [BlockingCoroutine{Active}@1c676ebb, BlockingEventLoop@4d1018a9],
 * but emission happened in [DispatchedCoroutine{Active}@128df400, Dispatchers.Default].
 * Please refer to 'flow' documentation or use 'flowOn' instead
 */
fun main() = runBlocking<Unit> {
    simple().collect { value -> println(value) }
}