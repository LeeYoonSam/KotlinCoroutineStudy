# 플로우 버퍼링
[https://dalinaum.github.io/coroutines-example/11](https://dalinaum.github.io/coroutines-example/11)

데이터를 만드는 생산과 소비자가 같은 속도로 움직일수 없기 때문에 버퍼를 만들어서 유연하게 만들 수 있습니다.

## 버퍼가 없는 플로우

- 보내는 쪽과 받는 쪽이 모두 바쁘다고 가정해봅시다.

```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.system.*

fun simple(): Flow<Int> = flow {
    for (i in 1..3) {
        delay(100)
        emit(i)
    }
}

fun main() = runBlocking<Unit> { 
    val time = measureTimeMillis {
        simple().collect { value -> 
            delay(300)
            println(value)
        } 
    }
    println("Collected in $time ms")
}
```

[Kotlin PlayGround](https://pl.kotl.in/lNFwpSN5J)

## buffer

- `buffer`로 버퍼를 추가해 보내는 측이 더 이상 기다리지 않게 합니다.
- `flow(생산측)`에 버퍼를 붙이면 `collect` 의 준비 유무에 관계없이 계속 데이터를 보낼수 있기 때문에 전체적인 지연시간을 줄일 수 있습니다.

```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.system.*

fun simple(): Flow<Int> = flow {
    for (i in 1..3) {
        delay(100)
        emit(i)
    }
}

fun main() = runBlocking<Unit> { 
    val time = measureTimeMillis {
        simple().buffer()
            .collect { value -> 
                delay(300)
                println(value)
            } 
    }
    println("Collected in $time ms")
}
```

[Kotlin PlayGround](https://pl.kotl.in/zMQGVGjXu)

## conflate

- `conflate`를 이용하면 중간의 값을 융합(conflate)할 수 있습니다.
- 처리보다 빨리 발생한 데이터의 중간 값들을 누락합니다.

```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.system.*

fun simple(): Flow<Int> = flow {
    for (i in 1..3) {
        delay(100)
        emit(i)
    }
}

fun main() = runBlocking<Unit> { 
    val time = measureTimeMillis {
        simple().conflate()
            .collect { value -> 
                delay(300)
                println(value)
            } 
    }
    println("Collected in $time ms")
}
```

[Kotlin PlayGround](https://pl.kotl.in/v6gLkvpHS)

## 마지막 값만 처리하기

- `conflate`와 같이 방출되는 값을 누락할 수도 있지만 수집 측이 느릴 경우 새로운 데이터가 있을 때 수집 측을 종료시키고 새로 시작하는 방법도 있습니다.
- `collectLatest`를 사용합니다.
- `collectLatest` 동작
    - 첫번째 값을 받고 처리하는 과정중에 두번째 값이 오면 리셋
    - 두번째 값을 받고 처리하는 과정중에 세번째 값이 오면 다시 리셋

```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.system.*

fun simple(): Flow<Int> = flow {
    for (i in 1..3) {
        delay(100)
        emit(i)
    }
}

fun main() = runBlocking<Unit> { 
    val time = measureTimeMillis {
        simple().collectLatest { value -> 
            println("값 ${value}를 처리하기 시작합니다.")
            delay(300)
            println(value)
            println("처리를 완료하였습니다.")
        }
    }   
    println("Collected in $time ms")
}
```

[Kotlin PlayGround](https://pl.kotl.in/_smANQCY3)