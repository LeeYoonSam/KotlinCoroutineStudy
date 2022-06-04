package com.ys.coroutinestudy.coroutine_sample.channel

import android.annotation.SuppressLint
import com.ys.coroutinestudy.coroutine_sample.delay.delay
import java.time.Instant

@SuppressLint("NewApi")
object Time {
	fun tick(millis: Long): ReceiveChannel<Instant> {
		val channel = Channel<Instant>()
		go {
			while (true) {
				delay(millis)
				channel.send(Instant.now())
			}
		}

		return channel
	}

	fun after(millis: Long): ReceiveChannel<Instant> {
		val channel = Channel<Instant>()
		go {
			delay(millis)
			channel.send(Instant.now())
			channel.close()
		}

		return channel
	}
}