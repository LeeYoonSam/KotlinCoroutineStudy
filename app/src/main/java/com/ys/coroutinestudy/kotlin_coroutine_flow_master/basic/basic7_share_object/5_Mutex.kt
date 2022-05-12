package com.ys.coroutinestudy.kotlin_coroutine_flow_master.basic.basic7_share_object

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

val mutex = Mutex()
private var counter = 0

fun main() = runBlocking {
	withContext(Dispatchers.Default) {
		massiveRun {
			mutex.withLock {
				counter++
			}
		}
	}

	println("Counter = $counter")
}