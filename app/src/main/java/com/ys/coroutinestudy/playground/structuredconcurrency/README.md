# Structured Concurrency

## CancellationException
- 일시 중단되는 동안 코루틴의 작업이 취소되면 취소 가능한 일시 중단 함수에 의해 throw됩니다.
- 코루틴의 정상적인 취소를 나타냅니다.
- 기본적으로 잡히지 않는 예외 처리기에 의해 콘솔/로그에 인쇄되지 않습니다.
- CoroutineExceptionHandler 참조


## Job.invokeOnCompletion
- 이 작업이 완료되면 동기적으로 한 번 호출되는 핸들러를 등록합니다.
- 작업이 이미 완료되면 작업의 예외 또는 취소 원인 또는 null과 함께 핸들러가 즉시 호출됩니다. 그렇지 않으면 이 작업이 완료될 때 핸들러가 한 번 호출됩니다.

핸들러에 전달되는 원인의 의미:
- 작업이 정상적으로 완료되면 원인이 null입니다.
- 원인은 작업이 정상적으로 취소되었을 때 CancellationException의 인스턴스입니다. 오류로 처리되어서는 안됩니다. 특히 오류 로그에 보고해서는 안 됩니다.
- 그렇지 않으면 작업이 실패한 것입니다.

결과 DisposableHandle은 이 핸들러의 등록을 삭제하고 호출이 더 이상 필요하지 않은 경우 메모리를 해제하는 데 사용할 수 있습니다.
이 작업이 완료된 후 핸들러를 폐기할 필요가 없습니다. 이 작업이 완료되면 모든 핸들러에 대한 참조가 해제됩니다.
설치된 핸들러는 예외를 발생시키지 않아야 합니다. \
  그렇다면, catch되어 CompletionHandlerException으로 래핑된 후 다시 발생하여 잠재적으로 관련 없는 코드의 충돌을 일으킬 수 있습니다.

참고: CompletionHandler 구현은 빠르고 비차단적이며 스레드로부터 안전해야 합니다. \
  이 핸들러는 주변 코드와 동시에 호출될 수 있습니다. 핸들러가 호출되는 실행 컨텍스트는 보장되지 않습니다.

```kotlin
public fun invokeOnCompletion(handler: CompletionHandler): DisposableHandle
```


## CoroutineScope.cancel

- 작업과 선택적 취소 원인이 있는 모든 자식을 포함하여 이 범위를 취소합니다.
- 원인을 사용하여 오류 메시지를 지정하거나 디버깅 목적으로 취소 이유에 대한 기타 세부 정보를 제공할 수 있습니다.
- 범위에 작업이 없으면 IllegalStateException을 던집니다.

```kotlin
public fun CoroutineScope.cancel(cause: CancellationException? = null) {
    val job = coroutineContext[Job] ?: error("Scope cannot be cancelled because it does not have a job: $this")
    job.cancel(cause)
}
```

## Job.children

```kotlin
public val children: Sequence<Job>
```

- 이 Job의 자식 시퀀스를 반환합니다. Job은 이 Job으로 구성될 때 이 Job의 자식이 됩니다. [CoroutineContext] 또는 명시적 '상위' 매개변수 사용.
- 부모-자식 관계는 다음과 같은 효과가 있습니다.
    - [cancel] 또는 예외적인 완료(실패)로 부모를 취소하면 모든 자식이 즉시 취소됩니다.
    - 부모는 모든 자식이 완료될 때까지 완료할 수 없습니다. 부모는 모든 자식이 _completing_ 또는 _cancelling_ 상태에서 완료될 때까지 기다립니다.
    - 자식의 잡히지 않은 예외는 기본적으로 부모를 취소합니다. \
  이는 [async][CoroutineScope.async] 및 기타 미래형 코루틴 빌더로 생성된 하위 항목에도 적용됩니다. \
  예외가 포착되어 결과에 캡슐화되더라도 마찬가지입니다. \
  이 기본 동작은 [SupervisorJob]으로 재정의할 수 있습니다.


## Job.cancelAndJoin
- 작업을 취소하고 취소된 작업이 완료될 때까지 호출 코루틴을 일시 중단합니다.
- 이 일시 중단 기능은 취소할 수 있으며 **항상** 호출 코루틴의 작업 취소를 확인합니다.
- 이 일시 중단 함수가 호출되거나 일시 중단되는 동안 호출 코루틴의 [Job]이 취소되거나 완료되면 이 함수는 [CancellationException]을 throw합니다.
- 특히, 자식 코루틴이 [supervisorScope] 내에서 실행되지 않는 한 자식 코루틴의 실패는 기본적으로 부모를 취소하기 때문에 \
  자식 코루틴에서 'cancelAndJoin'을 호출하는 부모 코루틴이 자식이 실패한 경우 [CancellationException]을 던진다는 것을 의미합니다. .
- [cancel][Job.cancel] 다음에 [join][Job.join]을 호출하는 단축키입니다.


```kotlin
public suspend fun Job.cancelAndJoin() {
    cancel()
    return join()
}
```

## SupervisorJob
- 활성 상태의 SupervisorJob 개체를 만듭니다. 수퍼바이저 작업의 하위 항목은 서로 독립적으로 실패할 수 있습니다.
- 1차 하위 구성요소의 실패 또는 취소는 SupervisorJob의 실패를 유발하지 않으며 다른 1차 하위 구성요소에 영향을 미치지 않으므로 감독자는 하위의 실패를 처리하기 위한 사용자 정의 정책을 구현할 수 있습니다.
- 실행을 사용하여 생성된 자식 작업의 실패는 컨텍스트에서 CoroutineExceptionHandler를 통해 처리할 수 있습니다.
- 비동기를 사용하여 생성된 자식 작업의 실패는 지연된 결과 값에서 Deferred.await를 통해 처리할 수 있습니다.
- 상위 작업이 지정된 경우 이 SupervisorJob은 상위의 하위 작업이 되고 상위 작업이 실패하거나 취소되면 취소됩니다. \
  이 경우 이 감독자의 자식도 모두 취소됩니다. 이 SupervisorJob에 대해 취소 예외(CancellationException 제외)를 호출하면 상위 작업도 취소됩니다.

매개변수:
- parent - 선택적 상위 작업.

```kotlin
@Suppress("FunctionName")
public fun SupervisorJob(parent: Job? = null) : CompletableJob = SupervisorJobImpl(parent)
```


```kotlin
private class SupervisorJobImpl(parent: Job?) : JobImpl(parent) {
    override fun childCancelled(cause: Throwable): Boolean = false
}
```
- child 취소하지 않는 옵션 적용


## CoroutineScope.isActive
- 현재 작업이 아직 활성 상태이면 true를 반환합니다(완료되지 않고 아직 취소되지 않은 경우).
- 취소를 지원하려면 장기 실행 계산 루프에서 이 속성을 확인하세요.

```kotlin
while (isActive) {
    // do some computation
}
```

- 이 속성은 CoroutineScope를 사용할 수 있는 경우 범위의 coroutineContext.isActive에 대한 바로 가기입니다. \
  coroutineContext, isActive 및 Job.isActive를 참조하세요.

```kotlin
@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
public val CoroutineScope.isActive: Boolean
    get() = coroutineContext[Job]?.isActive ?: true
```

## coroutineScope
- CoroutineScope를 만들고 이 범위로 지정된 일시 중단 블록을 호출합니다. 제공된 범위는 외부 범위에서 coroutineContext를 상속하지만 컨텍스트의 작업을 재정의합니다.
- 이 기능은 작업의 병렬 분해를 위해 설계되었습니다. 이 범위의 자식 코루틴이 실패하면 이 범위가 실패하고 나머지 자식은 모두 취소됩니다(다른 동작은 supervisorScope 참조). \
- 이 함수는 주어진 블록과 모든 자식 코루틴이 완료되는 즉시 반환됩니다.

범위의 사용 예는 다음과 같습니다.
```kotlin
suspend fun showSomeData() = coroutineScope {
    val data = async(Dispatchers.IO) { // <- extension on current scope
     ... load some UI data for the Main thread ...
    }

    withContext(Dispatchers.Main) {
        doSomeWork()
        val result = data.await()
        display(result)
    }
}
```

- 이 예의 범위에는 다음과 같은 의미가 있습니다.
    - showSomeData는 데이터가 로드되고 UI에 표시되자마자 반환됩니다.
    - doSomeWork에서 예외가 발생하면 비동기 작업이 취소되고 showSomeData가 해당 예외를 다시 발생시킵니다.
    - showSomeData의 외부 범위가 취소되면 시작된 비동기 블록과 withContext 블록이 모두 취소됩니다.
    - 비동기 블록이 실패하면 withContext가 취소됩니다.

- 메서드는 현재 작업이 외부적으로 취소된 경우 CancellationException을 throw하거나 이 범위에 처리되지 않은 예외가 있는 경우 \
  해당 처리되지 않은 throwable을 throw할 수 있습니다(예: 이 범위에서 실행과 함께 시작된 충돌 코루틴에서).

```kotlin
public suspend fun <R> coroutineScope(block: suspend CoroutineScope.() -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return suspendCoroutineUninterceptedOrReturn { uCont ->
        val coroutine = ScopeCoroutine(uCont.context, uCont)
        coroutine.startUndispatchedOrReturn(coroutine, block)
    }
}
```