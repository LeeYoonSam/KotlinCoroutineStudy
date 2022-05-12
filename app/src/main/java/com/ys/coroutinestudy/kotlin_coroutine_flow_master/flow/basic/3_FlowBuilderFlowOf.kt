package com.ys.coroutinestudy.kotlin_coroutine_flow_master.flow.basic

import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking

// flow 이외에도 몇가지 flowOf, asFlow등의 플로우 빌더가 있습니다. 먼저 flowOf를 살펴봅니다.
// flowOf는 여러 값을 인자로 전달해 플로우를 만듭니다.

fun main() = runBlocking {
	flowOf(1, 2, 3, 4, 5).collect {
		println(it)
	}
}