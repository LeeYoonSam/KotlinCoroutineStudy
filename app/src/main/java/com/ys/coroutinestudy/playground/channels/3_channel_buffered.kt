package com.ys.coroutinestudy.playground.channels

import com.ys.coroutinestudy.util.logWithThreadName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.random.Random

fun main() = runBlocking {

	// 64개 요소의 기본 크기 버퍼
	val channel = Channel<Int>(Channel.BUFFERED)

	val job1 = GlobalScope.launch(Dispatchers.Default) {
		repeat(3) {
			delay(500)
			channel.send(Random.nextInt(1, 1000))
		}

		// close 를 해주지 않으면 종료하지 않음
		channel.close()
	}

	val job2 = GlobalScope.launch {
		channel.consumeEach {
			logWithThreadName {
				println(it)
				println("Consumed")
			}
		}
	}

	joinAll(job1, job2)
}