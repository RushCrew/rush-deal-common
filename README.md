# ⏳ Time Deal Common Module

타임딜 MSA 프로젝트 전반에서 사용되는 공통 코드(Entity, Response, Error Handling 등)를 관리하는 라이브러리입니다.

---

## 1. 설치 방법 (Installation)

각 마이크로서비스의 `build.gradle` 파일에 아래 설정을 추가하여 라이브러리를 의존성으로 추가합니다.

### build.gradle

```groovy
repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' } // 1. JitPack 저장소 추가
}

dependencies {
     // 2. 공통 모듈 의존성 추가
    implementation 'com.github.RushCrew:rush-deal-common:v0.0.7' // 버전을 확인하세요
}
```




## 2. 필수 설정 (Configuration)

이 라이브러리의 Bean과 Entity를 인식하기 위해 Main Application Class에 아래 설정이 반드시 필요합니다.

### 1. 패키지 스캔 및 JPA Auditing 활성화

공통 모듈의 패키지 경로(com.github.rushcrew.common 등 실제 패키지명 확인 필요)를 스캔 범위에 포함시켜야 합니다.

```java
package com.rushdeal.order; // 각 서비스의 패키지

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@ComponentScan(basePackages = {
    "com.rushdeal.order",           // 1. 현재 내 서비스 패키지
    "com.github.rushcrew.common"    // 2. 공통 모듈 패키지 (라이브러리 내부 패키지명 확인)
})
@EntityScan(basePackages = {
    "com.rushdeal.order",           // 1. 현재 내 서비스 엔티티 경로
    "com.github.rushcrew.common"    // 2. 공통 모듈 엔티티 경로 (BaseEntity 위치)
})
public class OrderServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
```



<br></br>
## 3. 사용 가이드 (Usage)

### 1. BaseEntity (JPA Auditing)

모든 Entity는 BaseEntity를 상속받아 생성일(createdAt)과 수정일(updatedAt), 삭제일(deletedAt)을 자동으로 관리합니다.

```java
@Entity
public class Order extends BaseEntity { 

    @Id
    private Long id;

    // createdAt, updatedAt, deletedAt 컬럼이 자동으로 생성 및 관리됩니다.
}
```


### 2. 공통 응답 (ApiResponse)

Controller에서 응답을 반환할 때 통일된 포맷을 사용합니다. (ApiResponse)
```java
@PostMapping
public ApiResponse<OrderResponse> createOrder(@RequestBody OrderRequest request) {
    OrderResponse response = orderService.create(request);
    
    // 성공 응답 (data 포함)
    return ApiResponse.success(response); 
}

@GetMapping
public ApiResponse<Void> healthCheck() {
    // 성공 응답 (data 없음)
    return ApiResponse.success();
}
```


### 3. 예외 처리 (Global Exception Handling)

비즈니스 로직 실패 시 BusinessException을 던지면 GlobalExceptionHandler가 자동으로 표준 에러 응답을 내려줍니다.
서비스 별로 커스텀 Exception을 만들 경우, BusinessException을 상속하는 형태로 정의해주시면 됩니다.

#### 1. ErrorCode 정의 (각 서비스 내부에서 Enum 생성)
```java
// 각 서비스에서 ErrorCode 인터페이스를 구현
@Getter
@RequiredArgsConstructor
public enum OrderErrorCode implements ErrorCode {
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "ORD-001", "주문을 찾을 수 없습니다."),
    OUT_OF_STOCK(HttpStatus.BAD_REQUEST, "ORD-002", "재고가 부족합니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
```


#### 2. 서비스 로직 내에서 예외 발생시키기
```java
if (stock < 1) {
    // ErrorCode만 정의해서 던지면 됩니다.
    throw new BusinessException(OrderErrorCode.OUT_OF_STOCK);
}
```


<br></br>
## 4. 버전 관리 (Versioning)

기능 추가나 버그 수정 시 jitpack.yml 및 build.gradle 버전을 올리고 Github Release를 생성해야 합니다.
버전별 변경 이력은 아래와 같습니다.

- v0.0.1 ~ v0.0.7: 초기 구성 (BaseEntity, ApiResponse, Exception)
- v0.0.8 : UserRole, GlobalExceptionHandler 추가 변경



