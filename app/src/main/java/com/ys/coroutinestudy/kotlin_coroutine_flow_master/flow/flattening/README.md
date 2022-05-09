# 플로우 플래트닝하기
[https://dalinaum.github.io/coroutines-example/13](https://dalinaum.github.io/coroutines-example/13)

## 플로우 플래트닝하기

- 플로우에서는 3가지 유형의 `flatMap`을 지원하고 있습니다.
- `flatMapConcat`, `flatMapMerge`, `flatMapLatest`입니다.


## flatMapConcat

- `flatMapConcat`은 첫번째 요소에 대해서 플래트닝을 하고 나서 두번째 요소를 합니다.

```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

fun requestFlow(i: Int): Flow<String> = flow {
    emit("$i: First") 
    delay(500) // wait 500 ms
    emit("$i: Second")    
}

fun main() = runBlocking<Unit> { 
    val startTime = System.currentTimeMillis() // remember the start time 
    (1..3).asFlow().onEach { delay(100) } // a number every 100 ms 
        .flatMapConcat {
            requestFlow(it)
        }
        .collect { value -> // collect and print 
            println("$value at ${System.currentTimeMillis() - startTime} ms from start") 
        }
}
```

[Kotlin Playground - flatMapConcat](https://pl.kotl.in/39x8C0Z4f)

- `flatMapConcat` 는 결과를 이어 붙이는 형태가 됩니다.
    - requestFlow(1) 모든 결과 .. requestFlow(2) 모든 결과


## flatMapMerge

- `flatMapMerge`는 첫 요소의 플래트닝을 시작하며 이어 다음 요소의 플래트닝을 시작합니다.

```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

fun requestFlow(i: Int): Flow<String> = flow {
    emit("$i: First") 
    delay(500) // wait 500 ms
    emit("$i: Second")    
}

fun main() = runBlocking<Unit> { 
    val startTime = System.currentTimeMillis() // remember the start time 
    (1..3).asFlow().onEach { delay(100) } // a number every 100 ms 
        .flatMapMerge {
             requestFlow(it)
        }                                                                           
        .collect { value -> // collect and print 
            println("$value at ${System.currentTimeMillis() - startTime} ms from start") 
        }
}
```

[Kotlin Playground - flatMapMerge](https://pl.kotl.in/Nr3j7yPos)

- 호출이 끝날때까지 기다리지 않고 `delay` 가 되는 동안 다음 요청을 진행해서 값을 합칩니다.
    - First 먼저 호출 Second 호출


## flatMapLatest

- `flatMapLatest`는 다음 요소의 플래트닝을 시작하며 이전에 진행 중이던 플래트닝을 취소합니다.

```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

fun requestFlow(i: Int): Flow<String> = flow {
    emit("$i: First") 
    delay(500) // wait 500 ms
    emit("$i: Second")    
}

fun main() = runBlocking<Unit> { 
    val startTime = System.currentTimeMillis() // remember the start time 
    (1..3).asFlow().onEach { delay(100) } // a number every 100 ms 
        .flatMapLatest {
            requestFlow(it)
        }                                                                           
        .collect { value -> // collect and print 
            println("$value at ${System.currentTimeMillis() - startTime} ms from start") 
        }
}
```

[Kotlin Playground - flatMapLatest](https://pl.kotl.in/5vnYTyUWH)