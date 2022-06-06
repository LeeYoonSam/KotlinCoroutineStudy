package com.ys.coroutinestudy.coroutine_sample.channel

import android.annotation.SuppressLint
import com.ys.coroutinestudy.coroutine_sample.delay.delay
import com.ys.coroutinestudy.coroutine_sample.mutex.Mutex

// https://tour.golang.org/concurrency/9

/**
 * sync.Mutex
 *
 * 충돌을 피하기 위해 한 번에 하나의 고루틴만 변수에 접근할 수 있도록 하려면 어떻게 해야 할까요?
 * 이 개념을 상호 배제라고 하며 이를 제공하는 데이터 구조의 일반적인 이름은 뮤텍스입니다.
 *
 * Go의 표준 라이브러리는 sync.Mutex 및 두 가지 방법으로 상호 배제를 제공합니다.
 *  - Lock
 *  - UnLock
 */

class SafeCounter {
	private val v = mutableMapOf<String, Int>()
	private val mutex = Mutex()

	@SuppressLint("NewApi")
	suspend fun inc(key: String) {
		mutex.lock()
		try {
			v[key] = v.getOrDefault(key, 0) + 1
		} finally {
			mutex.unlock()
		}
	}

	suspend fun get(key: String): Int? {
		mutex.lock()
		return try {
			v[key]
		} finally {
			mutex.unlock()
		}
	}
}

fun main() = mainBlocking {
	val safeCounter = SafeCounter()
	val key = "somekey"

	for (i in 0..999) {
		go { safeCounter.inc(key) } // 1000 concurrent coroutines
	}
	delay(1000)
	println("${safeCounter.get(key)}")
}