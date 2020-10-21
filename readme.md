# 뿌리기 기능 구현하기

## 목차

* [요구 사항](#요구-사항)
* [상세 구현 요건 및 제약사항](#상세-구현-요건-및-제약사항)
* [개발 환경](#개발-환경)
* [문제해결 전략](#문제해결-전략)
* [구현 내역](#구현-내역)

## 요구 사항

* 뿌리기, 받기, 조회 기능을 수행하는 REST API 를 구현합니다.
    * 요청한 사용자의 식별값은 숫자 형태이며 "X-USER-ID" 라는 HTTP Header로 전달됩니다.
    * 요청한 사용자가 속한 대화방의 식별값은 문자 형태이며 "X-ROOM-ID" 라는 HTTP Header로 전달됩니다.
    * 모든 사용자는 뿌리기에 충분한 잔액을 보유하고 있다고 가정하여 별도로 잔액에 관련된 체크는 하지 않습니다.
* 작성하신 어플리케이션이 다수의 서버에 다수의 인스턴스로 동작하더라도 기능에 문제가 없도록 설계되어야 합니다.
* 각 기능 및 제약사항에 대한 단위테스트를 반드시 작성합니다.

## 상세 구현 요건 및 제약사항

### 1. 뿌리기 API

* 다음 조건을 만족하는 뿌리기 API를 만들어 주세요.
    * 뿌릴 금액, 뿌릴 인원을 요청값으로 받습니다.
    * 뿌리기 요청건에 대한 고유 token을 발급하고 응답값으로 내려줍니다.
    * 뿌릴 금액을 인원수에 맞게 분배하여 저장합니다. (분배 로직은 자유롭게 구현해 주세요.)
    * token은 3자리 문자열로 구성되며 예측이 불가능해야 합니다.
        
### 2. 받기 API

* 다음 조건을 만족하는 받기 API를 만들어 주세요.
    * 뿌리기 시 발급된 token을 요청값으로 받습니다.
    * token에 해당하는 뿌리기 건 중 아직 누구에게도 할당되지 않은 분배건 하나를
    API를 호출한 사용자에게 할당하고, 그 금액을 응답값으로 내려줍니다.
    * 뿌리기 당 한 사용자는 한번만 받을 수 있습니다.
    * 자신이 뿌리기한 건은 자신이 받을 수 없습니다.
    * 뿌린기가 호출된 대화방과 동일한 대화방에 속한 사용자만이 받을 수
    있습니다.
    * 뿌린 건은 10분간만 유효합니다. 뿌린지 10분이 지난 요청에 대해서는 받기 실패 응답이 내려가야 합니다.

### 3. 조회 API

* 다음 조건을 만족하는 조회 API를 만들어 주세요.
    * 뿌리기 시 발급된 token을 요청값으로 받습니다.
    * token에 해당하는 뿌리기 건의 현재 상태를 응답값으로 내려줍니다. 현재
    상태는 다음의 정보를 포함합니다.
    * 뿌린 시각, 뿌린 금액, 받기 완료된 금액, 받기 완료된 정보 ([받은 금액, 받은
    사용자 아이디] 리스트)
    * 뿌린 사람 자신만 조회를 할 수 있습니다. 다른사람의 뿌리기건이나 유효하지
    않은 token에 대해서는 조회 실패 응답이 내려가야 합니다.
    * 뿌린 건에 대한 조회는 7일 동안 할 수 있습니다.

## 개발 환경

* Language: Java 11
* Framework: Spring Boot 2.3.4.RELEASE
* Database: Mysql (Test: H2)

## 문제해결 전략

### 1. 용어 통일성 확보

* 단어사전, 용어사전 정의
![단어/용어사전](https://user-images.githubusercontent.com/35869083/94777256-2018ba00-03fe-11eb-935c-ee597366efc2.png)

### 2. DB 모델링

* 뿌리기(Sprinkling), 받기(Receiving) 테이블로 구성
![ERD](https://user-images.githubusercontent.com/35869083/94777408-6bcb6380-03fe-11eb-861d-dffcd5dcfe72.png)

    * 기본키: 자동증가 BIGINT 타입
    * 토큰은 중복 방지를 위해 Unique Key 적용
    * 데이터 정합성 확보를 위해 JPA의 낙관적 잠금 사용 (version 컬럼)

### 3. Git

* Issue
    * issue 등록 후 해당 issue 번호로 feature 브랜치 생성 후 개발 진행
* Branch
    * master: 운영 서버에 배포
    * develop: 개발 서버에 배포
    * feature: 요건 개발(브랜치 명명규칙: feature/[issue 번호]-[issue 명])
* Merge
    * feature -> develop: Squash and merge
    * develop -> master: Rebase and merge

### 4. Rest API

* Self-descriptive: Spring Rest Docs 사용
* HATEOS: Spring HATEOS 사용
    
### 5. 테스트

* Service 테스트: 기능 및 로직 테스트 
* Controller 테스트: 헤더, 파라미터 검증 테스트, Rest Docs 생성
    
### 6. 예외 처리

* 글로벌 예외 핸들러를 정의하여 일관성 있게 예외 메시지를 처리함
* 예외 발생 시 리턴할 Dto 클래스를 정의함: ApiError, ApiSubError

| 클래스 | 필드 | 타입 | 설명 |
| --- | --- | --- | --- |
| ApiError | timestamp | LocalDateTime | 예외가 발생한 일시 |
| | status | HttpStatus | 에러 코드 |
| | message | String | 노출용 메시지 |
| | debugMessage | String | 디버그용 메시지 |
| | subErrors | List&lt;ApiSubError&gt; | 파라미터 검증 메시지 |
| ApiSubError | object | String | 클래스명 |
| | field | String | 필드명 |
| | rejectedValue | Object | 검증 실패한 입력 값 |
| | message | String | 상세 메시지 |

### 7. 뿌리기 Token 생성

* SecureRandom 클래스를 사용하여 예측 불가능한 랜덤 문자열 생성
* 숫자, 대문자, 소문자를 랜덤하게 조합

### 8. 뿌리기 분배 방법

* 뿌릴 금액을 뿌릴 인원에 맞게 랜덤으로 금액 분배
* 1명에게 최소 1원은 배정되어야 함

### 9. Redis 적용

* Redis 캐싱을 통한 조회 속도 향상 및 서버 자원 사용 최소화
* 조회 API: token을 키로 사용해서 Redis에 캐싱함
* 받기 API: 받을때마다 token의 캐시를 삭제함(받을때만 조회 내역이 변경됨)
* 7일간만 조회 되어야 하는 이슈로 일단 보류

## 구현 내역

### 1. 요구사항 리스트

| 구분 | 요구사항 | 테스트 |
| --- | --- | --- |
| 공통 | 요청 Header 누락 확인 | SprinklingControllerTest#validateHeaderTest01 |
| | 요청 Header 값 검증 | SprinklingControllerTest#validateHeaderTest02 |
| 뿌리기 | 뿌리기를 요청하고 token을 반환 받음 | SprinklingServiceTest#sprinkleTest01 |
| | 뿌린 금액은 요청한 인원수에게 모든 금액이 분배됨 | SprinklingServiceTest#sprinkleTest02 |
| | 뿌린 금액이 요청한 인원수보다 적으면 예외 발생 | SprinklingServiceTest#sprinkleTest03 |
| | 다수의 서버/인스턴스로 동작하더라도 기능에 문제가 없어야 함 | 낙관적 잠금 적용으로 데이터 정합성 보장 |
| 받기 | 받기를 요청하고 받은 금액을 반환 받음 | ReceivingServiceTest#receiveTest01 |
| | 동일 사용자가 한 뿌리기에서 두번 이상 받으면 예외 발생 | ReceivingServiceTest#receiveTest02 |
| | 자신이 뿌린 건을 자신이 받으면 예외 발생 | ReceivingServiceTest#receiveTest03 |
| | 다른 대화방의 사용자가 받으면 예외 발생 | ReceivingServiceTest#receiveTest04 |
| | 뿌린지 10분이 지난 받기 요청은 예외 발생 | ReceivingServiceTest#receiveTest05 |
| | 미할당 건이 없으면 예외 발생 | ReceivingServiceTest#receiveTest06 |
| 조회 | 조회를 요청하고 뿌리기 상태를 반환 받음 | SprinklingServiceTest#readTest01 |
| | 뿌린 사용자 이외의 사용자가 조회하면 예외 발생 | SprinklingServiceTest#readTest02 |
| | 뿌린지 7일이 지난 조회 요청은 예외 발생 | SprinklingServiceTest#readTest03 |

### 2. API 명세

#### 뿌리기 API (`POST` /v1/sprinklings)

Request headers

| Name | Description |
| --- | --- |
| X-USER-ID | 사용자 ID |
| X-ROOM-ID | 대화방 ID |

Request fields

| Path | Type | Description |
| --- | --- | --- |
| amount | Number | 뿌릴 금액 |
| people | Number | 뿌릴 인원 |

HTTP request

```
POST /v1/sprinklings HTTP/1.1
Content-Type: application/json;charset=UTF-8
X-USER-ID: 900001
X-ROOM-ID: TEST-ROOM
Accept: application/hal+json
Content-Length: 27
Host: 15.164.70.143:8080

{"amount":20000,"people":3}
```

Response fields

| Path | Type | Description |
| --- | --- | --- |
| token | Number | 뿌리기 token |

Example response

```
HTTP/1.1 201 Created
Location: http://15.164.70.143:8080/v1/sprinklings/364
Content-Type: application/hal+json
Content-Length: 362

{
  "token" : "364",
  "_links" : {
    "self" : {
      "href" : "http://15.164.70.143:8080/v1/sprinklings"
    },
    "receiving" : {
      "href" : "http://15.164.70.143:8080/v1/receivings/364"
    },
    "read" : {
      "href" : "http://15.164.70.143:8080/v1/sprinklings/364"
    },
    "profile" : {
      "href" : "/docs/index.html#sprinkling"
    }
  }
}
```

* 받기 API

* 조회 API

### 3. 예외 리스트

 구분 | 예외 클래스 | 응답코드 | 메시지 |
| --- | --- | --- | --- |
| 공통 | MissingRequestHeaderException | 400 | 필수 Header 정보가 누락되었습니다. |
| | ConstraintViolationException | 400 | 요청 Header 정보가 잘못되었습니다. |
| | MethodArgumentNotValidException | 400 | 요청 Body 정보가 잘못되었습니다. |
| | ObjectOptimisticLockingFailureException | 500 | 일시적으로 받기 요청을 처리하지 못했습니다. 잠시 후 다시 시도해주세요. |
| 뿌리기 | InsufficientAmountException | 500 | 뿌린 금액이 요청한 인원수보다 적을 수 없습니다. |
| 받기 | SprinklingNotFoundException | 404 | 유효한 뿌리기 건이 존재하지 않습니다. |
| | DuplicateReceivingUserException | 500 | 동일 사용자가 한 뿌리기에서 두번 이상 받을 수 없습니다. |
| | DuplicateSprinklingUserException | 500 | 자신이 뿌린 건은 자신이 받을 수 없습니다. |
| | DifferentRoomException | 500 | 다른 대화방의 사용자가 받을 수 없습니다. |
| | ReceivingExpiredException | 500 | 뿌린지 10분이 지난 받기 요청은 처리할 수 없습니다. |
| | ReceivingCompletedException | 500 | 받기가 마감되어 받을 수 없습니다. |
| 조회 | PermissionDeniedException | 500 | 뿌린 사용자 이외의 사용자가 조회할 수 없습니다. |
| | ReadExpiredException | 500 | 뿌린지 7일이 지난 조회 요청은 처리할 수 없습니다. |
