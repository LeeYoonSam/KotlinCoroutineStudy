package com.ys.coroutinestudy.playground

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() = runBlocking { // this: CoroutineScope
	launch {
		delay(200L)
		println("Task from runBlocking")
	}

	coroutineScope { // 새로운 코루틴 범위를 생성합니다.
		launch {
			delay(900L)
			println("Task from nested launch")
		}

		delay(100L)
		println("Task from coroutine scope") // 이 행은 중첩 실행 전에 인쇄됩니다.
	}

	println("Coroutine scope is over") // 중첩 실행이 완료될 때까지 이 줄은 인쇄되지 않습니다.
}