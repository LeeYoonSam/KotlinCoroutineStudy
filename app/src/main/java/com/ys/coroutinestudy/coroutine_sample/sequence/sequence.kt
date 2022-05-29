package com.ys.coroutinestudy.coroutine_sample.sequence

import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.createCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.experimental.ExperimentalTypeInference

interface SequenceScope<in T> {
	suspend fun yield(value: T)
}

@OptIn(ExperimentalTypeInference::class)
fun <T> sequence(@BuilderInference block: suspend SequenceScope<T>.() -> Unit): Sequence<T> = Sequence {
	SequenceCoroutine<T>().apply {
		nextStep = block.createCoroutine(receiver = this, completion = this)
	}
}

private class SequenceCoroutine<T>: AbstractIterator<T>(), SequenceScope<T>, Continuation<Unit> {
	lateinit var nextStep: Continuation<Unit>

	// AbstractIterator 구현
	override fun computeNext() {
		nextStep.resume(Unit)
	}

	// 완료 continuation 구현
	override val context: CoroutineContext get() = EmptyCoroutineContext

	override fun resumeWith(result: Result<Unit>) {
		result.getOrThrow()
		done()
	}

	// Generator 구현
	override suspend fun yield(value: T) {
		setNext(value)
		return suspendCoroutine { continuation -> nextStep = continuation }
	}
}

