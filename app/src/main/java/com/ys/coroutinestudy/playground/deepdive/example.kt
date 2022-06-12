package com.ys.coroutinestudy.playground.deepdive

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking

fun main() {
	runDispatchersExample()
}

/**
 * GlobalScope
 *
 * - 전체 애플리케이션 수명 동안 작동하고 조기에 취소되지 않는 최상위 코루틴을 시작
 * - GlobalScope에서 시작된 코루틴은 구조적 동시성 원칙을 따르지 않으므로 문제(예: 느린 네트워크로 인해)로 인해 중단되거나 지연되는 경우 계속 작동하고 리소스를 소비합니다.
 * - loadConfiguration을 호출하면 취소하거나 완료될 때까지 기다리지 않고 백그라운드에서 작동하는 코루틴이 GlobalScope에 생성됩니다.
 * - 네트워크가 느리면 백그라운드에서 계속 대기하여 리소스를 소모합니다.
 * - loadConfiguration을 반복적으로 호출하면 점점 더 많은 리소스가 소모됩니다.
 */
fun loadConfiguration() {
	GlobalScope.launch {
		val config = fetchConfigFromServer() // network request
		updateConfiguration(config)
	}
}

// GlobalScope - fake fun
private fun fetchConfigFromServer(): Int = 0
private fun updateConfiguration(config: Int) {}

/**
 * Dispatchers
 *
 * - `launch { ... }`
 *      - 매개변수 없이 사용되면 시작되는 CoroutineScope에서 컨텍스트(따라서 디스패처)를 상속합니다.
 *      - 이 경우 메인 스레드에서 실행되는 메인 runBlocking 코루틴의 컨텍스트를 상속합니다.
 * - `Dispatchers.Unconfined`
 *      - Unconfined는 메인 스레드에서도 실행되는 것으로 보이는 특수 디스패처입니다.
 *      - 제한되지 않은 코루틴 디스패처는 호출자 스레드에서 코루틴을 시작하지만 첫 번째 중단 지점까지만 시작됩니다.
 *      - 일시 중단 후 호출된 일시 중단 함수에 의해 완전히 결정된 스레드에서 코루틴을 다시 시작합니다.
 *      - 무제한 디스패처는 CPU 시간을 소비하지 않고 특정 스레드에 국한된 공유 데이터(예: UI)를 업데이트하지 않는 코루틴에 적합합니다.
 * - `Dispatchers.Default`: 다른 디스패처가 범위에 명시적으로 지정되지 않은 경우 기본 디스패처가 사용됩니다. 기본값이며 스레드의 공유 배경 풀을 사용합니다.
 * - `newSingleThreadContext`
 *      - 코루틴이 실행할 스레드를 생성합니다. 전용 스레드는 매우 비싼 리소스입니다.
 *      - 실제 애플리케이션에서는 더 이상 필요하지 않을 때 닫기 기능을 사용하여 해제하거나 최상위 변수에 저장하고 애플리케이션 전체에서 재사용해야 합니다.
 */
fun runDispatchersExample() = runBlocking {
	launch { // 상위 runBlocking 코루틴의 컨텍스트
		println("main runBlocking : I’m working in thread ${Thread.currentThread().name}")
	}

	launch(Dispatchers.Unconfined) { // 제한되지 않음 — 메인 스레드와 함께 작동합니다.
		println("Unconfined : I’m working in thread ${Thread.currentThread().name}")
	}

	launch(Dispatchers.Default) { // DefaultDispatcher로 디스패치됩니다.
		println("Default : I’m working in thread ${Thread.currentThread().name}")
	}

	launch(newSingleThreadContext("MyOwnThread")) { // 자신의 새로운 스레드를 얻을 것입니다.
		println("newSingleThreadContext: I’m working in thread ${Thread.currentThread().name}")
	}
}