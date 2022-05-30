# 제너레이터(Generator) 란?

- `제너레이터(Generator)` 객체는 generator function 으로부터 반환된 값이며 이터러블 프로토콜과 이터레이터 프로토콜을 준수합니다.
- `제너레이터(Generator)` 는 중간에 원하는 부분에서 멈추었다가, 그 부분부터 다시 실행할 수 있는 능력을 가진 함수입니다.

## 문법

```jsx
function* gen() {
yield 1;
yield 2;
yield 3;
}

var g = gen(); // "Generator { }"
```

## 메서드

- `[Generator.prototype.next()](https://developer.mozilla.org/ko/docs/Web/JavaScript/Reference/Global_Objects/Generator/next)[yield](https://developer.mozilla.org/ko/docs/Web/JavaScript/Reference/Operators/yield)` 표현을 통해 yield된 값을 반환합니다.
- `[Generator.prototype.return()](https://developer.mozilla.org/ko/docs/Web/JavaScript/Reference/Global_Objects/Generator/return)`주어진 값을 반환하고 생성기를 종료합니다.
- `[Generator.prototype.throw()](https://developer.mozilla.org/ko/docs/Web/JavaScript/Reference/Global_Objects/Generator/throw)`생성기로 에러를 throw합니다.

## 예시

- 무한 반복자

```jsx
function* idMaker(){
var index = 0;
while(true)
yield index++;
}

var gen = idMaker(); // "Generator { }"

console.log(gen.next().value); // 0
console.log(gen.next().value); // 1
console.log(gen.next().value); // 2
// ...
```

## `제너레이터(Generator)` vs 일반 함수

### 제너레이터 함수는 함수 호출자에게 함수 실행의 제어권을 양도(yield)할 수 있습니다.

- `일반함수` 같은 경우에는 함수가 어딘가에서 호출되면 그 함수에 대한 제어권은 호출된 함수 자신에게 넘어갑니다.
- `제너레이터 함수` 는 함수 실행의 제어권을 함수 호출자에게 양도할 수 있습니다. 즉 함수 호출자는 함수 실행을 일시 중지시키거나 다시 시작하게 하도록 할 수 있습니다.

### 제너레이터 함수는 함수 호출자와 함수수의 상태를 주고받을 수 있습니다.

- `일반함수`는 호출되는 순간에 매개변수를 통해 함수 외부에서부터 값을 전달받고 실행 됩니다. 즉 함수가 실행되고 있는 동안에는 함수 외부에서 함수 내부로 값을 전달하여 함수의 상태를 변경할 수 없는것을 뜻합니다.
- `제너레이터 함수` 는 함수 호출자와 양방향으로 함수의 상태를 주고받을 수 있습니다. 즉 제너레이터 함수는 함수 호출자에게 자신의 상태를 전달할 수 있고, 함수 호출자로부터 추가적으로 상태를 전달받을 수 있습니다.

### 제너레이터 함수는 호출 시 제너레이터 객체를 생성해 반환합니다.

- `일반함수`는 호출이되면 함수의 코드 블록을 실행시킵니다.
- `제너레이터 함수` 는 코드 블록을 실행시키는 것이 아니라 제너레이터 객체를 생성해서 반환합니다.

## 제너레이터 객체(Generator object)란?

- `제너레이터 함수` 를 호출했을 때 리턴되는 객체입니다.
- `제너레이터 객체`는 `이터러블(iterable)`이면서 동시에 `이터레이터(iterator)` 입니다.

**1) 이터러블(iterable)**

- iterator 를 프로퍼티로 사용한 메소드를 직접 구현하거나, 프로토타입 체인을 통해 상속받은 객체를 말합니다.
- 배열, 문자열, Map, Set 모두 이터러블 입니다.

**2) 이터레이터(iterator)**

- 이터레이터는 이터러블에 Symbol.iterator 메소드를 호출했을 때 반환되는 값 입니다.
- 이터레이터는 next 라는 메서드를 가지고 있는데, 이걸 이용해서 이터러블의 각 요소를 순회할 수 있습니다.
- next 메서드를 호출하면, IteratorResult 객체가 반환됩니다.
- value, done 이라는 프로퍼티를 갖고 있습니다.

## yield 와 next

- `yield` 는 제너레이터 함수를 멈추거나 다시 시작하는데 사용하는 키워드 입니다.
- next 메서드를 사용하면 `yield` 키워드가 사용된 표현식까지 실행되고 함수가 일시 중지 됩니다. 이때 함수의 제어권이 함수 호출자(function caller)에게 양도 됩니다.
- next 메서드가 호출될 때마다 다음 `yield` 표현식까지 실행과 중지가 반복되는 것입니다.

# 참고

[yield 란?](https://www.notion.so/yield-1d6a6acd2d0046c9af5af8e9972fd5d4)
- [https://developer.mozilla.org/ko/docs/Web/JavaScript/Reference/Global_Objects/Generator](https://developer.mozilla.org/ko/docs/Web/JavaScript/Reference/Global_Objects/Generator)