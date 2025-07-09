# Functional In Java

---

> Reactive 프로그래밍을 이해하기 위해 필요한 기본적인 개념들을 정리합니다.

---

### Reactive Programming

`Reactive`는 '응답성이 좋은 / 지연시간이 적은' 이라는 뜻을 갖는 단어입니다. 간단히 요약하자면 Non-Blocking + Asynchronous 를 활용하면 빠른 응답성을 보장하는 Reactive 프로그래밍을 구현할 수 있습니다.

### Blocking / Non-Blocking

- Blocking
- Non-Blocking

### Synchronous / Asynchronous

- Synchronous
- Asynchronous

### Combination Of (Non)-Blocking, (A)Sync

- Sync + Blocking
- Sync + Non-Blocking
- Async + Non-Blocking
- Async + Blocking

### Reactive Streams

Reactive Streams 이란 JVM 환경에서 논블로킹, 백프레셔를 이용한 비동기 스트림 처리를 위한 표준 명세이며, 4가지 주요 인터페이스로 구성되어 있습니다.

`Publisher<T>`: 데이터를 발행하는 역할

```java
public static interface Publisher<T> {
  public void subscribe(Subscriber<? super T> subscriber);
}
```

`Subscriber<T>`: 데이터를 소비하는 역할

```java
public static interface Subscriber<T> {
  public void onSubscribe(Subscription subscription);
  public void onNext(T item);
  public void onError(Throwable throwable);
  public void onComplete();
}
```

`Subscription`: 데이터 요청, 취소 등 데이터에 대한 흐름을 제어하는 역할

```java
public static interface Subscription {
  public void request(long n);
  public void cancel();
}
```

`Processor<T, R>`: Publisher - Subscriber 중간에서 데이터를 처리하는 역할 (Optional)
```java
public static interface Processor<T,R> extends Subscriber<T>, Publisher<R> {
}
```

Reactive Streams 의 구현체로는 다음과 같은 것들이 있습니다.

- Reactor
- RxJava
- Akka Streams

위 요소들 중에 Spring WebFlux 에서 사용되는 구현체는 Reactor 입니다.

### Reactor

기본적인 Reactor 예제 코드를 통해 Reactive Streams 의 역할을 어떻게 구현하였는지 살펴보겠습니다.

```java
public class Ex_Reactor {
  public static void main(String[] args) {
    Flux<String> sequence = Flux.just("Hello", "Reactor");
    sequence.map(data -> data.toLowerCase())
        .subscribe(data -> System.out.println(data));
  }
}
```

- `Flux`: Publisher 역할을 하는 Reactor 클래스로 0건 이상의 데이터를 발행할 수 있습니다.
- `"Hello", "Reactor"`: 가공되지 않은 데이터로 Emitted / Source Data 라고 불립니다.
- `data -> System.out.println(data)`: Subscriber 역할을 하는 람다 표현식(Consumer)입니다. 내부적으로는 `LambdaSubscriber` 클래스에 전달되어 데이터를 처리합니다.
- `just(), map()`: Reactor 에서 지원하는 Operator 메서드로 데이터를 가공하는 역할을 합니다. Operator 의 반환 값을 Flux, Mono 형태의 새로운 Publisher 가 반환됩니다.

전반적인 흐름은 "데이터 발행(Publisher) -> 데이터 가공(Operator) -> 데이터 소비(Subscriber)" 으로 처리됩니다. 참고로 Operator 는 Reactor 에서 데이터를 가공할 때 사용되는 High-Level 메서드로, 표준 명세에서의 Processor (Low-Level Interface) 와는 성격이 다릅니다.

### Marble Diagram

Marble 은 구슬을 의미합니다. 즉, Marble Diagram 이란 구슬 형태의 다이어그램으로 Reactor 에서 지원하는 Operator 를 표현하는 데에 사용됩니다.

---
       Upstream   -----O----|-> onComplete signal emit

                    Operator

       Downstream -----X------> onError signal emit

---

- 기본적으로 왼쪽에서 오른쪽 방향으로 시간이 흐르고, 시간순으로 데이터 입력과 출력을 표현합니다.
- 오퍼레이터를 기준으로 위를 Upstream / 아래를 Downstream 이라고 부릅니다.
- Upstream 에서 Operator 로 데이터를 입력 -> Operator 에서 데이터를 가공 -> Operator 에서 Downstream 으로 데이터를 출력합니다.
- `|`: 정상 처리 Signal 을 나타냅니다.
- `X`: 에러 발생 Signal 을 나타냅니다.

### Reactor Example

- Mono 는 0 or 1 개의 데이터를 발행하는 Reactor Publisher
- Flux 는 0 개 이상의 데이터를 발행하는 Reactor Publisher

`Mono empty`
```java
/**
 * emitted onComplete signal 출력
 * - Mono.empty() 0개의 데이터로부터 시작하며, 작업이 끝났음을 알릴 때 사용
 */
public class Ex_MonoEmpty {
  public static void main(String[] args) {
    Mono
        .empty()
        .subscribe(
            none -> System.out.println("emitted onNext signal"),
            error -> System.out.println(error),
            () -> System.out.println("emitted onComplete signal")
        );
  }
}
```

`Mono just`
```java
/**
 * emitted data: ${data} -> emitted onComplete signal 순으로 출력
 * - RestTemplate 은 Blocking I/O 이므로 아래 코드는 Non-Blocking 의 장점을 사용할 수는 없다.
 * - 단, Operator 체인 형태로 흐름을 깔끔하게 제어할 수 있다.
 * - Mono.just() 는 1개의 데이터로부터 시작
 */
public class Ex_MonoJust {
  public static void main(String[] args) {
    // ...
    Mono.just(
        restTemplate.exchange(
            uri, HttpMethod.GET, new HttpEntity<String>(headers), String.class)
    ).map(response -> {
      return response.getBody();
    }).subscribe(
        data -> System.out.println("emitted data: " + data),
        error -> System.out.println(error),
        () -> System.out.println("emitted onComplete signal")
    );
  }
}
```

`Mono justOrEmpty`
```java
/**
 * 파라미터에 아무 데이터를 넘기지 않으면 empty() / 1개의 데이터를 넘기면 just() 로 동작
 */
public class Ex_MonoJustOrEmpty {
  public static void main(String[] args) {
    Mono
        .justOrEmpty()
        .subscribe(
            none -> System.out.println("emitted onNext signal"),
            error -> System.out.println(error),
            () -> System.out.println("emitted onComplete signal")
        );
  }
}
```

`Flux just`
```java
/**
 * 2 -> 4 -> 6 순으로 출력
 * - Flux.just() 는 1개 이상의 데이터로부터 시작
 */
public class Ex_FluxJust {
  public static void main(String[] args) {
    Flux.just(1, 2, 3)
        .map(num -> num * 2)
        .subscribe(System.out::println);
  }
}
```

`Flux fromArray`
```java
/**
 * 2 -> 6 순으로 출력
 * - Flux.fromArray() 는 배열(0개 이상의 데이터)로부터 시작
 */
public class Ex_FluxFromArray {
  public static void main(String[] args) {
    Flux.fromArray(new Integer[]{1, 2, 3})
        .filter(num -> num % 2 != 0)
        .map(num -> num * 2)
        .subscribe(System.out::println);
  }
}
```

`Mono concatWith`
```java
/**
 * emitted onNext signal: Hello
 * emitted onNext signal: ,
 * emitted onNext signal: World
 * emitted onComplete signal
 * 
 * - concatWith() 는 여러 Publisher 를 순차적으로 연결하여, 하나의 Flux 로 반환합니다.
 * - 반면, collectList() 는 Flux 에서 방출한 여러 개의 데이터를 하나의 Mono 로 반환합니다.
 */
public class Ex_MonoJustOrEmpty {
  public static void main(String[] args) {
    Flux<String> flux = Mono.just("Hello").concatWith(Flux.just(", ", "World"));
    flux.subscribe(
        data -> System.out.println("emitted onNext signal: " + data),
        error -> System.out.println(error),
        () -> System.out.println("emitted onComplete signal")
    );
  }
}
```

