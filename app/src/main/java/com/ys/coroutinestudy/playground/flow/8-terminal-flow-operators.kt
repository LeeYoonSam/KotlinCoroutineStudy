package com.ys.coroutinestudy.playground.flow

import com.ys.coroutinestudy.util.log
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.reduce
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
	val sum = (1..5).asFlow()
		.map { it * it } // squares of numbers from 1 to 5
		.reduce { a, b -> a + b} // sum them (terminal operator); .collect doesn't need to be called

	log(sum)
}

/**
 * Other Terminal operators
 *  - toList(), toSet() => Flow에서 내보낸 모든 객체를 수집하여 List 또는 Set으로 반환합니다. 제한된 흐름에서는 작동하지만 객체를 무기한 방출하는 흐름에서는 작동하지 않습니다.
 *  - reduce(), flow
 *  - single() => Flow에서 내보낸 첫 번째 객체를 반환하고 더 많은 항목이 내보내지면 예외를 던집니다.
 *  - first() => 흐름의 하나의 개체를 반환한 다음 소비를 중지합니다. 둘 이상의 값을 반환할 수 있는 Flow와 함께 사용하는 것이 안전합니다.
 */