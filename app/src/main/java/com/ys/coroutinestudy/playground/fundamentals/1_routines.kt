package com.ys.coroutinestudy.playground.fundamentals

fun main() {
	println("main starts")
	routine(1, 5000)
	routine(2, 3000)
	println("main ends")
}

fun routine(number: Int, delay: Long) {
	println("Routine $number starts work")
	Thread.sleep(delay)
	println("Routine $number has finished")
}