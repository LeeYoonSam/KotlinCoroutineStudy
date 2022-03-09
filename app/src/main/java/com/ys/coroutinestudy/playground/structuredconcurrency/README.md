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