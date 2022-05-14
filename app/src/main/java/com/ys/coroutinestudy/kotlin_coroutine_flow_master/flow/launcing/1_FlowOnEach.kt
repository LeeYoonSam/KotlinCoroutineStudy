package com.ys.coroutinestudy.kotlin_coroutine_flow_master.flow.launcing

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking

// `addEventListener` 대신 플로우의 `onEach`를 사용할 수 있습니다.
// 이벤트마다 `onEach`가 대응하는 것입니다.

fun events(): Flow<Int> = (1..3).asFlow().onEach { delay(100L) }

fun main() = runBlocking {

	// `collect`가 `flow`가 끝날 때 까지 기다리는 것이 문제입니다.

	events()
		.onEach { event -> println("Event: $event") }
		.collect() // 스트림이 끝날 때 까지 기다리게 됩니다. 이벤트는 계속 발생

	println("Done")
}
