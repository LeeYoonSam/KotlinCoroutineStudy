package com.ys.coroutinestudy.coroutine_sample.channel

import com.ys.coroutinestudy.coroutine_sample.delay.delay

// https://tour.golang.org/concurrency/6

// Default Selection
fun main() = mainBlocking {
	val tick = Time.tick(100)
	val boom = Time.after(500)
	whileSelect {
		tick.onReceive {
			println("tick.")
			true // 루프 계속 실행
		}

		boom.onReceive {
			println("BOOM!")
			false // 루프 종료
		}

		onDefault {
			println("    .")
			delay(50)
			true // 루프 계속 실행
		}
	}
}