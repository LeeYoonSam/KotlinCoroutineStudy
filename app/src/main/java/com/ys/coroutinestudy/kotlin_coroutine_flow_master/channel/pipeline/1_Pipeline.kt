package com.ys.coroutinestudy.kotlin_coroutine_flow_master.channel.pipeline

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.runBlocking

// 파이프 라인은 일반적인 패턴입니다.
// 하나의 스트림을 프로듀서가 만들고, 다른 코루틴에서 그 스트림을 읽어 새로운 스트림을 만드는 패턴.
// 채널을 이용해서 채널을 생성하는 형태를 `파이프라인`이라고 합니다.
// 파이프라인을 이용하면 여러 채널을 이용해서 데이터를 순차적으로 가공할수 있습니다.
fun CoroutineScope.produceNumbers() = produce { // 리시브 채널 반환
	var x = 1
	while (true) {
		send(x++) // 1, 2, 3..
	}
}

fun CoroutineScope.produceStringNumbers(
	numbers: ReceiveChannel<Int>
): ReceiveChannel<String> = produce { // 샌드 채널
	// produce 내에서만 send 를 할 수 있습니다.
	for (i in numbers) {
		send("${i}!") // 1!, 2!, 3! ...
	}
}

fun main() = runBlocking {
	// 채널, 리시브 채널. 리시브 메서드. send 메서드 X. 리시브 채널 + 샌드 채널이 합쳐져 있다.
	val numbers = produceNumbers()
	val stringNumbers = produceStringNumbers(numbers)

	repeat(5) {
		println(stringNumbers.receive()) // 명시적 receive
	}

	println("완료")
	coroutineContext.cancelChildren()
}