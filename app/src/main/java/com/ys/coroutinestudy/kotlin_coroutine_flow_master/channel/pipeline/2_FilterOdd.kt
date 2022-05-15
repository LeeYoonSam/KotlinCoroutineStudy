package com.ys.coroutinestudy.kotlin_coroutine_flow_master.channel.pipeline

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.runBlocking

// 파이프라인을 응용해 홀수 필터를 만들어 봅시다.
// 여러개 채널을 순차적으로 붙여서 데이터를 가공 해나가면서 응용할 수 있습니다.

fun CoroutineScope.filterOdd(numbers: ReceiveChannel<Int>): ReceiveChannel<String> = produce {
	// produce 내에서만 send 를 할 수 있습니다.
	for (i in numbers) {
		if (i % 2 == 1) {
			send("${i}!")
		}
	}
}

fun main() = runBlocking {
	val numbers = produceNumbers()
	val oddNumbers = filterOdd(numbers)

	repeat(10) {
		println(oddNumbers.receive())
	}

	println("완료")
	coroutineContext.cancelChildren()
}