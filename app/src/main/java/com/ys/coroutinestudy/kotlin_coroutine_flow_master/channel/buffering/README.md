# 채널 버퍼링
[https://dalinaum.github.io/coroutines-example/20](https://dalinaum.github.io/coroutines-example/20)

- 채널은 2개 이상의 코루틴에서 안전하고 효율적으로 데이터를 구현할수 있는 방법 입니다.


## 버퍼

- 이전에 만들었던 예제를 확장하여 버퍼를 지정해보자. `Channel` 생성자는 인자로 버퍼의 사이즈를 지정 받는다.
- 지정하지 않으면 버퍼를 생성하지 않는다.

```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*

fun main() = runBlocking<Unit> {
    val channel = Channel<Int>(10) // 채널의 버퍼 갯수 10
    launch {
        for (x in 1..20) {
            println("${x} 전송중")
            channel.send(x) // 받던 안받던 채널로 계속 보낸다
        }
        channel.close()
    }

    for (x in channel) {
        println("${x} 수신")
        delay(100L)
    }
    println("완료")
}
```

[Kotlin Playground](https://pl.kotl.in/TTwla26IH)

- 채널에 인자로 `10`을 지정했다. 10개까지는 수신자가 받지 않아도 계속 전송한다.
- 버퍼가 있으면 채널을 조금 더 유연하게 구현 가능하다.


## 랑데뷰

- 버퍼 사이즈를 랑데뷰(Channel.RENDEZVOUS)로 지정해봅시다.

```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*

fun main() = runBlocking<Unit> {
    val channel = Channel<Int>(Channel.RENDEZVOUS) // 버퍼 갯수 0을 의미
    launch {
        for (x in 1..20) {
            println("${x} 전송중")
            channel.send(x)
        }
        channel.close()
    }

    for (x in channel) {
        println("${x} 수신")
        delay(100L)
    }
    println("완료")
}
```

[Kotlin Playground](https://pl.kotl.in/pi9E-EHTy)

- 랑데뷰는 버퍼 사이즈를 `0`으로 지정하는 것입니다. 생성자에 사이즈를 전달하지 않으면 랑데뷰가 디폴트입니다.
- 이외에도 사이즈 대신 사용할 수 있는 다른 설정 값이 있습니다.
    - `UNLIMITED` - 무제한으로 설정
        - LinkedList 형태로 새로운 데이터를 들어올때 계속 할당해서 붙여줍니다.
        - 메모리가 부족하면 런타임 에러 발생
    - `CONFLATED` - 오래된 값이 지워짐.
    - `BUFFERED` - 64개의 버퍼. 오버플로우엔 suspend


## 버퍼 오버플로우

- 버퍼의 오버플로우 정책에 따라 다른 결과가 나올 수 있습니다.

```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*

fun main() = runBlocking<Unit> {
    val channel = Channel<Int>(2, BufferOverflow.DROP_OLDEST)
    launch {
        for (x in 1..50) {
            channel.send(x)
        }
        channel.close()
    }

    delay(500L)

    for (x in channel) {
        println("${x} 수신")
        delay(100L)
    }
    println("완료")
}
```

[Kotlin Playground](https://pl.kotl.in/8UnRS66kD)

- `SUSPEND` - 잠이 들었다 깨어납니다.
- `DROP_OLDEST` - 예전 데이터를 지웁니다.
- `DROP_LATEST` - 새 데이터를 지웁니다.