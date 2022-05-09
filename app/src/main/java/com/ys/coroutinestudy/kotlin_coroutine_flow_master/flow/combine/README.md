# 플로우 결합하기
[https://dalinaum.github.io/coroutines-example/12](https://dalinaum.github.io/coroutines-example/12)

## zip 으로 묶기

- `zip`은 양쪽의 데이터를 한꺼번에 묶어 새로운 데이터를 만들어 냅니다.
- 동시에 1개씩 값을 가져 옵니다.

```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

fun main() = runBlocking<Unit> { 
    val nums = (1..3).asFlow()
    val strs = flowOf("일", "이", "삼") 
    nums.zip(strs) { a, b -> "${a}은(는) $b" }
        .collect { println(it) }
}
```

[Kotlin Playground - zip](https://pl.kotl.in/xJSuH19Zh)

- 짝이 맞지 않으면 해당 데이터를 누락 시킵니다.

## combine 으로 묶기

- `combine`은 양쪽의 데이터를 같은 시점에 묶지 않고 한 쪽이 갱신되면 새로 묶어 데이터를 만듭니다.

```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

fun main() = runBlocking<Unit> { 
    val nums = (1..3).asFlow().onEach { delay(100L) }
    val strs = flowOf("일", "이", "삼").onEach { delay(200L) }
    nums.combine(strs) { a, b -> "${a}은(는) $b" }
        .collect { println(it) }
}
```

[Kotlin Playground - combine](https://pl.kotl.in/cDuYYJtP1)

- 예제에서는 적합한 코드는 아닙니다. 하지만 데이터가 짝을 이룰 필요없이 최신의 데이터를 이용해 가공해야 하는 경우에 사용할 수 있습니다.
- 짝과 관계없이 양쪽의 현재 값으로 묶습니다.