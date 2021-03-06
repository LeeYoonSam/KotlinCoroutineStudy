# 서스펜딩 함수
[https://dalinaum.github.io/coroutines-example/4](https://dalinaum.github.io/coroutines-example/4)


# suspend 함수들의 순차적인 수행

- 순차적으로 `suspend` 함수를 먼저 수행시켜봅시다.

```kotlin
import kotlin.random.Random
import kotlin.system.*
import kotlinx.coroutines.*

suspend fun getRandom1(): Int {
    delay(1000L)
    return Random.nextInt(0, 500)
}

suspend fun getRandom2(): Int {
    delay(1000L)
    return Random.nextInt(0, 500)
}

fun main() = runBlocking {
    val elapsedTime = measureTimeMillis {
        val value1 = getRandom1()
        val value2 = getRandom2()
        println("${value1} + ${value2} = ${value1 + value2}")
    }
    println(elapsedTime)
}
```

- `import kotlin.random.Random`은 `Random.nextInt`를 위해 추가하였습니다.
- `import kotlin.system.*`는 `measureTimeMillis`를 위해 추가하였습니다.
- 대략 2000ms 이상 수행된다는 것을 볼 수 있습니다.
- 순차적으로 수행되었기 때문에 `getRandom1`이 1000ms 정도를 소비하고 `getRandom2`가 1000ms 정도 소비하는 것입니다.


# async를 이용해 동시 수행하기

- `aync` 키워드를 이용하면 동시에 다른 블록을 수행할 수 있습니다. `launch`와 비슷하게 보이지만 수행 결과를 `await`키워드를 통해 받을 수 있다는 차이가 있습니다.
- 결과를 받아야 한다면 `async`, 결과를 받지 않아도 된다면 `launch`를 선택할 수 있습니다.
- `await` 키워드를 만나면 `async` 블록이 수행이 끝났는지 확인하고 아직 끝나지 않았다면 `suspend`되었다 나중에 다시 깨어나고 반환값을 받아옵니다.
- `await` 는 `job.join() + 결과` 도 가져온다고 볼수 있습니다.

```kotlin
import kotlin.random.Random
import kotlin.system.*
import kotlinx.coroutines.*

suspend fun getRandom1(): Int {
    delay(1000L)
    return Random.nextInt(0, 500)
}

suspend fun getRandom2(): Int {
    delay(1000L)
    return Random.nextInt(0, 500)
}

fun main() = runBlocking {
    val elapsedTime = measureTimeMillis {
        val value1 = async { getRandom1() }
        val value2 = async { getRandom2() }
        println("${value1.await()} + ${value2.await()} = ${value1.await() + value2.await()}")
    }
    println(elapsedTime)
}
```

- 수행 결과를 보면 `getRandom1`과 `getRandom2`를 같이 수행해서 경과시간이 거의 반으로 줄어들었습니다.
- 많은 다른 언어들이 `async`, `await` 키워드를 가지고 있는데 그것과는 비슷하게 보이지만 조금 다릅니다. 코틀린은 `suspend` 함수를 호출하기 위해 어떤 키워드도 필요하지 않습니다. 코틀린의 `suspend`가 다른 언어에서 `async`와 같다고 보시면 됩니다.
- `async`, `await` 짝을 맞추는 것은 Microsoft .net C#의 영향으로 일반화되었는데 어떠한 키워드를 붙이지 않는 Go언어의 양향을 받아 가능한 제거하려 노력했다고 합니다. 그럼에도 불구하고 Java언어와의 호환성 때문에 `suspend`(`async`) 키워드는 버릴 수 없었습니다.
- `await` 을 호출하면 잠들었다가 깨어나서 코드를 처리하게 됩니다.


# async 게으르게 사용하기

```kotlin
import kotlin.random.Random
import kotlin.system.*
import kotlinx.coroutines.*

suspend fun getRandom1(): Int {
    delay(1000L)
    return Random.nextInt(0, 500)
}

suspend fun getRandom2(): Int {
    delay(1000L)
    return Random.nextInt(0, 500)
}

fun main() = runBlocking {
    val elapsedTime = measureTimeMillis {
        val value1 = async(start = CoroutineStart.LAZY) { getRandom1() }
        val value2 = async(start = CoroutineStart.LAZY) { getRandom2() }

        value1.start() // 큐에 수행을 예약 한다.
        value2.start()

        println("${value1.await()} + ${value2.await()} = ${value1.await() + value2.await()}")
    }
    println(elapsedTime)
}
```

- `async` 키워드를 사용하는 순간 코드 블록이 수행을 준비하는데, `async(start = CoroutineStart.LAZY)`로 인자를 전달하면 우리가 원하는 순간 수행을 준비하게 할 수 있습니다. 이후 `start` 메서드를 이용해 수행을 준비하게 할 수 있습니다.


# async를 사용한 구조적인 동시성

- 코드를 수행하다 보면 예외가 발생할 수 있습니다.
- 예외가 발생하면 위쪽의 코루틴 스코프와 아래쪽의 코루틴 스코프가 취소됩니다.

```kotlin
import kotlin.random.Random
import kotlin.system.*
import kotlinx.coroutines.*

suspend fun getRandom1(): Int {
    try {
        delay(1000L)
        return Random.nextInt(0, 500)
    } finally {
        println("getRandom1 is cancelled.")
    }
}

suspend fun getRandom2(): Int {
    delay(500L)
    throw IllegalStateException()
}

suspend fun doSomething() = coroutineScope { // 부모 코루틴 / 문제로 인해 캔슬
    val value1 = async { getRandom1() } // 자식 코루틴 // 문제로 인해 캔슬
    val value2 = async { getRandom2() } // 자식 코루틴 // 문제 발생
    try {
        println("${value1.await()} + ${value2.await()} = ${value1.await() + value2.await()}")
    } finally {
        println("doSomething is cancelled.")
    }
}

fun main() = runBlocking {
    try {
        doSomething()
    } catch (e: IllegalStateException) {
        println("doSomething failed: $e")
    }
}
```

- `getRandom2`가 오류가 나서 `getRandom1`와 `doSomething`은 취소됩니다.
- (`JobCancellationException` 발생) 문제가 된 `IllegalStateException`도 외부에서 잡아줘야 합니다.