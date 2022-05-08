# 채널 기초
[https://dalinaum.github.io/coroutines-example/17](https://dalinaum.github.io/coroutines-example/17)

- 코루틴간의 데이터를 교환할 수 있는 채널


## 채널

- 채널은 일종의 파이프입니다.
- 송신측에서 채널에 `send`로 데이터를 전달하고 수신 측에서 채널을 통해 `receive` 받습니다. (`trySend`와 `tryReceive`도 있습니다. 과거에는 `null`을 반환하는 `offer`와 `poll`가 있었습니다.)

```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*

fun main() = runBlocking<Unit> {
    val channel = Channel<Int>() // Int 타입이 다니고 있는 파이프
    launch {
        for (x in 1..10) {
            channel.send(x) // suspension point
        }
    }

    repeat(10) {
        println(channel.receive()) // suspension point
    }
    println("완료")
}
```

[Kotlin Playground](https://pl.kotl.in/iqYrJ-vjN)

- `Channle.send()` 에서 받는 사람이 없으면 잠이 들었다가 받은 이후에 깨어나서 다음 데이터를 보냅니다.
- `Channel.receive()` 데이터가 없는 경우에는 잠이 들었다가 데이터가 들어온 이후에 깨어나서 수행합니다.
- `trySend`, `tryReceive` 는 suspension point 가 없기 때문에 기다리지 않는 함수라고 볼 수 있고 특별한 경우에만 사용합니다.


## 같은 코루틴에서 채널을 읽고 쓰면?

- `send`나 `receive`가 suspension point이고 서로에게 의존적이기 때문에 같은 코루틴에서 사용하는 것은 위험할 수 있습니다.

```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*

fun main() = runBlocking<Unit> {
    val channel = Channel<Int>()
    launch {
        for (x in 1..10) {
            channel.send(x)
        }

        repeat(10) {
            println(channel.receive())
        }
        println("완료")
    }
}
```

[Kotlin Playground](https://pl.kotl.in/PZcRM-SF4)

- 무한으로 대기하는 것을 볼 수 있습니다.
- 현재 코드의 `launch` 에 `send` 와 `receive` 가 있는데 만약 `send` 에서 수신자가 없으면 `launch` 블럭 자체가 잠이 들기 때문에 `receive` 를 실행할수 없고 반대의 케이스도 마찬가지입니다.
    - 별도의 코루틴을 만들어서 서로 잠이 들어서 문제가 없도록 코드를 작성해야 합니다.


## 채널 close

- 채널에서 더 이상 보낼 자료가 없으면 `close` 메서드를 이용해 채널을 닫을 수 있습니다.
- 채널은 for in 을 이용해서 반복적으로 `receive` 할 수 있고 `close` 되면 for in은 자동으로 종료됩니다.

```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*

fun main() = runBlocking<Unit> {
    val channel = Channel<Int>()
    launch {
        for (x in 1..10) {
            channel.send(x)
        }
        channel.close()
    }

    for (x in channel) {
        println(x)
    }
    println("완료")
}
```

[Kotlin Playground](https://pl.kotl.in/HdGE9hXm4)

- `channel.receive()` 로 받지 않고 `for 문`으로 받아서 처리를 할 수 있습니다.
- 채널이 `close` 가 되어있지 않은 상태에서 `for 문`을 사용하면 무한 대기 상태가 됩니다.
- `close` 가 되는것을 정확하게 알고 있을때 사용해야 합니다.


## 채널 프로듀서

- `생산자(producer)`와 `소비자(consumer)`는 굉장히 일반적인 패턴입니다. 채널을 이용해서 한 쪽에서 데이터를 만들고 다른 쪽에서 받는 것을 도와주는 확장 함수들이 있습니다.
    1. `produce` 코루틴을 만들고 채널을 제공합니다.
    2. `consumeEach` 채널에서 반복해서 데이터를 제공합니다.

- `ProducerScope`는 `CoroutineScope` 인터페이스와 `SendChannel` 인터페이스를 함께 상속받습니다. 그래서 코루틴 컨텍스트와 몇가지 채널 인터페이스를 같이 사용할 수 있는 특이한 스코프입니다.
- `produce`를 사용하면 `ProducerScope`를 상속받은 `ProducerCoroutine` 코루틴을 얻게 됩니다.

### 참고
- 우리가 흔히 쓰는 `runBlocking`은 `BlockingCoroutine`을 쓰는데 이는 `AbstractCoroutine`를 상속받고 있어요.
- 결국 코루틴 빌더는 코루틴을 만드는데 이들이 코루틴 스코프이기도 한거죠.
- `AbstractCoroutine`은 `JobSupport`, `Job`(인터페이스), `Continuation`(인터페이스), `CoroutineScope`(인터페이스)을 상속받고 있고요.
- `Continuation`은 다음에 무엇을 할지, `Job`은 제어를 위한 정보와 제어, `CoroutineScope`는 컨텍스트 제공의 역할을 합니다. `JobSupport`는 잡의 실무(?)를 한다고 봐야죠.

```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*

fun main() = runBlocking<Unit> {
    val oneToTen = produce { // ProducerScope = CoroutineScope + SendChannel
				// this.send // this.coroutineContext
        for (x in 1..10) {
            channel.send(x)
        }
    }

    oneToTen.consumeEach {
        println(it)
    }
    println("완료")
}
```

[Kotlin Playground](https://pl.kotl.in/zLLVN6HUk)

- `produce` 파트를 함수로 분리해봅시다.
- `suspend` 함수와 `CoroutineScope`의 확장 함수의 방식을 해봅시다. (`produce`는 `CoroutineScope`의 확장 함수)
- 채널 생성 , 코루틴 생성 후 `send` 하는 여러 작업을 `produce` 하나로 처리를 합니다.
    - 채널을 만들고 반환
    - 내부적으로 코루틴 블록 생성
    - 별도의 코루틴에서 코드 블럭을 실행하고 코드에게도 채널을 제공