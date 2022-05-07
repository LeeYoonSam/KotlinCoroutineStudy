package com.ys.coroutinestudy.playground.kotlin_coroutine_flow_master.flow.buffering

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis

// buffer() 를 추가해서 보내는 측이 더 이상 기다리지 않게 합니다.
// flow 에 buffer 를 붙이면 collect 준비 유무에 관계없이 계속 데이터를 보낼 수 있기 때문에 전체적인 지연시간을 줄일 수 있다.
// simple 에서 첫 delay(100 ms)가 호출되고
fun main() = runBlocking {
	val time = measureTimeMillis {
		simple()
			.buffer()
			.collect{
				delay(300L)
				println(it)
			}
	}

	println("Collected in $time ms")
}