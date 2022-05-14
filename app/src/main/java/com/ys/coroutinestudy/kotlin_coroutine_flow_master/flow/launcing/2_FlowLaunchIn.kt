package com.ys.coroutinestudy.kotlin_coroutine_flow_master.flow.launcing

import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking

// `launchIn`을 이용하면 별도의 코루틴에서 플로우를 런칭할 수 있습니다.

fun main() = runBlocking {

	// 계속 이벤트가 발생해서 추적을 하면서 대응하려면 `launchIn` 을 통해서 다른 코루틴에서 우리 이벤트를 감시할 것을 동작을 시켜야 하는것
	// 이벤트와 같이 상태가 바뀌는것을 추적할 때는 launchIn 을 통해서 다른 코루틴에서 관측하는 것이 더 유리합니다.
	events()
		.onEach { event -> println("Event: $event") }
		.launchIn(this)

	println("Done")
}