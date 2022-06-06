package com.ys.coroutinestudy.coroutine_sample.mutex

import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.*
import kotlin.coroutines.resume

class Mutex {
	/*
		참고: 이것은 이해하기 쉽게 설계된 최적화되지 않은 구현이므로
		뮤텍스당 개체 수를 최적화하기 위해 이러한 데이터 구조를 바로 여기에 포함하는 대신
		AtomicInteger 및 ConcurrentLinkedQueue만 사용합니다.
	*/

	// -1 == unlocked, >= 0 -> number of active waiters
	private val state = AtomicInteger(-1)

	// state 에 등록된 것보다 더 많은 웨이터를 가질 수 있음(웨이터를 먼저 추가함)
	private val waiters = ConcurrentLinkedQueue<Waiter>()

	suspend fun lock() {
		// fast path -- 잠겨있을때 시도
		if (state.compareAndSet(-1, 0)) return

		// slow path -- 다른 유형
		return suspendCoroutineUninterceptedOrReturn sc@ { continuation ->
			// 잠그기 전에 잠정적으로 웨이터를 추가합니다(그래야 재개할 수 있습니다)
			val waiter = Waiter(continuation.intercepted())
			waiters.add(waiter)
			loop@ while (true) { // 잠금을 해제하는 루프
				val curState = state.get()
				if (curState == -1) {
					if (state.compareAndSet(-1, 0)) {
						// 이번에는 성공적으로 잠겼으며 다른 웨이터가 없습니다.
						// 간단하게 하기 위해 대기열에서 Waiter 개체의 연결을 해제하려고 시도하지 않고
						// 대기열에서 이미 재개된 것으로 표시합니다(retrieveWaiter는 표시된 항목을 건너뜁니다).
						waiter.resumed = true
						return@sc Unit // 일시 중지하지 말고 잠금으로 실행을 계속하십시오.
					}
				} else { // state >= 0 -- 이미 잠금상태 --> waiters 의 숫자를 증가시키고 다시 시작될때 까지 잠든.
					check(curState >= 0)
					if (state.compareAndSet(curState, curState + 1)) {
						break@loop
					}
				}
			}

			COROUTINE_SUSPENDED // suspend
		}
	}

	fun unlock() {
		while (true) {
			val curState = state.get()
			if (curState == 0) {
				// 뮤텍스를 보유하고 있고 mutex-holder만 웨이터의 수를 줄일 수 있기 때문에 이 상태에서 웨이터를 가질 수 없습니다.
				if (state.compareAndSet(0, -1))
					return // 성공적으로 해제, 다시 시작하는 웨이터가 없습니다.
			} else {
				check(curState >= 1)

				// 이제 웨이터 수를 줄이고 웨이터를 다시 시작합니다.
				if (state.weakCompareAndSet(curState, curState -1)) {
					// 웨이터가 있어야 합니다.
					retrieveWaiter()!!.c.resume(Unit)
					return
				}
			}
		}
	}

	private fun retrieveWaiter(): Waiter? {
		while (true) {
			val waiter = waiters.poll() ?: return null
			// 이것이 _actual_ 웨이터인지 확인하십시오(slow-path에서 실제로 잠금을 획득한 잔여물이 아님)
			if (!waiter.resumed)
				return waiter
		}
	}

	private class Waiter(val c: Continuation<Unit>) {
		var resumed = false
	}
}