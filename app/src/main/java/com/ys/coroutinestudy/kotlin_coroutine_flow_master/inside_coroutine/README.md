# 인사이드 코루틴

# Monad

- 1950년 후반 로제 고드망이 개념을 만듬.
- 모나드는 파라미터 이름

```kotlin
monad: (T) → Box<U>
```

- 모나드(monad)는 값을 받아서 박스를 반환하는 함수 입니다.
- Null의 추상(Optional, Option, Maybe), 에러의 추상(Either), 비동기 실행(IO Monad) 에 사용됩니다.
- 1930년 경에는 Monad가 인자 하나를 받는 함수를 칭하는 말 이기도 했습니다.
    - 요즘에는 그런 표현이 쓰이지 않고 T 를 받아서  Box<U> 를 반환하는 것이라고 볼수 있습니다.


**모나드 : (값) → 박스(값)**

```kotlin
class Box<T> {
	fun <U> flatMap(monad: (T) -> Box<U>): Box<U>
}
```

- 모나드(monad)는 값을 받아서 박스를 반환하는 함수 입니다.
- 대부분의 flatMap(bind, chain, >>=) 함수의 인자는 모나드 입니다.
- flatMap 자체는 async 할 수도 sync 할 수도 있습니다.
    - RxJava 의 설계가 이상합니다. Flow 는 안 그래요

### Monad 는 flatMap 의 파라미터

```kotlin
.flatMap { v ->
	Box(v * 1.0) 
}
```

- Box<Int> (3) → Box<Float> (3.0)
- v -> Box(v * 1.0) → Monad

```kotlin
data class Box<T>(val value: T) {
	fun <U> flatMap(monad: (T) -> Box<U>): Box<U> = 
		monad(value)
}

fun main() {
	println(Box(3).flatMap { v -> Box(v * 1.0) }.value) // flatMap 은 기본적으로 동기 코드
}
```

- monad
    - monad: (T) -> Box<U>)
    - v -> Box(v * 1.0)
- flatMap 은 기본적으로 동기 코드
    - 박스자체를 확장해서 비동기 코드를 만들수 있습니다.

### Monad laws

```kotlin
Box(f).flatMap(g) == g(f)
// 좌항등원 (Left Identity)

m.flatMap { f -> Box(f) } == m
// 우항등원 (Right Identity)

m.flatMap { x -> f(x).flatMap(g) } == m.flatMap(f).flatMap(g)
// 교환 법칙 (Associative)
```

- 현실적으로는 모나드 법칙을 안 지키는 구현들도 꽤 많습니다.
- 이런게 있다고만 알아두면 됩니다. (구현할 경우에만 필요)
- 그런데 구현에서도 안 지키는 경우가 상당하다고 합니다.
- flatMap 안에 들어가는 인자가 모나드일 가능성이 높다. 라고 생각하면 됩니다.

# Monad의 사촌 Functor

- Functor 는 값을 받아 값을 리턴하는 파라미터

```kotlin
class Box<T>(val value: T) {
	fun <U> map(functor: (T) -> U): Box<U>
}
```

- 펑터(functor)는 값을 받아서 값을 반환하는 함수 입니다.
- 모나드와의 가장 큰 차이는 박스가 아닌 값을 바로 리턴한다는 점입니다.
- 항상 박스를 리턴하는 것은 괴롭고, 기존의 함수를 재사용할 수 있기 때문에 펑터는 유용합니다.
- 대무문의 map 함수의 인자는 펑터입니다.
- Functor 자체는 async 할 수도 sync 할 수도 있습니다.

```kotlin
data class Box<T>(val value: T) {
	fun <U> map(functor: (T) -> U): Box<U> = 
		Box(functor(value))
}

fun main() { 
	println(Box(3).map { v -> v * 1.0 }.value)
}
```

- Functor 는 값을 받아서 값을 변환하고 결과를 박스에 랩핑 시킨다.

## 연속적인 Monad 체인

- 비동기 연산을 마치고 박스에 담긴 자료로 넣어 연속적으로 호출 가능

# Either (Left, Right)

- 성공과 실패에 따라 Left, Right 로 연결

### Either 의 예

```kotlin
sealed class Either<L, R> {
	data class Left<L, R>(val value: L): Either<L, R>()
	data class Right<L, R>(val value: R): Either<L, R>()

	fun <T> flatMap(monad: (R) -> Either<L, T>): Either<L, T> = when (this) {
		is Left -> Left(value)
		is Right -> monad(value)
	}

	fun <T> map(functor: (R) -> T): Either<L, T> = flatMap {
		Right<L, T>(functor(it))
	}
}
```

# Monad 와 Functor 차이 요약

```kotlin
class Box<T>(val value: T) {
	fun <U> flatMap(monad: (T) -> Box<U>): Box<U>
	fun <U> map(functor: (T) -> U): Box<U>
}
```

- 모나드는 값을 받아서 박스를 반환하는 함수 입니다.
- 펑터는 값을 받아서 값을 반환하는 함수 입니다.
- 펑터와 모나드 자체는 asny 할 수도 sync 할 수도 있습니다.
- 순수 모나드는 아니지만 RxJava 의 성공이 어떻게 보면 놀랍습니다.
- map 은 동기, flatMap 은 비동기로 된 이상한 구현이 넘침 (예: RxJava)

## Monad 의 단점

- 함수(모나드, 펑터)와 박스를 조합해 프로그래밍하는 스타일이 어렵습니다.
- Haskell 공부하면 다들 모나드에서 포기할 정도로 모나드는 악명 높음.
- 좋은데.. 직관적이지 않습니다.

# Continuation Passing Style(1975), CPS (Gerald Jay Susan)

## Direct style(Return-based)

- 비동기 처리가 어려움

```kotlin
val bytesRead = inChannel.read(buf)
process(buf, bytesRead)
outChannel.write(buf)
outFile.close()
```

## Continuation

- (넓은 의미) 다음에 수행해야 할 내용을 의미함
- 코루틴 조차도 Continuation 으로 보기도 함

```kotlin
val bytesRead = inChannel.read(buf) // 현재
process(buf, bytesRead) // Continuation
outChannel.write(buf)
outFile.close()
```

- (좁은 의미) 다음에 수행해야 할 함수
- 실체화된 컨티뉴에이션(Reification of Continuation) 이라고 부르기도 함
- 계속 Continuation 을 전달하는 방식(Continuation Passing Style, CPS)

```kotlin
inChannel.read(buf) { bytesRead -> Continuation
	process(buf, bytesRead)

	outChannel.write(buf) {
		outFiles.close()
	}
}
```

## 콜백이랑 다른 점은?

- Continuation 는 last call 입니다.

```kotlin
inChannel.read(buf) { bytesRead // 1 -> (read()는 bytesRead 를 전달하면 더 이상 아무일도 하지 않습니다.)
	process(buf, bytesRead)

	outChannel.write(buf) { // 2 -> write() 는 Continuatio 을 호출하면 더 이상 아무일도 하지 않습니다.
		outFiles.close()
	}
}
```

## Last call 이 왜 중요한가?

- Last call optimization 이 가능합니다.
- 되돌아 갈 필요가 없다면 스택을 쌓을 필요가 전혀 없습니다.

```kotlin
inChannel.read(buf) { bytesRead ->  // 돌아가야 하면 스택을 push 해둬야 합니다.
	process(buf, bytesRead)

	outChannel.write(buf) { // 되돌아가야 하면 스택을 push 해둬야 합니다.
		outFiles.close()
	}
}
```

### Last call optimization

```kotlin
define internal fastcc void @f.destroy(%f.frame * frame.ptr.destroy) {
	entry :
		%0 = bitcast %f.frame * frame.ptr.destroy to 18 *
		tail call void @free(i8 * %0)
		ret void
}
```

- LLVM 의 tail call 지원
- Cocoa 는 Continuation 최적화가 가능하고 Java World 는 안됩니다.
- Project Loom  이 진행중

# 코루틴 내부

- 상태머신과 컨티뉴에이션으로 이루어져 있습니다.
- 코루틴과 다른 세상을 이어주기 위해 컨티뉴에이션을 이해를 해야하고 그것을 이용해서 조금 더 다채롭게 사용할 수 있습니다.