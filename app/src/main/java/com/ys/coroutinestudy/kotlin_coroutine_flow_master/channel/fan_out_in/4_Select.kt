package com.ys.coroutinestudy.kotlin_coroutine_flow_master.channel.fan_out_in

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.selects.select

// Select 표현식을 사용하면 여러 일시 중단 기능을 동시에 기다리고 사용할 수 있는 첫 번째 기능을 선택할 수 있습니다.
// 먼저 끝나는 요청을 처리하는 것이 중요할 수 있습니다. 이 경우에 `select`를 쓸 수 있습니다.

// 리턴값이 리시브채널
fun CoroutineScope.savFast() = produce {
	// 코루틴 스코프 + 샌드채널
	while (true) {
		delay(100L)
		send("패스트")
	}
}

// 리턴값이 리시브채널
fun CoroutineScope.savCampus() = produce {
	// 코루틴 스코프 + 샌드채널
	while (true) {
		delay(150L)
		send("캠퍼스")
	}
}

fun main() = runBlocking {
	val fasts = savFast()
	val campuses = savCampus()

	repeat(5) { // 5번 동안 select
		select<Unit> { // 먼저 끝내는 것만 처리 하겠다.
			fasts.onReceive {
				println("fast: $it") // fast: 패스트
			}
			campuses.onReceive {
				println("campus: $it") // campus: 캠퍼스
			}
		}
	}

	/**
	 * 채널에 대해 `onReceive`를 사용하는 것 이외에도 아래의 상황에서 사용할 수 있습니다.
	 * - `Job` - `onJoin`
	 * - `Deferred` - `onAwait`
	 * - `SendChannel` - `onSend`
	 * - `ReceiveChannel` - `onReceive`, `onReceiveCatching`
	 * - `delay` - `onTimeout`
	 */

	coroutineContext.cancelChildren()
}