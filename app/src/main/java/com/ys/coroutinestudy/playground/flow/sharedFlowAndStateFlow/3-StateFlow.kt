package com.ys.coroutinestudy.playground.flow.sharedFlowAndStateFlow

import androidx.compose.runtime.MutableState
import com.ys.coroutinestudy.util.log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// SharedFlow와 StateFlow의 차이점은 배압과 함께 제공됩니다.
// SharedFlow에는 배압을 처리하기 위한 몇 가지 옵션이 있습니다.
// StateFlow는 이전 값을 새 값으로 바꿉니다. StateFlow에는 단일 요소 버퍼가 있습니다.

// StateFlow에는 항상 값이 있습니다. StateFlow를 생성할 때 초기 값을 설정해야 합니다.

// StateFlow의 소비자는 현재 방출을 즉시 가져옵니다.
// .value = 현재 현재 값

// 상태 관리에 좋습니다.

// StateFlow를 노출하면 소비자가 마지막으로 방출된 상태를 얻을 수 있습니다.
private fun stateFlow(): Flow<String> {
	val letters = MutableStateFlow("A")

	GlobalScope.launch {
		listOf("B", "C", "D", "E", "F").forEach { letter ->
			letters.value = letter
			delay(500)
		}
	}

	return letters
}

fun main() = runBlocking {

	stateFlow().collect {
		delay(1100) // 배압
		log("Collected: $it")
	}
}