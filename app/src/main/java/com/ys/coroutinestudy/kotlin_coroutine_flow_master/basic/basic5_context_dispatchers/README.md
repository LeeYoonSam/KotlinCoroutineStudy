# 코루틴 컨텍스트와 디스패처

[https://dalinaum.github.io/coroutines-example/5](https://dalinaum.github.io/coroutines-example/5)

# 코루틴 디스패처

- 코루틴의 여러 디스패처 `Default`, `IO`, `Unconfined`, `newSingleThreadContext`을 사용해봅시다.

```kotlin
import kotlinx.coroutines.*

fun main() = runBlocking<Unit> {
    launch {
        println("부모의 컨텍스트 / ${Thread.currentThread().name}")
    }

    launch(Dispatchers.Default) {
        println("Default / ${Thread.currentThread().name}")
    }

    launch(Dispatchers.IO) {
        println("IO / ${Thread.currentThread().name}")
    }

    launch(Dispatchers.Unconfined) {
        println("Unconfined / ${Thread.currentThread().name}")
    }

    launch(newSingleThreadContext("Fast Campus")) {
        println("newSingleThreadContext / ${Thread.currentThread().name}")
    }
}
```

1. `Default`는 코어 수에 비례하는 스레드 풀에서 수행합니다.
2. `IO`는 코어 수 보다 훨씬 많은 스레드를 가지는 스레드 풀입니다. IO 작업은 CPU를 덜 소모하기 때문입니다.
3. `Unconfined`는 어디에도 속하지 않습니다. 지금 시점에는 부모의 스레드에서 수행될 것입니다.
4. `newSingleThreadContext`는 항상 새로운 스레드를 만듭니다.

# async에서 코루틴 디스패처 사용

- `launch`외에 `async`, `withContext`등의 코루틴 빌더에도 디스패처를 사용할 수 있습니다.

```kotlin
import kotlinx.coroutines.*

fun main() = runBlocking<Unit> {
    async {
        println("부모의 콘텍스트 / ${Thread.currentThread().name}")
    }

    async(Dispatchers.Default) {
        println("Default / ${Thread.currentThread().name}")
    }

    async(Dispatchers.IO) {
        println("IO / ${Thread.currentThread().name}")
    }

    async(Dispatchers.Unconfined) {
        println("Unconfined / ${Thread.currentThread().name}")
    }

    async(newSingleThreadContext("Fast Campus")) {
        println("newSingleThreadContext / ${Thread.currentThread().name}")
    }
}
```

- 한번이라도 잠이 들었다가 깨어나면 어디서 수행할지 모르는것이  Unconfied 입니다.


# Confined 디스패처 테스트

- `Confined`는 처음에는 부모의 스레드에서 수행됩니다. 하지만 한번 중단점(suspension point)에 오면 바뀌게 됩니다.

```kotlin
import kotlinx.coroutines.*

fun main() = runBlocking<Unit> {
    async(Dispatchers.Unconfined) {
        println("Unconfined / ${Thread.currentThread().name}")
        delay(1000L)
        println("Unconfined / ${Thread.currentThread().name}")
    }
}
```

- `Confined` 는 중단점 이후 어느 디스패처에서 수행될지 예측하기 어렵습니다. 가능하면 확실한 디스패처를 사용합시다.

# 부모가 있는 Job과 없는 Job

- 코루틴 스코프, 코루틴 컨텍스트는 구조화되어 있고 부모에게 계층적으로 되어 있습니다. 코루틴 컨텍스트의 `Job` 역시 부모에게 의존적입니다. 부모를 캔슬했을 때의 영향을 확인해보세요.

```kotlin
import kotlinx.coroutines.*

fun main() = runBlocking<Unit> {
    val job = launch {
        launch(Job()) {
            println(coroutineContext[Job])
            println("launch1: ${Thread.currentThread().name}")
            delay(1000L)
            println("3!")
        }

        launch {
            println(coroutineContext[Job])
            println("launch2: ${Thread.currentThread().name}")
            delay(1000L)
            println("1!")
        }
    }

    delay(500L)
    job.cancelAndJoin()
    delay(1000L)
}
```

- `job.cancelAndJoin()` 실행 후의 `delay`가 없다면 어떻게 될까요?

# 부모의 마음

- 구조화되어 계층화된 코루틴은 자식들의 실행을 지켜볼까요?

```kotlin
import kotlin.system.*
import kotlinx.coroutines.*

fun main() = runBlocking<Unit> {
    val elapsed = measureTimeMillis {
        val job = launch { // 부모
            launch { // 자식 1
                println("launch1: ${Thread.currentThread().name}")
                delay(5000L)
            }

            launch { // 자식 2
                println("launch2: ${Thread.currentThread().name}")
                delay(10L)
            }
        }
        job.join()
    }
    println(elapsed)
}
```

- 부모를 `join` 해서 기다려 보면 부모는 두 자식이 모두 끝날 때까지 기다린다는 것을 알 수 있습니다.

# 코루틴 엘리먼트 결합

- 여러 코루틴 엘리먼트를 한번에 사용할 수 있다. `+` 연산으로 엘리먼트를 합치면 된다. 합쳐진 엘리먼트들은 `coroutineContext[XXX]` 로 조회할 수 있다.

```kotlin
import kotlin.system.*
import kotlinx.coroutines.*

fun main() = runBlocking<Unit> {
    launch {
        launch(Dispatchers.IO + CoroutineName("launch1")) {
            println("launch1: ${Thread.currentThread().name}")
            println(coroutineContext[CoroutineDispatcher])
            println(coroutineContext[CoroutineName])
            delay(5000L)
        }

        launch(Dispatchers.Default + CoroutineName("launch2")) {
            println("launch2: ${Thread.currentThread().name}")
            println(coroutineContext[CoroutineDispatcher])
            println(coroutineContext[CoroutineName])
            delay(10L)
        }
    }
}
```