package com.ys.coroutinestudy.coroutine_sample.channel

import com.ys.coroutinestudy.coroutine_sample.delay.delay

// https://tour.golang.org/concurrency/1

suspend fun say(s: String) {
	for (i in 0..4) {
		delay(100)
		println(s)
	}
}

fun main() = mainBlocking {
	go { say("world") }
	// say("world")
	say("hello")
}