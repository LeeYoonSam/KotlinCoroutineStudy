package com.ys.coroutinestudy.coroutine_sample.context

import kotlinx.coroutines.runBlocking
import kotlin.coroutines.coroutineContext

suspend fun doSomeThing() {
	val currentUser = coroutineContext[AuthUser]?.name ?: throw SecurityException("unauthorized")
	println("Current user is $currentUser")
}

fun main() {
	runBlocking(AuthUser("admin")) {
		doSomeThing()
	}
}