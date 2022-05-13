package com.ys.coroutinestudy.kotlin_coroutine_flow_master.flow.exception

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking

private fun simple(): Flow<Int> = flow {
	for (i in 1..3) {
		println("Emitting $i")
		emit(i)
	}
}

// `catch` 연산자는 업스트림(catch 연산자를 쓰기 전의 코드)에만 영향을 미치고 다운스트림에는 영향을 미치지 않습니다.
// 이를 catch 투명성이라 합니다.
fun main() = runBlocking {
	simple()
		// `catch` 연산자의 `다운스트림(collect)` 에서 에러가 발생하므로 `catch` 에서 오류를 발견할 수 없습니다.
		.catch { e -> println("Caught $e") } // 다운스트림 예외를 잡지 못합니다.
		.collect {
			check(it <= 1) { "Collected $it" }
			println(it)
		}
}