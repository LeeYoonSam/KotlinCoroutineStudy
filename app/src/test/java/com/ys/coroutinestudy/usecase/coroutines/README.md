# Test 관련 기록

## advanceUntilIdle()
- 보류 중인 모든 작업을 즉시 실행하고 가상 시계 시간을 마지막 지연 시간으로 앞당깁니다.
- 가상 시간의 진행으로 인해 새 작업이 예약되면 AdvanceUntilIdle이 반환되기 전에 실행됩니다.

```kotlin
package kotlinx.coroutines.test
public interface DelayController

@ExperimentalCoroutinesApi // Since 1.2.1, tentatively till 1.3.0
public fun advanceUntilIdle(): Long
```
- Return: 이 Dispatcher의 시계가 전달된 지연 시간(밀리초)입니다.

### 실제 구현
```kotlin
class TestCoroutineDispatcher

override fun advanceUntilIdle(): Long {
    val oldTime = currentTime
    while(!queue.isEmpty) {
        runCurrent()
        val next = queue.peek() ?: break
        advanceUntilTime(next.time)
    }
    return currentTime - oldTime
}
```

- 실행 가능한 작업에 대한 순서가 지정된 대기열 queue 가 존재
- queue 가 다 비어질때까지 TimedRunnable 을 하나씩 실행
- 모든 queue 작업이 끝나면 현재시간 - 처음 기록했던 시간 을 계산해서 반환