package com.ys.coroutinestudy.asynchronous_techniques

// private fun postItem(item: Item) {
// 	preparePostAsync()
// 		.thenCompose { token ->
// 			submitPostAsyncFuture(token, item)
// 		}
// 		.thenAccept { post ->
// 			processPost(post)
// 		}
// }
//
// fun preparePostAsync(): Promise<Token> {
// 	// makes request and returns a promise that is completed later
// 	return promise
// }