# 플로우 런칭
[https://dalinaum.github.io/coroutines-example/16](https://dalinaum.github.io/coroutines-example/16)

## 이벤트를 Flow 로 처리하기

- `addEventListener` 대신 플로우의 `onEach`를 사용할 수 있습니다. 이벤트마다 `onEach`가 대응하는 것입니다.

```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

fun events(): Flow<Int> = (1..3).asFlow().onEach { delay(100) }

fun main() = runBlocking<Unit> {
    events()
        .onEach { event -> println("Event: $event") }
        .collect() // 스트림이 끝날 때 까지 기다리게 됩니다. 이벤트는 -> 계속 발생
    println("Done")
}
```

[Kotlin Playground - onEach](https://pl.kotl.in/mv3Q6dXHL)

- `collect`가 플로가 끝날 때 까지 기다리는 것이 문제입니다.


## launchIn 을 사용하여 런칭하기

- `launchIn`을 이용하면 별도의 코루틴에서 플로우를 런칭할 수 있습니다.

```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

fun events(): Flow<Int> = (1..3).asFlow().onEach { delay(100) }

fun main() = runBlocking<Unit> { // this: 코루틴 스코프, 코루틴
    events()
        .onEach { event -> println("Event: $event") }
        .launchIn(this) // 코루틴 스코프 // 새로운 코루틴
    println("Done")
}
```

[Kotlin Playground - launchIn](https://pl.kotl.in/lxgNfA_DE)

- `launchIn`
    - 계속 이벤트가 발생해서 추적을 하면서 대응하려면 `launchIn` 을 통해서  다른 코루틴에서 우리 이벤트를 감시할 것을 동작을 시켜야 하는것
    - 이벤트와 같이 상태가 바뀌는것을 추적할 때는 launchIn 을 통해서 다른 코루틴에서 관측하는 것이 더 유리합니다.