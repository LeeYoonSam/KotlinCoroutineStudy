package com.ys.coroutinestudy.coroutine_sample.sequence.optimized

import com.ys.coroutinestudy.coroutine_sample.sequence.SequenceScope
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.createCoroutineUnintercepted
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.resume

fun <T> sequence(block: suspend SequenceScope<T>.() -> Unit): Sequence<T> = Sequence {
	SequenceCoroutine<T>().apply {
		nextStep = block.createCoroutineUnintercepted(receiver = this, completion = this)
	}
}

class SequenceCoroutine<T>: AbstractIterator<T>(), SequenceScope<T>, Continuation<Unit> {
	lateinit var nextStep: Continuation<Unit>

	// AbstractIterator 구현
	override fun computeNext() {
		nextStep.resume(Unit)
	}

	// Completion continuation 구현
	override val context: CoroutineContext get() = EmptyCoroutineContext

	override fun resumeWith(result: Result<Unit>) {
		result.getOrThrow()
		done()
	}

	// 제네레이터 구현
	override suspend fun yield(value: T) {
		setNext(value)
		return suspendCoroutineUninterceptedOrReturn { continuation ->
			nextStep = continuation
			COROUTINE_SUSPENDED
		}
	}
}