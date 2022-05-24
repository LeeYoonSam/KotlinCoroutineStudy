package com.ys.coroutinestudy.coroutine_sample.future

import android.annotation.SuppressLint
import com.ys.coroutinestudy.coroutine_sample.context.CommonPool
import java.util.concurrent.CompletableFuture
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.startCoroutine

fun <T> future(context: CoroutineContext = CommonPool, block: suspend () -> T): CompletableFuture<T> =
	CompletableFutureCoroutine<T>(context).also { block.startCoroutine(completion = it) }

@SuppressLint("NewApi")
class CompletableFutureCoroutine<T>(override val context: CoroutineContext) : CompletableFuture<T>(), Continuation<T> {
	override fun resumeWith(result: Result<T>) {
		result
			.onSuccess { complete(it) }
			.onFailure { completeExceptionally(it) }
	}
}