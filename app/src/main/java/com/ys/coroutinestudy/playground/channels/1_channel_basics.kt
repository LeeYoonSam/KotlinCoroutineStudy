package com.ys.coroutinestudy.playground.channels

import com.ys.coroutinestudy.util.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.launch

fun main() {

	GlobalScope.launch {
		// 호출순서 - 1
		produceRandom().consumeEach { log(it) }

		// 호출순서 - 3
		launch {
			log("launch some task")
		}

		// 호출순서 - 2
		log("All values consumed")
	}

	log("main() finished")
	Thread.sleep(6000)
}

// 반면 flow()는 최상위 함수 => 생성은 CoroutineScope에 정의되어 있습니다.
private fun CoroutineScope.produceRandom() = produce {
	repeat(5) {
		kotlinx.coroutines.delay(1000)
		send(kotlin.random.Random.nextInt())
	}
}
