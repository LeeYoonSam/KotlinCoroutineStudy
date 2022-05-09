package com.ys.coroutinestudy.kotlin_coroutine_flow_master.channel.buffering

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
	// 랑데뷰
	val channel = Channel<Int>(Channel.RENDEZVOUS) // 버퍼 갯수 0을 의미

	launch {
		for (sendMessage in 1..20) {
			println("$sendMessage 전송중")
			channel.send(sendMessage)
		}

		channel.close()
	}

	for (receivedMessage in channel) {
		println("$receivedMessage 수신")
		delay(100L)
	}

	println("완료")
}