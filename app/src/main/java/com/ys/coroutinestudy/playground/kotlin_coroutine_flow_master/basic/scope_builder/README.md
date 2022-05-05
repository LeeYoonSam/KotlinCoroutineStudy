# 스코프 빌더

[https://dalinaum.github.io/coroutines-example/1](https://dalinaum.github.io/coroutines-example/1)

## 코루틴빌더

코루틴을 만드는 함수를 `코루틴빌더` 라고 합니다.

- `runBlocking` : 코루틴을 만들고 코드 블록의 수행이 끝날 때까지 runBlocking 다음의 코드를 수행하지 못하게 막습니다.
- `runBlocking` 안에서 `this` 를 수행하면 코루틴이 수신 객체(Receiver)인 것을 알 수 있습니다.
    - `"coroutine#1":BlockingCoroutine{Active}@3930015a` 이런 형태의 결과가 나옵니다

### 코루틴의 시작은 코루틴 스코프다.

```kotlin
import kotlinx.coroutines.*

fun main() = runBlocking {
    println(this)
    println(Thread.currentThread().name)
    println("Hello")
}
```

## 코루틴 컨텍스트

- 코루틴 스코프는 코루틴을 제대로 처리하기 위한 정보, 코루틴 컨텍스트(CoroutineContext)를 가지고 있습니다.
    - 수신 객체의 coroutineContext 를 호출
        - [CoroutineId(1), "coroutine#1":BlockingCoroutine{Active}@25618e91, BlockingEventLoop@7a92922]
    - 여러 정보를 통해 코루틴이 앞으로 수행될 방식들을 결정

```kotlin
import kotlinx.coroutines.*

fun main() = runBlocking {
    println(coroutineContext)
    println(Thread.currentThread().name)
    println("Hello")
}
```

## launch 코루틴 빌더

- 코루틴 내에서 다른 코루틴을 수행
- launch 란 빌더를 사용해서 코드를 수행
    - launch 는 코루틴 빌더입니다.
    - 새로운 코루틴을 만들기 때문에 새로운 코루틴 스코프를 만들게 됩니다.
    - launch 는 할수 있다면 다른 코루틴 코드를 같이 수행 시키는 코루틴 빌더입니다.
- launch 는 우리가 만들었던 코드를 큐에 넣어 놓고 다음 순서를 기다리고 있습니다.

```kotlin
import kotlinx.coroutines.*

fun main() = runBlocking {
    launch {
        println("launch: ${Thread.currentThread().name}")
        println("World!")
    }
    println("runBlocking: ${Thread.currentThread().name}")
    println("Hello")
}
```

## delay 함수

- 쓰레드를 다른 코루틴이 사용할수 있게 양보하는 것입니다.
- 코루틴의 장점은 하나의 스레드에서 양보를 하면서 최선의 결과를 얻어낼수 있습니다.
- delay 가 호출될때마다 현재 코드블록이 잠들게 되는데 이것을 서스펜션 포인트라고 부릅니다.(중단점)

```kotlin
import kotlinx.coroutines.*

fun main() = runBlocking {
    launch {
        println("launch: ${Thread.currentThread().name}")
        println("World!")
    }
    println("runBlocking: ${Thread.currentThread().name}")
    delay(500L)
    println("Hello")
}
```

## 용어

Expression Body

```kotlin
fun main() = runBlocking {
	...
}
```

- 함수 블록을 대신 = 으로 표시

## **코루틴 내에서 sleep**

- 일반적인 Thread.sleep 을 호출하게 되면 결과가 달라집니다.
- delay 는 쉬는동안 양보를 하지만 sleep 은 양보하지 않고 그대로 가지고 있습니다.

```kotlin
import kotlinx.coroutines.*

fun main() = runBlocking {
    launch {
        println("launch: ${Thread.currentThread().name}")
        println("World!")
    }
    println("runBlocking: ${Thread.currentThread().name}")
    Thread.sleep(500)
    println("Hello")
}
```

## 한번에 여러 launch

- delay 가 중요한 역할을 합니다.
- delay 값에 따라 suspend 된 이후 깨어나는 순서에 따라 출력 결과다 달라집니다.

```kotlin
import kotlinx.coroutines.*

fun main() = runBlocking {
    launch {
        println("launch1: ${Thread.currentThread().name}")
        delay(1000L)
        println("3!")
    }
    launch {
        println("launch2: ${Thread.currentThread().name}")
        println("1!")
    }
    println("runBlocking: ${Thread.currentThread().name}")
    delay(500L)
    println("2!")
}
```

## 상위 코루틴은 하위 코루틴을 끝까지 책임진다.

- `runBlocking` 안에 두 `launch` 가 속해 있는데 계층화되어 있어 구조적입니다.
- `runBlocking`은 그 안에 포함된 `launch`가 다 끝나기 전까지 종료되지 않습니다.
- 코틀린에서 코루틴은 부모가 취소되면 자식도 취소가 되는 계층적 구조로 되어있다.
    - 한번에 취소가 되기 때문에 관리가 편하다

```kotlin
import kotlinx.coroutines.*

fun main() {
    runBlocking {
        launch {
            println("launch1: ${Thread.currentThread().name}")
            delay(1000L)
            println("3!")
        }
        launch {
            println("launch2: ${Thread.currentThread().name}")
            println("1!")
        }
        println("runBlocking: ${Thread.currentThread().name}")
        delay(500L)
        println("2!")
    }
    print("4!")
}
```

## suspend 함수

- `delay`  `launch`  등 지금까지 봤던 함수들은 코루틴 내에서만 호출할수 있는데 이 함수들을 포함한 코드들을 어떻게 함수로 분리할 수 있을까요?
- 코드의 일부를 함수로 분리할 때는 함수의 앞에 `suspend` 키워드를 붙이면 됩니다.
- `delay` 는 반드시 코루틴 안이거나 suspend fun 안에서만 사용할수 있습니다.
- `doOne()` 함수 같은 경우에는 `delay` 를 사용하지 않기 때문에 `fun` 으로 사용해도 문제가 없습니다.
- `runBlocking<Type>` 을 지정해서 `return type` 을 지정할 수 있습니다.
    - 지금까지 없었던 이유는 컴파일러가 코루틴 함수 안에서 리턴값이 없는것을 알수 있는경우 기본적으로 `Unit` 을 붙여주기 때문에 `type` 을 지정하지 않았습니다.

```kotlin
import kotlinx.coroutines.*

suspend fun doThree() {
    println("launch1: ${Thread.currentThread().name}")
    delay(1000L)
    println("3!")
}

suspend fun doOne() {
    println("launch1: ${Thread.currentThread().name}")
    println("1!")
}

suspend fun doTwo() {
    println("runBlocking: ${Thread.currentThread().name}")
    delay(500L)
    println("2!")
}

fun main() = runBlocking {
    launch {
        doThree()
    }
    launch {
        doOne()
    }
    doTwo()
}
```