# Cancellation 정리

## Job(작업)
- 백그라운드 작업입니다. 개념적으로 작업은 완료로 끝나는 수명 주기가 있는 취소 가능한 것입니다.
- 작업은 부모를 취소하면 모든 자식이 재귀적으로 즉시 취소되는 부모-자식 계층으로 정렬될 수 있습니다. \
  CancellationException 이외의 예외가 있는 자식 실패는 즉시 부모를 취소하고 결과적으로 다른 모든 자식을 취소합니다. \
  이 동작은 SupervisorJob을 사용하여 사용자 지정할 수 있습니다.

<br/>

Job 인터페이스의 가장 기본적인 인스턴스는 다음과 같이 생성됩니다.
- 코루틴 작업은 코루틴 빌더 실행으로 생성됩니다. 지정된 코드 블록을 실행하고 이 블록이 완료되면 완료됩니다.
- CompletableJob은 Job() 팩토리 함수로 생성됩니다. CompletableJob.complete를 호출하여 완료됩니다.
- 개념적으로 작업 실행은 결과 값을 생성하지 않습니다. 작업은 부작용에 대해서만 시작됩니다. \
  결과를 생성하는 작업은 지연된 인터페이스를 참조하세요.

### Job states
작업에는 다음과 같은 상태가 있습니다.
- isActive
- isCompleted
- isCancelled

**State**

| State | isActive | isCompleted | isCancelled |
| --- | --- | --- | --- |
| New (optional initial state) | false | false | false |
| Active (default initial state) | true | false | false |
| Completing (transient state) | true | false | false |
| Cancelling (transient state) | false | false | true |
| Cancelled (final state) | false | true | true |
| Completed (final state) | false | true | false |

- 일반적으로 작업은 활성 상태(생성 및 시작됨)에서 생성됩니다. \
  그러나 선택적 시작 매개변수를 제공하는 코루틴 빌더는 이 매개변수가 CoroutineStart.LAZY로 설정된 경우 새 상태에서 코루틴을 생성합니다. \
  이러한 작업은 시작 또는 조인을 호출하여 활성화할 수 있습니다.
- 작업은 코루틴이 작동하는 동안 또는 CompletableJob이 완료될 때까지, 또는 실패하거나 취소될 때까지 활성 상태입니다.
- 예외가 있는 활성 작업이 실패하면 취소됩니다. 작업은 즉시 취소 상태로 전환하도록 강제하는 취소 기능을 사용하여 언제든지 취소할 수 있습니다. \
  작업 실행이 완료되고 모든 하위 항목이 완료되면 작업이 취소됩니다.
- 활성 코루틴 본문의 완료 또는 CompletableJob.complete에 대한 호출은 작업을 완료 상태로 전환합니다. \
  완료 상태로 전환하기 전에 모든 자식이 완료될 때까지 완료 상태에서 기다립니다. 완료 상태는 순전히 작업 내부입니다. \
  외부 관찰자의 경우 완료 작업은 여전히 활성 상태이고 내부적으로는 자식을 기다리고 있습니다.

### Cancellation cause (취소 사유)
- 코루틴 작업은 본문에서 예외를 throw할 때 예외적으로 완료된다고 합니다. \
  CompletableJob은 CompletableJob.completeExceptionally를 호출하여 예외적으로 완료됩니다. \
  예외적으로 완료된 작업은 취소되고 해당 예외는 작업의 취소 원인이 됩니다.
- 작업의 정상적인 취소는 취소를 일으킨 이 예외의 유형에 따라 실패와 구별됩니다. \
  CancellationException을 던진 코루틴은 정상적으로 취소된 것으로 간주됩니다. \
  취소 원인이 다른 예외 유형이면 작업이 실패한 것으로 간주됩니다. \
  작업이 실패하면 동일한 유형을 제외하고 부모가 취소되므로 작업의 일부를 자식에게 위임할 때 투명성이 보장됩니다.
- 작업의 취소 기능은 취소 원인으로 CancellationException만 허용하므로 취소를 호출하면 항상 작업이 정상적으로 취소되고 상위 작업이 취소되지는 않습니다. \
  이런 식으로 부모는 자신을 취소하지 않고 자신의 자식을 취소할 수 있습니다(모든 자식도 재귀적으로 취소).

### Concurrency and synchronization (동시성 및 동기화)
- 이 인터페이스와 이 인터페이스에서 파생된 모든 인터페이스의 모든 기능은 스레드로부터 안전하며 외부 동기화 없이 동시 코루틴에서 안전하게 호출할 수 있습니다.

### Not stable for inheritance (상속에 대해 안정적이지 않음)
- 작업 인터페이스 및 모든 파생 인터페이스는 향후 이 인터페이스에 새 메서드가 추가될 수 있으므로 타사 라이브러리의 상속에 대해 안정적이지 않지만 사용하기에는 안정적입니다.

## job.cancel()

```kotlin
public fun cancel(cause: CancellationException? = null)
```

- 선택적 취소 원인으로 이 작업을 취소합니다.
- 원인은 오류 메시지를 지정하거나 디버깅 목적으로 취소 이유에 대한 기타 세부 정보를 제공하는 데 사용할 수 있습니다.
- `cancellation machinery`에 대한 전체 설명은 작업 문서를 참조하십시오.

