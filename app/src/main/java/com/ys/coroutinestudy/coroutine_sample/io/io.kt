package com.ys.coroutinestudy.coroutine_sample.io

import android.annotation.SuppressLint
import java.nio.ByteBuffer
import java.nio.channels.AsynchronousFileChannel
import java.nio.channels.CompletionHandler
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@SuppressLint("NewApi")
suspend fun AsynchronousFileChannel.aRead(buf: ByteBuffer): Int =
	suspendCoroutine { continuation ->
		read(buf, 0L, Unit, object : CompletionHandler<Int, Unit> {
			override fun completed(bytesRead: Int, attachment: Unit) {
				continuation.resume(bytesRead)
			}

			override fun failed(exception: Throwable, attachment: Unit) {
				continuation.resumeWithException(exception)
			}
		})
	}