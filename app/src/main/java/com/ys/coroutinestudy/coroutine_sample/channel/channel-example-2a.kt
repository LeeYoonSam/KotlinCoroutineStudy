package com.ys.coroutinestudy.coroutine_sample.channel

import kotlin.system.measureTimeMillis

private suspend fun sum(list: List<Int>, sendChannel: SendChannel<Int>) {
	// 장기 실행 CPU 소모 계산 시뮬레이션
	var sum = 0
	val time = measureTimeMillis {
		repeat(100_000_000) {
			for (value in list) {
				sum += value
			}
		}
		sendChannel.send(sum)
	}
	println("Sum took $time ms")
}

fun main() = mainBlocking {
	val list = listOf(7, 2, 8, -9, 4, 0)
	val channel = Channel<Int>()
	go { sum(list.subList(list.size / 2, list.size), channel) }
	go { sum(list.subList(0, list.size / 2), channel) }
	val time = measureTimeMillis {
		val first = channel.receive()
		val second = channel.receive()
		println("$first $second ${first + second}")
	}
	println("Main code took $time ms")
}