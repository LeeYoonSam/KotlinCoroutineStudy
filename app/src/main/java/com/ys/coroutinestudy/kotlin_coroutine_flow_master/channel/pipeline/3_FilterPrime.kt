package com.ys.coroutinestudy.kotlin_coroutine_flow_master.channel.pipeline

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.runBlocking

// 파이프라인을 연속으로 타면서 원하는 결과를 얻을 수 있습니다.

fun CoroutineScope.numbersFrom(start: Int) = produce { // 샌드채널 + CoroutineScope
	var x = start
	while (true) {
		send(x++)
	}
}

fun CoroutineScope.filter(numbers: ReceiveChannel<Int>, prime: Int): ReceiveChannel<Int> = produce {
	for (i in numbers) {
		if (i % prime != 0) {
			send(i)
		}
	}
}

// 원한다면 디스패처를 이용해 CPU 자원을 효율적으로 이용하는 것이 가능합니다.
// 이런식으로도 파이프라인을 확장할수 있다는 의미로 받아 들여지면 좋을것 같습니다.

fun main() = runBlocking {
	var numbers = numbersFrom(2) // 리시브 채널 / 3, 4, 5 / loop가 돌때마다 채널 대체

	repeat(10) {
		val prime = numbers.receive() // 2
		println(prime)
		numbers = filter(numbers, prime) // numbers 3, 4, 5, prime 2
	}

	println("완료")
	coroutineContext.cancelChildren()
}