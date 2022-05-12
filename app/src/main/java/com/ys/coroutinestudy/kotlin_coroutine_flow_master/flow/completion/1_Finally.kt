package com.ys.coroutinestudy.kotlin_coroutine_flow_master.flow.completion

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking

private fun simple(): Flow<Int> = (1..3).asFlow()

// 완료를 처리하는 방법 중의 하나는 명령형의 방식으로 finally 블록을 이용하는 것입니다.
fun main() = runBlocking {
	try {
		simple().collect {
			println(it)
		}
	} finally {
		println("Done")
	}
}