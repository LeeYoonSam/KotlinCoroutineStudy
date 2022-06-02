package com.ys.coroutinestudy.coroutine_sample.channel

import java.lang.StringBuilder
import java.util.ArrayList
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.locks.ReentrantLock
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.startCoroutine
import kotlin.coroutines.suspendCoroutine

interface SendChannel<T> {
	suspend fun send(value: T)
	fun close()
	fun <R> selectSend(a: SendCase<T, R>): Boolean
}

interface ReceiveChannel<T> {
	suspend fun receive(): T // 닫힌 채널에서 NoSuchElementException이 발생합니다.
	suspend fun receiveOrNull(): T? // 닫힌 채널에서 null 을 반환 합니다.
	fun <R> selectReceive(a: ReceiveCase<T, R>): Boolean
	suspend operator fun iterator(): ReceiveIterator<T>
}

interface ReceiveIterator<out T> {
	suspend operator fun hasNext(): Boolean
	suspend operator fun next(): T
}

private const val CHANNEL_CLOSED = "Channel was closed"

private val channelCounter = AtomicLong() // 디버깅을 위한 채널 번호

class Channel<T>(val capacity: Int = 1) : SendChannel<T>, ReceiveChannel<T> {
	init { require(capacity >= 1) }
	private val number = channelCounter.incrementAndGet() // 디버깅을 위한 용도
	private var closed = false
	private val buffer = ArrayDeque<T>(capacity)
	private val waiters = SentinelWaiter<T>()

	private val empty: Boolean get() = buffer.isEmpty()
	private val full: Boolean get() = buffer.size == capacity

	override suspend fun send(value: T): Unit = suspendCoroutine sc@ { continuation ->
		var receiveWaiter: Waiter<T>? = null
		locked {
			check(!closed) { CHANNEL_CLOSED }
			if (full) {
				addWaiter(SendWaiter(continuation, value))
				return@sc // suspended
			} else {
				receiveWaiter = unlinkFirstWaiter()
				if (receiveWaiter == null) {
					buffer.add(value)
				}
			}
		}

		receiveWaiter?.resumeReceive(value)
		continuation.resume(Unit) // sent -> 이 코루틴을 즉시 재개
	}

	override fun <R> selectSend(sendCase: SendCase<T, R>): Boolean {
		var receiveWaiter: Waiter<T>? = null
		locked {
			if (sendCase.selector.resolved) return true // 이미 해결된 셀렉터, 아무작업 하지 않음
			check(!closed) { CHANNEL_CLOSED }
			if (full) {
				addWaiter(sendCase)
				return false // suspended
			} else {
				receiveWaiter = unlinkFirstWaiter()
				if (receiveWaiter == null) {
					buffer.add(sendCase.value)
				}
			}
			sendCase.unlink()
		}

		receiveWaiter?.resumeReceive(sendCase.value)
		sendCase.resumeSend() // sent -> 이 코루틴을 즉시 재개
		return true
	}

	@Suppress("UNCHECKED_CAST")
	override suspend fun receive(): T = suspendCoroutine sc@ { continuation ->
		var sendWaiter: Waiter<T>? = null
		var wasClosed = false
		var result: T? = null
		locked {
			if (empty) {
				if (closed) {
					wasClosed = true
				} else {
					addWaiter(ReceiveWaiter(continuation))
					return@sc // suspended
				}
			} else {
				result = buffer.removeFirst()
				sendWaiter = unlinkFirstWaiter()
				if (sendWaiter != null) buffer.add(sendWaiter!!.getSendValue())
			}
		}

		sendWaiter?.resumeSend()
		if (wasClosed) {
			continuation.resumeWithException(NoSuchElementException(CHANNEL_CLOSED))
		} else {
			continuation.resume(result as T)
		}
	}

	override suspend fun receiveOrNull(): T? = suspendCoroutine sc@ { continuation ->
		var sendWaiter: Waiter<T>? = null
		var result: T? = null
		locked {
			if (empty) {
				if (!closed) {
					addWaiter(ReceiveOrNullWaiter(continuation))
					return@sc // suspended
				}
			} else {
				result = buffer.removeFirst()
				sendWaiter = unlinkFirstWaiter()
				if (sendWaiter != null) buffer.add(sendWaiter!!.getSendValue())
			}
		}

		sendWaiter?.resumeSend()
		continuation.resume(result)
	}

	override fun <R> selectReceive(receiveCase: ReceiveCase<T, R>): Boolean {
		var sendWaiter: Waiter<T>? = null
		var wasClosed = false
		var result: T? = null
		locked {
			if (receiveCase.selector.resolved) return true
			if (empty) {
				if (closed) {
					wasClosed = true
				} else {
					addWaiter(receiveCase)
					return false // suspended
				}
			} else  {
				result = buffer.removeFirst()
				sendWaiter = unlinkFirstWaiter()
				if (sendWaiter != null) buffer.add(sendWaiter!!.getSendValue())
			}
			receiveCase.unlink()
		}

		sendWaiter?.resumeSend()
		if (wasClosed) {
			receiveCase.resumeClosed()
		} else {
			receiveCase.resumeReceive(result as T)
		}

		return true
	}

	override suspend fun iterator(): ReceiveIterator<T> = ReceiveIteratorImpl()

	inner class ReceiveIteratorImpl: ReceiveIterator<T> {
		private var computedNext = false
		private var hasNextValue = false
		private var nextValue: T? = null

		override suspend fun hasNext(): Boolean {
			if (computedNext) return hasNextValue
			return suspendCoroutine sc@ { continuation ->
				var sendWaiter: Waiter<T>? = null
				locked {
					if (empty) {
						if (!closed) {
							addWaiter(IteratorHasNextWaiter(continuation, this))
							return@sc // suspended
						} else {
							setClosed()
						}
					} else {
						setNext(buffer.removeFirst())
						sendWaiter = unlinkFirstWaiter()
						if (sendWaiter != null) buffer.add(sendWaiter!!.getSendValue())
					}
				}

				sendWaiter?.resumeSend()
				continuation.resume(hasNextValue)
			}
		}

		override suspend fun next(): T {
			// hasNext가 이전에 획득한 값을 반환
			if (computedNext) {
				val result = nextValue as T
				computedNext = false
				nextValue = null
				return result
			}

			// 보통 리시버는 hasNext가 이전에 호출되지 않았습니다.
			return receive()
		}

		fun setNext(value: T) {
			computedNext = true
			hasNextValue = true
			nextValue = value
		}

		fun setClosed() {
			computedNext = true
			hasNextValue = false
		}
	}

	override fun close() {
		var killList: ArrayList<Waiter<T>>? = null
		locked {
			if (closed) return // ignore repeated close
			closed = true
			if (empty || full) {
				killList = arrayListOf()
				while (true) {
					killList!!.add(unlinkFirstWaiter() ?: break)
				}
			} else {
				check (!hasWaiters) { "Channel with butter not-full and not-empty shall not have waiters"}
				return // nothing to do
			}
		}
		for (kill in killList!!) {
			kill.resumeClosed()
		}
	}

	private val hasWaiters: Boolean get() = waiters.next != waiters

	private fun addWaiter(w: Waiter<T>) {
		val last = waiters.prev!!
		w.prev = last
		w.next = waiters
		last.next = w
		waiters.prev = w
	}

	private fun unlinkFirstWaiter(): Waiter<T>? {
		val first = waiters.next!!
		if (first == waiters) return null
		first.unlink()
		return first
	}

	// 디버깅용
	private val waitersString: String get() {
		val stringBuilder = StringBuilder("[")
		var w = waiters.next!!
		while (w != waiters) {
			if (stringBuilder.length > 1) stringBuilder.append(", ")
			stringBuilder.append(w)
			w = w.next!!
		}
		stringBuilder.append("]")
		return stringBuilder.toString()
	}

	override fun toString(): String = locked {
		"Channel #$number closed=$closed, buffer=$buffer, waiters=$waitersString"
	}
}

// 이 잠금은 대기 목록을 관리하기 위해 짧은 시간 동안만 사용되며 그 아래에서 사용자 코드가 실행되지 않습니다.
private val lock = ReentrantLock()

private inline fun <R> locked(block: () -> R): R {
	lock.lock()
	return try { block() } finally { lock.unlock() }
}

sealed class Waiter<T> {
	var next: Waiter<T>? = null
	var prev: Waiter<T>? = null

	open fun resumeReceive(value: T) { throw IllegalStateException() }
	open fun resumeClosed() { throw IllegalStateException() }
	open fun getSendValue(): T { throw IllegalStateException() }
	open fun resumeSend() { throw IllegalStateException() }

	val linked: Boolean get() = next != null

	open fun unlink() { unlinkOne() }

	fun unlinkOne() {
		val prev = this.prev!!
		val next = this.next!!
		prev.next = next
		next.next = prev
		this.prev = null
		this.next = null
	}

	// 디버그
	override fun toString(): String = "${super.toString()} linked=$linked"
}

class SentinelWaiter<T> : Waiter<T>() {
	init {
		prev = this
		next = this
	}

	override fun unlink() { throw IllegalStateException() }
}

class SendWaiter<T>(val c: Continuation<Unit>, val value: T) : Waiter<T>() {
	override fun getSendValue(): T = value
	override fun resumeSend() = c.resume(Unit)
	override fun resumeClosed() = c.resumeWithException(IllegalStateException(CHANNEL_CLOSED))
}

class ReceiveWaiter<T>(val c: Continuation<T>) : Waiter<T>() {
	override fun resumeReceive(value: T) = c.resume(value)
	override fun resumeClosed() = c.resumeWithException(NoSuchElementException(CHANNEL_CLOSED))
}

class ReceiveOrNullWaiter<T>(val c: Continuation<T?>) : Waiter<T>() {
	override fun resumeReceive(value: T) = c.resume(value)
	override fun resumeClosed() = c.resume(null)
}

class IteratorHasNextWaiter<T>(val c: Continuation<Boolean>, val it: Channel<T>.ReceiveIteratorImpl) : Waiter<T>() {
	override fun resumeReceive(value: T) {
		it.setNext(value)
		c.resume(true)
	}

	override fun resumeClosed() {
		it.setClosed()
		c.resume(false)
	}
}

data class Selector<R>(val c: Continuation<R>, val cases: List<SelectCase<*, R>>) {
	var resolved = false

	fun resolve() {
		resolved = true
		cases
			.asSequence()
			.filter { it.linked }
			.forEach { it.unlinkOne() }
	}
}

sealed class SelectCase<T, R> : Waiter<T>() {
	lateinit var selector: Selector<R>
	abstract fun select(selector: Selector<R>): Boolean

	override fun unlink() {
		selector.resolve()
	}
}

class SendCase<T, R>(val c: SendChannel<T>, val value: T, val action: () -> R) : SelectCase<T, R>() {
	override fun getSendValue(): T = value
	override fun resumeSend() = selector.c.resume(action())
	override fun resumeClosed() = selector.c.resumeWithException(IllegalStateException(CHANNEL_CLOSED))
	override fun select(selector: Selector<R>): Boolean = c.selectSend(this)
}

class ReceiveCase<T, R>(val c: ReceiveChannel<T>, val action: (T) -> R) : SelectCase<T, R>() {
	override fun resumeReceive(value: T) = selector.c.resume(action(value))
	override fun resumeClosed() = selector.c.resumeWithException(NoSuchElementException(CHANNEL_CLOSED))
	override fun select(selector: Selector<R>): Boolean = c.selectReceive(this)
}

class DefaultCase<R>(val action: suspend () -> R) : SelectCase<Nothing, R>() {
	override fun select(selector: Selector<R>): Boolean {
		locked {
			if (selector.resolved) return true // 이미 해결된 셀렉터, 아무작업 하지 않음
			selector.resolve() // 기본 케이스는 셀렉터를 즉시 해결
		}

		// action 시작
		action.startCoroutine(completion = selector.c)
		return true
	}
}