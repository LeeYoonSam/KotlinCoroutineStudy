package com.ys.coroutinestudy.kotlin_coroutine_flow_master.channel.basic

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// `send`나 `receive`가 suspension point이고 서로에게 의존적이기 때문에 같은 코루틴에서 사용하는 것은 위험할 수 있습니다.

fun main() = runBlocking<Unit> {
	val channel = Channel<Int>()

	// 무한으로 대기하는 것을 볼 수 있습니다.
	// 현재 코드의 `launch` 에 `send` 와 `receive` 가 있는데
	// 만약 `send` 에서 수신자가 없으면 `launch` 블럭 자체가 잠이 들기 때문에
	// `receive` 를 실행할수 없고 반대의 케이스도 마찬가지입니다.
	// 별도의 코루틴을 만들어서 서로 잠이 들어서 문제가 없도록 코드를 작성해야 합니다.
	launch {
		for (x in 1..10) {
			channel.send(x)
		}

		repeat(10) {
			println(channel.receive())
		}

		println("완료")
	}
}