# 잡, 구조화된 동시성

## suspend 함수에서 코루틴 빌더 호출

```kotlin
import kotlinx.coroutines.*

suspend fun doOneTwoThree() {
    launch {
        println("launch1: ${Thread.currentThread().name}")
        delay(1000L)
        println("3!")
    }

    launch {
        println("launch2: ${Thread.currentThread().name}")
        println("1!")
    }

    launch {
        println("launch3: ${Thread.currentThread().name}")
        delay(500L)
        println("2!")
    }
    println("4!")
}

fun main() = runBlocking {
    doOneTwoThree()
    println("runBlocking: ${Thread.currentThread().name}")
    println("5!")
}
```

- `launch`, `runBlocking` 코드 블록 내에서 this(Receiver. 수신객체) → 코루틴
- 코루틴 빌더는 코루틴 스코프 내에서만 호출해야 합니다.
- launch 부분에 에러 발생 - Suspension functions 은 오직 coroutine body 에서 호출을 해야 합니다.
    - doOneTwoThree 는 코루틴 바디가 없어서 에러가 발생합니다.


# 코루틴 스코프

- 코루틴 스코프를 만드는 다른 방법은 스코프 빌더를 이용하는 것입니다.
- `coroutineScope` 를 이용해보세요.

```kotlin
import kotlinx.coroutines.*

suspend fun doOneTwoThree() = coroutineScope {
    launch {
        println("launch1: ${Thread.currentThread().name}")
        delay(1000L)
        println("3!")
    }

    launch {
        println("launch2: ${Thread.currentThread().name}")
        println("1!")
    }

    launch {
        println("launch3: ${Thread.currentThread().name}")
        delay(500L)
        println("2!")
    }
    println("4!")
}

fun main() = runBlocking {
    doOneTwoThree()
    println("runBlocking: ${Thread.currentThread().name}")
    println("5!")
}
```

- 코루틴 스코프는 `runBlocking` 썼을 때와 모양이 거의 비슷합니다.
- 둘의 차이는 `runBlocking` 은 현재 스레드를 멈추게 만들고 기다리지만 `coroutineScope` 는 현재 스레드를 멈추게 하지 않습니다. 호출한 쪽이 `suspend` 되고 시간이 되면 다시 활동하게 됩니다.
- `withContext`, `runBlocking` 가 일이 끝날때까지 스레드를 멈추게 만듭니다.

# Job 을 이용한 제어

- 코루틴 빌더 `launch`는 `Job` 객체를 반환하며 이를 통해 종료될 때까지 기다릴 수 있습니다.

```kotlin
import kotlinx.coroutines.*

suspend fun doOneTwoThree() = coroutineScope {
    val job = launch {
        println("launch1: ${Thread.currentThread().name}")
        delay(1000L)
        println("3!")
    }
    job.join() // suspension point

    launch {
        println("launch2: ${Thread.currentThread().name}")
        println("1!")
    }

    launch {
        println("launch3: ${Thread.currentThread().name}")
        delay(500L)
        println("2!")
    }
    println("4!")
}

fun main() = runBlocking {
    doOneTwoThree()
    println("runBlocking: ${Thread.currentThread().name}")
    println("5!")
}
```

- `job.join()` → suspension point, 첫번째 런치 블럭이 끝날때까지 잠들었다가 깨어나서 다음 코드를 호출
    - `delay` 가 있어도 해당 `launch` 블럭 코드 실행이 끝나기 전까지 대기를 합니다.

# 가벼운 코루틴

- 코루틴은 협력적으로 동작하기 때문에 여러 코루틴을 만드는 것이 큰 비용이 들지 않습니다.
- 10만개의 간단한 일을 하는 코루틴도 큰 부담은 아닙니다.(코틀린 플레이그라운드의 한계로 그렇게 많은 코루틴은 로그를 찍지 못합니다.)

```kotlin
import kotlinx.coroutines.*

suspend fun doOneTwoThree() = coroutineScope {
    val job = launch {
        println("launch1: ${Thread.currentThread().name}")
        delay(1000L)
        println("3!")
    }
    job.join()

    launch {
        println("launch2: ${Thread.currentThread().name}")
        println("1!")
    }

    repeat(1000) {
        launch {
            println("launch3: ${Thread.currentThread().name}")
            delay(500L)
            println("2!")
        }
    }
    println("4!")
}

fun main() = runBlocking {
    doOneTwoThree()
    println("runBlocking: ${Thread.currentThread().name}")
    println("5!")
}
```