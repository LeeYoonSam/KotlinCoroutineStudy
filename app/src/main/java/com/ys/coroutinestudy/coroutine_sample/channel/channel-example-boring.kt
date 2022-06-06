package com.ys.coroutinestudy.coroutine_sample.channel

import com.ys.coroutinestudy.coroutine_sample.delay.delay
import kotlin.random.Random

// https://talks.golang.org/2012/concurrency.slide#25

suspend fun boring(msg: String): ReceiveChannel<String> {
	val channel = Channel<String>()

	go {
		var i = 0
		while (true) {
			channel.send("$msg $i")
			delay(Random.nextInt(1000).toLong())
			i++
		}
	}

	return channel // 채널을 호출자에게 반환
}

// https://talks.golang.org/2012/concurrency.slide#26

fun main() = mainBlocking {
	val joe = boring("Joe")
	val ann = boring("Ann")
	for (i in 0..4) {
		println(joe.receive())
		println(ann.receive())
	}

	println("You're both boring; I'm leaving.")
}