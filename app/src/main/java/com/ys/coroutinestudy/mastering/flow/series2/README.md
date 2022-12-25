# [Creating Flow Using Flow Builder in Kotlin](https://amitshekhar.me/blog/creating-flow-using-flow-builder-in-kotlin)
**배울 내용**
- Types of flow builders
- Creating Flow Using Flow Builder

## Types of flow builders
Flow Builder 에는 4가지 유형이 있습니다.

1. `flowOf()` : 주어진 항목 집합에서 flow 를 생성하는 데 사용됩니다.
2. `asFlow()` : type 을 flow 로 변환하는 데 도움이 되는 확장 기능입니다.
3. `flow{}` : 이것이 Flow 의 Hello World 예제에서 사용한 것입니다.
4. `channelFlow{}` : 이 빌더는 빌더 자체에서 제공하는 보내기를 사용하여 요소로 흐름을 생성합니다.

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