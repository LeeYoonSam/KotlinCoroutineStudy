package com.ys.coroutinestudy.coroutine_sample.sequence.optimized

val fibonacci: Sequence<Int> = sequence {
	yield(1) // 첫번째 피보나치 숫자
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
	println(fibonacci)
	println(fibonacci.take(10).joinToString())
}