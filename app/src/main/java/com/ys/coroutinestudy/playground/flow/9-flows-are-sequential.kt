package com.ys.coroutinestudy.playground.flow

import com.ys.coroutinestudy.util.log
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {

	/**
	 * 순차적으로 실행
	 *
	 * 1,2,3 이 있으면
	 *
	 * 1 -> filter
	 * 2 -> filter -> map -> collect
	 * 3 -> filter
	 *
	 * 이런 형태로 순차적으로 operator 를 실행하고 결과를 반환
	 */
	(1..3).asFlow()
		.filter {
			log("Filter $it")
			it % 2 == 0
		}
		.map {
			log("Map $it")
			"string $it"
		}
		.collect {
			log("Collect $it")
		}

}