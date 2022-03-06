package com.ys.coroutinestudy.util

fun logWithThreadName(block: () -> Unit) {
	block()
	println(Thread.currentThread().name)
}