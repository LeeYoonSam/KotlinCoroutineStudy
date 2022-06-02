package com.ys.coroutinestudy.coroutine_sample.channel

// https://tour.golang.org/concurrency/3

fun main() = mainBlocking {
	val channel = Channel<Int>(2)
	channel.send(1)
	channel.send(2)
	println(channel.receive())
	println(channel.receive())
}