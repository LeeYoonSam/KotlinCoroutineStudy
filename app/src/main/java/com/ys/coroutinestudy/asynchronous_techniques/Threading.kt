package com.ys.coroutinestudy.asynchronous_techniques

import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis

private fun postItem(item: Item) {
	val token = preparePost()
	val post = submitPost(token, item)
	processPost(post)
}

// 장기 실행 프로세스, 사용자 인터페이스 차단 -> 별도의 스레드에서 실행
private fun preparePost(): Token {
	Thread.sleep(1000)
	return IdToken()
}

fun main() = runBlocking {
	val times = measureTimeMillis {
		postItem(Item(0, "Threading test"))
	}
	println("Threading: $times ms")
}