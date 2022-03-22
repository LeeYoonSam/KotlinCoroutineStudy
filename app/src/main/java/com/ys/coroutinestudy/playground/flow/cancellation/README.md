# Flow - Cancellation

## Job.ensureActive
- 현재 컨텍스트의 작업이 활성 상태인지 확인합니다.
- 작업이 더 이상 활성화되지 않으면 CancellationException이 발생합니다.
- 작업이 취소된 경우 throw된 예외에는 원래 취소 원인이 포함됩니다.\
  이러한 코루틴은 취소할 수 없기 때문에 이 함수는 컨텍스트에 작업이 없으면 아무 작업도 수행하지 않습니다.

```kotlin
public fun CoroutineContext.ensureActive() {
    get(Job)?.ensureActive()
}
```

이 메서드는 다음 코드에 대한 드롭인 대체이지만 더 정확한 예외가 있습니다.
```kotlin
if (!isActive) {
    throw CancellationException()
}
```

## Flow<T>.cancellable
- 각 방출에 대한 취소 상태를 확인하고 흐름 수집기가 취소된 경우 해당 취소 원인을 throw하는 흐름을 반환합니다.
- 흐름 빌더와 SharedFlow의 모든 구현은 기본적으로 취소할 수 있습니다.

```kotlin
public fun <T> Flow<T>.cancellable(): Flow<T> =
    when (this) {
        is CancellableFlow<*> -> this // Fast-path, already cancellable
        else -> CancellableFlowImpl(this)
    }
```

이 연산자는 `.onEach { currentCoroutineContext().ensureActive() }` 에 대한 바로 가기를 제공합니다.