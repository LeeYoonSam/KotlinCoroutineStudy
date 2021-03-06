# 플로우 연산
[https://dalinaum.github.io/coroutines-example/9](https://dalinaum.github.io/coroutines-example/9)

## 플로우와 map

- 플로우에서 `map` 연산을 통해 데이터를 가공할 수 있습니다.

```kotlin
import kotlin.random.Random
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

fun flowSomething(): Flow<Int> = flow {
    repeat(10) {
        emit(Random.nextInt(0, 500))
        delay(10L)
    }
}

fun main() = runBlocking {
    flowSomething().map {
        "$it $it"
    }.collect { value ->
        println(value)
    }
}
```

## 플로우와 filter

- `filter` 기능을 이용해 짝수만 남겨봅니다.

```kotlin
import kotlin.random.Random
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

fun main() = runBlocking<Unit> {
    (1..20).asFlow().filter {
        (it % 2) == 0 // 술어, predicate
    }.collect {
        println(it)
    }
}
```

## filterNot

- 만약 홀수만 남기고 싶을 때 술어(predicate)를 수정 할 수도 있습니다.
- 하지만 술어를 그대로 두고 `filterNot` 을 사용 할 수도 있습니다.

```kotlin
import kotlin.random.Random
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

fun main() = runBlocking<Unit> {
    (1..20).asFlow().filterNot {
        (it % 2) == 0
    }.collect {
        println(it)
    }
}
```

## transform 연산자

- `transform` 연산자를 이용해 조금 더 유연하게 스트림을 변형할 수 있습니다.

```kotlin
import kotlin.random.Random
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

suspend fun someCalc(i: Int): Int {
    delay(10L)
    return i * 2
}

fun main() = runBlocking<Unit> {
    (1..20).asFlow().transform {
        emit(it)
        emit(someCalc(it))
    }.collect {
        println(it)
    }
}
```

## take 연산자

- `take` 연산자는 몇개의 수행 결과만 취합니다.

```kotlin
import kotlin.random.Random
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

suspend fun someCalc(i: Int): Int {
    delay(10L)
    return i * 2
}

fun main() = runBlocking<Unit> {
    (1..20).asFlow().transform {
        emit(it)
        emit(someCalc(it))
    }.take(5)
    .collect {
        println(it)
    }
}
```

## takeWhile 연산자

`takeWhile` 을 이용해 조건을 만족하는 동안만 값을 가져오게 할 수 있습니다.

```kotlin
import kotlin.random.Random
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

suspend fun someCalc(i: Int): Int {
    delay(10L)
    return i * 2
}

fun main() = runBlocking<Unit> {
    (1..20).asFlow().transform {
        emit(it)
        emit(someCalc(it))
    }.takeWhile {
        it < 15
    }.collect {
        println(it)
    }
}
```

- `takeWhile` 의 조건으로 첫번째 값이 만족하지 못하면 뒤따르는 값들은 조건에 만족하더라도 출력이 되지 않습니다.

## drop 연산자

- `drop` 연산자는 처음 몇개의 결과를 버립니다. `take`가 `takeWhile`을 가지듯 `dropWhile`도 있습니다.

```kotlin
import kotlin.random.Random
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

suspend fun someCalc(i: Int): Int {
    delay(10L)
    return i * 2
}

fun main() = runBlocking<Unit> {
    (1..20).asFlow().transform {
        emit(it)
        emit(someCalc(it))
    }.drop(5)
    .collect {
        println(it)
    }
}
```

## reduce 연산자

- `collect`, `reduce`, `fold`, `toList`, `toSet` 과 같은 연산자는 플로우를 끝내는 함수라 종단 연산자(terminal operator)라고 합니다.
- `reduce` 는 흔히 `map` 과 `reduce` 로 함께 소개되는 함수형 언어의 오래된 메커니즘 입니다.
- 첫번째 값을 결과에 넣은 후 각 값을 가져와 누진적으로 계산합니다.

```kotlin
import kotlin.random.Random
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

fun main() = runBlocking<Unit> {
    val value = (1..10)
        .asFlow()
        .reduce { a, b ->
            a + b
        }
    println(value)
}
```

## fold 연산자

- `fold` 연산자는 `reduce`와 매우 유사합니다. 초기값이 있다는 차이만 있습니다.

```kotlin
import kotlin.random.Random
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

fun main() = runBlocking<Unit> {
    val value = (1..10)
        .asFlow()
        .fold(10) { a, b ->
            a + b
        }
    println(value)
}
```

## count 연산자

- `count`의 연산자는 술어를 만족하는 자료의 갯수를 셉니다. 짝수의 갯수를 세어봅시다.

```kotlin
import kotlin.random.Random
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

fun main() = runBlocking<Unit> {
    val counter = (1..10)
        .asFlow()
        .count {
            (it % 2) == 0
        }
    println(counter)
}
```

- `count` 는 종단 연산자, terminal operator. 특정 값, 컬렉션, 결과
- 중간 연산자(`filter`, `map` 등) = 결과 X