# 카테고리 API

카테고리 관련 정보를 등록/조회/수정/삭제하는 HTTP API 입니다.

REST API 방식으로 호출하는 방법은 동일하나, OAuth 2.0 인증 기반이므로 Authorization API를 통해 접근 토큰(access token)을 발급받아 HTTP Header에 포함시켜 전송해야 합니다.



## 1. 카테고리 등록

### 1-1. 기본 정보

| 메서드 | URI            | 출력 포멧 | 설명          |
| ------ | -------------- | --------- | ------------- |
| POST   | /v1/categories | JSON      | 카테고리 등록 |



### 1-2. 요청 헤더

| 헤더 key      | 필수 여부 | 설명                                  |
| ------------- | --------- | ------------------------------------- |
| Authorization | O         | Bearer ${ACCESS_TOKEN} |
| Content-Type  | O         | application/json                      |



### 1-3. 요청 본문

| 변수명          | 데이터 타입 | 제약사항 | 필수여부 | 기본값 | 설명                          |
| --------------- | ----------- | -------- | -------- | ------ | ----------------------------- |
| name            | String      |          | O        |        | 카테고리명                    |
| description     | String      |          | X        |        | 카테고리 설명                 |
| operations      | JSON Array  |          | O        |        | 해당 카테고리에 포함된 시술들 |
| operations.id   | String      |          | O        |        | 시술 아이디                   |
| operations.name | String      |          | O        |        | 시술명                        |



### 1-4. 요청 예시

``` http
POST /v1/categories HTTP/1.1
Authorization: Bearer ${ACCESS_TOKEN}
Content-Type: application/json

{
  "name": "머리미용",
  "description": "헤어라인모발이식, 머리모발이식, 이마축소, 두피문신 등의 시술이 포함된 카테고리 입니다.",
  "operations": [
    {
      "id": "ace52d35",
      "name": "헤어라인모발이식"
    },
    {
      "id": "6a473a23",
      "name": "머리모발이식"
    },
    {
      "id": "a422f3f0",
      "name": "이마축소"
    },
    {
      "id": "53adfcce",
      "name": "두피문신"
    }
  ]
}
```



### 1-5. 응답

``` http
HTTP/1.1 200 OK

{
  "returnValue": {
    "categoryId": "3f799cde"
  }
}
```

- categoryId: 생성된 카테고리 건의 ID
- 에러 응답은 [7. 에러](#7-에러) 참고





## 2. 카테고리 리스트 조회

### 2-1. 기본 정보

| 메서드 | URI            | 출력 포멧 | 설명                 |
| ------ | -------------- | --------- | -------------------- |
| GET    | /v1/categories | JSON      | 카테고리 리스트 조회 |

- 파라미터(Query String)

  | 파라미터명 | 필수 여부 | 설명                                                         |
    | ---------- | --------- | ------------------------------------------------------------ |
  | page       | X         | 페이지 수<br />- default `0`                                 |
  | count      | X         | 조회 결과 개수<br />- default `10`                           |
  | order      | X         | 정렬 조건<br />- `asc`: 카테고리명 오름차순 (default)<br />- `desc`: 카테고리명 내림차순 |



### 2-2. 요청 헤더

| 헤더 key      | 필수 여부 | 설명                                  |
| ------------- | --------- | ------------------------------------- |
| Content-Type  | O         | application/x-www-form-urlencoded     |



### 2-3. 요청 예시

``` http
GET /v1/categories?page=0&count=10&order=asc HTTP/1.1
Content-Type: application/x-www-form-urlencoded
```



### 2-4. 응답

``` http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "returnValue": [
    {
      "id": "788948b2",
      "name": "머리미용",
      "description": "헤어라인모발이식, 머리모발이식, 이마축소, 두피문신 등의 시술이 포함된 카테고리 입니다."
    },
    ...
  ]
}
```

| 필드명      | 데이터 타입 | 설명            |
| ----------- | ----------- | --------------- |
| id          | String      | 카테고리 아이디 |
| name        | String      | 카테고리명      |
| description | String      | 카테고리 설명   |

- 에러 응답은 [7. 에러](#7-에러) 참고



## 3. 카테고리 상세 조회

### 3-1. 기본 정보

| 메서드 | URI                          | 출력 포멧 | 설명               |
| ------ | ---------------------------- | --------- | ------------------ |
| GET    | /v1/categories/details/${id} | JSON      | 카테고리 상세 조회 |

- 경로 변수

  | 변수명 | 필수 여부 | 설명                  |
  | ------ | --------- | --------------------- |
  | ${id}  | O         | 조회 대상 카테고리 id |



### 3-2. 요청 헤더

| 헤더 key      | 필수 여부      | 설명                                  |
| ------------- |------------| ------------------------------------- |
| Content-Type  | O          | application/x-www-form-urlencoded     |



### 3-3. 요청 예시

```http
GET /v1/categories/details/788948b2 HTTP/1.1
Content-Type: application/x-www-form-urlencoded
```

### 3-4. 응답

``` http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "returnValue": [
    {
      "id": "788948b2",
      "name": "머리미용",
      "description": "헤어라인모발이식, 머리모발이식, 이마축소, 두피문신 등의 시술이 포함된 카테고리 입니다.",
      "operations": [
        {
          "id": "ace52d35",
          "name": "헤어라인모발이식"
        },
        {
          "id": "6a473a23",
          "name": "머리모발이식"
        },
        {
          "id": "a422f3f0",
          "name": "이마축소"
        },
        {
          "id": "53adfcce",
          "name": "두피문신"
        }
      ]
    },
    ...
  ]
}
```

| 필드명          | 데이터 타입 | 설명                          |
| --------------- | ----------- | ----------------------------- |
| id              | String      | 카테고리 아이디               |
| name            | String      | 카테고리명                    |
| description     | String      | 카테고리 설명                 |
| operations      | JSON Array  | 해당 카테고리에 포함된 시술들 |
| operations.id   | String      | 시술 아이디                   |
| operations.name | String      | 시술명                        |

- 에러 응답은 [7. 에러](#7-에러) 참고



## 4. 카테고리 기반 샵 리스트 검색

### 4-1. 기본 정보

| 메서드 | URI                        | 출력 포멧 | 설명                         |
| ------ | -------------------------- | --------- | ---------------------------- |
| GET    | /v1/categories/${id}/shops | JSON      | 카테고리 기반 샵 리스트 검색 |

- 경로 변수(Path Variable)

  | 변수명 | 필수 여부 | 설명                  |
  | ------ | --------- | --------------------- |
  | ${id}  | O         | 조회 대상 카테고리 id |

- 파라미터(Query String)

  | 파라미터명 | 필수 여부 | 설명                                                         |
    | ---------- | --------- | ------------------------------------------------------------ |
  | keyword    | O         | 검색 키워드                                                  |
  | page       | X         | 페이지 수<br />- default `0`                                 |
  | count      | X         | 조회 결과 개수<br />- default `10`                           |
  | order      | X         | 정렬조건<br />- `asc`: 오름차순(default)<br />- `desc`: 내림차순 |



### 4-2. 요청 헤더

| 헤더 key      | 필수 여부 | 설명                                  |
| ------------- | --------- | ------------------------------------- |
| Content-Type  | O         | application/x-www-form-urlencoded     |



### 4-3. 요청 예시

``` http
GET /v1/categories/788948b2/shops?keyword=머리미용&page=0&count=10&order=asc HTTP/1.1
Content-Type: application/x-www-form-urlencoded
```



### 4-4. 응답

``` http
HTTP/1.1 200 OK
Content-Type: application/json


{
  "returnValue": [
    {
      "shopId": "2360c169",
      "name": "시술소1",
      "operations": ["두피문신", "눈썹문신", "입술문신"],
      "supportFacilities": ["주차가능", "와이파이", "샤워실"],
      "rate": "4.5",
      "likes": 132,
      "thumbnail": "base64 encoded string"
    },
    ...
  ]
}
```

| 필드명            | 데이터 타입 | 설명                                                         |
| ----------------- | ----------- | ------------------------------------------------------------ |
| shopId            | String      | 샵 아이디                                                    |
| name              | String      | 샵 이름                                                      |
| operations        | JSON Array  | 샵에서 수행하는 시술                                         |
| supportFacilities | JSON Array  | 편의시설                                                     |
| rate              | String      | 평점                                                         |
| likes             | int         | 좋아요 수                                                    |
| thumbnail         | String      | Base64로 인코딩된 이미지 파일 (이미지 태그에 바로 넣을 수 있는 형태) |

- 에러 응답은 [7. 에러](#7-에러) 참고



## 5. 카테고리 수정

### 5-1. 기본 정보

| 메서드 | 요청 URI             | 출력 포멧 | 설명          |
| ------ | -------------------- | --------- | ------------- |
| PATCH  | /v1/categories/${id} | JSON      | 카테고리 수정 |

- 경로 변수 (Path Variable)

  | 변수명 | 필수 여부 | 설명                    |
  | ------ | --------- | ----------------------- |
  | ${id}  | O         | 수정 대상 카테고리의 Id |



### 5-2. 요청 헤더

| 헤더 key      | 필수 여부 | 설명                                  |
| ------------- | --------- | ------------------------------------- |
| Authorization | O         | Bearer ${ACCESS_TOKEN} |
| Content-Type  | O         | application/json                      |



### 5-3. 요청 본문

[1. Category 등록 > 1-3. 요청 본문](#1-3-요청-본문) 과 동일한 형태로 수정이 필요한 필드값만 작성하면 됩니다.



### 5-4. 요청 예시

``` http
PATCH /v1/categories/788948b2 HTTP/1.1
Content-Type: application/json
Authorization: Bearer ${ACCESS_TOKEN}

{
  "name": "피부미용",
  "description": "피부보톡스, 아쿠아필링 등의 시술이 포함된 카테고리 입니다.",
  "operations": [
    {
      "id": "b0bcd81a",
      "name": "피부보톡스"
    },
    {
      "id": "20fa0fc1",
      "name": "아쿠아필링"
    }
  ]
}
```



### 5-5. 응답

``` http
HTTP/1.1 200 OK

{
  "returnValue": {
    "categoryId": "788948b2"
  }
}
```

- 에러 응답은 [7. 에러](#7-에러) 참고



## 6. 카테고리 삭제

### 6-1. 기본 정보

| 메서드 | 요청 URI             | 출력 포멧 | 설명          |
| ------ | -------------------- | --------- | ------------- |
| DELETE | /v1/categories/${id} | JSON      | 카테고리 삭제 |

- 경로 변수 (Path Variable)

  | 변수명 | 필수 여부 | 설명                    |
  | ------ | --------- | ----------------------- |
  | ${id}  | O         | 삭제 대상 카테고리의 Id |



### 6-2. 요청 헤더

| 헤더 key      | 필수 여부 | 설명                                  |
| ------------- | --------- | ------------------------------------- |
| Authorization | O         | Bearer ${ACCESS_TOKEN} |
| Content-Type  | O         | application/x-www-form-urlencoded     |



### 6-3. 요청 예시

``` http
DELETE /v1/categories/788948b2 HTTP/1.1
Authorization: Bearer ${ACCESS_TOKEN}
Content-Type: application/x-www-form-urlencoded
```



### 6-4. 응답

``` http
HTTP/1.1 204 No Content
```

- 에러 응답은 [7. 에러](#7-에러) 참고





## 7. 에러

### 7-1. 에러 응답 형식

``` http
HTTP/1.1 403 Forbidden
Content-type: application/json

{
  "errorCode": "FB001"
  "errorMessage": "해당 API 사용 권한이 없습니다."
}
```

에러 메시지의 형식은 에러가 발생하는 위치와 관계없이 동일하며, 에러의 성격에 따라 400번대 혹은 500번대 에러 코드가 HTTP 응답 코드로 반환됩니다.



### 7-2. 에러 코드

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

