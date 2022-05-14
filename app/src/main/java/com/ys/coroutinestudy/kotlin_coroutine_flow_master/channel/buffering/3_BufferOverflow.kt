package com.ys.coroutinestudy.kotlin_coroutine_flow_master.channel.buffering

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
	// 버퍼가 다 차면 잠이 들었다가 버퍼가 여유가 있을때 깨어납니다.
	bufferOverFlow(BufferOverflow.SUSPEND)

	delay(500L)

	// 버퍼에서 최신 데이터를 버리고 먼저 받은 데이터가 우선이 됩니다.
	bufferOverFlow(BufferOverflow.DROP_LATEST)

	delay(500L)

	// 버퍼에서 오래된 데이터를 버리고 최신 데이터가 우선이 됩니다.
	bufferOverFlow(BufferOverflow.DROP_OLDEST)
}

suspend fun bufferOverFlow(bufferOverflow: BufferOverflow) = coroutineScope {

	println(bufferOverflow.name)

	val channel = Channel<Int>(2, bufferOverflow)

	launch {
		for (sendMessage in 1..50) {
			println("$sendMessage 전송중")
			channel.send(sendMessage) // 받던 안받던 채널로 계속 전송
		}
		// 더이상 보낼 데이터가 없으면 close 처리(close 하지 않으면 무한 대기중)
		channel.close()
	}

	delay(500L)

	for (receivedMessage in channel) {
		println("$receivedMessage 수신")
		delay(100L)
	}

	println("${bufferOverflow.name} 완료")
}