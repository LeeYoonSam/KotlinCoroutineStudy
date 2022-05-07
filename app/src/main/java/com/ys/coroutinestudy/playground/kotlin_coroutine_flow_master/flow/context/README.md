# 플로우 컨텍스트
[https://dalinaum.github.io/coroutines-example/10](https://dalinaum.github.io/coroutines-example/10)

## 플로우는 코루틴 컨텍스트에서

- 플로우는 현재 코루틴 컨텍스트에서 호출 됩니다.

```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

fun log(msg: String) = println("[${Thread.currentThread().name}] $msg")

fun simple(): Flow<Int> = flow {
    log("flow를 시작합니다.")
    for (i in 1..10) {
        emit(i)
    }
}

fun main() = runBlocking<Unit> {
    launch(Dispatchers.IO) {
        simple()
            .collect { value -> log("${value}를 받음.") } 
    }
}
```

## 다른 컨텍스트로 옮겨갈 수 없는 플로우

```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

fun log(msg: String) = println("[${Thread.currentThread().name}] $msg")

fun simple(): Flow<Int> = flow {
    withContext(Dispatchers.Default) {
        for (i in 1..10) {
            delay(100L)
            emit(i)
        }
    }
}

fun main() = runBlocking<Unit> {
    launch(Dispatchers.IO) {
        simple()
            .collect { value -> log("${value}를 받음.") } 
    }
}
```

- flow 빌더 내에서는 컨텍스트를 바꿀 수 없기 때문에 `IllegalStateException` 이 발생합니다.

## flowOn 연산자

- `flowOn` 연산자를 통해 컨텍스트를 올바르게 변경 할 수 있습니다.
- 업스트림 대상을 어떤 컨텍스트에서 호출할지 결정 합니다.

```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

fun log(msg: String) = println("[${Thread.currentThread().name}] $msg")

fun simple(): Flow<Int> = flow {
    for (i in 1..10) {
        delay(100L)
        log("값 ${i}를 emit합니다.")
        emit(i)
    } // 업스트림 // Dispatchers.Default
}.map { // 업 스트림 // Dispatchers.Default
	it * 2 
}.flowOn(Dispatchers.Default) // 위치

fun main() = runBlocking<Unit> {
    simple().collect { value -> // 다운스트림
        log("${value}를 받음.")
    }
}
```