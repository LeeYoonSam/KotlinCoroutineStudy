package com.ys.coroutinestudy.coroutine_sample.channel

// https://tour.golang.org/concurrency/5

suspend fun fibonacci(sendChannel: SendChannel<Int>, quit: ReceiveChannel<Int>) {
	var x = 0
	var y = 1

	whileSelect {
		sendChannel.onSend(x) {
			val next = x + y
			x = y
			y = next
			true // while 루프 계속 진행
		}

		quit.onReceive {
			println("quit")
			false // while 루프 종료
		}
	}
}

fun main() = mainBlocking {
	val channel = Channel<Int>(2)
	val quitChannel = Channel<Int>(2)

	go {
		for (i in 0..9)
			println(channel.receive())

		quitChannel.send(0)
	}

	fibonacci(channel, quitChannel)
}