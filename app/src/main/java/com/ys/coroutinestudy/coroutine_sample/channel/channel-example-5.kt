package com.ys.coroutinestudy.coroutine_sample.channel

// https://tour.golang.org/concurrency/5

/**
 * Select
 *
 * select 문을 사용하면 고루틴이 여러 통신 작업을 기다릴 수 있습니다.
 * 선택 A는 해당 사례 중 하나가 실행될 때까지 차단한 다음 해당 사례를 실행합니다.
 * 여러 개가 준비되어 있으면 무작위로 하나를 선택합니다.
 */

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