package com.ys.coroutinestudy.suspendingSequence

import com.ys.coroutinestudy.coroutine_sample.context.newSingleThreadContext
import com.ys.coroutinestudy.coroutine_sample.delay.delay
import com.ys.coroutinestudy.coroutine_sample.run.runBlocking
import com.ys.coroutinestudy.util.log
import java.util.Random

fun main() {
	val context = newSingleThreadContext("MyThread")

	runBlocking(context) {
		// 비동기적으로 500ms 마다 숫자를 생성
		val seq = suspendingSequence(context) {
			log("Starting generator")
			for (i in 1..10) {
				log("Generator yields $i")
				yield(i)
				val generatorSleep = 500L
				log("Generator goes to sleep for $generatorSleep ms")
				delay(generatorSleep)
			}
			log("Generator is done")
		}

		// sleep 시간을 무작위로 정해서 비동기 시뮬레이션
		val random = Random()

		// 일반적인 for 루프로 비동기 시퀀스 사용
		for(value in seq) {
			log("Consumer got value = $value")
			val consumerSleep = random.nextInt(1000).toLong()
			log("Consumer goes to sleep for $consumerSleep ms")
			delay(consumerSleep)
		}
	}
}