package com.ys.coroutinestudy.kotlin_coroutine_flow_master.flow.basic

import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking

// asFlow는 컬렉션이나 시퀀스를 전달해 플로우를 만들 수 있습니다.
fun main() = runBlocking {
	listOf(1, 2, 3, 4, 5).asFlow().collect {
		println(it)
	}

	(6..10).asFlow().collect {
		println(it)
	}
}