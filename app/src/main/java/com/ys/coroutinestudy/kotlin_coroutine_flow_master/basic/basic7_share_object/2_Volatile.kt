package com.ys.coroutinestudy.kotlin_coroutine_flow_master.basic.basic7_share_object

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

// 손 쉽게 생각할 수 있는 방법은 `volatile` 입니다.
@Volatile
private var counter = 0

fun main() = runBlocking {
	withContext(Dispatchers.Default) {
		massiveRun {
			counter++
		}
	}

	// volatile은 가시성 문제만을 해결할 뿐 동시에 읽고 수정해서 생기는 문제를 해결하지 못합니다.
	// 스레드에서 어떤값이 변경되면 현재값을 정확하게 볼수는 있지만 다른 스레드에서 동시에 값을 증가시킬때 문제가 발생
	println("Counter = $counter")
}