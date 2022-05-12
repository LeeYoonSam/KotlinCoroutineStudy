package com.ys.coroutinestudy.kotlin_coroutine_flow_master.basic.basic7_share_object

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.system.measureTimeMillis

suspend fun massiveRun(action: suspend () -> Unit) {
	val n = 100 // 시작할 코루틴의 갯수
	val k = 1000 // 코루틴 내에서 반복할 횟수
	val elapsed = measureTimeMillis {
		coroutineScope {
			repeat(n) {
				launch {
					repeat(k) { action() }
				}
			}
		}
	}
	println("$elapsed ms동안 ${n * k}개의 액션을 수행했습니다.")
}

private var counter = 0

fun main() = runBlocking {
	// `withContext`는 수행이 완료될 때 까지 기다리는 코루틴 빌더입니다.
	withContext(Dispatchers.Default) {
		massiveRun {
			counter++
		}
	}
	// 잠이 들었다 `withContext` 블록의 코드가 모두 수행되면 깨어나 호출됩니다.
	println("Counter = $counter")

	// 위의 코드는 불행히도 항상 100000이 되는 것은 아닙니다.
	// Dispatchers.Default에 의해 코루틴이 어떻게 할당되냐에 따라 값이 달라집니다.
}