package com.ys.coroutinestudy.playground.fundamentals

import kotlin.concurrent.thread

fun main() {
	// Exception in thread "main" java.lang.OutOfMemoryError 발생
	repeat(1_000_000) {
		thread {
			Thread.sleep(5000)
			print(".")
		}
	}
}