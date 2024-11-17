# 리뷰 API

리뷰 관련 정보를 등록/조회/삭제하는 HTTP API 입니다.

REST API 방식으로 호출하는 방법은 동일하나, OAuth 2.0 인증 기반이므로 로그인 API를 통해 접근 토큰(access token)을 발급받아 HTTP Header에 포함시켜 전송해야 합니다.



## 1. 리뷰 등록

### 1-1. 기본 정보

| 메서드 | URI         | 출력 포멧 | 설명      |
| ------ | ----------- | --------- | --------- |
| POST   | /v1/reviews | JSON      | 리뷰 등록 |



### 1-2. 요청 헤더

| 헤더 key      | 필수 여부 | 설명                   |
| ------------- | --------- | ---------------------- |
| Authorization | O         | Bearer ${ACCESS_TOKEN} |
| Content-Type  | O         | application/json       |



### 1-3. 요청 본문

| 변수명         | 데이터 타입      | 제약사항 | 필수여부 | 기본 값 | 설명             |
| -------------- | ---------------- | -------- | -------- | ------- | ---------------- |
| shop           | JSON             |          | O        |         | 미용 시술소 정보 |
| shop.id        | String           |          | O        |         | 시술소 아이디    |
| shop.name      | String           |          | O        |         | 시술소 이름      |
| opeartion      | JSON             |          | O        |         | 시술 정보        |
| operation.id   | String           |          | O        |         | 시술 아이디      |
| operation.name | String           |          | O        |         | 시술명           |
| operation.date | Long (unix time) |          | O        |         | 시술 날짜        |
| member         | JSON             |          | O        |         | 회원 정보        |
| member.id      | String           |          | O        |         | 회원 아이디      |
| member.name    | String           |          | O        |         | 회원 이름        |
| rate           | String           |          | O        |         | 평점             |
| content        | String           |          | X        |         | 리뷰 내용        |



### 1-4. 요청 예시

``` http
POST /v1/reviews HTTP/1.1
Authorization: Bearer ${ACCESS_TOKEN}
Content-Type: application/json

{
  "shop": {
    "id": "80a490b6",
    "name": "시술소1"
  },
  "operation": {
    "id": "d939f8ed",
    "name": "두피문신",
    "date": 1730437200000
  },
  "member": {
    "id": "sssukho",
    "name": "임석호"
  },
  "rate": "4.5",
  "content": "깔끔하게 잘 해주셨습니다."
}
```



### 1-5. 응답

``` http
HTTP/1.1 200 OK

{
  "returnValue": {
    "reviewId": "bd1cc4f9"
  }
}
```

- 에러 응답은 [5. 에러](#5-에러) 참고



## 2. 샵 리뷰 리스트 조회

### 2-1. 기본 정보

| 메서드 | URI                    | 출력 포멧 | 설명                       |
| ------ | ---------------------- | --------- | -------------------------- |
| GET    | /v1/reviews/shops/{id} | JSON      | 샵에 속한 리뷰 리스트 조회 |

- 경로 변수(Path Variable)

  | 변수명 | 필수 여부 | 설명            |
    | ------ | --------- | --------------- |
  | ${id}  | O         | 조회 대상 샵 id |

- 파라미터(Query String)

  | 파라미터명 | 필수 여부 | 설명                                                         |
    | ---------- | --------- | ------------------------------------------------------------ |
  | page       | X         | 페이지 수 - default `0`                                      |
  | count      | X         | 조회 결과 개수 - default `10`                                |
  | order      | X         | 정렬조건 <br />- `asc`: 오름차순(default) <br />- `desc`: 내림차순 |



### 2-2. 요청 헤더

| 헤더 key      | 필수 여부 | 설명                              |
| ------------- | --------- | --------------------------------- |
| Authorization | O         | Bearer ${ACCESS_TOKEN}            |
| Content-Type  | O         | application/x-www-form-urlencoded |



### 2-3. 요청 예시

``` http
GET /v1/reviews/shops/732e934?page=0&count=10&order=asc HTTP/1.1
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
      "id": "bd1cc4f9",
      "rate": "4.5",
      "member": {
        "id": "sssukho",
        "name": "임석호"
      },
      "operation": {
        "id": "d939f8ed",
        "name": "두피문신",
        "date": 1730437200000
      }
    },
    ...
  ]
}
```

| 필드명         | 데이터 타입     | 설명                |
| -------------- | --------------- | ------------------- |
| id             | String          | 리뷰 아이디         |
| rate           | String          | 평점                |
| member         | JSON            | 리뷰 남긴 회원 정보 |
| member.id      | String          | 회원 아이디         |
| member.name    | String          | 회원 이름           |
| operation      | JSON            | 시술 정보           |
| operation.id   | String          | 시술 아이디         |
| operation.name | String          | 시술명              |
| operation.date | Long(unix time) | 시술 받은 날짜      |

- 에러 응답은 [5. 에러](#5-에러) 참고



## 3. 리뷰 상세 조회

### 3-1. 기본 정보

| 메서드 | URI              | 출력 포멧 | 설명      |
| ------ | ---------------- | --------- | --------- |
| GET    | /v1/reviews/{id} | JSON      | 리뷰 조회 |

- 경로 변수(Path Variable)

  | 변수명 | 필수 여부 | 설명    |
    | ------ | --------- | ------- |
  | ${id}  | O         | 리뷰 id |



### 3-2. 요청 헤더

| 헤더 key      | 필수 여부 | 설명                              |
| ------------- | --------- | --------------------------------- |
| Authorization | O         | Bearer ${ACCESS_TOKEN}            |
| Content-Type  | O         | application/x-www-form-urlencoded |



### 3-3. 요청 예시

``` http
GET /v1/reviews/bd1cc4f9 HTTP/1.1
Authorization: Bearer ${ACCESS_TOKEN}
Content-Type: application/x-www-form-urlencoded
```



### 3-4. 응답

``` http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "returnValue": {
    "id": "bd1cc4f9",
    "rate": "4.5",
    "content": "깔끔하게 잘 해주셨습니다.",
    "member": {
      "id": "sssukho",
      "name": "임석호"
    },
    "operation": {
      "id": "d939f8ed",
      "name": "두피문신",
      "date": 1730437200000
    }
  }
}
```

| 필드명         | 데이터 타입     | 설명                |
| -------------- | --------------- | ------------------- |
| id             | String          | 리뷰 아이디         |
| rate           | String          | 평점                |
| content        | String          | 리뷰 내용           |
| member         | JSON            | 리뷰 남긴 회원 정보 |
| member.id      | String          | 회원 아이디         |
| member.name    | String          | 회원 이름           |
| operation      | JSON            | 시술 정보           |
| operation.id   | String          | 시술 아이디         |
| operation.name | String          | 시술명              |
| operation.date | Long(unix time) | 시술 받은 날짜      |

- 에러 응답은 [5. 에러](#5-에러) 참고



## 4. 리뷰 삭제

### 4-1. 기본 정보

| 메서드 | URI              | 출력 포멧 | 설명      |
| ------ | ---------------- | --------- | --------- |
| DELETE | /v1/reviews/{id} | JSON      | 리뷰 삭제 |

- 경로 변수(Path Variable)

  | 변수명 | 필수 여부 | 설명    |
    | ------ | --------- | ------- |
  | ${id}  | O         | 리뷰 id |



### 4-2. 요청 헤더

| 헤더 key      | 필수 여부 | 설명                              |
| ------------- | --------- | --------------------------------- |
| Authorization | O         | Bearer ${ACCESS_TOKEN}            |
| Content-Type  | O         | application/x-www-form-urlencoded |



### 4-3. 요청 예시

``` http
DELETE /v1/reviews/bd1cc4f9 HTTP/1.1
Authorization: Bearer ${ACCESS_TOKEN}
Content-Type: application/x-www-form-urlencoded
```



### 4-4. 응답

``` http
HTTP/1.1 204 No Content
```

- 에러 응답은 [5. 에러](#5-에러) 참고



## 5. 에러

### 5-1. 에러 응답 형식

``` http
HTTP/1.1 403 Forbidden
Content-Type: application/json

{
  "errorCode": "FB001",
  "errorMessage": "해당 API 사용 권한이 없습니다."
}
```

에러 메시지의 형식은 에러가 발생하는 위치와 관계없이 동일하며, 에러의 성격에 따라 400번대 혹은 500번대 에러 코드가 HTTP 응답 코드로 반환됩니다.



### 5-2. 에러 코드

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





