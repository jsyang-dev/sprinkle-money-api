# 카카오페이 뿌리기 기능 구현하기

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
* Database: Mysql 8 (Test Scope: H2)

## 문제해결 전략

### 1. 용어 통일성 확보

* 단어사전, 용어사전 정의
![](https://user-images.githubusercontent.com/35869083/94777256-2018ba00-03fe-11eb-935c-ee597366efc2.png)

### 2. DB 모델링

* 뿌리기(Sprinkling), 받기(Receiving) 테이블로 구성
![](https://user-images.githubusercontent.com/35869083/94777408-6bcb6380-03fe-11eb-861d-dffcd5dcfe72.png)

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
* Controller 테스트: API 레이어 테스트(헤더, 파라미터 검증), Rest Docs 생성
    
### 6. 예외 처리

* 글로벌 예외 핸들러를 정의하여 일관성 있게 예외 메시지를 처리함
* 예외 발생 시 리턴할 Dto 클래스를 정의함: ApiError, ApiSubError

| 클래스 | 필드 | 타입 | 설명 |
| --- | --- | --- | --- |
| ApiError | timestamp | LocalDateTime | 예외가 발생한 일시 |
| | status | HttpStatus | 에러 코드 |
| | message | String | 노출용 메시지 |
| | debugMessage | String | 디버그용 메시지 |
| | subErrors | List&lt;ApiSubError&gt; | 파라미터 검증 서브 메시지 |
| ApiSubError | object | String | 클래스명 |
| | field | String | 필드명 |
| | rejectedValue | Object | 검증 실패한 입력 값 |
| | message | String | 상세 메시지 |

### 7. 뿌리기 Token 생성

* SecureRandom 클래스를 사용하여 랜덤 문자열 생성
* 숫자, 대문자, 소문자를 랜덤하게 조합

### 8. 뿌리기 분배 방법

* 뿌릴 금액을 뿌릴 인원에 맞게 랜덤으로 금액 분배
* 1명에게 최소 1원은 배정되어야 함

## 구현 내역

### 1. 요구사항 리스트

### 2. API 명세

### 3. 예외 리스트
