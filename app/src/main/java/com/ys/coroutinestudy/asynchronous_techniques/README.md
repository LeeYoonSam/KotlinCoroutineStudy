# 비동기 프로그래밍 기법

수십 년 동안 개발자로서 우리는 해결해야 할 문제, 즉 애플리케이션이 차단되는 것을 방지하는 방법에 직면해 있습니다. </br>
데스크톱, 모바일 또는 서버 측 애플리케이션을 개발하든지 간에 사용자가 기다리거나 더 나쁜 것은 애플리케이션 확장을 방해하는 병목 현상을 유발하는 것을 피하고 싶습니다. </br>
</br>

다음을 포함하여 이 문제를 해결하기 위한 많은 접근 방식이 있었습니다.
- [Threading](https://kotlinlang.org/docs/async-programming.html#threading)
- [Callbacks](https://kotlinlang.org/docs/async-programming.html#callbacks)
- [Futures, promises, and others](https://kotlinlang.org/docs/async-programming.html#futures-promises-and-others)
- [Reactive Extensions](https://kotlinlang.org/docs/async-programming.html#reactive-extensions)
- [Coroutines](https://kotlinlang.org/docs/async-programming.html#coroutines)

코루틴이 무엇인지 설명하기 전에 다른 솔루션에 대해 간략히 살펴보겠습니다.

## Threading(스레딩)
스레드는 응용 프로그램이 차단되지 않도록 하는 가장 잘 알려진 접근 방식일 것입니다. </br>

```kotlin
fun postItem(item: Item) {
    val token = preparePost()
    val post = submitPost(token, item)
    processPost(post)
}

fun preparePost(): Token {
    // makes a request and consequently blocks the main thread
    return token
}
```

위의 코드에서 preparePost가 장기 실행 프로세스이고 결과적으로 사용자 인터페이스를 차단한다고 가정해 보겠습니다.</br>
우리가 할 수 있는 것은 별도의 스레드에서 실행하는 것입니다.</br>
그러면 UI가 차단되는 것을 방지할 수 있습니다.</br>
이것은 매우 일반적인 기술이지만 다음과 같은 일련의 단점이 있습니다.</br>

- 스레드는 저렴하지 않습니다. 스레드에는 비용이 많이 드는 컨텍스트 전환이 필요합니다.
- 스레드는 무한하지 않습니다. 실행할 수 있는 스레드 수는 기본 운영 체제에 의해 제한됩니다. 서버 측 응용 프로그램에서 이는 주요 병목 현상을 일으킬 수 있습니다.
- 스레드를 항상 사용할 수 있는 것은 아닙니다. JavaScript와 같은 일부 플랫폼은 스레드도 지원하지 않습니다.
- 스레드는 쉽지 않습니다. 스레드 디버깅, 경쟁 조건 방지는 다중 스레드 프로그래밍에서 우리가 겪는 일반적인 문제입니다.

## Callbacks(콜백)
콜백을 사용하면 한 함수를 매개변수로 다른 함수에 전달하고 프로세스가 완료되면 이 함수를 호출합니다.</br>

```kotlin
fun postItem(item: Item) {
    preparePostAsync { token ->
        submitPostAsync(token, item) { post ->
            processPost(post)
        }
    }
}

fun preparePostAsync(callback: (Token) -> Unit) {
    // make request and return immediately
    // arrange callback to be invoked later
}
```

이것은 원칙적으로 훨씬 더 우아한 솔루션처럼 느껴지지만 다시 한 번 몇 가지 문제가 있습니다.</br>

- 중첩된 콜백의 어려움. 일반적으로 콜백으로 사용되는 함수는 종종 자체 콜백을 필요로 합니다. 이것은 이해할 수 없는 코드로 이어지는 일련의 중첩된 콜백으로 이어집니다. 패턴은 종종 제목이 붙은 크리스마스 트리라고 합니다(중괄호는 트리의 가지를 나타냄).
- 오류 처리가 복잡합니다. 중첩 모델은 오류 처리 및 전파를 다소 복잡하게 만듭니다.

콜백은 JavaScript와 같은 이벤트 루프 아키텍처에서 매우 일반적이지만, 거기에서도 일반적으로 사람들은 약속이나 반응적 확장과 같은 다른 접근 방식을 사용하는 쪽으로 옮겨갔습니다.</br>


## Futures, promises, and others (퓨처, 프로미스 등)
Futures 또는 promises 뒤에 있는 아이디어(언어/플랫폼에 따라 참조할 수 있는 다른 용어도 있음)는 호출을 할 때 어느 시점에서 promises라는 객체와 함께 반환될 것이라고 약속한다는 것입니다.

```kotlin
fun postItem(item: Item) {
    preparePostAsync()
        .thenCompose { token ->
            submitPostAsync(token, item)
        }
        .thenAccept { post ->
            processPost(post)
        }

}

fun preparePostAsync(): Promise<Token> {
    // makes request and returns a promise that is completed later
    return promise
}
```

이 접근 방식을 사용하려면 특히 다음과 같이 프로그래밍 방식에 일련의 변경이 필요합니다.</br>
- 다른 프로그래밍 모델, 콜백과 유사하게 프로그래밍 모델은 하향식 명령형 접근 방식에서 연결 호출이 있는 구성 모델로 이동합니다. 루프, 예외 처리 등과 같은 전통적인 프로그램 구조는 일반적으로 이 모델에서 더 이상 유효하지 않습니다.
- 다른 API. 일반적으로 플랫폼에 따라 다를 수 있는 thenCompose 또는 thenAccept와 같은 완전히 새로운 API를 배울 필요가 있습니다.
- 특정 반환 유형. 반환 유형은 우리가 필요로 하는 실제 데이터에서 멀어지고 대신 자체 검사해야 하는 새 유형 Promise를 반환합니다.
- 오류 처리는 복잡할 수 있습니다. 오류의 전파 및 연결이 항상 간단한 것은 아닙니다.


## Reactive extensions(반응형 확장)
Rx(Reactive Extensions)는 Erik Meijer가 C#에 도입했습니다.</br>
확실히 .NET 플랫폼에서 사용되었지만 Netflix가 RxJava로 이름을 지정하여 Java로 이식할 때까지 주류 채택에 도달하지 못했습니다.</br>
이후 JavaScript(RxJS)를 비롯한 다양한 플랫폼에 대해 수많은 포트가 제공되었습니다.</br>
</br>
Rx의 이면에 있는 아이디어는 관찰 가능한 스트림으로 이동하여 이제 데이터를 스트림(무한한 양의 데이터)으로 생각하고 이러한 스트림을 관찰할 수 있도록 하는 것입니다.</br>
실용적인 측면에서 Rx는 단순히 데이터에 대해 작업할 수 있도록 하는 일련의 확장이 있는 [관찰자 패턴](https://en.wikipedia.org/wiki/Observer_pattern)입니다.</br>
</br>
접근 방식은 Future와 매우 유사하지만 Future는 개별 요소를 반환하는 것으로 생각할 수 있지만 Rx는 스트림을 반환합니다.</br>
그러나 이전과 유사하게 다음과 같이 유명한 프로그래밍 모델에 대한 완전히 새로운 사고 방식을 소개합니다.</br>
</br>
`everything is a stream, and it's observable`
</br>
이는 문제에 접근하는 다른 방법과 동기 코드를 작성할 때 사용하던 것과는 상당히 큰 차이가 있음을 의미합니다.</br>
Future와 반대되는 한 가지 이점은 C#, Java, JavaScript 또는 Rx를 사용할 수 있는 기타 언어에 관계없이 일반적으로 많은 플랫폼에 이식되어 일관된 API 경험을 찾을 수 있다는 것입니다.</br>
</br>
또한, Rx는 오류 처리에 대해 좀 더 나은 접근 방식을 도입합니다.</br>


## Coroutines(코루틴)
비동기 코드 작업에 대한 Kotlin의 접근 방식은 코루틴을 사용하는 것입니다. 이는 일시 중단 가능한 계산의 개념입니다. 즉, 함수가 특정 시점에서 실행을 일시 중단하고 나중에 다시 시작할 수 있다는 개념입니다.</br>

```kotlin
fun postItem(item: Item) {
    launch {
        val token = preparePost()
        val post = submitPost(token, item)
        processPost(post)
    }
}

suspend fun preparePost(): Token {
    // makes a request and suspends the coroutine
    return suspendCoroutine { /* ... */ }
}
```

이 코드는 메인 스레드를 차단하지 않고 장기 실행 작업을 시작합니다.</br>
preparePost는 suspendable 함수라고 하는 것이므로 suspend 키워드가 접두사로 붙습니다.</br>
위에서 언급한 바와 같이 이것이 의미하는 바는 함수가 실행되고, 실행을 일시 중지하고, 특정 시점에서 다시 시작된다는 것입니다.</br>
</br>
- 기능 서명은 정확히 동일하게 유지됩니다. 유일한 차이점은 일시 중단이 추가된다는 것입니다. 그러나 반환 유형은 반환하려는 유형입니다.
- 코드는 본질적으로 코루틴을 시작하는 launch라는 함수를 사용하는 것 외에 특별한 구문 없이 하향식으로 동기식 코드를 작성하는 것처럼 여전히 작성됩니다(다른 자습서에서 다룹니다).
- 프로그래밍 모델과 API는 동일하게 유지됩니다. 루프, 예외 처리 등을 계속 사용할 수 있으며 완전한 새 API 세트를 배울 필요가 없습니다.
- 플랫폼에 독립적입니다. JVM, JavaScript 또는 기타 플랫폼을 대상으로 하든 우리가 작성하는 코드는 동일합니다. 내부적으로 컴파일러는 각 플랫폼에 맞게 조정합니다.

코루틴은 Kotlin이 발명한 것은 고사하고 새로운 개념이 아닙니다. </br>
그들은 수십 년 동안 주변에 있었고 Go와 같은 다른 프로그래밍 언어에서 인기가 있습니다.</br>
그러나 주목해야 할 중요한 점은 Kotlin에서 구현되는 방식에서 대부분의 기능이 라이브러리에 위임된다는 것입니다.</br>
실제로 suspend 키워드 외에는 다른 키워드가 언어에 추가되지 않습니다.</br>
이것은 비동기가 있고 구문의 일부로 대기하는 C#과 같은 언어와 다소 다릅니다.</br>
Kotlin에서는 라이브러리 함수일 뿐입니다.


## 참고
### race condition
두 개 이상의 프로세스가 공통 자원을 병행적으로(concurrently) 읽거나 쓰는 동작을 할 때, 공용 데이터에 대한 접근이 어떤 순서에 따라 이루어졌는지에 따라 그 실행 결과가 같지 않고 달라지는 상황을 말한다.</br>
Race의 뜻 그대로, 간단히 말하면 경쟁하는 상태, 즉 두 개의 스레드가 하나의 자원을 놓고 서로 사용하려고 경쟁하는 상황을 말한다.</br>
</br>
그러나 코루틴의 이점 중 하나는 개발자의 경우 비차단 코드를 작성하는 것이 본질적으로 차단 코드를 작성하는 것과 동일하다는 것입니다. 프로그래밍 모델 자체는 실제로 변경되지 않습니다.</br>
</br>