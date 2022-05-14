package com.ys.coroutinestudy.kotlin_coroutine_flow_master.channel.basic

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.channels.*

/**
 * `생산자(producer)`와 `소비자(consumer)`는 굉장히 일반적인 패턴입니다. 채널을 이용해서 한 쪽에서 데이터를 만들고 다른 쪽에서 받는 것을 도와주는 확장 함수들이 있습니다.
 *  1. `produce` 코루틴을 만들고 채널을 제공합니다.
 *  2. `consumeEach` 채널에서 반복해서 데이터를 제공합니다.
 *
 * `ProducerScope`는 `CoroutineScope` 인터페이스와 `SendChannel` 인터페이스를 함께 상속받습니다.
 * 그래서 코루틴 컨텍스트와 몇가지 채널 인터페이스를 같이 사용할 수 있는 특이한 스코프입니다.
 * `produce`를 사용하면 `ProducerScope`를 상속받은 `ProducerCoroutine` 코루틴을 얻게 됩니다.
 */
fun main() = runBlocking {

	/**
	 * `produce` 파트를 함수로 분리해봅시다.
	 * `suspend` 함수와 `CoroutineScope`의 확장 함수의 방식을 해봅시다. (`produce`는 `CoroutineScope`의 확장 함수)
	 * 채널 생성 , 코루틴 생성 후 `send` 하는 여러 작업을 `produce` 하나로 처리를 합니다.
	 * 채널을 만들고 반환, 내부적으로 코루틴 블록 생성
	 *  - 별도의 코루틴에서 코드 블럭을 실행하고 코드에게도 채널을 제공
	 */
	val oneToTen = produce { // ProducerScope = CoroutineScope + SendChannel
		// this.send // this.coroutineContext
		for (x in 1..10) {
			channel.send(x)
		}
	}

	oneToTen.consumeEach {
		println(it)
	}

	println(oneToTen) // "coroutine#2":ProducerCoroutine{Completed}@5ccd43c2
	println("완료")
}