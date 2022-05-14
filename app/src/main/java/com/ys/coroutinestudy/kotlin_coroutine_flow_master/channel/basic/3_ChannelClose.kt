package com.ys.coroutinestudy.kotlin_coroutine_flow_master.channel.basic

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// 채널에서 더 이상 보낼 자료가 없으면 `close` 메서드를 이용해 채널을 닫을 수 있습니다.
// 채널은 for in 을 이용해서 반복적으로 `receive` 할 수 있고 `close` 되면 for in은 자동으로 종료됩니다.

fun main() = runBlocking {
	val channel = Channel<Int>()

	launch {
		for (x in 1..10) {
			channel.send(x)
		}
		channel.close()
	}

	println(channel) // RendezvousChannel@4c98385c{EmptyQueue}

	// `channel.receive()` 로 받지 않고 `for 문`으로 받아서 처리를 할 수 있습니다.
	// 채널이 `close` 가 되어있지 않은 상태에서 `for 문`을 사용하면 무한 대기 상태가 됩니다.
	// `close` 가 되는것을 정확하게 알고 있을때 사용해야 합니다.
	for (x in channel) {
		println(x)
	}

	println(channel) // RendezvousChannel@200a570f{Closed@16b3fc9e[null]}

	println("완료")
}