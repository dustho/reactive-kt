# Functional In Java

---

> Reactive 프로그래밍을 이해하기 위해 필요한 기본적인 개념들을 정리합니다.

---

### Functional Interface

추상 메서드가 한 개만 정의된 `Interface`를 `Functional Interface`라고 부릅니다. Java 8부터 지원하는 default 메서드를 정의하더라도, 추상 메서드가 1개 뿐이라면 함수형 인터페이스입니다. 컴파일러에게 힌트를 주기 위해 @FunctionalInterface 어노테이션과 함께 사용할 수 있습니다. 

대표적인 예시로는 정렬 방식을 결정할 때 사용하는 `Comparator`가 있습니다.

```java
@FunctionalInterface
public interface Comparator<T> {
  int compare(T o1, T o2);
  
  // ...
}
```

### Lambda Expression

Java 에서는 인터페이스를 활용해 다음과 같이 익명 객체를 생성할 수 있습니다.

```java
import java.util.Collections;
import java.util.Comparator;

public class ExAnonymousInstance {
  public static void main(String[] args) {
    int[] array = new int[]{1, 2, 3};
    Collections.sort(array, new Comparator<Integer>() {
      @Override
      int compare(int o1, int o2) {
        return o2 - o1;
      }
    });
  }  
}
```

위와 같이 함수형 인터페이스에 대한 익명 객체를 생성할 때, `() -> {}` 화살표 함수 형태로 코드를 작성할 수 있습니다.

```java
import java.util.Collections;
import java.util.Comparator;

public class ExAnonymousInstance {
  public static void main(String[] args) {
    int[] array = new int[]{1, 2, 3};
    Collections.sort(array, (o1, o2) -> o2 - o1);
  }  
}
```

이렇게 함수 형태로 코드를 작성하는 것을 람다 표현식이라고 부르며, 실제로는 (일급) 객체이기 때문에 파라미터 혹은 반환 값으로 사용 가능합니다.

### Method Reference

람다 표현식을 더 간결하게 사용할 수 있고, 총 4가지 타입이 있습니다.

**static 메서드 참조 `ClassName::staticMethodName`**
```java
import java.util.List;

public class ExMethodRef {
  public static void main(String[] args) {
    List<Integer> list = List.of(1, 2, 3);
    
    // Lambda
    list.sort((i1, i2) -> Integer.compare(i1, i2));

    // Method Ref
    list.sort(Integer::compare);
  }
}
```

**인스턴스 메서드 참조 1 `instance::instanceMethodName`**
```java
import java.util.List;

public class ExMethodRef {
  public static void main(String[] args) {
    String compareStr = "Hello World!";
    List<String> list = List.of("str1", "str2", "str3");
    
    // Lambda
    list.stream().filter(str -> compareStr.equals(str));

    // Method Ref
    list.stream().filter(compareStr::equals);
  }
}
```

**인스턴스 메서드 참조 2 `ClassName::instanceMethodName`**
```java
public class ExMethodRef {
  public static void main(String[] args) {
    String compareStr = "Hello World!";
    List<String> list = List.of("str1", "str2", "str3");
    
    // Lambda
    list.stream().filter(str -> str.toUpperCase());
    
    // Method Ref
    list.stream().filter(String::toUpperCase);
  }
}
```

**생성자 참조 `ClassName::new`**
```java
public class ExMethodRef {
  public static void main(String[] args) {
    List<String> list = List.of("str1", "str2", "str3");

    // Lambda
    list.stream().map((str) -> new StringWrapper(str));
    
    // Method Ref
    list.stream().map(StringWrapper::new);
  }
  
  static class StringWrapper {
    String data;
    
    public StringWrapper(String data) {
      this.data = data;
    }
  }
}
```

### Function Descriptor

파라미터와 반환 타입에 따라, Java 에서 미리 정의해둔 함수형 인터페이스를 `Function Descriptor`라고 부릅니다.

`Predicate<T>` | `(T) -> boolean`: 필터링 처리에 사용됩니다.

```java
@FunctionalInterface
public interface Predicate<T> {

  boolean test(T t);
  // ...
}
```

`Consumer<T>` | `(T) -> void`: 로직 처리만 필요하고, 반환 값이 필요없는 경우에 사용됩니다. 
```java
@FunctionalInterface
public interface Consumer<T> {

  boolean accept(T t);
  // ...
}
```

`Function<T, R>` | `(T) -> R`: 로직 처리 이후, 반환 값이 필요한 경우에 사용됩니다.
```java
@FunctionalInterface
public interface Function<T, R> {

  R apply(T t);
  // ...
}
```

`Supplier<T>` | `() -> T`: 데이터를 제공하는 용도로 사용됩니다.
```java
@FunctionalInterface
public interface Supplier<T> {

    T get();
}
```

`Bixxx`: 파라미터가 2개인 Functional Descriptor 로 3개의 타입이 있습니다.
- `BiPredicate<L, R>` | `(L, R) -> boolean`
- `BiConsumer<T, U>` | `(T, U) -> void`
- `BiFunction<T, U, R>` | `(T, U) -> R`


