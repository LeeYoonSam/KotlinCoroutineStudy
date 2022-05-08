# 채널 파이프 라인
[https://dalinaum.github.io/coroutines-example/18](https://dalinaum.github.io/coroutines-example/18)


## 파이프라인
- 파이프 라인은 일반적인 패턴입니다.
- 하나의 스트림을 프로듀서가 만들고, 다른 코루틴에서 그 스트림을 읽어 새로운 스트림을 만드는 패턴.
- 채널을 이용해서 채널을 생성하는 형태를 `파이프라인`이라고 합니다.

```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*

fun CoroutineScope.produceNumbers() = produce<Int> { // 1, 2, 3, 4, 5, 6, 7, 8...
    var x = 1
    while (true) {
        send(x++)
    }
}

fun CoroutineScope.produceStringNumbers(numbers: ReceiveChannel<Int>): ReceiveChannel<String> = produce {
    for (i in numbers) {
        send("${i}!") // "1!", "2!", ...
    }
}

fun main() = runBlocking<Unit> {
    val numbers = produceNumbers() // 1, 2, 3, 4, 5, 6, 7, 8..., // number: 채널, 리시브 채널. 리시브 메서드. send 메서드 X. 리시브 채널 + 샌드 채널이 합쳐져 있다.
    val stringNumbers = produceStringNumbers(numbers) // "1!", "2!", ...

    repeat(5) { // "1!", "2!", ... // for(x in stringNumbers) X
        println(stringNumbers.receive()) // 명시적 receive
    }
    println("완료")
    coroutineContext.cancelChildren() // produceNumbers, produceStringNumbers 모두 취소를 시킵니다.
}
```

[Kotlin Playground](https://pl.kotl.in/nkozas7qI)

- 파이프라인을 이용하면 여러 채널을 이용해서 데이터를 순차적으로 가공할수 있습니다.


## 홀수 필터

- 파이프라인을 응용해 홀수 필터를 만들어 봅시다.

```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*

// 첫번째 파이프
fun CoroutineScope.produceNumbers() = produce<Int> { // 1, 2, 3, 4, 5, 6, 7 .. // 리시브 채널 반환
    var x = 1
    while (true) {
        send(x++)
    }
}

fun CoroutineScope.filterOdd(numbers: ReceiveChannel<Int>): ReceiveChannel<String> = produce { // 샌드 채널
		// produce 내에서만 send 를 할 수 있습니다.
    for (i in numbers) {
        if (i % 2 == 1) { // 1!, 3!, 5! ..
            send("${i}!")
        }
    }
}

fun main() = runBlocking<Unit> {
    val numbers = produceNumbers() // numbers 리시브 채널, send X
		// numbers를 filterOdd 를 통해 데이터를 가공
    val oddNumbers = filterOdd(numbers) // send X

    repeat(10) {
        println(oddNumbers.receive())
    }
    println("완료")
    coroutineContext.cancelChildren()
}
```

[Kotlin Playground](https://pl.kotl.in/Sca28KlMz)

- 여러개 채널을 순차적으로 붙여서 데이터를 가공 해나가면서 응용할 수 있습니다.


## 소수 필터

- 파이프라인을 연속으로 타면서 원하는 결과를 얻을 수 있습니다.

```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*

fun CoroutineScope.numbersFrom(start: Int) = produce<Int> { // 샌드채널 + CoroutineScope
    var x = start
    while (true) {
        send(x++)
    }
}

fun CoroutineScope.filter(numbers: ReceiveChannel<Int>, prime: Int): ReceiveChannel<Int> = produce {
    for (i in numbers) {
        if (i % prime != 0) {
            send(i)
        }
    }
}

fun main() = runBlocking<Unit> {
    var numbers = numbersFrom(2) // 리시브 채널, 3, 4, 5 // loop 가 돌때마다 채널 대체

    repeat(10) {
        val prime = numbers.receive() // 2
        println(prime)
        numbers = filter(numbers, prime) // numbers 3, 4, 5 prime 2
    }
    println("완료")
    coroutineContext.cancelChildren()
}
```

[Kotlin Playground](https://pl.kotl.in/1HRKWmBNr)

- 누가 이렇게 할까 싶은 예제입니다. 원한다면 디스패처를 이용해 CPU 자원을 효율적으로 이용하는 것이 가능합니다.
- 이런식으로도 파이프라인을 확장할수 있다는 의미로 받아 들여지면 좋을것 같습니다.