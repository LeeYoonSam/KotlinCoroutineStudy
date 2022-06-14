package com.ys.coroutinestudy.asynchronous_techniques

interface Token
class IdToken : Token
class EmptyToken : Token

data class Item(
	val id: Int,
	val name: String
)

data class Post(
	val token: Token,
	val item: Item
)

fun submitPost(token: Token, item: Item): Post {
	return Post(token, item)
}

fun processPost(post: Post) {
	println("processPost: $post")
}

fun submitPostAsync(token: Token, item: Item, callback: (Post) -> Unit) {
	callback(Post(token, item))
}