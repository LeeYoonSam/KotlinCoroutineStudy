# 복습 및 내용정리

코틀린 함수는 import 를 통해서 가져와야 사용가능 합니다.

```kotlin
import kotlinx.coroutines.*
```

- suspend 는 기본적으로 내재화 되어있는 키워드이므로 import 없이 사용가능

`launch`는 가능하면 다른 코드와 함께 수행하도록 노력하는 코드

`delay` → `suspension point`, 잠이들었다가 깨어날수 있습니다.

- delay 도 suspend fun 입니다.

`suspend fun` 은 잠들고 깨어날수 있다는것을 인지해야 합니다.