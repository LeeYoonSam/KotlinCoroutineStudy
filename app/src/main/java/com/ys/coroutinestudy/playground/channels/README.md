# Channels

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