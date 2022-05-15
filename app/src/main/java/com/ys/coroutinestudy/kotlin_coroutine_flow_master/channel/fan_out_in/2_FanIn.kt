package com.ys.coroutinestudy.kotlin_coroutine_flow_master.channel.fan_out_in

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// 팬 인은 반대로 생산자가 많은 것입니다.
// 데이터를 생산하는 코루틴이 여러 개이고, 이를 하나의 채널로 스트리밍되는 구조

suspend fun produceNumbers(channel: SendChannel<Int>, from: Int, interval: Long) {
	var x = from

	while (true) {
		channel.send(x)
		x += 2
		delay(interval)
	}
}

fun CoroutineScope.processNumber(channel: ReceiveChannel<Int>) = launch {
	channel.consumeEach {
		println("${it}을 받았습니다.")
	}
}

fun main() = runBlocking {
	val channel = Channel<Int>() // Channel = Receive Channel (receive) + Send Channel (send)
	launch {
		produceNumbers(channel, 1, 100L) // 1, 3, 5, 7, 9.. // 100ms 잠을자면서 채널에 값을 보냄
	}
	launch {
		produceNumbers(channel, 2, 150L) // 2, 4, 6, 8, 10.. // 150ms 잠을 생산면서 채널에 값을 보냄
	}

	// 생산자2, 소비자1
	processNumber(channel)
	delay(1000L)
	coroutineContext.cancelChildren()

	/**
	 * `coroutineContext`의 자식이 아닌 본인을 취소하면 어떻게 될까요?
	 *  -> kotlinx.coroutines.JobCancellationException: BlockingCoroutine was cancelled;
	 *      job="coroutine#1":BlockingCoroutine{Cancelled}@6adca536
 	 */
	// coroutineContext.cancel()

	/**
	 * `processNumber`를 suspend 함수의 형태로 변형하면 어떻게 될까요?
	 * -> 취소 되지 않고 무한으로 실행
 	 */
	// processNumberUsingSuspend(channel)

	// 다른 방법으로 취소할 수 있을까요?
}

// suspend fun processNumberUsingSuspend(channel: ReceiveChannel<Int>) = coroutineScope {
// 	channel.consumeEach {
// 		println("${it}을 받았습니다.")
// 	}
// }