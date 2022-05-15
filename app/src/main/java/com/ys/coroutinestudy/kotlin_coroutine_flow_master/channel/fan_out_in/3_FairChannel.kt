package com.ys.coroutinestudy.kotlin_coroutine_flow_master.channel.fan_out_in

import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// 두 개의 코루틴에서 채널을 서로 사용할 때 공정하게 기회를 준다는 것을 알 수 있습니다.

suspend fun someone(channel: Channel<String>, name: String) {
	for (comment in channel) {
		println("${name}: $comment")
		channel.send(comment.drop(1) + comment.first())
		delay(100L)
	}
}

fun main() = runBlocking {
	val channel = Channel<String>()
	launch {
		someone(channel, "민준")
	}

	launch {
		someone(channel, "서연")
	}

	channel.send("패스트 캠퍼스")
	delay(1000L)

	/**
	 * 선택적 취소 원인으로 이 작업 자체의 상태를 건드리지 않고 이 컨텍스트에서 작업의 모든 자식을 취소합니다.
	 * 작업 취소를 참조하십시오.
	 * 컨텍스트에 작업이 없거나 자식이 없으면 아무 작업도 수행하지 않습니다.
	 */
	coroutineContext.cancelChildren()

	// 첫번째 launch: "coroutine#2":StandaloneCoroutine{Cancelling}@383534aa,
	// 두번째 launch: "coroutine#3":StandaloneCoroutine{Cancelling}@6bc168e5
	// println("job1: $job1, job2: $job2")
}