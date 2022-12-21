# [Mastering Flow API in Kotlin](https://amitshekhar.me/blog/flow-api-in-kotlin)

**Flow의 주요 구성 요소**
- Flow Builder
- Operator
- Collector

**주요 구성요소 비유**
Flow Builder	->	Speaker
Operator	->	Translator
Collector	->	Listener

## Flow Builder
간단히 말해서 작업을 수행하고 항목을 내보내는 데 도움이 된다고 할 수 있습니다.<br/>
예를 들어 몇 개의 숫자(1, 2, 3)만 내보내는 것과 같이 작업을 수행하지 않고 항목을 내보내야 하는 경우가 있습니다.<br/> 
여기에서 흐름 빌더는 그렇게 하는 데 도움이 됩니다. <br/>
우리는 이것을 스피커로 생각할 수 있습니다. <br/>
스피커는 생각(작업 수행)하고 말할(항목 방출) 것입니다.<br/>

## Operator
연산자는 데이터를 한 형식에서 다른 형식으로 변환하는 데 도움이 됩니다.<br/> 
연산자를 번역기로 생각할 수 있습니다. <br/>
Speaker는 프랑스어로 말하고 Collector(Listener)는 영어만 이해한다고 가정합니다.<br/> 
따라서 프랑스어를 영어로 번역하려면 번역가가 있어야 합니다. <br/>
그 번역가는 오퍼레이터입니다. <br/>
연산자는 실제로 이보다 더 많습니다. 연산자를 사용하여 작업이 수행될 스레드를 제공할 수도 있습니다.<br/>

## Collector
수집기는 연산자에 의해 변환된 Flow Builder를 사용하여 내보낸 항목을 수집합니다.<br/>
컬렉터를 리스너로 생각할 수 있습니다.<br/>
실제로 Collector는 Terminal Operator로 알려진 operator에 속하기도 합니다.<br/>
수집가는 터미널 운영자입니다.

## Flow API Source Code
Flow 인터페이스는 코루틴의 소스 코드에서 아래와 같습니다.
```kotlin
public fun interface FlowCollector<in T> {
    public suspend fun emit(value: T)
}
```

```kotlin
public interface Flow<out T> {
    public suspend fun collect(collector: FlowCollector<T>)
}
```

### Hello World of Flow
```kotlin
flow {
    (0..10).forEach {
        emit(it)
    }
}.map {
    it * it
}.collect {
    Log.d(TAG, it.toString())
}
```
- flow { }	->	Flow Builder
- map { }	->	Operator
- collect {}	->	Collector

코드 분석
- 먼저 0에서 10까지 방출하는 흐름 빌더가 있습니다.
- 그런 다음 각각의 모든 값과 제곱(it * it)을 취하는 맵 연산자가 있습니다. 맵은 Intermediate Operator입니다.
- 그런 다음 방출된 값을 가져오고 0, 1, 4, 9, 16, 25, 36, 49, 64, 81, 100으로 인쇄하는 수집기가 있습니다.

참고: 실제로 수집 방법을 사용하여 Flow Builder와 Collector를 모두 연결하면 실행이 시작됩니다.

## Types of flow builders
Flow Builder 에는 4가지 유형이 있습니다.

1. flowOf(): 주어진 항목 집합에서 flow 를 생성하는 데 사용됩니다.
2. asFlow(): type 을 flow 로 변환하는 데 도움이 되는 확장 기능입니다.
3. flow{}: 이것이 Flow의 Hello World 예제에서 사용한 것입니다.
4. channelFlow{}: 이 빌더는 빌더 자체에서 제공하는 보내기를 사용하여 요소로 흐름을 생성합니다.

### 예제
`flowOf()`
```kotlin
flowOf(4, 2, 5, 1, 7)
.collect {
    Log.d(TAG, it.toString())
}
```

`asFlow()`
```kotlin
(1..5).asFlow()
.collect {
    Log.d(TAG, it.toString())
}
```

`flow{}`
```kotlin
flow {
    (0..10).forEach {
        emit(it)
    }
}
.collect {
    Log.d(TAG, it.toString())
}
```

`channelFlow{}`
```kotlin
channelFlow {
    (0..10).forEach {
        send(it)
    }
}
.collect {
    Log.d(TAG, it.toString())
}
```

## `flowOn` Operator
`flowOn` 연산자는 작업이 수행될 스레드를 제어할 때 매우 편리합니다.<br/> 
일반적으로 Android에서는 백그라운드 스레드에서 작업을 수행하고 결과를 UI 스레드에 표시합니다.<br/> 


예를 들어 살펴보겠습니다. <br/>
지연을 시뮬레이트하기 위해 흐름 빌더 내부에 500밀리초의 지연을 추가했습니다.<br/>

```kotlin
val flow = flow {
    // Run on Background Thread (Dispatchers.Default)
    (0..10).forEach {
        // emit items with 500 milliseconds delay
        delay(500)
        emit(it)
    }
}
.flowOn(Dispatchers.Default)
```

```kotlin
CoroutineScope(Dispatchers.Main).launch {
    flow.collect {
        // Run on Main Thread (Dispatchers.Main)
        Log.d(TAG, it.toString())
    }
}
```

- 여기서 Flow Builder 내부의 작업은 Dispatchers.Default인 백그라운드 스레드에서 수행됩니다.
- 이제 UI 스레드로 전환해야 합니다. 이를 달성하려면 Dispatchers.Main을 사용하여 출시 내부에 수집 API를 래핑해야 합니다.
- 이것이 flowOn 연산자를 사용하여 스레드를 제어하는 방법입니다.

디스패처: 작업을 수행해야 하는 스레드를 결정하는 데 도움을 줍니다.<br/>
주로 IO, Default 및 Main의 세 가지 유형의 Dispatcher가 있습니다. <br/>
IO 디스패처는 네트워크 및 디스크 관련 작업에 사용됩니다. <br/>
기본값은 CPU를 많이 사용하는 작업에 사용됩니다. <br/>
Main은 Android의 UI 스레드입니다.<br/>

## Creating Flow Using Flow Builder

### 1. 한 위치에서 다른 위치로 파일 이동
> 여기서는 백그라운드 스레드의 한 위치에서 다른 위치로 파일을 이동하기 위해 Flow Builder를 사용하여 흐름을 생성하고 완료 상태를 기본 스레드로 보냅니다.

```kotlin
val moveFileflow = flow {
        // move file on background thread
        FileUtils.move(source, destination)
        emit("Done")
}
.flowOn(Dispatchers.IO)
```

```kotlin
CoroutineScope(Dispatchers.Main).launch {
    moveFileflow.collect {
        // when it is done
    }
}
```

### 2. 이미지 다운로드
> 여기서는 이미지를 다운로드하기 위해 Flow Builder를 사용하여 백그라운드 스레드에서 이미지를 다운로드하고 진행률을 기본 스레드의 수집기로 계속 보내는 흐름을 생성합니다.

```kotlin
val downloadImageflow = flow {
        // start downloading
        // send progress
        emit(10)
        // downloading...
        // ......
        // send progress
        emit(75)
        // downloading...
        // ......
        // send progress
        emit(100)
}
.flowOn(Dispatchers.IO)
```

```kotlin
CoroutineScope(Dispatchers.Main).launch {
    downloadImageflow.collect {
        // we will get the progress here
    }
}
```

## 참고
- [Android용 실제 예제를 통해 Kotlin Flow 구현](https://github.com/amitshekhariitbhu/Learn-Kotlin-Flow)
