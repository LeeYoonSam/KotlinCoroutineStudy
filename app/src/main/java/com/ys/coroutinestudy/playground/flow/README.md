# Flow

## FlowCollector
- FlowCollector는 flow의 중간 또는 터미널 수집기로 사용되며 Flow에서 내보내는 값을 수락하는 엔터티를 나타냅니다.
- 이 인터페이스는 일반적으로 직접 구현하지 않고 사용자 지정 연산자를 구현할 때 flow 빌더에서 수신기로 사용해야 합니다. 이 인터페이스의 구현은 스레드로부터 안전하지 않습니다.

```kotlin
public interface FlowCollector<in T> {
    public suspend fun emit(value: T)
}
```

### FlowCollector - emit
- 업스트림에서 내보낸 값을 수집합니다.
- 이 메서드는 스레드로부터 안전하지 않으며 동시에 호출하면 안 됩니다.

## flow builder
- 지정된 일시 중단 가능한 [block]에서 cold_flow를 만듭니다.
- flow가 cold_라는 것은 결과 flow에 터미널 연산자가 적용될 때마다 [block]이 호출된다는 의미입니다.
- [flow] 빌더의 방출은 기본적으로 [cancellable]입니다. \
  [emit][FlowCollector.emit]에 대한 각 호출은 [ensureActive][CoroutineContext.ensureActive]도 호출합니다.
- `emit`는 flow 컨텍스트를 유지하기 위해 [block]의 디스패처에서 엄격하게 발생해야 합니다.

```kotlin
public fun <T> flow(@BuilderInference block: suspend FlowCollector<T>.() -> Unit): Flow<T> = SafeFlow(block)
```

- 예를 들어 다음 코드는 [IllegalStateException]을 발생시킵니다.

```kotlin
flow {
    emit(1) // Ok
    withContext(Dispatcher.IO) {
        emit(2) // Will fail with ISE
    }
}
```

- flow의 실행 컨텍스트를 전환하려면 [flowOn] 연산자를 사용합니다.

## Timeout.withTimeoutOrNull

- 지정된 시간 초과로 코루틴 내에서 지정된 일시 중단 코드 블록을 실행하고 이 시간 초과가 초과되면 null을 반환합니다.
- 블록 내에서 실행 중인 코드는 시간 초과 시 취소되고 블록 내에서 취소 가능한 일시 중단 함수의 활성 또는 다음 호출은 TimeoutCancellationException을 발생시킵니다.
- 시간 초과 시 예외를 발생시키는 형제 함수는 withTimeout입니다. onTimeout 절을 사용하여 선택 호출에 대해 시간 초과 작업을 지정할 수 있습니다.
- 타임아웃 이벤트는 블록에서 실행 중인 코드와 관련하여 비동기식이며 타임아웃 블록 내부에서 반환되기 직전에도 언제든지 발생할 수 있습니다. \
  블록 외부에서 닫거나 해제해야 하는 블록 내부의 일부 리소스를 열거나 획득하는 경우 이 점을 염두에 두십시오. \
  자세한 내용은 코루틴 가이드의 비동기 시간 초과 및 리소스 섹션을 참조하세요.
- 구현 참고 사항: 시간을 정확히 추적하는 방법은 컨텍스트의 CoroutineDispatcher에 대한 구현 세부 정보입니다.

```kotlin
public suspend fun <T> withTimeoutOrNull(timeMillis: Long, block: suspend CoroutineScope.() -> T): T? {
    if (timeMillis <= 0L) return null

    var coroutine: TimeoutCoroutine<T?, T?>? = null
    try {
        return suspendCoroutineUninterceptedOrReturn { uCont ->
            val timeoutCoroutine = TimeoutCoroutine(timeMillis, uCont)
            coroutine = timeoutCoroutine
            setupTimeout<T?, T?>(timeoutCoroutine, block)
        }
    } catch (e: TimeoutCancellationException) {
        // Return null if it's our exception, otherwise propagate it upstream (e.g. in case of nested withTimeouts)
        if (e.coroutine === coroutine) {
            return null
        }
        throw e
    }
}
```

## Emitters.transform
- 주어진 flow 의 각 값에 변형 기능을 적용합니다.
- 변환의 수신자는 FlowCollector이므로 변환은 방출된 요소를 변환하거나 건너뛰거나 여러 번 방출할 수 있는 유연한 함수입니다.
- 이 연산자는 필터 및 맵 연산자를 일반화하고 다른 연산자의 빌딩 블록으로 사용할 수 있습니다.

예를 들면 다음과 같습니다.
```kotlin
fun Flow<Int>.skipOddAndDuplicateEven(): Flow<Int> = transform { value ->
    if (value % 2 == 0) { // Emit only even values, but twice
        emit(value)
        emit(value)
    } // Do nothing if odd
}
```

### transform
```kotlin
public inline fun <T, R> Flow<T>.transform(
    @BuilderInference crossinline transform: suspend FlowCollector<R>.(value: T) -> Unit
): Flow<R> = flow { // Note: safe flow is used here, because collector is exposed to transform on each operation
    collect { value ->
        // kludge, without it Unit will be returned and TCE won't kick in, KT-28938
        return@collect transform(value)
    }
}
```