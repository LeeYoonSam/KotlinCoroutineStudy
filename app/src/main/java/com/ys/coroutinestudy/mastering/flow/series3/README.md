# [Terminal Operators in Kotlin Flow](https://amitshekhar.me/blog/terminal-operators-in-kotlin-flow)

## What are Terminal Operators?
> 터미널 연산자는 Flow Builder, 연산자와 수집기를 연결하여 실제로 flow 를 시작하는 연산자입니다.

```kotlin
(1..5).asFlow()
.filter {
    it % 2 == 0
}
.map {
    it * it
}.collect {
    Log.d(TAG, it.toString())
}
```
- 여기서 `collect`는 터미널 연산자 입니다.
- 가장 기본적인 터미널 연산자는 수집기인 `collect` 연산자입니다.

다음과 같이 작성하면 flow 가 시작되지 않습니다.
```kotlin
(1..5).asFlow()
.filter {
    it % 2 == 0
}
.map {
    it * it
}
```
- 터미널 연산자를 사용하여 시작해야 합니다(이 경우 `collect`).

`reduce` 연산자인 또 다른 터미널 연산자를 살펴보겠습니다.
- `reduce`: 내보낸 각 항목에 함수를 적용하고 최종 값을 내보냅니다.

```kotlin
val result = (1..5).asFlow()
    .reduce { a, b -> a + b }

Log.d(TAG, result.toString())
```
- 여기서 결과는 15가 됩니다.

**설명**
- 초기 단계에는 방출될 1, 2, 3, 4, 5가 있습니다. 
- 처음에는 a = 0 및 b = 1이며 단계에 따라 계속 변경됩니다.

Step 1:
```text
a = 0, b = 1

a = a + b = 0 + 1 = 1
```

Step 2:
```text
a = 1, b = 2

a = a + b = 1 + 2 = 3
```

Step 3:
```text
a = 3, b = 3

a = a + b = 3 + 3 = 6
```

Step 4:
```text
a = 6, b = 4

a = a + b = 6 + 4 = 10
```

Step 5:
```text
a = 10, b = 5

a = a + b = 10 + 5 = 15
```

이것이 결과가 15가 되는 방법입니다.

터미널 연산자에 관한 것입니다.
