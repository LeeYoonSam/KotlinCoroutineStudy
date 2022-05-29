package com.ys.coroutinestudy.coroutine_sample.sequence

val fibonacci = sequence {
	yield(1) // 처음 피보나치 숫자
	var cur = 1
	var next = 1
	while (true) {
		yield(next) // 다음 피보나치 숫자
		val tmp = cur + next
		cur = next
		next = tmp
	}
}

fun main() {
	println(fibonacci.take(10).joinToString())
}