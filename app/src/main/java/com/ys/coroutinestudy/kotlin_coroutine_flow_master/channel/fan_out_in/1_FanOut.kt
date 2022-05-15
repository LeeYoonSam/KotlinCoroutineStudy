package com.ys.coroutinestudy.kotlin_coroutine_flow_master.channel.fan_out_in

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// 여러 코루틴이 동시에 채널을 구독할 수 있습니다.

fun CoroutineScope.produceNumbers() = produce {
	var x = 1
	while (true) {
		send(x++)
		delay(100L)
	}
}

fun CoroutineScope.processNumber(id: Int, channel: ReceiveChannel<Int>) = launch {
	channel.consumeEach {
		println("${id}가 ${it}을 받았습니다.")
	}
}

// 총 5개 (0~4)의 코루틴에서 각자 채널에서 값을 하나씩 가져가고 있고 한번 가져간 값은 다른 곳에서 가져갈수가 없습니다.
fun main() = runBlocking {
	val producer = produceNumbers()
	repeat(5) {
		processNumber(it, producer)
	}
	delay(1000L)
	producer.cancel()
}