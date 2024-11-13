## 예약 API

예약 관련 정보를 등록/조회/수정/삭제하는 HTTP API 입니다.

REST API 방식으로 호출하는 방법은 동일하나, OAuth 2.0 인증 기반이므로 로그인 API를 통해 접근 토큰(access token)을 발급받아 HTTP Header 에 포함시켜 전송해야 합니다.



## 1. 예약 등록

### 1-1. 기본 정보

| 메서드 | URI              | 출력 포멧 | 설명      |
| ------ | ---------------- | --------- | --------- |
| POST   | /v1/reservations | JSON      | 예약 등록 |



### 1-2. 요청 헤더

| 헤더 key      | 필수 여부 | 설명                                  |
| ------------- | --------- | ------------------------------------- |
| Authorization | O         | Authorization: Bearer ${ACCESS_TOKEN} |
| Content-Type  | O         | application/json                      |



### 1-3. 요청 본문

| 변수명          | 데이터 타입 | 제약사항 | 필수여부 | 기본값 | 설명                            |
| --------------- | ----------- | -------- | -------- | ------ | ------------------------------- |
| startTime       | Long        |          | O        |        | 예약 시작 시간(UTC0, UNIX time) |
| endTime         | Long        |          | O        |        | 예약 종료 시간(UTC0, UNIX time) |
| operation       | JSON Object |          | O        |        | 예약한 시술 정보                |
| operations.id   | String      |          | O        |        | 시술 아이디                     |
| operations.name | String      |          | O        |        | 시술명                          |
| user            | JSON Object |          | O        |        | 예약자 정보                     |
| user.id         | String      |          | O        |        | 예약자 아이디                   |
| user.name       | String      |          | O        |        | 예약자 이름                     |
| user.phone      | String      |          | O        |        | 예약자 연락처                   |



### 1-4. 요청 예시

``` http
POST /v1/reservations HTTP/1.1
Authorization: Bearer ${ACCESS_TOKEN}
Content-Type: application/json

{
  "startTime": 1733025600000,
  "endTime": 1733031000000,
  "operation": {
    "id": "ace52d35-9ee8-49fb-93af-27195c1e0519",
    "name": "두피문신"
  },
  "user": {
    "id": "sssukho",
    "name": "임석호",
    "phone": "010-1234-5678"
  },
  "shop": {
    "id": "80a490b6",
    "name": "시술소1"
  }
}
```



### 1-5. 응답

``` http
HTTP/1.1 200 OK

{
  "returnValue": {
    "reservationId": "732e934"
  }
}
```

- reservationId: 생성된 예약 건의 ID
- 에러 응답은 [6. 에러](#6-에러) 참고



## 2. 샵 예약된 시간들 조회

### 2-1. 기본 정보

| 메서드 | URI                          | 출력 포멧 | 설명                  |
| ------ | ---------------------------- | --------- | --------------------- |
| GET    | /v1/reservations/shops/${id} | JSON      | 샵 예약된 시간들 조회 |

- 경로변수(Path Variable)

  | 변수명 | 필수 여부 | 설명              |
  | ------ | --------- | ----------------- |
  | ${id}  | O         | 조회 대상 샵의 id |

- 파라미터(Query String)

  | 파라미터명 | 필수 여부 | 설명           |
  | ---------- | --------- | -------------- |
  | startDate  | O         | 조회 시작 날짜 |
  | endDate    | O         | 조회 종료 날짜 |

  

### 2-2. 요청 헤더

| 헤더 key      | 필수 여부 | 설명                                  |
| ------------- | --------- | ------------------------------------- |
| Authorization | O         | Authorization: Bearer ${ACCESS_TOKEN} |
| Content-Type  | O         | application/x-www-form-urlencoded     |



### 2-3. 요청 예시

``` http
POST /v1/reservations/shops/2360c169?startDate=1733011200000&endDate=1733043600000 HTTP/1.1
Authorization: Bearer ${ACCESS_TOKEN}
Content-Type: application/x-www-form-urlencoded
```



### 2-4. 응답

``` http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "returnValue": [
    {
      "startTime": 1733025600000,
      "endTime": 1733031000000
    },
    {
      "startTime": 1733032800000,
      "endTime": 1733036400000
    },
    ...
  ]
}
```

- 에러 응답은 [6. 에러](#6-에러) 참고





## 3. 사용자 예약 현황  조회

### 3-1. 기본 정보

| 메서드 | URI                    | 출력 포멧 | 설명                  |
| ------ | ---------------------- | --------- | --------------------- |
| GET    | /v1/reservations/users | JSON      | 사용자 예약 현황 조회 |

- 파라미터(Query String)

  | 파라미터명 | 필수 여부 | 설명           |
  | ---------- | --------- | -------------- |
  | startDate  | O         | 조회 시작 날짜 |
  | endDate    | O         | 조회 종료 날짜 |



### 3-2. 요청 헤더

| 헤더 key      | 필수 여부 | 설명                                  |
| ------------- | --------- | ------------------------------------- |
| Authorization | O         | Authorization: Bearer ${ACCESS_TOKEN} |
| Content-Type  | O         | application/x-www-form-urlencoded     |



### 3-3. 요청 예시

``` http
POST /v1/reservations/users HTTP/1.1
Authorization: Bearer ${ACCESS_TOKEN}
Content-Type: application/x-www-form-urlencoded
```



### 3-4. 응답

``` http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "returnValue": [
    {
      "startTime": 1733025600000,
      "endTime": 1733031000000,
      "opertaion": {
        "id": "f949707a",
        "name": "두피문신"
      },
      "shop": {
        "id": "785381c0",
        "name": "시술소1"
      }
    },
    {
      "startTime": 1733032800000,
      "endTime": 1733036400000,
      "opertaion": {
        "id": "ef925a64",
        "name": "피부미백"
      },
      "shop": {
        "id": "b38ac17f",
        "name": "시술소2"
      }
    },
    ...
  ]
}
```

- 에러 응답은 [6. 에러](#6-에러) 참고



## 4. 예약 수정

### 4-1. 기본 정보

| 메서드 | URI                    | 출력 포멧 | 설명      |
| ------ | ---------------------- | --------- | --------- |
| PATCH  | /v1/reservations/${id} | JSON      | 예약 수정 |

- 경로변수(Path Variable)

  | 변수명 | 필수 여부 | 설명                |
  | ------ | --------- | ------------------- |
  | ${id}  | O         | 수정 대상 예약의 id |



### 4-2. 요청 헤더

| 헤더 key      | 필수 여부 | 설명                                  |
| ------------- | --------- | ------------------------------------- |
| Authorization | O         | Authorization: Bearer ${ACCESS_TOKEN} |
| Content-Type  | O         | application/json                      |



### 4-3. 요청 본문

[1. 예약 등록 > 1-3. 요청 본문](#1-3-요청-본문) 과 동일한 형태로 수정이 필요한 필드값만 작성하면 됩니다.



### 4-4. 요청 예시

``` http
POST /v1/reservations HTTP/1.1
Authorization: Bearer ${ACCESS_TOKEN}
Content-Type: application/json

{
  "startTime": 1733025600000,
  "endTime": 1733031000000
}
```



### 4-5. 응답

``` http
HTTP/1.1 200 OK

{
  "returnValue": {
    "reservationId": "732e934"
  }
}
```

- reservationId: 생성된 예약건의 ID
- 에러 응답은 [6. 에러](#6-에러) 참고



## 5. 예약 삭제

### 5-1. 기본 정보

| 메서드 | URI                    | 출력 포멧 | 설명      |
| ------ | ---------------------- | --------- | --------- |
| DELETE | /v1/reservations/${id} | JSON      | 예약 삭제 |

- 경로변수(Path Variable)

  | 변수명 | 필수 여부 | 설명              |
  | ------ | --------- | ----------------- |
  | ${id}  | O         | 조회 대상 샵의 id |



### 5-2. 요청 헤더

| 헤더 key      | 필수 여부 | 설명                                  |
| ------------- | --------- | ------------------------------------- |
| Authorization | O         | Authorization: Bearer ${ACCESS_TOKEN} |
| Content-Type  | O         | application/x-www-form-urlencoded     |



### 5-3. 요청 예시

``` http
POST /v1/reservations/732e934 HTTP/1.1
Authorization: Bearer ${ACCESS_TOKEN}
Content-Type: application/x-www-form-urlencoded
```



### 5-4. 응답

``` http
HTTP/1.1 200 OK
Content-Type: application/json
```

- 에러 응답은 [6. 에러](#6-에러) 참고



## 6. 에러

에러 메시지의 형식은 에러가 발생하는 위치와 관계없이 동일하며, 에러의 성격에 따라 400번대 혹은 500번대 에러 코드가 HTTP 응답 코드로 반환됩니다.

``` http
HTTP/1.1 400 Bad Request
Content-type: application/json

{
  "errorCode": "BR001"
  "errorMessage": "요청 파라미터 '732e934'가 잘못되었습니다."
}
```

| HTTP 상태 코드 (에러 유형) | 에러 코드 | 에러 메시지                                            | 에러 발생 원인                                               |
| -------------------------- | --------- | ------------------------------------------------------ | ------------------------------------------------------------ |
| 400(잘못된 요청 변수)      | BR001     | 요청 파라미터 '${parameter}' 가 잘못되었습니다.        | 필수 요청 변수가 없거나 요청 변수 이름이 잘못된 경우         |
| 400(형식에 맞지 않는 본문) | BR002     | 본문(Request Body) 형식이 맞지 않습니다.               | Request Body 형태가 잘못된 경우 (=서버측과 맞지 않는 JSON scheme 등) |
| 401(인증 실패)             | UA001     | ACCESS_TOKEN 이 잘못되었습니다.                        | HTTP 헤더에 접근 토큰(ACCESS_TOKEN)이 없는 경우              |
| 401(인증 실패)             | UA002     | ACCESS_TOKEN 이 만료되었습니다.                        | 접근 토큰(ACCESS_TOKEN)이 만료된 경우                        |
| 403(접근 금지)             | FB001     | 해당 API 사용 권한이 없습니다.                         | 로그인된 사용자의 접근 권한이 낮을 경우                      |
| 404(존재하지 않는 API)     | NF001     | 요청 URL이 잘못되었습니다.                             | 존재하지 않는 API에 요청을 보낸 경우                         |
| 404(존재하지 않는 리소스)  | SH001     | 등록되지 않은 미용시술소(Shop Id) 입니다.              | 서버상에 존재하지 않는 ID에 관련 기능을 요청한 경우          |
| 405(메서드 허용 안함)      | MN001     | 허용된 HTTP Method 가 아닙니다.                        | GET으로 호출해야 하는 API인데, POST로 호출하는 경우 등등     |
| 500(서버 오류)             | IS001     | 시스템 에러가 발생하였습니다. 관리자에게 문의해주세요. | 연동된 외부 시스템에서의 에러 혹은 개발 단계에서 식별하지 못했던 에러 발생 |
| 503(비정상적인 서버 상태)  |           |                                                        | 서버가 내려가있거나 심각한 장애상황이 발생한 상태            |





