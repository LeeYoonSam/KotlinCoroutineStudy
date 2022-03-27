# SharedFlow

## SharedFlow - MutableSharedFlow
- 주어진 구성 매개변수를 사용하여 MutableSharedFlow를 만듭니다.
- 이 함수는 지원되지 않는 매개변수 값 또는 이들의 조합에 대해 IllegalArgumentException을 발생시킵니다.

매개변수:
replay - 새 구독자에게 재생되는 값의 수입니다(음수일 수 없으며 기본값은 0).

extraBufferCapacity - 재생 외에 버퍼링된 값의 수. 방출은 버퍼 공간이 남아 있는 동안 일시 중단되지 않습니다(선택 사항, 음수가 될 수 없으며 기본값은 0).

onBufferOverflow - 버퍼 오버플로에 대한 방출 작업을 구성합니다. 선택 사항, 기본값은 값을 내보내려는 시도를 일시 중단하는 것입니다. \
  BufferOverflow.SUSPEND 이외의 값은 재생 > 0 또는 extraBufferCapacity > 0인 경우에만 지원됩니다. \
  버퍼 오버플로는 새 값을 수락할 준비가 되지 않은 구독자가 하나 이상 있는 경우에만 발생할 수 있습니다. \
  구독자가 없는 경우 가장 최근의 재생 값만 저장되고 버퍼 오버플로 동작은 트리거되지 않으며 영향을 미치지 않습니다.

```kotlin
public fun <T> MutableSharedFlow(
    replay: Int = 0,
    extraBufferCapacity: Int = 0,
    onBufferOverflow: BufferOverflow = BufferOverflow.SUSPEND
): MutableSharedFlow<T> {
    require(replay >= 0) { "replay cannot be negative, but was $replay" }
    require(extraBufferCapacity >= 0) { "extraBufferCapacity cannot be negative, but was $extraBufferCapacity" }
    require(replay > 0 || extraBufferCapacity > 0 || onBufferOverflow == BufferOverflow.SUSPEND) {
        "replay or extraBufferCapacity must be positive with non-default onBufferOverflow strategy $onBufferOverflow"
    }
    val bufferCapacity0 = replay + extraBufferCapacity
    val bufferCapacity = if (bufferCapacity0 < 0) Int.MAX_VALUE else bufferCapacity0 // coerce to MAX_VALUE on overflow
    return SharedFlowImpl(replay, bufferCapacity, onBufferOverflow)
}
```

### StateFlow - MutableStateFlow
- 값에 대한 설정자를 제공하는 변경 가능한 StateFlow입니다. \
  MutableStateFlow(value) 생성자 함수를 사용하여 초기 값이 지정된 MutableStateFlow의 인스턴스를 생성할 수 있습니다.
- 상태 흐름에 대한 자세한 내용은 StateFlow 설명서를 참조하세요.
- 상속에 대해 안정적이지 않음
- MutableStateFlow 인터페이스는 미래에 이 인터페이스에 새로운 메서드가 추가될 수 있으므로 타사 라이브러리의 상속에 대해 안정적이지 않지만 사용하기에 안정적입니다. \
  MutableStateFlow() 생성자 함수를 사용하여 구현을 만듭니다.

```kotlin
public interface MutableStateFlow<T> : StateFlow<T>, MutableSharedFlow<T> {
    /**
     * The current value of this state flow.
     *
     * Setting a value that is [equal][Any.equals] to the previous one does nothing.
     *
     * This property is **thread-safe** and can be safely updated from concurrent coroutines without
     * external synchronization.
     */
    public override var value: T

    /**
     * Atomically compares the current [value] with [expect] and sets it to [update] if it is equal to [expect].
     * The result is `true` if the [value] was set to [update] and `false` otherwise.
     *
     * This function use a regular comparison using [Any.equals]. If both [expect] and [update] are equal to the
     * current [value], this function returns `true`, but it does not actually change the reference that is
     * stored in the [value].
     *
     * This method is **thread-safe** and can be safely invoked from concurrent coroutines without
     * external synchronization.
     */
    public fun compareAndSet(expect: T, update: T): Boolean
}
```