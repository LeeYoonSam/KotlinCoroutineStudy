package com.ys.coroutinestudy.util

fun logWithThreadName(block: () -> Unit) {
	block()
	println("|---- Current Thread: ${Thread.currentThread().name} ---|")
}

fun log(msg: String) = println("[${Thread.currentThread().name}] $msg")