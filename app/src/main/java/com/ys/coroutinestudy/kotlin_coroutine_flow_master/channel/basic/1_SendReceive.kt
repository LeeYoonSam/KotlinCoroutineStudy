package com.ys.coroutinestudy.kotlin_coroutine_flow_master.channel.basic

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// 채널은 일종의 파이프입니다.
// 송신측에서 채널에 `send`로 데이터를 전달하고 수신 측에서 채널을 통해 `receive` 받습니다.
// (`trySend`와 `tryReceive`도 있습니다. 과거에는 `null`을 반환하는 `offer`와 `poll`가 있었습니다.)
// `trySend`, `tryReceive` 는 suspension point 가 없기 때문에 기다리지 않는 함수라고 볼 수 있고 특별한 경우에만 사용합니다.
fun main() = runBlocking {
	val channel = Channel<Int>() // Int 타입이 다니고 있는 파이프

	launch {
		for (x in 1..10) {
			// `Channel.send()` 에서 받는 사람이 없으면 잠이 들었다가 받은 이후에 깨어나서 다음 데이터를 보냅니다.
			channel.send(x) // suspension point
		}
	}

	repeat(10) {
		// `Channel.receive()` 데이터가 없는 경우에는 잠이 들었다가 데이터가 들어온 이후에 깨어나서 수행합니다.
		println(channel.receive()) // suspension point
	}

	// 계속해서 receive 를 하고 있으면 "완료" 를 출력하지 못하고 무한대기 상태로 빠진다.
	// 10개만 send 를 하고 close 를 한 상태에서 한번 더 receive 를 하면 ClosedReceiveChannelException 발생
	// println(channel.receive())

	println("완료")
}