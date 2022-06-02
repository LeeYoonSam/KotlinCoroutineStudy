package com.ys.coroutinestudy.coroutine_sample.channel

private suspend fun sum(list: List<Int>, sendChannel: SendChannel<Int>) {
	var sum = 0
	for (value in list) {
		sum += value
	}
	sendChannel.send(sum)
}

fun main() = mainBlocking {
	val list = listOf(7, 2, 8, -9, 4, 0)
	val channel = Channel<Int>()
	go { sum(list.subList(list.size / 2, list.size), channel) }
	go { sum(list.subList(0, list.size / 2), channel) }
	val first = channel.receive()
	val second = channel.receive()
	println("$first $second ${first + second}")
}