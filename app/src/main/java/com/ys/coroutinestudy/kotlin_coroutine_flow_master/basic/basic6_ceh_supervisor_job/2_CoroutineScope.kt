package com.ys.coroutinestudy.kotlin_coroutine_flow_master.basic.basic6_ceh_supervisor_job

import com.ys.coroutinestudy.kotlin_coroutine_flow_master.printCurrentThreadName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.random.Random

// `GlobalScope`보다 권장되는 형식은 `CoroutineScope`를 사용하는 것입니다.
// `CoroutineScope`는 인자로 `CoroutineContext`를 받는데 코루틴 엘리먼트를 하나만 넣어도 좋고 이전에 배웠듯 엘리먼트를 합쳐 코루틴 컨텍스트를 만들어도 됩니다.

private suspend fun printRandom() {
	delay(500L)
	printCurrentThreadName()
	println(Random.nextInt(0, 500))
}

fun main() = runBlocking {

	// 하나의 코루틴 엘리먼트, 디스패처 `Dispatchers.Default`만 넣어도 코루틴 컨텍스트가 만들어지기 때문에 이렇게 사용할 수 있습니다.
	// 이제부터 `scope`로 계층적으로 형성된 코루틴을 관리할 수 있습니다. 우리의 필요에 따라 코루틴 스코프를 관리할 수 있습니다.

	val scope = CoroutineScope(Dispatchers.Default)
	val job = scope.launch(Dispatchers.IO) {
		launch { printRandom() }
	}
	Thread.sleep(1000L)
}