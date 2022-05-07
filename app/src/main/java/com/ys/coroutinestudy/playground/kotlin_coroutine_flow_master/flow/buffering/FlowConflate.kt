package com.ys.coroutinestudy.playground.kotlin_coroutine_flow_master.flow.buffering

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis

fun main() = runBlocking {
	val time = measureTimeMillis {
		simple().conflate()
			.collect {
				delay(300L)
				println(it)
			}
	}

	println("Collected in $time ms")
}