# exception handling 추가 정리


## CoroutineScope()
- 주어진 코루틴 [context]를 감싸는 [CoroutineScope]를 생성합니다.
- 주어진 [context]에 [Job] 요소가 없으면 기본 `Job()`이 생성됩니다.
- 이런 식으로 이 범위의 자식 코루틴을 취소하거나 실패하면 다른 모든 자식이 취소됩니다.
- [coroutineScope] 블록 내부와 동일합니다.

```kotlin
@Suppress("FunctionName")
public fun CoroutineScope(context: CoroutineContext): CoroutineScope =
    ContextScope(if (context[Job] != null) context else context + Job())
```

## CoroutineExceptionHandler

- **캐치되지 않은** 예외를 처리하기 위한 코루틴 컨텍스트의 선택적 요소입니다.
- 일반적으로 포착되지 않은 예외는 [launch][CoroutineScope.launch] 빌더를 사용하여 생성된 _root_ 코루틴에서만 발생할 수 있습니다.
- 모든 _children_ 코루틴(다른 [Job]의 컨텍스트에서 생성된 코루틴)은 예외 처리를 상위 코루틴에 위임하며, \
  상위 코루틴도 상위 코루틴에 위임하는 식으로 루트까지 계속됩니다. 따라서 컨텍스트에 설치된 `CoroutineExceptionHandler`는 다음과 같습니다. 사용된 적이 없습니다.
- [SupervisorJob]으로 실행되는 코루틴은 예외를 부모에게 전파하지 않으며 루트 코루틴처럼 취급됩니다.
- [async][CoroutineScope.async]를 사용하여 생성된 코루틴은 항상 모든 예외를 포착하고 결과 [Deferred] 객체에서 이를 나타내므로 포착되지 않은 예외가 발생할 수 없습니다.

### 코루틴 예외 처리
- `CoroutineExceptionHandler`는 전역 "catch all" 동작에 대한 최후의 수단 메커니즘입니다.
- `CoroutineExceptionHandler`의 예외에서 복구할 수 없습니다. 코루틴은 이미 완료되었습니다.
- 핸들러가 호출될 때 해당 예외가 있습니다. \
  일반적으로 핸들러는 예외를 기록하고, 일종의 오류 메시지를 표시하고, 애플리케이션을 종료 및/또는 다시 시작하는 데 사용됩니다.
- 코드의 특정 부분에서 예외를 처리해야 하는 경우 코루틴 내부의 해당 코드 주위에 `try`/`catch`를 사용하는 것이 좋습니다. \
  이렇게 하면 예외가 있는 코루틴의 완료를 방지하고(예외는 이제 _caught_임), 작업을 재시도하고, 기타 임의의 작업을 수행할 수 있습니다.

```kotlin
scope.launch { // launch child coroutine in a scope
    try {
         // do something
    } catch (e: Throwable) {
         // handle exception
    }
}
```

### 구현 세부정보
- 기본적으로 핸들러가 설치되어 있지 않은 경우 uncaught exception은 다음과 같이 처리됩니다.
    - 예외가 [CancellationException]이면 무시됩니다(실행 중인 코루틴을 취소하는 메커니즘으로 간주되기 때문).
- 그렇지 않으면:
    - 컨텍스트에 [Job]이 있으면 [Job.cancel]이 호출됩니다.
    - 그렇지 않으면 [ServiceLoader] 및 현재 스레드의 [Thread.uncaughtExceptionHandler]를 통해 찾은 [CoroutineExceptionHandler]의 모든 인스턴스가 호출됩니다.
- [CoroutineExceptionHandler]는 임의의 스레드에서 호출될 수 있습니다.

```kotlin
public interface CoroutineExceptionHandler : CoroutineContext.Element {
    /**
     * Key for [CoroutineExceptionHandler] instance in the coroutine context.
     */
    public companion object Key : CoroutineContext.Key<CoroutineExceptionHandler>

    /**
     * Handles uncaught [exception] in the given [context]. It is invoked
     * if coroutine has an uncaught exception.
     */
    public fun handleException(context: CoroutineContext, exception: Throwable)
}
```


```kotlin
[CoroutineExceptionHandler] 인스턴스를 생성합니다.
@param handler 코루틴이 던진 예외를 처리하는 함수

@Suppress("FunctionName")
public inline fun CoroutineExceptionHandler(crossinline handler: (CoroutineContext, Throwable) -> Unit): CoroutineExceptionHandler =
    object : AbstractCoroutineContextElement(CoroutineExceptionHandler), CoroutineExceptionHandler {
        override fun handleException(context: CoroutineContext, exception: Throwable) =
            handler.invoke(context, exception)
    }
```