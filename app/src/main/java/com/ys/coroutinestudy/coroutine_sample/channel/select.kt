package com.ys.coroutinestudy.coroutine_sample.channel

import kotlin.coroutines.suspendCoroutine

/**
 * Select
 *
 * select 문을 사용하면 고루틴이 여러 통신 작업을 기다릴 수 있습니다.
 * 선택 A는 해당 사례 중 하나가 실행될 때까지 차단한 다음 해당 사례를 실행합니다.
 * 여러 개가 준비되어 있으면 무작위로 하나를 선택합니다.
 */

suspend inline fun <R> select(block: SelectorBuilder<R>.() -> Unit): R =
	SelectorBuilder<R>().apply { block() }.doSelect()

class SelectorBuilder<R> {
	private val cases = mutableListOf<SelectCase<*, R>>()

	fun <T> SendChannel<T>.onSend(value: T, action: () -> R) {
		cases.add(SendCase(this, value, action))
	}

	fun <T> ReceiveChannel<T>.onReceive(action: (T) -> R) {
		cases.add(ReceiveCase(this, action))
	}

	fun onDefault(action: suspend () -> R) {
		cases.add(DefaultCase(action))
	}

	suspend fun doSelect(): R {
 		require(cases.isNotEmpty())

		return suspendCoroutine { continuation ->
			val selector = Selector(continuation, cases)
			for (case in cases) {
				case.selector = selector
				if (case.select(selector)) break
			}
		}
	}
}

suspend fun whileSelect(block: SelectorBuilder<Boolean>.() -> Unit) {
	while (select(block)) { /*loop*/ }
}