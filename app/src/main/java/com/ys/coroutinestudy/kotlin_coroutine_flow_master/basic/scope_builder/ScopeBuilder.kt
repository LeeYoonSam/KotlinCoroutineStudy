package com.ys.coroutinestudy.kotlin_coroutine_flow_master.basic.scope_builder

import com.ys.coroutinestudy.kotlin_coroutine_flow_master.getCurrentThreadName
import com.ys.coroutinestudy.kotlin_coroutine_flow_master.printCurrentThreadName
import com.ys.coroutinestudy.kotlin_coroutine_flow_master.printTitle
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() {
	simpleCoroutine()
	println()

	receiverOfCoroutineBuilder()
	println()

	coroutineContextSample()
	println()

	launchCoroutineBuilder()
	println()

	delaySample()
	println()

	sleepInCoroutine()
	println()

	multipleLaunch()
	println()

	responsibilityForCoroutine()
	println()
}

fun simpleCoroutine() = runBlocking {
	printTitle("간단한 코루틴")

	printCurrentThreadName()
	println("Hello")
}

fun receiverOfCoroutineBuilder() = runBlocking {
	printTitle("코루틴 빌더의 수신 객체")

	println(this)
	printCurrentThreadName()
	println("Hello")
}

fun coroutineContextSample() = runBlocking {
	printTitle("코루틴 컨텍스트")

	println(coroutineContext)
	printCurrentThreadName()
	println("Hello")
}

fun launchCoroutineBuilder() = runBlocking {
	printTitle("launch 코루틴 빌더")

	launch {
		printCurrentThreadName()
		println("World!")
	}

	printCurrentThreadName()
	println("Hello")
}

fun delaySample() = runBlocking {
	printTitle("delay 함수")

	launch {
		println("launch: ${getCurrentThreadName()}")
		println("World!")
	}

	println("runBlocking: ${getCurrentThreadName()}")
	delay(500L)
	println("Hello")
}

fun sleepInCoroutine() = runBlocking {
	printTitle("코루틴 내에서 sleep")

	launch {
		println("launch: ${getCurrentThreadName()}")
		println("World!")
	}

	println("runBlocking: ${getCurrentThreadName()}")
	Thread.sleep(500L)
	println("Hello")
}

fun multipleLaunch() = runBlocking {
	printTitle("한번에 여러 launch")

	launch {
		println("launch1: ${getCurrentThreadName()}")
		delay(1000L)
		println("3!")
	}

	launch {
		println("launch2: ${getCurrentThreadName()}")
		println("1!")
	}

	println("runBlocking: ${getCurrentThreadName()}")
	delay(500L)
	println("2!")
}

fun responsibilityForCoroutine() {
	runBlocking {
		printTitle("상위 코루틴은 하위 코루틴을 끝까지 책임진다.")

		launch {
			println("launch1: ${getCurrentThreadName()}")
			delay(1000L)
			println("3!")
		}

		launch {
			println("launch2: ${getCurrentThreadName()}")
			println("1!")
		}

		println("runBlocking: ${getCurrentThreadName()}")
		delay(500L)
		println("2!")
	}

	println("4!")
}