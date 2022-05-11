package com.ys.coroutinestudy.kotlin_coroutine_flow_master.basic.basic5_context_dispatchers

import com.ys.coroutinestudy.kotlin_coroutine_flow_master.getCurrentThreadName
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis

fun main() = runBlocking {
	val elapsed = measureTimeMillis {
		val job = launch { // 부모
			launch { // 자식
				println("launch1: ${getCurrentThreadName()}")
				delay(5000L)
			}

			launch { // 자식 2
				println("launch2: ${getCurrentThreadName()}")
				delay(10L)
			}
		}

		// 부모를 `join` 해서 기다려 보면 부모는 두 자식이 모두 끝날 때까지 기다린다는 것을 알 수 있습니다.
		job.join()
	}

	println(elapsed)
}