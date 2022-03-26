# Operators

## Delay - Flow<T>.sample

- 주어진 샘플링 기간 동안 원래 flow에서 방출한 최신 값만 방출하는 flow를 반환합니다.

```kotlin
flow {
    repeat(10) {
        emit(it)
        delay(110)
    }
}.sample(200)
```

- 다음 배출을 생성합니다
    - 1, 3, 5, 7, 9

- 샘플링 창에 맞지 않으면 최신 요소가 내보내지지 않습니다.


```kotlin
@FlowPreview
public fun <T> Flow<T>.sample(periodMillis: Long): Flow<T> {
    // periodMillis는 0 을 초과하는 수
    require(periodMillis > 0) { "Sample period should be positive" }
    return scopedFlow { downstream ->
        val values = produce(capacity = Channel.CONFLATED) {
            collect { value -> send(value ?: NULL) }
        }
        var lastValue: Any? = null
        val ticker = fixedPeriodTicker(periodMillis)
        while (lastValue !== DONE) {
            select<Unit> {
                values.onReceiveCatching { result ->
                    result
                        .onSuccess { lastValue = it }
                        .onFailure {
                            it?.let { throw it }
                            ticker.cancel(ChildCancelledException())
                            lastValue = DONE
                        }
                }

                // todo: shall be start sampling only when an element arrives or sample aways as here?
                ticker.onReceive {
                    val value = lastValue ?: return@onReceive
                    lastValue = null // Consume the value
                    downstream.emit(NULL.unbox(value))
                }
            }
        }
    }
}
```

## FlowCoroutine - scopedFlow
- 각 수집기에 대한 CoroutineScope 을 제공하는 flow를 생성합니다.

```kotlin
flow {
    flowScope {
        ...
    }
}
```

- 취소에 대한 추가 제약이 있습니다.
- 자신을 취소하지 않고 자식을 취소하려면 cancel(ChildCancelledException())을 사용해야 합니다.

```kotlin
internal fun <R> scopedFlow(@BuilderInference block: suspend CoroutineScope.(FlowCollector<R>) -> Unit): Flow<R> =
    flow {
        flowScope { block(this@flow) }
    }
```

## FlowCoroutine

```kotlin
private class FlowCoroutine<T>(
    context: CoroutineContext,
    uCont: Continuation<T>
) : ScopeCoroutine<T>(context, uCont) {
    public override fun childCancelled(cause: Throwable): Boolean {
        if (cause is ChildCancelledException) return true
        return cancelImpl(cause)
    }
}
```

## FlowCoroutine - flowScope
- CoroutineScope를 만들고 이 범위로 지정된 일시 중단 블록을 호출합니다.
- 이 빌더는 취소와 관련하여 자식의 수명 주기와 자신을 연결하므로 자식 중 하나가 취소되면 취소된다는 점만 제외하고 coroutineScope와 유사합니다.

```kotlin
flowScope {
    launch {
        throw CancellationException()
    }
} // <- CE will be rethrown here
```

```kotlin
internal suspend fun <R> flowScope(@BuilderInference block: suspend CoroutineScope.() -> R): R =
    suspendCoroutineUninterceptedOrReturn { uCont ->
        val coroutine = FlowCoroutine(uCont.context, uCont)
        coroutine.startUndispatchedOrReturn(coroutine, block)
    }
```

## Scope - ScopeCoroutine
- coroutineScope 빌더에서 생성한 코루틴 인스턴스입니다.