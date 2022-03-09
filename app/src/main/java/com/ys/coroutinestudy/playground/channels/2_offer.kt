package com.ys.coroutinestudy.playground.channels

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ClosedSendChannelException
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.onClosed
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.random.Random

fun main() = runBlocking {

	val getRandomIntsChannel = generateRandom()

	delay(2500)

	getRandomIntsChannel.consumeEach {
		// 제안이 있는 경우 5-6개의 제안된 항목만 소비합니다.
		println("Random number $it consumed")
	}

	println("Done!")
}

// 반환 유형은 ReceiveChannel입니다.
private fun CoroutineScope.generateRandom() = produce {
	repeat(10) {
		delay(500)

		// 누군가가 아이템을 받을 수 있을 때까지 플로우로 방출하거나 채널에서 보내기가 일시 중단됩니다.
		trySend(Random.nextInt())
			.onClosed { throw it ?: ClosedSendChannelException("Channel was closed normally") }
			.isSuccess

		// send 를 사용하면 전체다 방출
		// send(Random.nextInt())
	}
}