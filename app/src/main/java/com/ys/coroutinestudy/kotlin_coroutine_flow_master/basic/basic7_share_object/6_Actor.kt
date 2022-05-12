package com.ys.coroutinestudy.kotlin_coroutine_flow_master.basic.basic7_share_object

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

// 액터는 1973년에 칼 휴이트가 만든 개념으로 액터가 독점적으로 자료를 가지며 그 자료를 다른 코루틴과 공유하지 않고 액터를 통해서만 접근하게 만듭니다.

/**
 * 1. 실드(sealed) 클래스는 외부에서 확장이 불가능한 클래스이다.
 *      CounterMsg는 IncCounter와 GetCounter 두 종류로 한정됩니다.
 * 2. IncCounter는 싱글톤으로 인스턴스를 만들 수 없습니다. 액터에게 값을 증가시키기 위한 신호로 쓰입니다.
 * 3. GetCounter는 값을 가져올 때 쓰며 CompletableDeferred<Int>를 이용해 값을 받아옵니다.
 */
sealed class CounterMsg
object IncCounter : CounterMsg()
class GetCounter(val response: CompletableDeferred<Int>) : CounterMsg()

// channel : 한쪽에서 데이터를 보내고 다른 한쪽에서 데이터를 받을수 있는 것입니다.
// 채널은 송신 측에서 값을 send할 수 있고 수신 측에서 receive를 할 수 있는 도구입니다.
fun CoroutineScope.counterActor() = actor<CounterMsg> {
	var counter = 0 // 액터 안에 상태를 캡슐화해두고 다른 코루틴이 접근하지 못하게 합니다.

	// actor 코루틴 빌더는 ActorScope 안에서 실행되며 여기는 channel 이 기본으로 제공된다.
	for (msg in channel) { // 외부에서 보내는 것은 채널을 통해서만 받을 수 있습니다.(receive)
		when (msg) {
			is IncCounter -> counter++ // 증가시키는 신호
			is GetCounter -> msg.response.complete(counter) // 현재 상태를 반환 합니다.
		}
	}
}

fun main() = runBlocking<Unit> {
	val counter = counterActor()
	withContext(Dispatchers.Default) {
		massiveRun {
			counter.send(IncCounter) // suspension point
		}
	}

	val response = CompletableDeferred<Int>()
	counter.send(GetCounter(response))
	println("Counter = ${response.await()}") // suspension point
	counter.close()
}