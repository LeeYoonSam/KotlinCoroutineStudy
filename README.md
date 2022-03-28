# KotlinCoroutineStudy

## 루틴, routine

### 사전적 의미
> 컴퓨터 프로그램의 일부로서, 특정한 일을 실행하기 위한 일련의 명령

### 루틴과 서브루틴
- 컴퓨터 프로그래밍에서 루틴과 서브 루틴은 어떤 프로그램이 실행될 때 불려지거나 반복해서 사용되도록 만들어진 일련의 코드들을 지칭하는 용어이다.
- 이를 이용하면 프로그램을 더 짧으면서도 읽고 쓰기 쉽게 만들 수 있으며, \
  하나의 루틴이 다수의 프로그램에서 사용될 수 있어서 다른 프로그래머들이 코드를 다시 작성하지 않도록 해준다.
- 프로그램 로직의 주요 부분에서는 필요할 경우 공통 루틴으로 분기할 수 있으며, 해당 루틴의 작업이 완료되면 분기된 명령의 다음 명령으로 복귀한다.
- 어셈블러 언어에서는 매크로 명령어라 불리는 인터페이스를 가진 매크로 정의 부분에 변수의 입력을 필요로 하는 루틴이 코딩될 수 있다.
- 프로그래머는 루틴을 포함하거나 그 루틴으로 분기하는 대신 매크로 명령어를 사용할 수 있다. \
  매크로 정의 및 명령어는 다수의 프로그램 특히, 소프트웨어 개발 프로젝트에 참여한 프로그래머 사이에서 공유되는 경우가 많다.
- 고급 프로그래밍 언어에서는 공통적으로 필요한 많은 루틴이 미리 함수로 만들어진다. \
  어떤 함수는 프로그램의 다른 코드와 함께 컴파일 되고, \
  어떤 함수는 프로그램이 실행될 때 시스템 서비스를 위한 동적 호출(dynamic call)을 하는 부분에서 컴파일 되기도 한다.
- 함수들은 때때로 라이브러리 루틴이라고도 불린다. 컴파일러와 라이브러리 루틴은 대체로 관련 소프트웨어 개발 패키지의 일부분이 된다.
- 윈도우와 같은 PC의 운영체계에서는 특정 입출력 장치와 상호작용 하는 기능을 수행하는 시스템 루틴을 동적 링크 라이브러리(DLL) 루틴이라 한다. \
  이 루틴들은 처음 불려질 때 비로소 메모리에 실제 로딩되기 때문에 앞에 동적(動的)이라는 말이 붙는다.
- 비교적 최근에 쓰이기 시작한 용어인 `프로시저`도 의미상으로는 루틴과 거의 비슷하다.

### 서브루틴
> 프로그래밍에서는 함수안에 함수가 있을경우 안쪽의 함수를 서브 루틴이라 부른다.

```
Routine -----           ------->
            |           |
        SubRoutine ----->
```

```kotlin
fun routine1() {
    routine2() // 서브루틴
}

fun routine2() {
    ...
}
```

- 코드에서 `routine1()`을 실행하면 `routine2()`가 함수 안에서 수행된다. 이러한 `routine2`를 `routine1`의 서브루이라 부른다.\
  즉, 서브로 실행되는 함수를 서브루틴 이라고 부르는 것이다.
- 서브루틴은 루틴에 대해 순차적으로 수행되는 특징이 있다. 만약 루틴이 실행되지 않을경우 서브루틴 또한 수행되지 않는 것이다.


## Co-Routine 이란?
> 코루틴이란 함께(Co) 수행되는 함수(Routine) 이다.

```
Thread1
[Coroutine1]--- 1.작업1 수행 -> 2.양보 -------------- 5.작업1 재개 ---->
[Coroutine2]---------------- 3.작업2 수행 --------- 4.양보 -------->
```

- 여기서 `Coroutine1` 속에서 `Coroutine2` 가 수행되는 것이 아니다. 각각은 서로 다른 함수(Routine) 이며, 함께 수행되고 있다.
- 이들이 하나의 쓰레드를 점유하고 있을 때 한 `Routine`이 다른 `Routine`에게 Thread 점유 권한을 양보함으로써 함께 수행되는 것이다.
- 이것은 서브루틴과는 다른 개념이며, 루틴 속의 서브루틴은 무조건 순차적으로 실행되어야 하지만 `Coroutine`은 함께 수행되며 서로 무제한 양보를 할수 있다.

```
Thread1
[Coroutine1]--- 1.작업1 수행 -> 2.양보 -------------- 5.작업1 수행 ----------- 6.양보 ------------------->
[Coroutine2]---------------- 3.작업2 수행 --------- 4.양보 ---------------- 7.작업2 수행 ---> 8.양보 --->
```

- `Coroutine1`과 `Coroutine2`는 여러번 양보를 수행하며 같이 수행될 수 있다. 이렇게 하면 스레드의 자원을 최대한 활용 할수 있어 효과적이다.

### 참고
[Coroutine과 Subroutine의 차이](https://kotlinworld.com/214)
[Coroutine 은 어떻게 스레드 작업을 최적화 하는가?](https://kotlinworld.com/139)

## CoroutineScope.launch
- 현재 스레드를 차단하지 않고 새 코루틴을 시작하고 코루틴에 대한 참조를 작업으로 반환합니다.
- 결과 작업이 취소되면 코루틴이 취소됩니다.
- 코루틴 컨텍스트는 CoroutineScope에서 상속됩니다.
- 추가 컨텍스트 요소는 컨텍스트 인수로 지정할 수 있습니다.
- 컨텍스트에 디스패처나 다른 ContinuationInterceptor가 없으면 Dispatchers.Default가 사용됩니다.
- 상위 작업도 CoroutineScope에서 상속되지만 해당 컨텍스트 요소로 재정의될 수도 있습니다.
- 기본적으로 코루틴은 즉시 실행되도록 예약됩니다.
- 다른 시작 옵션은 시작 매개변수를 통해 지정할 수 있습니다. 자세한 내용은 CoroutineStart를 참조하세요.
- 선택적 시작 매개변수를 CoroutineStart.LAZY로 설정하여 코루틴을 느리게 시작할 수 있습니다. 이 경우 코루틴 Job은 새로운 상태로 생성됩니다.
- 시작 기능으로 명시적으로 시작할 수 있으며 조인의 첫 번째 호출에서 암시적으로 시작됩니다.
- 이 코루틴의 잡히지 않은 예외는 기본적으로 컨텍스트의 상위 작업을 취소합니다(CoroutineExceptionHandler가 명시적으로 지정되지 않은 경우). \
  이는 시작이 다른 코루틴의 컨텍스트와 함께 사용될 때 포착되지 않은 예외가 상위 코루틴의 취소로 이어진다는 것을 의미합니다.

```kotlin
public fun CoroutineScope.launch(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
): Job {
    val newContext = newCoroutineContext(context)
    val coroutine = if (start.isLazy)
        LazyStandaloneCoroutine(newContext, block) else
        StandaloneCoroutine(newContext, active = true)
    coroutine.start(start, coroutine, block)
    return coroutine
}
```

## CoroutineScope.async
- 코루틴을 만들고 Deferred의 구현으로 미래 결과를 반환합니다.
- 실행 중인 코루틴은 지연된 결과가 취소되면 취소됩니다.
- 결과 코루틴은 다른 언어 및 프레임워크의 유사한 기본 요소와 비교하여 중요한 차이점이 있습니다.
- 구조화된 동시성 패러다임을 시행하지 못하면 상위 작업(또는 외부 범위)을 취소합니다.
- 해당 동작을 변경하려면 감독하는 부모(SupervisorJob 또는 SupervisorScope)를 사용할 수 있습니다.
- 코루틴 컨텍스트는 CoroutineScope에서 상속되며 컨텍스트 인수로 추가 컨텍스트 요소를 지정할 수 있습니다.
- 컨텍스트에 디스패처나 다른 ContinuationInterceptor가 없으면 Dispatchers.Default가 사용됩니다.
- 상위 작업도 CoroutineScope에서 상속되지만 해당 컨텍스트 요소로 재정의될 수도 있습니다.
- 기본적으로 코루틴은 즉시 실행되도록 예약됩니다. 다른 옵션은 시작 매개변수를 통해 지정할 수 있습니다.
- 선택적 시작 매개변수를 CoroutineStart.LAZY로 설정하여 코루틴을 느리게 시작할 수 있습니다. \
  이 경우 결과 Deferred가 새 상태로 생성됩니다.
- start 함수로 명시적으로 시작할 수 있으며 join, await 또는 awaitAll의 첫 번째 호출에서 암시적으로 시작됩니다.

```kotlin
public fun <T> CoroutineScope.async(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> T
): Deferred<T> {
    val newContext = newCoroutineContext(context)
    val coroutine = if (start.isLazy)
        LazyDeferredCoroutine(newContext, block) else
        DeferredCoroutine<T>(newContext, active = true)
    coroutine.start(start, coroutine, block)
    return coroutine
}
```

## Parcelize 디펜던시
@Parcelize 에서 에러발생시 Android Extensions 플러그인 추가

```
import kotlinx.android.parcel.Parcelize

@Parcelize
```

### 플러그인 추가 방법
- app/buiild.gradle

```
plugins {
    ...
    id 'kotlin-android-extensions'
}
```

