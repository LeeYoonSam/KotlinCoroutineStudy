# Flow

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