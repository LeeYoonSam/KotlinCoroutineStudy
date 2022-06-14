package com.ys.coroutinestudy.asynchronous_techniques

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis

private suspend fun postItem(item: Item) {
	preparePostAsync { token ->
		submitPostAsync(token, item) { post ->
			processPost(post)
		}
	}
}

// 장기 실행 프로세스
private suspend fun preparePostAsync(callback: (Token) -> Unit) {
	delay(1000)
	callback(IdToken())
}

fun main() = runBlocking {
	val times = measureTimeMillis {
		postItem(Item(0, "Callbacks test"))
	}
	println("Callbacks: $times ms")
}

