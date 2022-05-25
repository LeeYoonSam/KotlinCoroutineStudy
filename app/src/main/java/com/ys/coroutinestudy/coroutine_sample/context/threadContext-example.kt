package com.ys.coroutinestudy.coroutine_sample.context

import com.ys.coroutinestudy.coroutine_sample.future.await
import com.ys.coroutinestudy.coroutine_sample.future.future
import com.ys.coroutinestudy.coroutine_sample.util.log
import kotlinx.coroutines.delay

fun main() {
	log("Starting MyEventThread")
	val context = newSingleThreadContext("MyEventThread")
	val f = future(context) {
		log("Hello, world!")
		val f1 = future(context) {
			log("f1 is sleeping")
			delay(1000)
			log("f1 returns 1")
			1
		}
		val f2 = future(context) {
			log("f2 is sleeping")
			delay(1000)
			log("f2 returns 2")
			2
		}
		log("I'll wait for both f1 and f2. It should take just a second!")
		val sum = f1.await() + f2.await()
		log("And the sum is $sum")
	}
	f.get()
	log("Terminated")
}