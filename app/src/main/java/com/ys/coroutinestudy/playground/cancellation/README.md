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

## NonCancellable
항상 활성 상태인 취소할 수 없는 작업입니다.

취소 없이 실행되어야 하는 코드 블록의 취소를 방지하는 withContext 함수를 위해 설계되었습니다.

다음과 같이 사용하십시오.

```kotlin
withContext(NonCancellable) {
    // this code will not be cancelled
}
```

### 경고
이 개체는 시작, 비동기 및 기타 코루틴 빌더와 함께 사용하도록 설계되지 않았습니다.
`launch(NonCancellable) { ... }` 를 쓰면 부모가 취소될 때 새로 시작된 작업이 취소되지 않을 뿐만 아니라 부모와 자식 간의 전체 부모-자식 관계가 끊어집니다.
부모는 자녀가 완료될 때까지 기다리지 않으며 자녀가 충돌했을 때 취소되지도 않습니다.

## GlobalScope

```kotlin
@DelicateCoroutinesApi
public object GlobalScope : CoroutineScope {
    /**
    * Returns [EmptyCoroutineContext].
    */
    override val coroutineContext: CoroutineContext
    get() = EmptyCoroutineContext
}
```

작업에 바인딩되지 않은 전역 CoroutineScope입니다. 전역 범위는 전체 애플리케이션 수명 동안 작동하고 조기에 취소되지 않는 최상위 코루틴을 시작하는 데 사용됩니다.
GlobalScope에서 시작된 활성 코루틴은 프로세스를 활성 상태로 유지하지 않습니다. 그들은 데몬 스레드와 같습니다.

이것은 섬세한 API입니다. GlobalScope를 사용할 때 실수로 리소스 또는 메모리 누수가 발생하기 쉽습니다. \
  GlobalScope에서 시작된 코루틴은 구조적 동시성의 원칙을 따르지 않으므로 문제(예: 느린 네트워크로 인해)로 인해 중단되거나 지연되는 경우 계속 작동하고 리소스를 소비합니다.

```kotlin
fun loadConfiguration() {
    GlobalScope.launch {
        val config = fetchConfigFromServer() // network request
        updateConfiguration(config)
    }
}
```

- `loadConfiguration`을 호출하면 취소하거나 완료될 때까지 기다리지 않고 백그라운드에서 작동하는 코루틴이 GlobalScope에 생성됩니다.
- 네트워크가 느리면 백그라운드에서 계속 대기하여 리소스를 소모합니다.
- loadConfiguration에 대한 반복적인 호출은 점점 더 많은 리소스를 소모합니다.

### 가능한 교체
많은 경우 GlobalScope 사용은 제거되어야 하며 포함 작업을 일시 중단으로 표시해야 합니다.

```kotlin
suspend fun loadConfiguration() {
    val config = fetchConfigFromServer() // network request
    updateConfiguration(config)
}
```

<br/>

GlobalScope.launch가 여러 동시 작업을 시작하는 데 사용된 경우 해당 작업은 대신 coroutineScope와 함께 그룹화됩니다.

```kotlin
// 구성 및 데이터를 동시에 로드
suspend fun loadConfigurationAndData() {
    coroutinesScope {
        launch { loadConfiguration() }
        launch { loadData() }
    }
}
```

- 최상위 코드에서 일시 중단되지 않은 컨텍스트에서 동시 작업 작업을 시작할 때 GlobalScope 대신 적절하게 제한된 CoroutineScope 인스턴스를 사용해야 합니다. \
  자세한 내용은 CoroutineScope 문서를 참조하세요.

### GlobalScope vs custom scope
GlobalScope.launch { ... }를 CoroutineScope().launch { ... } 생성자 함수 호출로 바꾸지 마십시오. \
  후자는 GlobalScope와 동일한 함정이 있습니다.

CoroutineScope() 생성자 함수의 의도된 사용법에 대한 CoroutineScope 문서를 참조하십시오.


### 합법적인 사용 사례
응용 프로그램의 전체 기간 동안 활성 상태를 유지해야 하는 최상위 백그라운드 프로세스와 같이 GlobalScope를 합법적이고 안전하게 사용할 수 있는 제한된 상황이 있습니다.
이 때문에 GlobalScope를 사용하려면 다음과 같이 @OptIn(DelicateCoroutinesApi::class)과 함께 명시적 옵트인이 필요합니다.

```kotlin
// 1초마다 통계를 기록하는 전역 코루틴은 항상 활성 상태여야 합니다.
@OptIn(DelicateCoroutinesApi::class)
val globalScopeReporter = GlobalScope.launch {
    while (true) {
        delay(1000)
        logStatistics()
    }
}
```