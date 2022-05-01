# 1-3 취소와 타임아웃
https://dalinaum.github.io/coroutines-example/3

## Job에 대해 취소

- 명시적인 Job에 대해 cancel 메서드를 호출해 취소할 수 있습니다.

```kotlin
import kotlinx.coroutines.*

suspend fun doOneTwoThree() = coroutineScope {
    val job1 = launch {
        println("launch1: ${Thread.currentThread().name}")
        delay(1000L)
        println("3!")
    }

    val job2 = launch {
        println("launch2: ${Thread.currentThread().name}")
        println("1!")
    }

    val job3 = launch {
        println("launch3: ${Thread.currentThread().name}")
        delay(500L)
        println("2!")
    }

    delay(800L)
    job1.cancel()
    job2.cancel()
    job3.cancel()
    println("4!")
}

fun main() = runBlocking {
    doOneTwoThree()
    println("runBlocking: ${Thread.currentThread().name}")
    println("5!")
}
```

- `delay` 간격들을 변경해 보면서 테스트 해보세요.
- `cancel` 은 더이상 작업을 하지말라는 뜻과 동일

## 취소 불가능한 Job

- 아래의 예제는 취소가 불가능한 Job 입니다.
- `launch(Dispatchers.Default)`는 그 다음 코드 블록을 다른 스레드에서 수행을 시킬 것입니다.

```kotlin
import kotlinx.coroutines.*

suspend fun doCount() = coroutineScope {
    val job1 = launch(Dispatchers.Default) {
        var i = 1
        var nextTime = System.currentTimeMillis() + 100L

        while (i <= 10) {
            val currentTime = System.currentTimeMillis()
            if (currentTime >= nextTime) {
                println(i)
                nextTime = currentTime + 100L
                i++
            }
        }
    }

    delay(200L)
    job1.cancel()
    println("doCount Done!")
}

fun main() = runBlocking {
    doCount()
}
```

- 2가지 신경쓰이는 부분
    - `job1` 이 취소든 종료든 다 끝난 이후에 `doCount Done!` 을 출력하고 싶다.
    - 취소가 되지 않았다.
- 먼저 취소든 종료든 다 끝난 이후에 `doCount Done!` 을 출력 합시다.

# cancel 과 join

- 실제 작업이 끝난 후에 출력하기 위해서는 `cancel` 과 `join` 을 사용해야 합니다.

```kotlin
import kotlinx.coroutines.*

suspend fun doCount() = coroutineScope {
    val job1 = launch(Dispatchers.Default) {
        var i = 1
        var nextTime = System.currentTimeMillis() + 100L

        while (i <= 10) {
            val currentTime = System.currentTimeMillis()
            if (currentTime >= nextTime) {
                println(i)
                nextTime = currentTime + 100L
                i++
            }
        }
    }

    delay(200L)
    job1.cancel()
    job1.join() // cancel 을하고 실제 cancel 이 될때까지 대기
    println("doCount Done!")
}

fun main() = runBlocking {
    doCount()
}
```

- `cancel` 이후에 `join` 을 넣어서 실제로 `doCount` 가 끝날 때 `doCount Done!` 가 출력하게 했습니다.

# cancleAndJoin

- `cancel`을 하고 `join`을 하는 일은 자주 일어나는 일이기 때문에 한번에 하는 `cancelAndJoin`이 준비되어 있습니다.

```kotlin
import kotlinx.coroutines.*

suspend fun doCount() = coroutineScope {
    val job1 = launch(Dispatchers.Default) {
        var i = 1
        var nextTime = System.currentTimeMillis() + 100L

        while (i <= 10) {
            val currentTime = System.currentTimeMillis()
            if (currentTime >= nextTime) {
                println(i)
                nextTime = currentTime + 100L
                i++
            }
        }
    }

    delay(200L)
    job1.cancelAndJoin()
    println("doCount Done!")
}

fun main() = runBlocking {
    doCount()
}
```

# cancel 가능한 코루틴

- `isActive`를 호출하면 해당 코루틴이 여전히 활성화된지 확인할 수 있습니다. `isActive`를 루프에 추가해봅시다.

```kotlin
import kotlinx.coroutines.*

suspend fun doCount() = coroutineScope {
    val job1 = launch(Dispatchers.Default) {
        var i = 1
        var nextTime = System.currentTimeMillis() + 100L

        while (i <= 10 && isActive) {
            val currentTime = System.currentTimeMillis()
            if (currentTime >= nextTime) {
                println(i)
                nextTime = currentTime + 100L
                i++
            }
        }
    }

    delay(200L)
    job1.cancelAndJoin()
    println("doCount Done!")
}

fun main() = runBlocking {
    doCount()
}
```

- `isActive` 를 통해서 이 코루틴이 활성화 되어있는지 확인을 할수 있습니다.

# finally를 같이 사용

- `launch`에서 자원을 할당한 경우에는 어떻게 정리해야할까요?
- `suspend` 함수들은 `JobCancellationException`를 발생하기 때문에 표준 try catch finally로 대응할 수 있습니다.
- 파일이나 소켓등을 `launch` 에서 사용하고 취소가 되었을 경우 안전하게 종료하는 처리

```kotlin
import kotlinx.coroutines.*

suspend fun doOneTwoThree() = coroutineScope {
    val job1 = launch {
        try {
            println("launch1: ${Thread.currentThread().name}")
            delay(1000L)
            println("3!")
        } finally {
            println("job1 is finishing!")
						// 실제로는 파일, 소켓을 닫아주는 코드 등을 입력
        }
    }

    val job2 = launch {
        try {
            println("launch2: ${Thread.currentThread().name}")
            delay(1000L)
            println("1!")
        } finally {
            println("job2 is finishing!")
        }
    }

    val job3 = launch {
        try {
            println("launch3: ${Thread.currentThread().name}")
            delay(1000L)
            println("2!")
        } finally {
            println("job3 is finishing!")
        }
    }

    delay(800L)
    job1.cancel()
    job2.cancel()
    job3.cancel()
    println("4!")
}

fun main() = runBlocking {
    doOneTwoThree()
    println("runBlocking: ${Thread.currentThread().name}")
    println("5!")
}
```

- 취소 가능한 `Job` 에서 취소가 되었을때 `finally` 를 사용해서 처리

# 취소 불가능한 블록

- 어떤 코드는 취소가 불가능해야 합니다.
- `withContext(NonCancellable)`을 이용하면 취소 불가능한 블록을 만들 수 있습니다.

```kotlin
import kotlinx.coroutines.*

suspend fun doOneTwoThree() = coroutineScope {
    val job1 = launch {
        withContext(NonCancellable) {
            println("launch1: ${Thread.currentThread().name}")
            delay(1000L)
            println("3!")
        }
        delay(1000L)
        print("job1: end")
    }

    val job2 = launch {
        withContext(NonCancellable) {
            println("launch1: ${Thread.currentThread().name}")
            delay(1000L)
            println("1!")
        }
        delay(1000L)
        print("job2: end")
    }

    val job3 = launch {
        withContext(NonCancellable) {
            println("launch1: ${Thread.currentThread().name}")
            delay(1000L)
            println("2!")
        }
        delay(1000L)
        print("job3: end")
    }

    delay(800L)
    job1.cancel()
    job2.cancel()
    job3.cancel()
    println("4!")
}

fun main() = runBlocking {
    doOneTwoThree()
    println("runBlocking: ${Thread.currentThread().name}")
    println("5!")
}
```

- 취소 불가능한 코드를 `finally`절에 사용할 수도 있습니다.

# 타임 아웃

- 일정 시간이 끝난 후에 종료하고 싶다면 `withTimeout`을 이용할 수 있습니다.

```kotlin
import kotlinx.coroutines.*

suspend fun doCount() = coroutineScope {
    val job1 = launch(Dispatchers.Default) {
        var i = 1
        var nextTime = System.currentTimeMillis() + 100L

        while (i <= 10 && isActive) {
            val currentTime = System.currentTimeMillis()
            if (currentTime >= nextTime) {
                println(i)
                nextTime = currentTime + 100L
                i++
            }
        }
    }
}

fun main() = runBlocking {
    withTimeout(500L) {
        doCount()
    }
}
```

- 취소가 되면 `TimeoutCancellationException` 예외가 발생합니다.
- withTimeout 을 try, catch 로 예외처리를 하기는 번거롭기 때문에 `withTimeoutOrNull`을 사용해서 처리를 합니다.

# withTimeoutOrNull

- 예외를 핸들하는 것은 귀찮은 일입니다.
- `withTimeoutOrNull`을 이용해 타임 아웃할 때 `null`을 반환하게 할 수 있습니다.

```kotlin
import kotlinx.coroutines.*

suspend fun doCount() = coroutineScope {
    val job1 = launch(Dispatchers.Default) {
        var i = 1
        var nextTime = System.currentTimeMillis() + 100L

        while (i <= 10 && isActive) {
            val currentTime = System.currentTimeMillis()
            if (currentTime >= nextTime) {
                println(i)
                nextTime = currentTime + 100L
                i++
            }
        }
    }
}

fun main() = runBlocking {
    val result = withTimeoutOrNull(500L) {
        doCount()
        true
    } ?: false
    println(result)
}
```

- 성공할 경우 `whithTimeoutOrNull`의 마지막에서 `true`를 리턴하게 하고 실패했을 경우 `null`을 반환할테니 엘비스 연산자(`?:`)를 이용해 `false`를 리턴하게 했습니다.
    - 엘비스 연산자는 `null` 값인 경우에 다른 값으로 치환합니다.
- 코틀린의 예외는 식(expression)이어 활용이 어렵지는 않습니다만 개인적으로는 `null`을 리턴하고 엘비스 연산자로 다루는게 더 편한 것 같습니다.