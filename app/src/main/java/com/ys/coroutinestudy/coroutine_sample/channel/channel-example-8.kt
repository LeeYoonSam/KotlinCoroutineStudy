package com.ys.coroutinestudy.coroutine_sample.channel

// https://tour.golang.org/concurrency/7
// https://tour.golang.org/concurrency/8

/**
 * Equivalent Binary Trees (등가 이진 트리)
 */
const val treeSize = 10

suspend fun Tree.walk(name: String, sendChannel: SendChannel<Int>) {
	println("$name before left")
	left?.walk(name, sendChannel)
	println("$name before send: $value")
	sendChannel.send(value)
	println("$name before right")
	right?.walk(name, sendChannel)
}

suspend fun same(t1: Tree, t2: Tree): Boolean {
	val c1 = Channel<Int>()
	val c2 = Channel<Int>()
	go { t1.walk("t1", c1) }
	go { t2.walk("t2", c2) }
	var same = true
	for (i in 1..treeSize) {
		val v1 = c1.receive()
		val v2 = c2.receive()
		if (v1 != v2) same = false
	}

	return same
}

fun main() = mainBlocking {
	val t1 = newTree(1)
	val t2 = newTree(1)
	val t3 = newTree(2)
	println("t1 = $t1")
	println("t2 = $t2")
	println("t3 = $t3")
	println("t1 same as t2? ${same(t1, t2)}")
	println("t1 same as t3? ${same(t1, t3)}")
}

// https://github.com/golang/tour/blob/master/tree/tree.go

data class Tree(val value: Int, val left: Tree? = null, val right: Tree? = null)

fun Tree?.insert(v: Int): Tree {
	if (this == null) return Tree(v)
	return if (v < value) {
		Tree(value, left.insert(v), right)
	} else {
		Tree(value, left, right.insert(v))
	}
}

fun newTree(k: Int): Tree {
	var t: Tree? = null
	val list = (1..treeSize).toMutableList()
	list.shuffle()
	for (v in list) {
		t = t.insert(v * k)
	}

	return t!!
}
