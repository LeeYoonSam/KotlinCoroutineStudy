# Channels
- 채널은 송신자(SendChannel을 통해)와 수신자(ReceiveChannel을 통해) 간의 통신을 위한 비차단 프리미티브입니다.
- 개념적으로 채널은 Java의 BlockingQueue와 유사하지만 차단 작업 대신 일시 중단 작업이 있으며 닫힐 수 있습니다.

## 채널 만들기

> Channel(capacity) 팩토리 함수는 용량 정수 값에 따라 다른 종류의 채널을 생성하는 데 사용됩니다.

- 용량(capacity)이 0일 때 — 랑데뷰 채널을 생성합니다. 이 채널에는 버퍼가 전혀 없습니다. \
  요소는 송신 및 수신 호출이 정시에 만날 때만(랑데부) 송신자에서 수신자로 전송되므로 다른 코루틴이 수신을 호출할 때까지 송신이 일시 중단되고 다른 코루틴이 송신을 호출할 때까지 수신이 일시 중단됩니다.
- 용량(capacity)이 Channel.UNLIMITED인 경우 — 버퍼가 사실상 무제한인 채널을 생성합니다. \
  이 채널에는 무제한 용량의 연결 목록 버퍼가 있습니다(사용 가능한 메모리에 의해서만 제한됨). 이 채널로 보내는 것은 일시 중단되지 않으며 trySend는 항상 성공합니다.
- 용량(capacity)이 Channel.CONFLATED일 때 — 병합된 채널을 생성합니다. \
  이 채널은 최대 하나의 요소를 버퍼링하고 모든 후속 send 및 trySend 호출을 병합하므로 수신자는 항상 마지막 요소가 전송되도록 합니다. \
  연속 전송된 요소는 병합됩니다 — 마지막으로 전송된 요소만 수신되고 이전에 전송된 요소는 손실됩니다. \
  이 채널로 보내는 것은 일시 중단되지 않으며 trySend는 항상 성공합니다.
- 용량(capacity)이 양수이지만 UNLIMITED보다 작으면 지정된 용량으로 어레이 기반 채널을 생성합니다. \
  이 채널에는 고정 용량의 어레이 버퍼가 있습니다. 버퍼가 가득 찼을 때만 송신이 일시 중단되고 버퍼가 비어 있을 때만 수신이 일시 중단됩니다.

## CoroutineScope.produce
- 새 코루틴을 시작하여 값 스트림을 채널로 보내고 코루틴에 대한 참조를 ReceiveChannel로 반환합니다. \
  이 결과 객체는 이 코루틴에 의해 생성된 요소를 수신하는 데 사용할 수 있습니다.
- 코루틴의 범위는 코루틴이 send를 직접 호출할 수 있도록 CoroutineScope와 SendChannel을 모두 구현하는 ProducerScope 인터페이스를 포함합니다. \
  코루틴이 완료되면 채널이 닫힙니다. 실행 중인 코루틴은 수신 채널이 취소되면 취소됩니다.
- 코루틴 컨텍스트는 이 CoroutineScope에서 상속됩니다. 추가 컨텍스트 요소는 컨텍스트 인수로 지정할 수 있습니다. \
  컨텍스트에 디스패처나 다른 ContinuationInterceptor가 없으면 Dispatchers.Default가 사용됩니다. \
  상위 작업도 CoroutineScope에서 상속되지만 해당 컨텍스트 요소로 재정의될 수도 있습니다.
- 이 코루틴에서 포착되지 않은 예외는 이 예외를 원인으로 하여 채널을 닫고 결과 채널은 실패하므로 이후에 수신하려는 모든 시도에서 예외가 발생합니다.
- 결과 채널의 종류는 지정된 용량 매개변수에 따라 다릅니다. 자세한 내용은 채널 인터페이스 설명서를 참조하십시오.
- 새로 생성된 코루틴에 사용할 수 있는 디버깅 기능에 대한 설명은 newCoroutineContext를 참조하세요.
- 참고: 이것은 실험용 API입니다. 취소 및 오류 처리와 관련하여 부모 범위에서 자식으로 작동하는 생산자의 동작은 향후 변경될 수 있습니다.


매개변수:
- context - 코루틴의 CoroutineScope.coroutineContext 컨텍스트에 추가됩니다.
- capacity - 채널 버퍼의 용량(기본적으로 버퍼 없음).
- BuilderInference - 코루틴 코드

```kotlin
@ExperimentalCoroutinesApi
public fun <E> CoroutineScope.produce(
    context: CoroutineContext = EmptyCoroutineContext,
    capacity: Int = 0,
    @BuilderInference block: suspend ProducerScope<E>.() -> Unit
): ReceiveChannel<E> =
    produce(context, capacity, BufferOverflow.SUSPEND, CoroutineStart.DEFAULT, onCompletion = null, block = block)
```


## Channel - trySend

```kotlin
// Channel.kt
public fun trySend(element: E): ChannelResult<Unit>
```

- 용량 제한을 위반하지 않는 경우 지정된 요소를 이 채널에 즉시 추가하고 성공적인 결과를 반환합니다. \
  그렇지 않으면 실패하거나 닫힌 결과를 반환합니다. \
  이것은 send가 일시 중단되거나 throw되는 상황에서 백오프하는 send의 동기 변형입니다.
- trySend 호출이 성공하지 못한 결과를 반환하면 요소가 소비자에게 전달되지 않았음을 보장하고 이 채널에 대해 설치된 onUndeliveredElement를 호출하지 않습니다. \
  전달되지 않은 요소를 처리하는 방법에 대한 자세한 내용은 채널 문서의 "전달되지 않은 요소" 섹션을 참조하세요.

## Channel - send

```kotlin
public suspend fun send(element: E)
```

- 지정된 요소를 이 채널로 전송하고, 이 채널의 버퍼가 가득 차거나 존재하지 않는 경우 호출자를 일시 중단하거나, \
  전송을 위해 채널이 닫힌 경우 예외를 throw합니다(자세한 내용은 닫기 참조).
- 채널을 닫는 것은 개념적으로 이 채널을 통해 특별한 "닫기 토큰"을 보내는 것과 같기 때문에 이 기능이 일시 중단된 후 채널을 닫는다고 이 일시 중단된 보내기 호출이 중단되지 않습니다. \
  채널을 통해 전송된 모든 요소는 선입선출 순서로 전달됩니다. 보낸 요소는 닫기 토큰 전에 수신자에게 전달됩니다.
- 이 일시 중지 기능은 취소할 수 있습니다. \
  이 함수가 일시 중단된 동안 현재 코루틴의 작업이 취소되거나 완료되면 이 함수는 CancellationException과 함께 즉시 재개됩니다. \
  신속한 취소 보장이 있습니다. 이 기능이 일시 중단된 동안 작업이 취소된 경우 성공적으로 재개되지 않습니다. \
  보내기 호출은 요소를 채널로 보낼 수 있지만 그런 다음 CancellationException을 throw하므로 예외가 요소 전달 실패로 처리되어서는 안 됩니다. \
  전달되지 않은 요소를 처리하는 방법에 대한 자세한 내용은 채널 문서의 "전달되지 않은 요소" 섹션을 참조하세요.

## Channel<Int>(Channel.BUFFERED)
- 지정된 버퍼 용량으로(또는 기본적으로 버퍼 없이) 채널을 만듭니다. 자세한 내용은 채널 인터페이스 설명서를 참조하십시오.

매개변수
capacity - 양의 채널 용량 또는 Channel.Factory에 정의된 상수 중 하나입니다.
onBufferOverflow - 버퍼 오버플로에 대한 작업을 구성합니다(선택 사항, 기본값은 값을 보내기 위한 일시 중단 시도로, \
  용량 >= 0 또는 용량 == Channel.BUFFERED인 경우에만 지원됨, 암시적으로 하나 이상의 버퍼링된 요소가 있는 채널을 생성함).
onUndeliveredElement - 요소가 전송되었지만 소비자에게 전달되지 않았을 때 호출되는 선택적 함수입니다. \
  채널 문서의 "전달되지 않은 요소" 섹션을 참조하세요.

```kotlin
public fun <E> Channel(
    capacity: Int = RENDEZVOUS,
    onBufferOverflow: BufferOverflow = BufferOverflow.SUSPEND,
    onUndeliveredElement: ((E) -> Unit)? = null
): Channel<E> =
    when (capacity) {
        RENDEZVOUS -> {
            if (onBufferOverflow == BufferOverflow.SUSPEND)
                RendezvousChannel(onUndeliveredElement) // an efficient implementation of rendezvous channel
            else
                ArrayChannel(1, onBufferOverflow, onUndeliveredElement) // support buffer overflow with buffered channel
        }
        CONFLATED -> {
            require(onBufferOverflow == BufferOverflow.SUSPEND) {
                "CONFLATED capacity cannot be used with non-default onBufferOverflow"
            }
            ConflatedChannel(onUndeliveredElement)
        }
        UNLIMITED -> LinkedListChannel(onUndeliveredElement) // ignores onBufferOverflow: it has buffer, but it never overflows
        BUFFERED -> ArrayChannel( // uses default capacity with SUSPEND
            if (onBufferOverflow == BufferOverflow.SUSPEND) CHANNEL_DEFAULT_CAPACITY else 1,
            onBufferOverflow, onUndeliveredElement
        )
        else -> {
            if (capacity == 1 && onBufferOverflow == BufferOverflow.DROP_OLDEST)
                ConflatedChannel(onUndeliveredElement) // conflated implementation is more efficient but appears to work in the same way
            else
                ArrayChannel(capacity, onBufferOverflow, onUndeliveredElement)
        }
    }
```

- 지정된 버퍼 용량으로(또는 기본적으로 버퍼 없이) 채널을 만듭니다.
- 자세한 내용은 채널 인터페이스 설명서를 참조하십시오.