# KotlinCoroutineStudy

## CoroutineScope.launch
- 현재 스레드를 차단하지 않고 새 코루틴을 시작하고 코루틴에 대한 참조를 작업으로 반환합니다.
- 결과 작업이 취소되면 코루틴이 취소됩니다.
- 코루틴 컨텍스트는 CoroutineScope에서 상속됩니다.
- 추가 컨텍스트 요소는 컨텍스트 인수로 지정할 수 있습니다.
- 컨텍스트에 디스패처나 다른 ContinuationInterceptor가 없으면 Dispatchers.Default가 사용됩니다.
- 상위 작업도 CoroutineScope에서 상속되지만 해당 컨텍스트 요소로 재정의될 수도 있습니다.
- 기본적으로 코루틴은 즉시 실행되도록 예약됩니다.
- 다른 시작 옵션은 시작 매개변수를 통해 지정할 수 있습니다. 자세한 내용은 CoroutineStart를 참조하세요.
- 선택적 시작 매개변수를 CoroutineStart.LAZY로 설정하여 코루틴을 느리게 시작할 수 있습니다. 이 경우 코루틴 Job은 새로운 상태로 생성됩니다.
- 시작 기능으로 명시적으로 시작할 수 있으며 조인의 첫 번째 호출에서 암시적으로 시작됩니다.
- 이 코루틴의 잡히지 않은 예외는 기본적으로 컨텍스트의 상위 작업을 취소합니다(CoroutineExceptionHandler가 명시적으로 지정되지 않은 경우). \
  이는 시작이 다른 코루틴의 컨텍스트와 함께 사용될 때 포착되지 않은 예외가 상위 코루틴의 취소로 이어진다는 것을 의미합니다.

```kotlin
public fun CoroutineScope.launch(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
): Job {
    val newContext = newCoroutineContext(context)
    val coroutine = if (start.isLazy)
        LazyStandaloneCoroutine(newContext, block) else
        StandaloneCoroutine(newContext, active = true)
    coroutine.start(start, coroutine, block)
    return coroutine
}
```

## CoroutineScope.async
- 코루틴을 만들고 Deferred의 구현으로 미래 결과를 반환합니다.
- 실행 중인 코루틴은 지연된 결과가 취소되면 취소됩니다.
- 결과 코루틴은 다른 언어 및 프레임워크의 유사한 기본 요소와 비교하여 중요한 차이점이 있습니다.
- 구조화된 동시성 패러다임을 시행하지 못하면 상위 작업(또는 외부 범위)을 취소합니다.
- 해당 동작을 변경하려면 감독하는 부모(SupervisorJob 또는 SupervisorScope)를 사용할 수 있습니다.
- 코루틴 컨텍스트는 CoroutineScope에서 상속되며 컨텍스트 인수로 추가 컨텍스트 요소를 지정할 수 있습니다.
- 컨텍스트에 디스패처나 다른 ContinuationInterceptor가 없으면 Dispatchers.Default가 사용됩니다.
- 상위 작업도 CoroutineScope에서 상속되지만 해당 컨텍스트 요소로 재정의될 수도 있습니다.
- 기본적으로 코루틴은 즉시 실행되도록 예약됩니다. 다른 옵션은 시작 매개변수를 통해 지정할 수 있습니다.
- 선택적 시작 매개변수를 CoroutineStart.LAZY로 설정하여 코루틴을 느리게 시작할 수 있습니다. \
  이 경우 결과 Deferred가 새 상태로 생성됩니다.
- start 함수로 명시적으로 시작할 수 있으며 join, await 또는 awaitAll의 첫 번째 호출에서 암시적으로 시작됩니다.

```kotlin
public fun <T> CoroutineScope.async(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> T
): Deferred<T> {
    val newContext = newCoroutineContext(context)
    val coroutine = if (start.isLazy)
        LazyDeferredCoroutine(newContext, block) else
        DeferredCoroutine<T>(newContext, active = true)
    coroutine.start(start, coroutine, block)
    return coroutine
}
```