# 채널 팬아웃, 팬인
[https://dalinaum.github.io/coroutines-example/19](https://dalinaum.github.io/coroutines-example/19)


## 팬 아웃

- 여러 코루틴이 동시에 채널을 구독할 수 있습니다.

```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*

fun CoroutineScope.produceNumbers() = produce<Int> {
    var x = 1
    while (true) {
        send(x++)
        delay(100L)
    }
}

// 각각 launch 를 통해서 새로운 코루틴을 만들어내게 되고 그 코루틴에서 채널을 구독하게 됩니다.
fun CoroutineScope.processNumber(id: Int, channel: ReceiveChannel<Int>) = launch {
    channel.consumeEach {
        println("${id}가 ${it}을 받았습니다.")
    }
}

fun main() = runBlocking<Unit> {
    val producer = produceNumbers()
    repeat (5) {
        processNumber(it, producer)
    }
    delay(1000L)
    producer.cancel()
}
```

[Kotlin Playground](https://pl.kotl.in/MuZd_wVHs)

- 총 5개 (0~4)의 코루틴에서 각자 채널에서 값을 하나씩 가져가고 있고 한번 가져간 값은 다른 곳에서 가져갈수가 없습니다.



## 팬 인

- 팬 인은 반대로 생산자가 많은 것입니다.

```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*

suspend fun produceNumbers(channel: SendChannel<Int>, from: Int, interval: Long) {
    var x = from
    while (true) {
        channel.send(x)
        x += 2
        delay(interval)
    }
}

fun CoroutineScope.processNumber(channel: ReceiveChannel<Int>) = launch {
    channel.consumeEach {
        println("${it}을 받았습니다.")
    }
}

fun main() = runBlocking<Unit> {
    val channel = Channel<Int>() // Channel = Receive Channel (receive) + Send Channel (send)
    launch {
        produceNumbers(channel, 1, 100L) // 1, 3, 5, 7, 9 .. // 100ms 잠을자면서 채널에 값을 보냄
    }
    launch {
        produceNumbers(channel, 2, 150L) // 2, 4, 6, 8, 10 .. // 150ms 잠을자면서 채널에 값을 보냄
    } // 생산자 2, 소비자 1
    processNumber(channel)
    delay(1000L)
    coroutineContext.cancelChildren()
}
```

[Kotlin Playground](https://pl.kotl.in/uUWhVXFXD)

[https://pl.kotl.in/MuZd_wVHs](https://pl.kotl.in/MuZd_wVHs)

- `coroutineContext`의 자식이 아닌 본인을 취소하면 어떻게 될까요?
- `processNumber`를 suspend 함수의 형태로 변형하면 어떻게 될까요?
- 다른 방법으로 취소할 수 있을까요?



## 공정한 채널

- 두 개의 코루틴에서 채널을 서로 사용할 때 공정하게 기회를 준다는 것을 알 수 있습니다.

```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*

suspend fun someone(channel: Channel<String>, name: String) {
    for (comment in channel) {
        println("${name}: ${comment}") // 민준: 패스트 캠퍼스 // 서연:
        channel.send(comment.drop(1) + comment.first()) // "스트 캠퍼스" + "패" 서연: 트 캠퍼스패스
        delay(100L)
    }
}

fun main() = runBlocking<Unit> {
    val channel = Channel<String>()
    launch {
        someone(channel, "민준")
    }
    launch {
        someone(channel, "서연")
    }
    channel.send("패스트 캠퍼스")
    delay(1000L)
    coroutineContext.cancelChildren()
}
```

[Kotlin Playground](https://pl.kotl.in/3EVOgu64F)

- 채널이 공평하게 기회를 부여하고 있습니다.



## select

- 먼저 끝나는 요청을 처리하는 것이 중요할 수 있습니다. 이 경우에 `select`를 쓸 수 있습니다.

```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.selects.*

// 리턴값이 리시브채널
fun CoroutineScope.sayFast() = produce<String> {
		// 코루틴 스코프 + 샌드채널
    while (true) {
        delay(100L)
        send("패스트")
    }
}

// 리턴값이 리시브채널
fun CoroutineScope.sayCampus() = produce<String> {
		// 코루틴 스코프 + 샌드채널
    while (true) {
        delay(150L)
        send("캠퍼스")
    }
}

fun main() = runBlocking<Unit> {
    val fasts = sayFast()
    val campuses = sayCampus()
    repeat (5) { // 5번 동안 select. // 2
        select<Unit> { // 먼저 끝내는 것만 처리 하겠다.
            fasts.onReceive {
                println("fast: $it") // fast: 패스트
            }
            campuses.onReceive {
                println("campus: $it") // campus: 캠퍼스
            }
        }
    }
    coroutineContext.cancelChildren()
}
```

[Kotlin Playground](https://pl.kotl.in/2v4KZqxO8)

채널에 대해 `onReceive`를 사용하는 것 이외에도 아래의 상황에서 사용할 수 있습니다.

- `Job` - `onJoin`
- `Deferred` - `onAwait`
- `SendChannel` - `onSend`
- `ReceiveChannel` - `onReceive`, `onReceiveCatching`
- `delay` - `onTimeout`