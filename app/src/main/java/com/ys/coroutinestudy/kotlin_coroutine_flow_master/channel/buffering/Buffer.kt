package com.ys.coroutinestudy.kotlin_coroutine_flow_master.channel.buffering

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
	val channel = Channel<Int>(10) // 채널의 버퍼 갯수
	launch {
		for (sendMessage in 1..20) {
			println("$sendMessage 전송중")
			channel.send(sendMessage) // 받던 안받던 채널로 계속 전송
		}
		// 더이상 보낼 데이터가 없으면 close 처리(close 하지 않으면 무한 대기중)
		channel.close()
	}

	for (receivedMessage in channel) {
		println("$receivedMessage 수신")
		delay(100L)
	}

	println("완료")
}