![image](https://github.com/Invincible-Backend-Study/modern-java-in-action/assets/66772624/e4eaaa12-64b0-465e-9314-d4d3fbb08f2c)# 📌 11장: null 대신 Optional 클래스

## ✅ 11.1 값이 없는 상황을 어떻게 처리할까?

```java
public class Person {

    private Car car;

    public Car getCar() {
        return car;
    }
}

public class Car {

    private Insurance insurance;

    public Insurance getInsurance() {
        return insurance;
    }
}

public class Insurance {

    private String name;

    public String getName() {
        return name;
    }
}
```

- Person을 통해서 Insurance.name을 얻으려면 연속된 참조가 필요합니다.
- `person.getCar().getInsurance().getName()`
- 코드 자체에는 문제가 없어 보이지만, 위 중에서 하나라도 null이라면 NPE가 발생될 여지가 큽니다.

<br>

### NPE 줄이기

1. 깊은 의심
    
    ```java
    public String getCarInsuranceNameNullSafeV1(PersonV1 person) {
        if (person != null) {
            CarV1 car = person.getCar();
            if (car != null) {
                Insurance insurance = car.getInsurance();
                if (insurance != null) {
                    return insurance.getName();
                }
            }
        }
        return "Unknown";
    }
    ```
    
2. 너무 많은 메서드의 출구
    
    ```java
    public String getCarInsuranceNameNullSafeV2(PersonV1 person) {
        if (person == null) {
            return "Unknown";
        }
        CarV1 car = person.getCar();
        if (car == null) {
            return "Unknown";
        }
        Insurance insurance = car.getInsurance();
        if (insurance == null) {
            return "Unknown";
        }
        return insurance.getName();
    }
    ```
    

위와 같은 코드는 NPE에 안전해졌을 순 있지만, 가독성이 상당히 좋지 못합니다. 따라서 값이 있거나 없음을 표현할 수 있는 좋은 방법이 필요합니다.

<br>

### null 때문에 발생하는 문제

- `에러의 근원`: NPE만 봐도 그렇다.
- `코드를 어지럽힘`: 무수한 null check 코드는 메인 로직을 가립니다.
- `아무 의미가 없음`: null은 어떤 의미도 없습니다.
- `자바 철학에 위배`: 자바는 모든 포인터를 숨겼지만, null포인터를 숨기지 못함 (null을 참조하는 것이 아닐까?)
- `형식 시스템에 구멍을 만든다`: null은 무형식, 정보가 없으며 모든 참조형식에 할당될 수 있습니다. 이 자체가 시스템에 null을 퍼뜨리고, 어디서 부터 null이 시작됐는지 알 수 없게 만듭니다.

위와 같은 문제점이 존재하므로 Java 8부터는 ‘선택형값’ (스칼라, 하스켈)의 영향을 받아 Optional<T> 클래스를 제공합니다.

<br><br>

## ✅ 11.2 Optional 클래스 소개

- Optional은 선택형 값을 캡슐화 하는 클래스입니다.
    
    ![image](https://github.com/Invincible-Backend-Study/modern-java-in-action/assets/66772624/105340de-2d77-48dd-aec9-49cc8f36f3bf)

    
- Optional에서 값이 있다면 감싸고 없다면 Optional.empty라는 정적 메서드를 통해 특별한 싱글턴 인스턴스를 반환 합니다.
    - `private static final Optional<?> EMPTY = new Optional<>(null);`
- null 참조는 NPE를 발생시키지만, Optional.empty()는 객체이므로 다양한 방식으로 활용할 수 있습니다.
- Optional클래스를 사용하면, 적용한 모델의 의미가 더욱 명확해집니다.
    - Optional의 역할은 더 이해하기 쉬운 API를 설계하는것이고, 메서드의 시그니처 만으로 선택형값인지 필수값인지의 여부를 알 수 있게 합니다.

따라서 위의 클래스들은 다음과 같이 Optional을 적용할 수 있습니다.

```java
public class Person {

    private Car car;

		// 사람이 차를 가지고 있지 않을 수 있음
    public Optional<Car> getCar() {
        return Optional.ofNullable(car);
    }
}

public class Car {

    private Insurance insurance;

		// 자동차 보험이 없을 수 있음
    public Optional<Insurance> getInsurance() {
        return Optional.ofNullable(insurance);
    }
}

public class Insurance {

    private String name;
		
		// 보험이 있다면 보험의 이름은 반드시 존재해야 함을 간접적으로 알 수 있음
    public String getName() {
        return name;
    }
}
```

<br><br>

## ✅ 11.3 Optional 적용 패턴

### Optional 객체 생성

- 빈 Optional
    
    ```java
    public static<T> Optional<T> empty() {
        @SuppressWarnings("unchecked")
        Optional<T> t = (Optional<T>) EMPTY;
        return t;
    }
    ```
    
- null이 아닌 값으로 Optional만들기
    
    ```java
    public static <T> Optional<T> of(T value) {
    		// value가 null이라면 NPE 발생
        return new Optional<>(Objects.requireNonNull(value));
    }
    ```
    
- null값으로 Optional 만들기
    
    ```java
    @SuppressWarnings("unchecked")
    public static <T> Optional<T> ofNullable(T value) {
        return value == null ? (Optional<T>) EMPTY
                             : new Optional<>(value);
    }
    ```
    
<br>

### map을 통해 Optional의 값을 추출하고 변환하기

다음 코드는 Optional을 활용하면 한 줄로 끝낼 수 있습니다.

```java
// Optional활용 전
String nae = null;
if (insurance != null) {
		name = insurance.getName();
}

// Optional활용
Optional<Insurance> optInsurance = Optional.ofNullable(insurance);
Optional<String> name = optInsurance.map(Insurance::getName);
```

Optional  객체는 마치 `최대 요소가 한 개 이하인 데이터 컬렉션 처럼 생각할 수 있고 사용할 수 있습니다.`

![image](https://github.com/Invincible-Backend-Study/modern-java-in-action/assets/66772624/97cc6dc3-ee75-4179-b32f-0cce7bce39ce)


<br>

### flatMap을 통해 Optional 객체 연결

```java
public String getCarInsuranceName(Person person) {
		Optional<Person> optPerson = Optional.of(person);
		Optional<String> name = optPerson.map(Person::getCar) // (1)
				    .map(Car::getInsurance) // (2)
				    .map(Insurance::getName);
return name.orElse("Unknown");
```

- (1)의 반환타입은 Optional<Car>
- (2)는 getInsurance라는 메서드를 Optional<Car>에게 요청하고 있으므로 컴파일 에러

결국 Optional<Optional<Car>>와 같은 형식으로 진행되므로 문제가 발생됩니다. 이를 해결 하기 위해 Stream API의 flatMap처럼 Optional도 flatMap을 제공해줍니다.

```java
public Strign getCarInsurnaceName(Optional<Person> person) {
		return Optional<String> name = person.flatMap(Person::getCar)
							.flatMap(Car::getInsurance)
							.map(Insurance::getName)
							.orElse("Unknown");
}
```

- 도메인 모델에 대한 암묵적인 지식에 의존하지 않고, 명시적인 형식 시스템을 정의할 수 있습니다.
- Optional을 인수로 받거나 Optional을 반환하는 메서드를 정의한다면 **해당 메서드를 사용하는 모든 사람에게 이 메서드가 빈 값을 받거나 빈 결과를 반환할 수 있음을 잘 문서화해 제공하는것과 동일한 효과를 줍니다.**

**Optional을 이용한 Person/Car/Insurance 참조 체인**

flatMap과 map의 내부 구현을 살펴보면 동작원리를 쉽게 이해할 수 있습니다.

- flatMap()
    
    ```java
    public <U> Optional<U> flatMap(Function<? super T, ? extends Optional<? extends U>> mapper) {
        Objects.requireNonNull(mapper); // Function<T, R>은 null이면 안됨
        if (!isPresent()) {  // 자기 자신이 널이라면 EMPTY 반환
            return empty();
        } else {
            @SuppressWarnings("unchecked")
            Optional<U> r = (Optional<U>) mapper.apply(value); // apply이후 결과를
            return Objects.requireNonNull(r); // null체크 후 그대로 반환 -> Optional로 감싸지 않음
        }
    }
    ```
    
- map()
    
    ```java
    public <U> Optional<U> map(Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper);
        if (!isPresent()) {
            return empty();
        } else {
            return Optional.ofNullable(mapper.apply(value)); // 전부 동일하지만, 반환시 Optional로 감싸서 반환
        }
    }
    ```
    

**Optional은 직렬화 할 수 없음**

- Optional 설계시 선택형 반환값 만을 지원하기 위함으로 못박았기에 Serializable 인터페이스를 구현하지 않음
- 따라서 직렬화 모델이 필요하다면 Optional로 값을 반환할 수 있는 메서드를 추가하는 것을 권장합니다.
    
    ```java
    public class Car {
    
        private Insurance insurance;
    
        public Optional<Insurance> getInsurance() {
            return Optional.ofNullable(insurance);
        }
    }
    ```
    
<br>

### Java 9: Optional 스트림 조작

Optional을 포함하는 스트림을 쉽게 처리할 수 있도록 stream() 메서드를 제공합니다.

```java
// Optional<T>에서 제공하는 메서드
public Stream<T> stream() {
    if (!isPresent()) {
        return Stream.empty();
    } else {
        return Stream.of(value);
    }
}
```

현재 Optional<T>객체에 값이 있다면 Stream으로 감싼 값을, 아니라면 empty값을 반환합니다.

따라서 다음과 같이 활용할 수 있습니다.

```java
public Set<String> getCarInsuranceNames(List<Person> persons) {
    return persons.stream()
            .map(Person::getCar)
            .map(optCar -> optCar.flatMap(Car::getInsurance))
            .map(optInsurance -> optInsurance.map(Insurance::getName))
            .flatMap(Optional::stream) // Stream<Optional<T>> -> Stream<T>
            .collect(toSet());
}
```

<br>

### 디폴트 액션 및 Optional unwrap

- `get()`: Optional에 있는 값을 꺼냄, 없으면 NoSuchElementException 발생 (값이 확실하게 있을때만 사용)
- `orElse(T other)`: 값이 없다면 other라는 기본값을 제공
- `orElseGet(Supplier<? extends T> other)`: orElse에 대응해 other가 로직 생성 관련 코드라면 실제 값이 없을때만 해당 로직이 실행되도록 Supplier를 통해 지연시킬 수 있습니다.
- `orElseThrow(Supplier<? extends X> exceptionSupplier)`: Optional이 비어있을때 예외를 발생시킵니다. → get과 비슷하지만 발생 예외를 선택할 수 있음
- `ifPresent(Consumer<? super T> consumer)`: 값이 존재할때만 인수로 넘겨준 동작을 실행함 값이 없으면 아무일도 하지 않음
- `ifPresentOrElse(Consumer<? super T> action, Runnable emptyAction)`: Optional이 비어있을때 Runnable 인수를 실행시키는 걸 제외하고 위와 동일

<br>

### 두 Optional 합치기

```java
public Insurance findCheapestInsurance(Person person, Car car) {
    // 다른 보험사에서 제공한 질의 서비스
    // 모든 데이터 비교
    Insurance cheapestCompany = new Insurance();
    return cheapestCompany;
}

public Optional<Insurance> nullSafeFindCheapestInsurance(Optional<Person> person, Optional<Car> car) {
    if (person.isPresent() && car.isPresent()) {
        return Optional.of(findCheapestInsurance(person.get(), car.get()));
    } else {
        return Optional.empty();
    }
}
```

위와 같은 코드가 있을때 이를 flatMap과 map을 활용해 더 직관적으로 표현할 수 있습니다.

```java
public Optional<Insurance> nullSafeFindCheapestInsuranceQuiz(Optional<Person> person, Optional<Car> car) {
    return person.flatMap(p -> car.map(c -> findCheapestInsurance(p, c)));
}
```

null 체크는 메서드 내부에서 해주므로 하나라도 null이면 Optional.empty()가 실행됩니다.

<br>

### Optional 필터링 하기

filter 메서드(Stream API에서 제공하는것과 유사)를 활용하면 필터링도 간단하게 할 수 있습니다.

```java
Insurance insurance = ...;
if(insurance != null && "CambridgeInsurance".equals(insurance.getName())){
		System.out.println("ok");
}

Optional<Insurance> optInsurance = ...;
optInsurance.filter(insurance -> "CambridgeInsurance".equals(insurance.getName()))
        .ifPresent(x -> System.out.println("ok"));
```

<br><br>

## ✅ 11.4 Optional을 사용한 실용 예제

### 잠재적으로 null이 될 수 있는 대상을 Optional로 감싸기

```java
Object value = map.get("key"); // Map<K, V> map

Optional<Object> value = Optional.ofNullable(map.get("key"));
```

<br>

### 예외와 Optional 클래스

일부 자바 API는 값을 제공할 수 없을때 null이 아닌 예외를 발생시키기도 합니다. (Integer.parseInt(…))

이를 Optional.empty()를 활용한 유틸 메서드를 통해 해결할 수 있습니다.

```java
public static Optional<Integer> StringToInteger(String s) {
    try {
        return of(Integer.parseInt(s));
    } catch (NumberFormatException e) {
        return empty();
    }
}
```

<br>

### 기본형 Optional을 사용하지 말아야 하는 이유

기본형 특화 Optional을 사용하면 다음과 같은 문제가 존재합니다.

- 어차피 Optional 최대 요소 수는 한 개이므로 기본형 특화 Optional을사용해도 성능의 큰 개선이 없음
- 기본형 특화 Optional은 map, filter, flatMap을 지원하지 않음
    - 따라서 일반 Optional과 혼용하여 사용할 수 없음

제가 생각하는 핵심은 Optional을 사용하면서 사용자는 메서드의 시그니처만 보고도 Optional 값이 사용되거나 반환되는지 예측할 수 있다는 점이고 이를 통해 더 좋은 API 설계가 가능하다는 점이었습니다.

<br><br><br>

# 📌 12장: 새로운 날짜와 시간 API

- Java 8이전에 제공해주던 시간, 날짜 라이브러리의 문제점 (`java.util.Date`)
    - Date 클래스는 특정 시점을 날짜가 아닌 밀리초 단위로 표현
    - 1900년을 기준으로 하는 오프셋
    - 0에서 시작하는 달 인덱스 등 모호한 설계
    - toString으로 반환되는 문자열 추가 활용 어려움
    - Date는 JVM기본시간대인 CET를 활용 → 자체적으로 시간대 정보를 알고 있지 않음
- 대안으로 `java.util.Calendar`가 등장했지만 여전히 문제가 있음
    - 달의 인덱스는 여전히 0부터 시작
    - Date, Calendar중 어떤 클래스를 사용해야 할지 혼동 야기함
- **따라서 Java8에서는 많은 기능을 포함하고 보완한 java.time 패키지가 추가됐음**

<br><br>

## ✅ 12.1 LocalDate, LocalTime, Instant, Duration, Period

java.time 패키지는 `LocalDate`, `LocalTime`, `Instant`, `Duration`, `Period`와 같은 새로운 날짜, 시간 관련 클래스를 제공합니다.

<br>

### LocalDate, LocalTime

- LocalDate (날짜를 표현)
    - 시간을 제외한 날짜를 표현하는 불변 객체
    - 어떤 시간대 정보도 포함하지 않음
    - 정적 팩토리 메서드 of로 인스턴스를 생성하거나, parse를 통해 생성할 수 있음
        
        ```java
        LocalDate date = LocalDate.of(2023, 8, 22);
        
        date = LocalDate.now(); // 현재 날짜 정보를 얻음
        
        date = LocalDate.parse("2023-08-22");
        ```
        
    - 년, 월, 일의 정보를 가져올 수 있음
        
        ```java
        int year = date.getYear(); // 2023
        Month month = date.getMonth(); // AUGUST
        int day = date.getDayOfMonth(); // 22
        DayOfWeek dow = date.getDayOfWeek(); // TUESDAY
        int len = date.lengthOfMonth(); // 31 (8월의 길이)
        boolean leap = date.isLeapYear(); // false (윤년이 아님)
        
        // ChronoField를 전달해 날짜 정보를 가져올 수 있음
        int y = date.get(ChronoField.YEAR);
        int m = date.get(ChronoField.MONTH_OF_YEAR);
        int d = date.get(ChronoField.DAY_OF_MONTH);
        ```
        

- LocalTime (시간을 표현)
    - `시간`, `분`을 이용하거나 `시간`, `분`, `초`를 이용한 정적 팩토리 메서드 of 혹은 parse를 통해 생성
        
        ```java
        LocalTime time = LocalTime.of(13, 45, 20); // 13:45:20
        int hour = time.getHour(); // 13
        int minute = time.getMinute(); // 45
        int second = time.getSecond(); // 20
        
        time = LocalTime.parse("13:45:20");
        ```

<br>

### 날짜 및 시간을 조합한 `LocalDateTime`

LocalDateTime은 LocalDate와 LocalTime을 쌍으로 갖는 복합 클래스 입니다.

- LocalTime에 날짜를 부여하여 생성하거나, LocalDate에 시간을 부여해 생성할 수 있음
    
    ```java
    LocalDateTime dt4 = date.atTime(time); // LocalDate date
    LocalDateTime dt5 = time.atDate(date); // LocalTime time
    ```
    
- 일반적인 정적 팩토리 메서드 of도 제공
- LocalDateTime → LocalDate, LocalTime 추출 가능 (toLocalXxx 메서드)

<br>

### Instant 클래스 : 기계의 날짜와 시간

- 위에서 사용한 날짜, 시간, 날짜 + 시간 클래스는 인간 친화적
- **기계 관점에서는 연속된 시간에서 특정 지점을 하나의 큰 수로 표현하는 방법이 가장 자연스러운 시간 표현 방법임**
- java.time.Instant 클래스는 UTC를 기준으로 특정 지점까지의 시간을 초로 표현합니다.
- 팩토리 메서드 ofEpochSecond에 초를 넘겨주며 인스턴스를 생성할 수 있음 (혹은 두번째 인수로 나노초를 넘겨주며 시간을 보정할 수 있다.)
    - 10억분의 1초(나노초)의 정밀도를 제공함
    
    ```java
    Instant instant = Instant.ofEpochSecond(44 * 365 * 86400);
    Instant instant = Instant.ofEpochSecond(44 * 365 * 86400, 1_000_000_000); // 첫번째인수 + 1초
    Instant now = Instant.now();
    ```
    
    - now()를 통해 받은 시간은 사람이 읽을 수 있는 시간 정보를 제공하진 않음 → 일반적인 `ChronoField`를 통해 값을 가져올 수 없음
    - 대신 Duration, Period 클래스와 함께 활용이 가능함

<br>

### Duration과 Period의 정의

- Duration은 두 **시간** 객체 사이의 지속시간을 생성합니다.(LocalDate 사용 불가)
- 정적 팩토리 메서드 between을 이용해 두 시간 객체사이의 지속성 만듦(두 개의 LocalTime, LocalDateTime, Instant로 생성가능)
    
    ```java
    LocalDateTime dt1 = LocalDateTime.of(2014, Month.MARCH, 18, 13, 45, 19); // 2014-03-18T13:45
    LocalDateTime dt2 = LocalDateTime.of(2014, Month.MARCH, 19, 13, 45, 20); // 2014-03-18T13:45
    LocalTime time = LocalTime.of(13, 45, 20); // 13:45:20
    Instant instant = Instant.ofEpochSecond(44 * 365 * 86400);
    Instant now = Instant.now();
    
    Duration d1 = Duration.between(LocalTime.of(13, 45, 10), time); // LocalTime, LocalTime
    Duration d2 = Duration.between(instant, now); // Instant, Instant
    
    Duration d3 = Duration.between(time, dt1); // LocalTime, LocalDateTime (더 작은 범위 ~ 큰 범위 가능)
    // Duration d4 = Duration.between(dt1, time); // LocalDateTime, LocalTime (큰 범위 ~ 작은 범위 불가능)
    
    // Instant는 다른 클래스와 혼용 불가능
    // Duration d5 = Duration.between(instant, dt1);
    // Duration d6 = Duration.between(time, instant);
    ```
    

- 년, 월, 일로 시간을 표현하려면 Period 클래스를 사용합니다.
    
    ```java
    public static Period between(LocalDate startDateInclusive, LocalDate endDateExclusive) {
        return startDateInclusive.until(endDateExclusive);
    }
    ```
    
- 굳이 시간 객체를 사용하지 않고도 Duration과 Period 클래스의 인스턴스를 만들 수 있는 팩토리 메서드들을 제공합니다. → Java API DOCS 참고
    - https://docs.oracle.com/javase/8/docs/api/index.html
    - https://docs.oracle.com/javase/8/docs/api/index.html

<br><br>

## ✅ 12.2 날짜 조정, 파싱 formatting

지금까지 살펴본 클래스들은 불변입니다. 일관성을 유지함에 있어서 불변은 좋은 특징이지만, 특정 시간을 계산하여 새로운 시간을 만들려면 불편함이 존재합니다.

이런 부분을 보완하기 위해 날짜를 조정, 파싱, 포멧팅하는 방법을 제시합니다.

<br>

### 날짜 조정

```java
LocalDate date1 = LocalDate.of(2017, 9, 21);
LocalDate date2 = date1.withYear(2011); // 2011-09-21
LocalDate date3 = date2.withDayOfMonth(25); // 2011-09-25
LocalDate date4 = date3.with(ChronoField.MONTH_OF_YEAR, 2); // 2011-02-25

// 선언형 방식
LocalDate date1 = LocalDate.of(2017, 9, 21);
LocalDate date2 = date1.plusWeeks(1);
LocalDate date3 = date2.minusYears(6);
LocalDate date4 = date3.plus(6, ChronoUnit.MONTHS);
```

기존 LocalDate를 고치지 않고, 새로운 인스턴스를 반환하여 날짜를 변경할 수 있습니다.

<br>

### TemporalAdjusters를 활용한 조금 더 세밀한 날짜 조정

TemporalAdjusters를 사용하면 다음 주 일요일, 돌아오는 평일, 이번달의 마지막날과 같은 좀 더 세밀하고 복잡한 날짜 조정 기능을 제공합니다. with 메서드에서 다양한 동작을 수행하는 TemporalAdjusters를 전달하면 됩니다.

```java
LocalDate date = LocalDate.of(2014, 3, 18);
date = date.with(nextOrSame(DayOfWeek.SUNDAY));
date = date.with(lastDayOfMonth());
```

또한 커스텀하게 TemporalAdjusters를 구현하여 제공할 수 있습니다. (FunctionalInterface임)

```java
class NextWorkingDay implements TemporalAdjusters {
    @Override
    public Temporal adjustInto(Temporal temporal) {
        DayOfWeek dow = DayOfWeek.of(temporal.get(ChronoField.DAY_OF_WEEK));
        int dayToAdd = 1;
        if (dow == DayOfWeek.FRIDAY) {
            dayToAdd = 3;
        }
        if (dow == DayOfWeek.SATURDAY) {
            dayToAdd = 2;
        }
        return temporal.plus(dayToAdd, ChronoUnit.DAYS);
    }
}

// 사용
date = date.with(new NextWorkingDay());

// 람다 활용: 토요일, 일요일은 건너뛰고 날짜를 하루씩 다음날로 바꿈
date = date.with(temporal -> {
    DayOfWeek dow = DayOfWeek.of(temporal.get(ChronoField.DAY_OF_WEEK));
    int dayToAdd = 1;
    if (dow == DayOfWeek.FRIDAY) {
        dayToAdd = 3;
    }
    if (dow == DayOfWeek.SATURDAY) {
        dayToAdd = 2;
    }
    return temporal.plus(dayToAdd, ChronoUnit.DAYS);
});
```

<br>

### 날짜와 시간 객체 출력 및 파싱

포맷팅과 파싱 전용 패키지인 java.time.format 패키지도 있을만큼 날짜, 시간에서 포맷팅, 파싱은 빠질 수 없습니다.

주로 DateTimeFormatter의 정적 팩토리 메서드 및 상수를 활용해 손쉽게 포맷터를 생성할 수 있습니다.

```java
LocalDate date = LocalDate.of(2014, 3, 18);
DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
DateTimeFormatter italianFormatter = DateTimeFormatter.ofPattern("d. MMMM yyyy", Locale.ITALIAN);

// basic 포맷팅
String basicFormat1 = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
String basicFormat2 = date.format(DateTimeFormatter.BASIC_ISO_DATE);
System.out.println(basicFormat1);
System.out.println(basicFormat2);

// basic 포맷팅 -> basic 파싱
LocalDate basicFormat1Parse1 = LocalDate.parse(basicFormat1, DateTimeFormatter.ISO_LOCAL_DATE);
LocalDate basicFormat2Parse2 = LocalDate.parse(basicFormat2, DateTimeFormatter.BASIC_ISO_DATE);
System.out.println(basicFormat1Parse1);
System.out.println(basicFormat2Parse2);

// 커스텀 formatter 포맷팅
System.out.println(date.format(formatter));
System.out.println(date.format(italianFormatter));

// Builder 활용한 커스텀 포맷터 생성
DateTimeFormatter complexFormatter = new DateTimeFormatterBuilder()
        .appendText(ChronoField.DAY_OF_MONTH)
        .appendLiteral(". ")
        .appendText(ChronoField.MONTH_OF_YEAR)
        .appendLiteral(" ")
        .appendText(ChronoField.YEAR)
        .parseCaseInsensitive()
        .toFormatter(Locale.ITALIAN);

System.out.println(date.format(complexFormatter));

// 결과
2014-03-18
20140318
2014-03-18
2014-03-18
18/03/2014
18. marzo 2014
18. marzo 2014
```

<br><br>

## ✅ 12.3 다양한 시간대 및 캘린더 활용

java.time.ZoneId 클래스는 시간대에 관련된 정보를 제공합니다. 
(Summer Time과 같은 복잡한 사항들이 자동 적용됨)

- IANA Time Zone Database에서 제공하는 지역 집합 정보를 사용합니다.
- 해당 정보를 사용하여 ZoneId를 가져오거나, 기존 java.util.TimeZone 객체를 ZoneId로 변경할 수 있습니다.

```java
// 사용
ZoneId seoulZone = ZoneId.of("Asia/Seoul");
ZoneId defaultZone = TimeZone.getDefault().toZoneId();
```

- ZoneId 객체를 LocalDate, LocalDateTime, Instant를 이용해 ZonedDateTime 인스턴스로 변환할 수 있습니다.
    
    ```java
    LocalDate date = LocalDate.of(2023, 8, 22);
    ZonedDateTime zonedDateTime1 = date.atStartOfDay(seoulZone);
    
    LocalDateTime dateTime = LocalDateTime.of(2023, 8, 22, 15, 10);
    ZonedDateTime zonedDateTime2 = dateTime.atZone(seoulZone);
    
    Instant now = Instant.now();
    ZonedDateTime zonedDateTime3 = now.atZone(seoulZone);
    
    System.out.println(zonedDateTime1);
    System.out.println(zonedDateTime2);
    System.out.println(zonedDateTime3);
    
    // 결과 -> 시간이 바뀌진 않고 해당 시간이 어디 지역인지 표시됨
    2023-08-22T00:00+09:00[Asia/Seoul]
    2023-08-22T15:10+09:00[Asia/Seoul]
    2023-08-22T17:30:26.115524+09:00[Asia/Seoul]
    ```
    

- Instant to LocalDateTime
    
    ```java
    Instant now = Instant.now();
    ZoneId londonZone = ZoneId.of("Europe/London");
    LocalDateTime localDateTime = LocalDateTime.ofInstant(now, londonZone);
    System.out.println(localDateTime);
    
    // 결과
    2023-08-22T09:39:05.472433 // 현재 시간을 지정한 zone의 시간을 기준으로 가져옴(런던은 한국 기준 -9시간)
    ```
    
- LocatDate, LocalTime, LocalDateTime, ZoneId, ZonedDateTime의 포함관계

![image](https://github.com/Invincible-Backend-Study/modern-java-in-action/assets/66772624/895c2e4c-4fea-4ccb-b05a-6ab34977b553)

<br>

### UTC/Greenwich 기준의 고정 오프셋

ZoneId의 하위 클래스인 ZoneOffset 클래스는 본초자오선을 기준으로 시간을 더하고, 빼며 커스텀한 시간대의 차이를 생성합니다.

뉴욕의 경우 본초자오선보다 5시간 느리므로 다음과 같이 표현됩니다.

```java
// 오프셋을 이용해 OffsetDateTime을 구할 수 있음
LocalDateTime dateTime = LocalDateTime.of(2022, 8, 22, 14, 0);
OffsetDateTime offsetDateTime = OffsetDateTime.of(dateTime, newYorkOffset);
System.out.println(offsetDateTime);

// 결과
2022-08-22T14:00-05:00
```

<br>

### 대안 캘린더 시스템

Java 8부터 여러 나라의 캘린더 시스템을 제공합니다.

`ThaiBuddhistDate`, `MinguoDate`, `JapaneseDate`, `HijrahDate`
