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