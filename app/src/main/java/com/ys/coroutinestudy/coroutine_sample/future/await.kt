package com.ys.coroutinestudy.coroutine_sample.future

import android.annotation.SuppressLint
import java.util.concurrent.CompletableFuture
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@SuppressLint("NewApi")
suspend fun <T> CompletableFuture<T>.await(): T =
	suspendCoroutine { continuation: Continuation<T> ->
		whenComplete { result, exception ->
			if (exception == null) {
				continuation.resume(result)
			} else {
				continuation.resumeWithException(exception)
			}
		}
	}