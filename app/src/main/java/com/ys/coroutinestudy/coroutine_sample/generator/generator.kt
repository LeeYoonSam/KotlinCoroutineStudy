package com.ys.coroutinestudy.coroutine_sample.generator

import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.resume
import kotlin.coroutines.startCoroutine

/*
	코루틴과 외부 코드 사이에서 양방향으로 값을 보낼 수 있는 ES6 스타일 생성기.

    ES6-generators에서 `next()`의 첫 번째 호출은 매개변수를 받아들이지 않고 후속 `yield`까지 코루틴을 시작합니다.
    항상 매개변수와 함께 호출되고 코루틴이 시작할 때 `next`에 대한 첫 번째 매개변수를 받도록 합니다(그래서 손실되지 않음).
    또한 위임된 생성기를 시작하려면 'yieldAll'에 추가 매개변수를 도입해야 합니다.
 */

interface Generator<out T, in R> {
	fun next(param: R): T? // 제너레이터가 끝나면 'null'을 반환합니다.
}

interface GeneratorBuilder<in T, R> {
	suspend fun yield(value: T): R
	suspend fun yieldAll(generator: Generator<T, R>, param: R)
}

fun <T, R> generate(block: suspend GeneratorBuilder<T, R>.(R) -> Unit): Generator<T, R> {
	val coroutine = GeneratorCoroutine<T, R>()
	val initial: suspend (R) -> Unit = { result -> block(coroutine, result) }
	coroutine.nextStep = { param -> initial.startCoroutine(param, coroutine) }
	return coroutine
}

// 제너레이터 코루틴 구현 클래스
internal class GeneratorCoroutine<T, R>: Generator<T, R>, GeneratorBuilder<T, R>, Continuation<Unit> {
	lateinit var nextStep: (R) -> Unit
	private var lastValue: T? = null
	private var lastException: Throwable? = null

	override fun next(param: R): T? {
		nextStep(param)
		lastException?.let { throw it }
		return lastValue
	}

	// GeneratorBuilder<T, R> 구현
	override suspend fun yield(value: T): R = suspendCoroutineUninterceptedOrReturn { cont ->
		lastValue = value
		nextStep = { param -> cont.resume(param) }
		COROUTINE_SUSPENDED
	}

	override suspend fun yieldAll(generator: Generator<T, R>, param: R): Unit = suspendCoroutineUninterceptedOrReturn sc@ { cont ->
		lastValue = generator.next(param)
		if (lastValue == null) return@sc Unit // 위임된 코루틴은 아무 것도 생성하지 않습니다 -- 재개
		nextStep = { param ->
			lastValue = generator.next(param)
			if (lastValue == null) cont.resume(Unit)
		}
		COROUTINE_SUSPENDED
	}

	// Continuation<Unit> 구현
	override val context: CoroutineContext get() = EmptyCoroutineContext

	override fun resumeWith(result: Result<Unit>) {
		result
			.onSuccess { lastValue = null }
			.onFailure { lastException = it }
	}
}