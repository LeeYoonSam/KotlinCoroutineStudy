package com.ys.coroutinestudy.kotlin_coroutine_flow_master.flow.exception

import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

// 빌더 코드 블록 내에서 예외를 처리하는 것은 예외 투명성을 어기는 것입니다.
// 플로우에서는 `catch` 연산자를 이용하는 것을 권합니다.
// `catch` 블록에서 예외를 새로운 데이터로 만들어 `emit`을 하거나, 다시 예외를 던지거나, 로그를 남길 수 있습니다.

private fun simple(): kotlinx.coroutines.flow.Flow<String> = flow {
	for (i in 1..3) {
		println("Emitting $i")
		emit(i) // emit next value
	}
}.map {
	check(it <= 1) { "Crashed on $it" }
	"string $it"
}

fun main() = runBlocking {
	simple()
		.catch { e -> emit("Caught $e") } // emit on exception
		.collect { println(it) }

}