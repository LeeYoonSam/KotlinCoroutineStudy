# 추가 기록

## joinAll()

```kotlin
public suspend fun joinAll(vararg jobs: Job): Unit = jobs.forEach { it.join() }
```

- 주어진 모든 작업이 완료될 때까지 현재 코루틴을 일시 중단합니다.
- 이 방법은 모든 주어진 작업을 jobs.forEach { it.join() }으로 하나씩 결합하는 것과 의미상 동일합니다.
- 이 일시 중지 기능은 취소할 수 있습니다.
- 이 일시 중단 함수가 기다리는 동안 현재 코루틴의 작업이 취소되거나 완료되면 이 함수는 CancellationException과 함께 즉시 재개됩니다.
- 이 기능이 일시 중단된 동안 작업이 취소된 경우 성공적으로 재개되지 않습니다.
- 낮은 수준의 세부 정보는 suspendCancellableCoroutine 설명서를 참조하세요.

## async

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

- 코루틴을 만들고 [Deferred]의 구현으로 미래 결과를 반환합니다.
- 실행 중인 코루틴은 지연된 결과가 [취소됨][Job.cancel]일 때 취소됩니다.
- *구조화된 동시성* 패러다임을 시행하지 못하면 상위 작업(또는 외부 범위)을 취소합니다.
- 해당 동작을 변경하려면 감독하는 부모([SupervisorJob] 또는 [supervisorScope])를 사용할 수 있습니다.
- 코루틴 컨텍스트는 [CoroutineScope]에서 상속되며, 추가 컨텍스트 요소는 [context] 인수로 지정할 수 있습니다.
- 컨텍스트에 디스패처나 다른 [ContinuationInterceptor]가 없으면 [Dispatchers.Default]가 사용됩니다.
- 부모 작업도 [CoroutineScope]에서 상속되지만 해당 [context] 요소로 재정의될 수도 있습니다.
- 기본적으로 코루틴은 즉시 실행되도록 예약됩니다.
- 다른 옵션은 `start` 매개변수를 통해 지정할 수 있습니다. 자세한 내용은 [CoroutineStart]를 참조하세요.
- 선택적 [start] 매개변수는 [CoroutineStart.LAZY]로 설정하여 코루틴 _lazily_를 시작할 수 있습니다. \
  이 경우 결과 [Deferred]가 _new_ 상태로 생성됩니다. \
  [start][Job.start] 함수로 명시적으로 시작할 수 있으며 \
  [join][Job.join], [await][Deferred.await] 또는 [awaitAll]의 첫 번째 호출에서 암시적으로 시작됩니다.


## delay

```kotlin
public suspend fun delay(timeMillis: Long) {
    if (timeMillis <= 0) return // don't delay
    return suspendCancellableCoroutine sc@ { cont: CancellableContinuation<Unit> ->
        // if timeMillis == Long.MAX_VALUE then just wait forever like awaitCancellation, don't schedule.
        if (timeMillis < Long.MAX_VALUE) {
            cont.context.delay.scheduleResumeAfterDelay(timeMillis, cont)
        }
    }
}
```

- 스레드를 차단하지 않고 주어진 시간 동안 코루틴을 지연하고 지정된 시간 후에 다시 시작합니다.
- 이 일시 중지 기능은 취소할 수 있습니다.
- 이 일시 중단 함수가 기다리는 동안 현재 코루틴의 작업이 취소되거나 완료되면 이 함수는 CancellationException과 함께 즉시 재개됩니다.
- 신속한 취소 보장이 있습니다.
- 이 기능이 일시 중단된 동안 작업이 취소된 경우 성공적으로 재개되지 않습니다.
- 취소할 때까지 영원히 연기하려면 대신 awaitCancellation을 사용하는 것이 좋습니다.
- onTimeout 절을 사용하여 선택 호출에 지연을 사용할 수 있습니다.
- 구현 참고 사항: 정확히 시간을 추적하는 방법은 컨텍스트에서 CoroutineDispatcher의 구현 세부 정보입니다.