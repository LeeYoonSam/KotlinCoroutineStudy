package com.ys.coroutinestudy.playground.kotlin_coroutine_flow_master.flow.combine

import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.runBlocking

/**
 * zip 은 양쪽의 데이터를 한꺼번에 묶어 새로운 데이터를 만들어 냅니다.
 * 동시에 1개씩 값을 가져옵니다.
 * 짝이 맞지 않으면 해당 데이터를 누락 시킵니다.
 */
fun main() = runBlocking {
	val nums = (1..3).asFlow()
	val strs = flowOf("일", "이", "삼")

	nums.zip(strs) { a, b -> "${a}은(는) $b" }
		.collect(::println)

	// 짝이 안맞는 경우 nums2 에서는 1, 2, 3만 zip 이 되고 4,5 는 누락시킵니다.
	val nums2 = (1..5).asFlow()
	nums2.zip(strs) { a, b -> "${a}은(는) $b" }
		.collect(::println)
}