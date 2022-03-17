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

## Buffer
- 버퍼는 지정된 용량의 채널을 통해 배출물을 흐르게 하고 별도의 코루틴에서 수집기를 실행합니다.
- 일반적으로 flow은 순차적입니다. 모든 연산자의 코드가 동일한 코루틴에서 실행된다는 의미입니다.
- 예를 들어, oneEach 및 수집 연산자를 사용하는 다음 코드를 고려하십시오.

```kotlin
flowOf("A", "B", "C")
    .onEach  { println("1$it") }
    .collect { println("2$it") }
```

이 코드를 호출하는 코루틴 Q에 의해 다음 순서로 실행됩니다.
> Q : -->-- [1A] -- [2A] -- [1B] -- [2B] -- [1C] -- [2C] -->--

- 따라서 연산자의 코드를 실행하는 데 상당한 시간이 걸린다면 총 실행 시간은 모든 연산자의 실행 시간을 합한 것입니다.
- 버퍼 연산자는 적용되는 flow에 대해 실행 중에 별도의 코루틴을 만듭니다. 다음 코드를 고려하십시오.

```kotlin
flowOf("A", "B", "C")
    .onEach  { println("1$it") }
    .buffer()  // <--------------- buffer between onEach and collect
    .collect { println("2$it") }
```
- 코드 실행을 위해 두 개의 코루틴을 사용합니다.

이 코드를 호출하는 코루틴 Q는 수집을 실행하고 버퍼 이전의 코드는 Q와 동시에 별도의 새 코루틴 P에서 실행됩니다.

```
P : -->-- [1A] -- [1B] -- [1C] ---------->--  // flowOf(...).onEach { ... }

                      |
                      | channel               // buffer()
                      V

Q : -->---------- [2A] -- [2B] -- [2C] -->--  // collect
```

- 연산자의 코드를 실행하는 데 시간이 걸리면 flow의 총 실행 시간이 줄어듭니다.
- 채널은 코루틴 P에 의해 방출된 요소를 코루틴 Q로 보내기 위해 코루틴 사이에 사용됩니다.
- 버퍼 연산자 앞의 코드(코루틴 P)가 버퍼 연산자 뒤의 코드(코루틴 Q)보다 빠르면 이 채널 어느 시점에서 가득 차서 소비자 코루틴 Q가 따라잡을 때까지 생산자 코루틴 P를 일시 중단합니다.
- 용량 매개변수는 이 버퍼의 크기를 정의합니다.

### 버퍼 초과 (Buffer overflow)
- 기본적으로 emitter 는 버퍼가 오버플로될 때 일시 중단되어 수집기가 따라잡을 수 있도록 합니다.
- 이 전략은 emitter 가 일시 중단되지 않도록 선택적 onBufferOverflow 매개 변수로 재정의할 수 있습니다.
- 이 경우 버퍼 오버플로 시 DROP_OLDEST 전략을 사용하여 버퍼에서 가장 오래된 값을 삭제하고 가장 최근에 내보낸 값을 버퍼에 추가하거나 \
  버퍼를 그대로 유지하면서 내보낸 최신 값을 DROP_LATEST 전략으로 삭제합니다.\
- 사용자 정의 전략 중 하나를 구현하기 위해 하나 이상의 요소 버퍼가 사용됩니다.

### 오퍼레이터 융합 (Operator fusion)
- channelFlow, flowOn, buffer, generateIn 및 broadcastIn의 인접 애플리케이션은 항상 융합되어 제대로 구성된 하나의 채널만 실행에 사용됩니다.
- 명시적으로 지정된 버퍼 용량은 어떤 크기의 버퍼라도 효과적으로 요청하는 buffer() 또는 buffer(Channel.BUFFERED) 호출보다 우선합니다. \
  지정된 버퍼 크기의 여러 요청은 요청된 버퍼 크기의 합계를 포함하는 버퍼를 생성합니다.
- onBufferOverflow 매개변수의 기본값이 아닌 버퍼 호출은 업스트림을 일시 중단하지 않으므로 업스트림 버퍼가 사용되지 않기 때문에 바로 앞의 모든 버퍼링 연산자를 재정의합니다.

### 개념적 구현 (Conceptual implementation)
- 버퍼의 실제 구현은 융합으로 인해 사소하지 않지만 개념적으로 기본 구현은 채널을 생성하기 위해 생성 코루틴 빌더를 사용하여 작성할 수 있는 다음 코드와 이를 소비하기 위한 consumerEach 확장과 동일합니다.

```kotlin
fun <T> Flow<T>.buffer(capacity: Int = DEFAULT): Flow<T> = flow {
    coroutineScope { // limit the scope of concurrent producer coroutine
        val channel = produce(capacity = capacity) {
            collect { send(it) } // send all to channel
        }
        // emit all received values
        channel.consumeEach { emit(it) }
    }
}
```

### 융합 (Conflation)
- Channel.CONFLATED의 용량으로 이 함수를 사용하는 것은 buffer(onBufferOverflow = BufferOverflow.DROP_OLDEST)에 대한 바로 가기이며 별도의 병합 연산자를 통해 사용할 수 있습니다.

---

매개변수:
- capacity - 코루틴 간의 버퍼 유형/용량. 허용되는 값은 Channel(...) 팩토리 함수에서와 동일합니다: \
  BUFFERED(기본값), CONFLATED, RENDEZVOUS, UNLIMITED 또는 명시적으로 요청된 크기를 나타내는 음수가 아닌 값.
- onBufferOverflow - 버퍼 오버플로에 대한 작업을 구성합니다\
  (선택 사항, 기본값은 SUSPEND, 용량 >= 0 또는 용량 == Channel.BUFFERED인 경우에만 지원됨, 적어도 하나의 버퍼링된 요소가 있는 채널을 암시적으로 생성).

```kotlin
public fun <T> Flow<T>.buffer(capacity: Int = BUFFERED, onBufferOverflow: BufferOverflow = BufferOverflow.SUSPEND): Flow<T> {
    require(capacity >= 0 || capacity == BUFFERED || capacity == CONFLATED) {
        "Buffer size should be non-negative, BUFFERED, or CONFLATED, but was $capacity"
    }
    require(capacity != CONFLATED || onBufferOverflow == BufferOverflow.SUSPEND) {
        "CONFLATED capacity cannot be used with non-default onBufferOverflow"
    }
    // desugar CONFLATED capacity to (0, DROP_OLDEST)
    var capacity = capacity
    var onBufferOverflow = onBufferOverflow
    if (capacity == CONFLATED) {
        capacity = 0
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    }
    // create a flow
    return when (this) {
        is FusibleFlow -> fuse(capacity = capacity, onBufferOverflow = onBufferOverflow)
        else -> ChannelFlowOperatorImpl(this, capacity = capacity, onBufferOverflow = onBufferOverflow)
    }
}
```

## conflate
- 통합 채널을 통해 flow 방출을 통합하고 별도의 코루틴에서 수집기를 실행합니다.
- 이것의 효과는 느린 수집기로 인해 emitter 가 일시 중단되지 않지만 수집기는 항상 가장 최근에 방출된 값을 가져옵니다.

예를 들어, 1에서 30 사이에 100ms 지연이 있는 정수를 방출하는 flow을 고려하십시오.
```kotlin
val flow = flow {
    for (i in 1..30) {
        delay(100)
        emit(i)
    }
}
```

'conflate()' 연산자를 적용하면 각 요소에서 1초를 지연하는 수집기가 정수 1, 10, 20, 30을 얻을 수 있습니다.
```kotlin
val result = flow.conflate().onEach { delay(1000) }.toList()
assertEquals(listOf(1, 10, 20, 30), result)
```

- 'conflate' 연산자는 'capacity'가 [Channel.CONFLATED][Channel.CONFLATED]인 [buffer]에 대한 바로 가기이며, \
  이는 차례에 의해 생성된 최신 요소만 유지하는 버퍼에 대한 바로 가기입니다. \
  buffer(onBufferOverflow = [BufferOverflow.DROP_OLDEST][BufferOverflow.DROP_OLDEST]).

```kotlin
public fun <T> Flow<T>.conflate(): Flow<T> = buffer(CONFLATED)
```

### 오퍼레이터 융합
- `conflate`/[buffer], [channelFlow], [flowOn] 및 [produceIn]의 인접 애플리케이션은 항상 하나의 적절하게 구성된 채널만 실행에 사용되도록 융합됩니다.
- [StateFlow]의 모든 인스턴스는 이미 `conflate` 연산자가 적용된 것처럼 작동하므로 `StateFlow`에 `conflate`를 적용해도 효과가 없습니다.
- Operator Fusion에 대한 [StateFlow] 문서를 참조하십시오.

## collectLatest
- 제공된 작업으로 주어진 flow를 수집하는 터미널 flow 연산자입니다.
- 수집과의 중요한 차이점은 원래 flow가 새 값을 내보내면 이전 값에 대한 작업 블록이 취소된다는 것입니다.

다음 예를 통해 증명할 수 있습니다.
```kotlin
flow {
    emit(1)
    delay(50)
    emit(2)
}.collectLatest { value ->
    println("Collecting $value")
    delay(100) // Emulate work
    println("$value collected")
}
prints "Collecting 1, Collecting 2, 2 collected"
```