# 플로우 완료처리하기
[https://dalinaum.github.io/coroutines-example/15](https://dalinaum.github.io/coroutines-example/15)


## 명령형 finally 블록

- 완료를 처리하는 방법 중의 하나는 명령형의 방식으로 `finally` 블록을 이용하는 것입니다.

```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

fun simple(): Flow<Int> = (1..3).asFlow()

fun main() = runBlocking<Unit> {
    try {
        simple().collect { value -> println(value) }
    } finally {
        println("Done")
    }
}
```

[Kotlin Playground - finally](https://pl.kotl.in/I8NFNo4CM)

## 선언적으로 완료 처리하기

- `onCompletion` 연산자를 선언해서 완료를 처리할 수 있습니다.

```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

fun simple(): Flow<Int> = (1..3).asFlow()

fun main() = runBlocking<Unit> {
    simple()
        .onCompletion { println("Done") }
        .collect { value -> println(value) }
}
```

[Kotlin Playground - onCompletion](https://pl.kotl.in/XmamBth2E)

- 중간에 예외가 발생하더라도 `onCompletion` 은 작동합니다.


## onCompletion 의 장점

- `onCompletion`은 종료 처리를 할 때 예외가 발생되었는지 여부를 알 수 있습니다.

```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

fun simple(): Flow<Int> = flow {
    emit(1)
    throw RuntimeException()
}

fun main() = runBlocking<Unit> {
    simple()
        .onCompletion { cause -> if (cause != null) println("Flow completed exceptionally") }
        .catch { cause -> println("Caught exception") }
        .collect { value -> println(value) }
}
```

[Kotlin Playground - onCompletion cause](https://pl.kotl.in/L5b0r5qHp)

- `finally` 에서는 문제가 발생했는지 알수 없지만 `onCompletion` 에서는 `cause` 의 값에 따라 에러 발생 여부를 알수 있는것이 가장 큰 장점입니다.