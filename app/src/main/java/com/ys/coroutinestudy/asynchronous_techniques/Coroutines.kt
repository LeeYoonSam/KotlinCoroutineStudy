package com.ys.coroutinestudy.asynchronous_techniques

import com.ys.coroutinestudy.coroutine_sample.run.launch
import kotlin.coroutines.suspendCoroutine

private fun postItem(item: Item) {
	launch {
		val token = preparePost()
		val post = submitPost(token, item)
		processPost(post)
	}
}

private suspend fun preparePost(): Token {
	// makes a request and suspends the coroutine
	return suspendCoroutine { /* ... */ }
}