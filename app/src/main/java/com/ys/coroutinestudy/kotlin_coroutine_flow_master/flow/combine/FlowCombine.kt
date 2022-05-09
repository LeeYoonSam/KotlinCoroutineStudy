package com.ys.coroutinestudy.kotlin_coroutine_flow_master.flow.combine

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking

/**
 * combine
 *
 * 양쪽의 데이터를 같은 시점에 묶지 않고 한 쪽이 갱신되면 새로 묶어 데이터를 만듭니다.
 * 짝과 관계없이 양쪽의 현재 값으로 묶습니다.
 */
fun main() = runBlocking {
	val nums = (1..3).asFlow().onEach { delay(100L) }
	val strs = flowOf("일", "이", "삼").onEach { delay(200L) }

	nums.combine(strs) { a, b -> "${a}은(는) $b" }
		.collect(::println)

	println("--------------")

	// 짝이 안맞는 경우
	val nums2 = (1..5).asFlow().onEach { delay(100L) }
	val strs2 = flowOf("일", "이", "삼").onEach { delay(200L) }
	nums2.combine(strs2) { a, b -> "${a}은(는) $b" }
		.collect(::println)
}