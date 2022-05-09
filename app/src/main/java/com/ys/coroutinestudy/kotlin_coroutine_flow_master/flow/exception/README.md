# 플로우 예외 처리하기
[https://dalinaum.github.io/coroutines-example/14](https://dalinaum.github.io/coroutines-example/14)

## 수집기 측에서 예외처리하기

- 예외는 `collect`을 하는 수집기 측에서도 try-catch 식을 이용해 할 수 있습니다.

```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

fun simple(): Flow<Int> = flow {
    for (i in 1..3) {
        println("Emitting $i")
        emit(i) // emit next value
    }
}

fun main() = runBlocking<Unit> {
    try {
        simple().collect { value ->
            println(value)
            check(value <= 1) { "Collected $value" }
        }
    } catch (e: Throwable) {
        println("Caught $e")
    }
}
```

[Kotlin Playground - try-catch](https://pl.kotl.in/-k826rbPw)


## 모든 예외는 처리가능

- 어느 곳에서 발생한 예외라도 처리가 가능합니다.

```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

fun simple(): Flow<String> = 
    flow {
        for (i in 1..3) {
            println("Emitting $i")
            emit(i) // emit next value
        }
    }
    .map { value ->
        check(value <= 1) { "Crashed on $value" }
        "string $value"
    }

fun main() = runBlocking<Unit> {
    try {
        simple().collect { value -> println(value) }
    } catch (e: Throwable) {
        println("Caught $e")
    }
}
```

[Kotlin Playground - 모든 예외 처리](https://pl.kotl.in/wEYuhsVGr)

## 예외 투명성

- 빌더 코드 블록 내에서 예외를 처리하는 것은 예외 투명성을 어기는 것입니다. 플로우에서는 `catch` 연산자를 이용하는 것을 권합니다.
- `catch` 블록에서 예외를 새로운 데이터로 만들어 `emit`을 하거나, 다시 예외를 던지거나, 로그를 남길 수 있습니다.

```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

fun simple(): Flow<String> = 
    flow {
        for (i in 1..3) {
            println("Emitting $i")
            emit(i) // emit next value
        }
    }
    .map { value ->
        check(value <= 1) { "Crashed on $value" }
        "string $value"
    }

fun main() = runBlocking<Unit> {
    simple()
        .catch { e -> emit("Caught $e") } // emit on exception
        .collect { value -> println(value) }
}
```

[Kotlin Playground - catch](https://pl.kotl.in/GbWYFdd3S)

- `catch` 연산자의 `업스트림(simple() 함수)`에서 예외가 발생해서 오류를 발견하고 처리


## catch 투명성

- `catch` 연산자는 업스트림(catch 연산자를 쓰기 전의 코드)에만 영향을 미치고 다운스트림에는 영향을 미치지 않습니다. 이를 catch 투명성이라 합니다.

```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

fun simple(): Flow<Int> = flow {
    for (i in 1..3) {
        println("Emitting $i")
        emit(i)
    }
}

fun main() = runBlocking<Unit> {
    simple()
        .catch { e -> println("Caught $e") } // does not catch downstream exceptions
        .collect { value ->
            check(value <= 1) { "Collected $value" }
            println(value)
        }
}
```

[Kotlin Playground - catch 투명성](https://pl.kotl.in/ksYuE1RUp)

- 이 코드에서는 `catch` 연산자의 `다운스트림(collect)` 에서 에러가 발생하므로 `catch` 에서 오류를 발견할 수 없습니다.