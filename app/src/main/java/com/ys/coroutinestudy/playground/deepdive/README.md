# Kotlin Coroutines — A deep dive

## 배경
이전에 CPU는 단일 코어를 가지고 있기 때문에 한번에 단일 프로세스를 수행하는 데 사용되었습니다. </br>
나중에 듀얼 코어, 쿼드 코어, 옥타 코어 등이 시장에 나왔습니다. </br>
다중 코어가 있는 이러한 CPU는 프로세스를 병렬로 실행할 수 있습니다. </br>
오늘날에도 최신 CPU에는 매우 제한된 수의 코어가 있습니다. </br>
각 코에어는 두 개의 하드웨어 스레드가 연결되어 있으며 이를 통해 명령어가 코어로 흐를 수 있습니다. </br>
이를 극복하기 위해 OS는 하나의 코어에 여러 스레드가 연결된 멀테스레딩 개념을 제시했습니다. </br>
각 스레드는 코어 작업을 제공할수 있습니다. </br>
이러한 작업은 다양한 프로세스 스케줄링 알고리즘(예: FCFS, round robing, shortest-job-next, shortest-remaining-time, priority scheduling, multiple level queues scheduling, etc)을 사용하여 OS에 의해 스레드에서 관리 됩니다.</br>
따라서 이러한 소프트웨어 스레드는 컴퓨터의 효율성을 크게 향상시킵니다. </br>
</br>
그러나 이것으로 충분하지 않았고 일부 현대 프로그래밍 언어는 이 개념을 다른 수준으로 끌어 올렸습니다. </br>
Kotlin의 코루틴, Golang의 코루틴, JS의 promise는 위 개념의 몇 가지 예입니다. </br>
두 개의 하드웨어 스레드가 코어의 효율성을 향상시키고 소프트웨어 스레드가 하드웨어 스레드의 용량을 향상시키는 것처럼 코루틴은 소프트웨어 스레드의 효율성을 향상시킵니다. </br>
</br>
백그라운드 작업이 병렬로 실행되는것 처럼 느껴서 이 구문을 자주 사용하지만 특정 시점에서 실행되는 프로세스의 수는 CPU의 코어 수를 초과할 수 없습니다. </br>
프로세스는 다른 프로세스를 시작하기 전에 반드시 완료되어야 하는 것은 아니므로 병렬로 실행되고 있다고 느낍니다. </br>

## Coroutines
- 코루틴은 가벼운 스레드로 생각할 수 있지만 스레드는 아닙니다.
- 코드의 나머지 부분과 동시에 작동하는 코드 블록을 실행한다는 점에서 개념적으로 스레드와 유사합니다.
- 그러나 코루틴은 특정 스레드에 바인딩되지 않습니다.
- 한 스레드에서 실행을 일시중지하고 다른 스레드에서 다시 시작할 수 있습니다.
- 스레드는 리소스 집약적이지만 코루틴은 매우 가볍습니다.

## Scope
- Kotlin 코루틴의 범위는 Kotlin 코루틴이 실행되는 제한으로 정의할 수 있습니다.
- 코루틴에 라이프사이클을 제공합니다.

### CoroutineScope
- CoroutineScope는 자식 코루틴 실행을 담당하는 잘 정의된 수명 주기가 있는 엔터티의 속성으로 선언되어야 합니다.
- 해당 인스턴스 CoroutineScope는 CoroutineScope() 또는 MainScope() 함수로 생성됩니다.
    - `CoroutineScope()` : Dispatcher를 사용합니다. 코루틴의 기본값입니다.
    - `MainScope()` : 코루틴에 Dispatchers.Main을 사용합니다.
- 새로운 범위를 생성하고 모든 자식 코루틴이 완료될 때까지 완료되지 않습니다.
- 범위를 만들고 코루틴을 실행하고 범위 내에서 다른 코루틴을 만들 수 있습니다.
- CustomScope 사용자 지정 사용의 핵심 부분은 수명 주기가 끝날 때 이를 취소하는 것입니다.
- CoroutineScope.cancel 확장 기능은 코루틴을 시작하던 엔터티가 더 이상 필요하지 않을 때 사용됩니다. 아직 실행 중일 수 있는 모든 코루틴을 취소합니다.

### GlobalScope
- 전역 CoroutineScope는 어떤 작업에도 바인딩되지 않습니다.
- GlobalScope는 전체 애플리케이션 수명 동안 작동하고 조기에 취소되지 않는 최상위 코루틴을 시작하는 데 사용됩니다.
- GlobalScope에서 시작된 활성 코루틴은 프로세스를 활성 상태로 유지하지 않습니다. 데몬 스레드와 같습니다.
- 이것은 섬세한 API입니다. GlobalScope를 사용할 때 실수로 리소스 또는 메모리 누수가 발생하기 쉽습니다.
- GlobalScope에서 시작된 코루틴은 구조적 동시성의 원칙을 따르지 않으므로 문제(예: 느린 네트워크로 인해)로 인해 중단되거나 지연되는 경우 계속 작동하고 리소스를 소비합니다.

```kotlin
fun loadConfiguration() {     
   GlobalScope.launch {         
      val config = fetchConfigFromServer() // network request 
         updateConfiguration(config)
      } 
}
```
- loadConfiguration을 호출하면 취소하거나 완료될 때까지 기다리지 않고 백그라운드에서 작동하는 코루틴이 GlobalScope에 생성됩니다.
- 네트워크가 느리면 백그라운드에서 계속 대기하여 리소스를 소모합니다. loadConfiguration을 반복적으로 호출하면 점점 더 많은 리소스가 소모됩니다.

### runBlocking
- 이름에서 알 수 있듯이 차단 호출입니다. runBlocking 블록 내의 모든 코드가 실행을 완료할 때까지 전체 코드가 실행되는 것을 차단합니다.
- runBlocking과 관련하여 이상한 점은 이 코드를 UI 스레드에 작성하면 Android 앱이 영원히 교착 상태에 빠지게 된다는 것입니다.
- 일반적으로 runBlocking은 Android의 단위 테스트나 동기 코드의 다른 경우에 사용됩니다.
- 프로덕션 코드에는 runBlocking이 권장되지 않습니다.

## Coroutine Context
- 코루틴은 항상 Kotlin 표준 라이브러리에 정의된 CoroutineContext 유형의 값으로 표시되는 일부 컨텍스트에서 실행됩니다.
- 코루틴 컨텍스트는 다양한 요소의 집합입니다.
- 주요 요소는 코루틴의 `Job`과 `Dispatchers`입니다.

### Job
- 백그라운드 작업입니다. 개념적으로 작업은 완료로 끝나는 수명 주기가 있는 취소 가능한 것입니다.
- Job은 부모를 취소하면 모든 자식이 재귀적으로 즉시 취소되는 부모-자식 계층 구조로 정렬될 수 있습니다.
- CancellationException 이외의 예외가 있는 자식 실패는 즉시 부모를 취소하고 결과적으로 다른 모든 자식을 취소합니다.
- 이 동작은 SupervisorJob을 사용하여 사용자 지정할 수 있습니다.
- Job 인터페이스의 가장 기본적인 인스턴스는 다음과 같이 생성됩니다.
    - `Coroutine job`은 시작 코루틴 빌더로 생성됩니다. 지정된 코드 블록을 실행하고 이 블록이 완료되면 완료됩니다.
    - `CompletableJob`은 Job() 팩토리 함수로 생성됩니다. CompletableJob.complete를 호출하여 완료됩니다.
- 개념적으로 작업 실행은 결과 값을 생성하지 않습니다. 작업은 부작용에 대해서만 시작됩니다. 결과를 생성하는 작업은 지연된 인터페이스를 참조하세요.
- 작업의 취소 함수는 취소 원인으로 CancellationException만 허용하므로 취소를 호출하면 항상 작업이 정상적으로 취소되고 상위 작업이 취소되지는 않습니다. \
  이런 식으로 부모는 자신을 취소하지 않고 자신의 자식을 취소할 수 있습니다(모든 자식도 재귀적으로 취소).

### Dispatchers
- 코루틴 컨텍스트에는 해당 코루틴이 실행에 사용하는 스레드를 결정하는 코루틴 디스패처(CoroutineDispatcher 참조)가 포함됩니다.
- 코루틴 디스패처는 코루틴 실행을 특정 스레드로 제한하거나 스레드 풀에 디스패치하거나 제한 없이 실행되도록 할 수 있습니다.
- launch 및 async와 같은 모든 코루틴 빌더는 새 코루틴 및 기타 컨텍스트 요소에 대한 디스패처를 명시적으로 지정하는 데 사용할 수 있는 선택적 CoroutineContext 매개변수를 허용합니다.

```kotlin
launch {       // 상위 runBlocking 코루틴의 컨텍스트
println(“main runBlocking : I’m working in thread
    ${Thread.currentThread().name}”)
}
launch(Dispatchers.Unconfined) { // 제한되지 않음 — 메인 스레드와 함께 작동합니다.
println(“Unconfined : I’m working in thread
     ${Thread.currentThread().name}”)
}
launch(Dispatchers.Default) { // DefaultDispatcher로 디스패치됩니다.
      println(“Default : I’m working in thread
      ${Thread.currentThread().name}”)
}
launch(newSingleThreadContext(“MyOwnThread”)) { // 자신의 새로운 스레드를 얻을 것입니다.
       println(“newSingleThreadContext: I’m working in thread
       ${Thread.currentThread().name}”)
}
```
- `launch { ... }` 가 매개변수 없이 사용되면 시작되는 CoroutineScope에서 컨텍스트(따라서 디스패처)를 상속합니다. 이 경우 메인 스레드에서 실행되는 메인 runBlocking 코루틴의 컨텍스트를 상속합니다.
- `Dispatchers.Unconfined`
    - Unconfined는 메인 스레드에서도 실행되는 것으로 보이는 특수 디스패처입니다.
    - 제한되지 않은 코루틴 디스패처는 호출자 스레드에서 코루틴을 시작하지만 첫 번째 중단 지점까지만 시작됩니다.
    - 일시 중단 후 호출된 일시 중단 함수에 의해 완전히 결정된 스레드에서 코루틴을 다시 시작합니다.
    - 무제한 디스패처는 CPU 시간을 소비하지 않고 특정 스레드에 국한된 공유 데이터(예: UI)를 업데이트하지 않는 코루틴에 적합합니다.
- `Dispatchers.Default`: 다른 디스패처가 범위에 명시적으로 지정되지 않은 경우 기본 디스패처가 사용됩니다. 기본값이며 스레드의 공유 배경 풀을 사용합니다.
- `newSingleThreadContext`
    - 코루틴이 실행할 스레드를 생성합니다. 전용 스레드는 매우 비싼 리소스입니다.
    - 실제 애플리케이션에서는 더 이상 필요하지 않을 때 닫기 기능을 사용하여 해제하거나 최상위 변수에 저장하고 애플리케이션 전체에서 재사용해야 합니다.


### Suspending functions
- 일시 중단 기능은 모든 코루틴의 중심에 있습니다.
- 일시 중지 기능은 단순히 일시 중지했다가 나중에 다시 시작할 수 있는 기능입니다.
- 장기 실행 작업을 실행하고 차단 없이 완료될 때까지 기다릴 수 있습니다.
- 일시 중단 함수의 구문은 suspend 키워드의 추가를 제외하고 일반 함수의 구문과 유사합니다.
- 매개변수를 사용하고 반환 유형을 가질 수 있습니다. 그러나 일시 중단 함수는 다른 일시 중단 함수나 코루틴 내에서만 호출할 수 있습니다.

### Coroutine Builders
- 코루틴 빌더는 일반 함수 또는 비일시 중단 범위에서 코루틴을 시작하는 방법을 제공합니다.
- 확장 기능 CoroutineScope이며 범위의 CoroutineContext를 상속하고 호출됩니다.
- CoroutineScope는 기본적으로 코루틴의 수명 주기를 제어하며 잘 정의된 범위 또는 수명 주기가 있는 엔터티에서 구현되어야 합니다.
- 가장 인기 있는 두 가지 코루틴 빌더는 launch 와 async 이며 둘 다 유사한 매개변수를 허용하지만 사용 사례는 다릅니다.

- `launch`: 
    - 백그라운드에서 새 코루틴을 시작하고 이에 대한 참조를 Job 객체로 반환합니다.
    - 시작 코루틴 빌더는 실행하고 잊어버리며, 백그라운드 작업에 대한 핸들인 작업 인스턴스를 제외하고 호출자에게 결과를 반환하지 않습니다.
    - 호출된 범위에서 컨텍스트와 작업을 상속하지만 재정의할 수 있습니다.
- `async`:
    - 비동기 코루틴 빌더는 구조에서 시작과 유사하지만 작업 대신 Deferred<T>를 반환합니다.
    - Deferred<T>는 나중에 결과를 제공하겠다는 약속을 나타내는 가벼운 비 차단 미래입니다.
    - 최종 결과를 얻으려면 지연된 개체에서 suspending 함수 await를 호출해야 합니다.

- async에 제공되는 매개변수는 시작과 거의 동일합니다. 반환된 Deferred<T> 는 Job 과 같은 방식으로 사용될 수 있도록 Job 을 확장합니다.
- 비동기 코루틴 빌더를 사용하여 여러 독립 작업을 병렬로 실행할 수 있습니다.

### withContext
- 지정된 코루틴 컨텍스트로 지정된 일시 중단 블록을 호출하고 완료될 때까지 일시 중단하고 결과를 반환합니다.
- 블록에 대한 결과 컨텍스트는 coroutineContext + 컨텍스트를 사용하여 현재 코루틴 컨텍스트를 지정된 컨텍스트와 병합하여 파생됩니다(CoroutineContext.plus 참조).
- 이 일시 중지 기능은 취소할 수 있습니다.
- 결과 컨텍스트의 취소를 즉시 확인하고 활성화되지 않은 경우 CancellationException을 던집니다.
- 이 함수는 새 컨텍스트의 디스패처를 사용하여 새 디스패처가 지정된 경우 블록 실행을 다른 스레드로 이동하고 완료되면 원래 디스패처로 돌아갑니다.


## 코드 예제
- 두 개의 코루틴을 병렬로 호출하고 세 번째 코루틴을 순차적으로 호출하기 전에 완료될 때까지 기다리는 방법을 살펴보겠습니다.

```kotlin
fun sequentialJobsDemo() {
    val totalTime = measureTimeMillis {
        runBlocking {
            Log.i(
                "CoroutineDemo",
                "Parallel Coroutine Demo Started on ${Thread.currentThread().name}"
            )
            val job1 = launch { coroutineDemoSequenceVoidReturnApiCall1() }
            val job2 = launch { coroutineDemoSequenceVoidReturnApiCall2() }
            job1.join()
            job2.join()
            launch { coroutineDemoSequenceVoidReturnApiCall3() }
        }
    }
    Log.i("CoroutineDemo", "Time to finish coroutineDemo: $totalTime")
}
suspend fun coroutineDemoSequenceVoidReturnApiCall1() {
    val time2 = measureTimeMillis {
        Log.i(
            "CoroutineDemo",
            "Coroutine Demo Api Call 1 Started on ${Thread.currentThread().name}"
        )
        delay(700)
        seqResult1 = "Coroutine 1 Executed"
        Log.i("CoroutineDemo", "Coroutine Demo Api Call 1 Finished")
    }
    Log.i("CoroutineDemo", "Time to finish coroutineDemoApiCall1: $time2")
}

suspend fun coroutineDemoSequenceVoidReturnApiCall2() {
    val time2 = measureTimeMillis {
        Log.i(
            "CoroutineDemo",
            "Coroutine Demo Api Call 2 Started on ${Thread.currentThread().name}"
        )
        delay(500)
        seqResult2 = "Coroutine 2 Executed"
        Log.i("CoroutineDemo", "Coroutine Demo Api Call 2 Finished")
    }
    Log.i("CoroutineDemo", "Time to finish coroutineDemoApiCall2: $time2")
}

suspend fun coroutineDemoSequenceVoidReturnApiCall3() {
    val time3 = measureTimeMillis {
        Log.i(
            "CoroutineDemo",
            "Coroutine Demo Api Call 3 Started on ${Thread.currentThread().name}"
        )
        delay(500)
        Log.i("CoroutineDemo", "Coroutine 3 Executed After $seqResult1 and $seqResult2")
        Log.i("CoroutineDemo", "Coroutine Demo Api Call 3 Finished")
    }
    Log.i("CoroutineDemo", "Time to finish coroutineDemoApiCall3: $time3")
}
```
- 위의 코드에서 job1과 job2라는 두 개의 코루틴이 실행되고 병렬로 실행됩니다.
- job1과 job2가 모두 완료된 후에 job3이 실행되기를 원합니다. 이를 위해 join 키워드를 사용했습니다. 아래는 위 코드의 출력입니다.
```
2022–05–14 13:59:00.219 11543–11543/com.example.coroutinesdemo I/CoroutineDemo: Parallel Coroutine Demo Started on main
2022–05–14 13:59:00.224 11543–11543/com.example.coroutinesdemo I/CoroutineDemo: Coroutine Demo Api Call 1 Started on main
2022–05–14 13:59:00.224 11543–11543/com.example.coroutinesdemo I/CoroutineDemo: Coroutine Demo Api Call 2 Started on main
2022–05–14 13:59:00.729 11543–11543/com.example.coroutinesdemo I/CoroutineDemo: Coroutine Demo Api Call 2 Finished
2022–05–14 13:59:00.729 11543–11543/com.example.coroutinesdemo I/CoroutineDemo: Time to finish coroutineDemoApiCall2: 505
2022–05–14 13:59:00.927 11543–11543/com.example.coroutinesdemo I/CoroutineDemo: Coroutine Demo Api Call 1 Finished
2022–05–14 13:59:00.927 11543–11543/com.example.coroutinesdemo I/CoroutineDemo: Time to finish coroutineDemoApiCall1: 703
2022–05–14 13:59:00.932 11543–11543/com.example.coroutinesdemo I/CoroutineDemo: Coroutine Demo Api Call 3 Started on main
2022–05–14 13:59:01.437 11543–11543/com.example.coroutinesdemo I/CoroutineDemo: Coroutine 3 Executed After Coroutine 1 Executed and Coroutine 2 Executed
2022–05–14 13:59:01.438 11543–11543/com.example.coroutinesdemo I/CoroutineDemo: Coroutine Demo Api Call 3 Finished
2022–05–14 13:59:01.438 11543–11543/com.example.coroutinesdemo I/CoroutineDemo: Time to finish coroutineDemoApiCall3: 506
2022–05–14 13:59:01.438 11543–11543/com.example.coroutinesdemo I/CoroutineDemo: Time to finish coroutineDemo: 1219
```

- 이제 Async 및 Await를 사용하여 유사한 기능을 구현해 보겠습니다.

```kotlin
fun sequentialJobsAsyncDemo() {
    val totalTime = measureTimeMillis {
        runBlocking {
            Log.i(
                "CoroutineDemo",
                "Parallel Coroutine Async Demo Started on ${Thread.currentThread().name}"
            )
            val job1 = async { coroutineDemoSequenceAsyncReturnApiCall1() }.await()
            val job2 = async { coroutineDemoSequenceAsyncReturnApiCall2() }.await()
            launch { coroutineDemoSequenceAsyncReturnApiCall3(job1, job2) }
        }
    }
    Log.i("CoroutineDemo", "Time to finish coroutineDemo: $totalTime")
}
suspend fun coroutineDemoSequenceAsyncReturnApiCall1(): String {
    val time1 = measureTimeMillis {
        Log.i(
            "CoroutineDemo",
            "Coroutine Demo Api Call 1 Started on ${Thread.currentThread().name}"
        )
        delay(700)
        Log.i("CoroutineDemo", "Coroutine Demo Api Call 1 Finished")
    }
    Log.i("CoroutineDemo", "Time to finish coroutineDemoApiCall1: $time1")
    return "Coroutine 1 Executed"
}

suspend fun coroutineDemoSequenceAsyncReturnApiCall2(): String {
    val time2 = measureTimeMillis {
        Log.i(
            "CoroutineDemo",
            "Coroutine Demo Api Call 2 Started on ${Thread.currentThread().name}"
        )
        delay(500)
        Log.i("CoroutineDemo", "Coroutine Demo Api Call 2 Finished")
    }
    Log.i("CoroutineDemo", "Time to finish coroutineDemoApiCall2: $time2")
    return "Coroutine 2 Executed"
}

suspend fun coroutineDemoSequenceAsyncReturnApiCall3(s1: String, s2: String):String {
    val time3 = measureTimeMillis {
        Log.i(
            "CoroutineDemo",
            "Coroutine Demo Api Call 3 Started on ${Thread.currentThread().name}"
        )
        delay(500)
        Log.i("CoroutineDemo", "Coroutine 3 Executed After $seqResult1 and $seqResult2")
        Log.i("CoroutineDemo", "Coroutine Demo Api Call 3 Finished")
    }
    Log.i("CoroutineDemo", "Time to finish coroutineDemoApiCall3: $time3")
    return "Coroutine 2 Executed After $s1 and $s2"
}
```
- 위 코드의 출력
```
2022-05-14 14:10:25.431 11965-11965/com.example.coroutinesdemo I/CoroutineDemo: Parallel Coroutine Async Demo Started on main
2022-05-14 14:10:25.442 11965-11965/com.example.coroutinesdemo I/CoroutineDemo: Coroutine Demo Api Call 1 Started on main
2022-05-14 14:10:25.450 11965-11965/com.example.coroutinesdemo I/CoroutineDemo: Coroutine Demo Api Call 2 Started on main
2022-05-14 14:10:25.954 11965-11965/com.example.coroutinesdemo I/CoroutineDemo: Coroutine Demo Api Call 2 Finished
2022-05-14 14:10:25.955 11965-11965/com.example.coroutinesdemo I/CoroutineDemo: Time to finish coroutineDemoApiCall2: 505
2022-05-14 14:10:26.148 11965-11965/com.example.coroutinesdemo I/CoroutineDemo: Coroutine Demo Api Call 1 Finished
2022-05-14 14:10:26.148 11965-11965/com.example.coroutinesdemo I/CoroutineDemo: Time to finish coroutineDemoApiCall1: 706
2022-05-14 14:10:26.150 11965-11965/com.example.coroutinesdemo I/CoroutineDemo: Coroutine Demo Api Call 3 Started on main
2022-05-14 14:10:26.652 11965-11965/com.example.coroutinesdemo I/CoroutineDemo: Coroutine 3 Executed After  and 
2022-05-14 14:10:26.653 11965-11965/com.example.coroutinesdemo I/CoroutineDemo: Coroutine Demo Api Call 3 Finished
2022-05-14 14:10:26.653 11965-11965/com.example.coroutinesdemo I/CoroutineDemo: Time to finish coroutineDemoApiCall3: 503
2022-05-14 14:10:26.653 11965-11965/com.example.coroutinesdemo I/CoroutineDemo: Time to finish coroutineDemo: 1271
```

## Conclusions
코루틴은 여러 백그라운드 프로세스를 수행하는 매우 쉽고 효율적인 방법을 제공합니다.</br>

1. suspend me 함수의 코드는 순차적으로 실행됩니다. 제어가 다음 작업으로 이동한 후 한 작업이 완료될 때까지 대기합니다.

```kotlin
suspend fun singleCoroutineDemo() {
    val totalTime = measureTimeMillis {
        Log.i("CoroutineDemo", "Coroutine Demo Started on ${Thread.currentThread().name}")
        delay(1000)
        coroutineDemoApiCall1()
        coroutineDemoApiCall2()
    }
    Log.i("CoroutineDemo", "Time to finish coroutineDemo: $totalTime")
}
suspend fun coroutineDemoApiCall1() {
    val time1 = measureTimeMillis {
        Log.i(
            "CoroutineDemo",
            "Coroutine Demo Api Call 1 Started on ${Thread.currentThread().name}"
        )
        delay(2000)
        Log.i("CoroutineDemo", "Coroutine Demo Api Call 1 Finished")
    }
    Log.i("CoroutineDemo", "Time to finish coroutineDemoApiCall1: $time1")

}

suspend fun coroutineDemoApiCall2() {
    val time2 = measureTimeMillis {
        Log.i(
            "CoroutineDemo",
            "Coroutine Demo Api Call 2 Started on ${Thread.currentThread().name}"
        )
        delay(500)
        Log.i("CoroutineDemo", "Coroutine Demo Api Call 2 Finished")
    }
    Log.i("CoroutineDemo", "Time to finish coroutineDemoApiCall2: $time2")
}
```
- 먼저 coroutineDemoApiCall1이 호출되고 완료되면 coroutineDemoApiCall2가 실행됩니다.

```
2022–05–14 16:48:40.101 1652–1652/com.example.coroutinesdemo I/CoroutineDemo: Coroutine Demo Api Call 1 Started on main
2022–05–14 16:48:42.101 1652–1652/com.example.coroutinesdemo I/CoroutineDemo: Coroutine Demo Api Call 1 Finished
2022–05–14 16:48:42.101 1652–1652/com.example.coroutinesdemo I/CoroutineDemo: Time to finish coroutineDemoApiCall1: 2000
2022–05–14 16:48:42.102 1652–1652/com.example.coroutinesdemo I/CoroutineDemo: Coroutine Demo Api Call 2 Started on main
2022–05–14 16:48:42.603 1652–1652/com.example.coroutinesdemo I/CoroutineDemo: Coroutine Demo Api Call 2 Finished
2022–05–14 16:48:42.603 1652–1652/com.example.coroutinesdemo I/CoroutineDemo: Time to finish coroutineDemoApiCall2: 501
2022–05–14 16:48:42.603 1652–1652/com.example.coroutinesdemo I/CoroutineDemo: Time to finish coroutineDemo: 3505
```

2. 여러 코루틴이 suspend function에서 시작되면 동시에 시작되고 동일하거나 다른 스레드에서 독립적인 코루틴으로 병렬로 실행됩니다.
3. 여러 코루틴이 코루틴에서 호출되고 그 중 하나가 실패하면 부모 코루틴이 취소되어 다른 모든 자식 코루틴이 취소됩니다. `supervisor scope`에서 시작된 경우는 제외합니다.


### 참고
- https://anant-raman.medium.com/kotlin-coroutines-a-deep-dive-e8c9d4451a0b
- https://developer.android.com/kotlin/coroutines
- https://kotlinlang.org/docs/coroutines-overview.html