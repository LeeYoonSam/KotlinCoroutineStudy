package com.ys.coroutinestudy.coroutine_sample.channel

// https://talks.golang.org/2012/concurrency.slide#27

suspend fun fanIn(input1: ReceiveChannel<String>, input2: ReceiveChannel<String>): ReceiveChannel<String> {
	val channel = Channel<String>()

	go {
		for (v in input1) {
			channel.send(v)
		}
	}

	go {
		for (v in input2) {
			channel.send(v)
		}
	}

	return channel
}

/**
 * Multiplexing(다중화)
 * 이러한 프로그램을 통해 Joe와 Ann은 긴밀하게 협력합니다.
 * 대신 팬인 기능을 사용하여 준비된 사람이 말할 수 있도록 할 수 있습니다.
 */
fun main() = mainBlocking {
	val channel = fanIn(boring("Joe"), boring("Ann"))

	repeat(10) {
		println(channel.receive())
	}

	println("You're both boring; I'm leaving.")
}