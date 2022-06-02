package com.ys.coroutinestudy.coroutine_sample.channel

// https://tour.golang.org/concurrency/4

suspend fun fibonacci(n: Int, sendChannel: SendChannel<Int>) {
	var x = 0
	var y = 1
	for (i in 0 until n) {
		sendChannel.send(x)
		val next = x + y
		x = y
		y = next
	}
	sendChannel.close()
}

fun main() = mainBlocking {
	val channel = Channel<Int>(2)
	go { fibonacci(10, channel) }
	for (i in channel) {
		println(i)
	}
}