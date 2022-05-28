package com.ys.coroutinestudy.coroutine_sample.io

import android.annotation.SuppressLint
import com.ys.coroutinestudy.coroutine_sample.run.launch
import com.ys.coroutinestudy.util.log
import java.nio.ByteBuffer
import java.nio.channels.AsynchronousFileChannel
import java.nio.file.Paths

@SuppressLint("NewApi")
fun main() {
	launch {
		val fileName = "coroutine_sample/io/io.kt"
		log("Asynchronously loading file \"$fileName\" ...")
		val channel = AsynchronousFileChannel.open(Paths.get(fileName))
		channel.use { channel ->
			val buf = ByteBuffer.allocate(4096)
			val bytesRead = channel.aRead(buf)
			log("Read $bytesRead bytes starting with \"${String(buf.array().copyOf(10))}\"")
		}
	}
}