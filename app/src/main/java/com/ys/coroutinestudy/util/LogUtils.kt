package com.ys.coroutinestudy.util

fun logWithThreadName(block: () -> Unit) {
	block()
	println("Current Thread: ${Thread.currentThread().name}")
}