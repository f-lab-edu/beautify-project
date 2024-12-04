# 샵(미용 시술소) API

샵(미용 시술소) 관련 정보를 등록/조회/수정/삭제하는 HTTP API 입니다.

REST API 방식으로 호출하는 방법은 동일하나, OAuth 2.0 인증 기반이므로 Authorization API를 통해 접근 토큰(access token)을 발급받아 HTTP
Header에 포함시켜 전송해야 합니다.

## 1. 샵 등록

### 1-1. 기본 정보

| 메서드  | URI       | 출력 포멧 | 설명      |
|------|-----------|-------|---------|
| POST | /v1/shops | JSON  | 카테고리 등록 |

### 1-2. 요청 헤더

| 헤더 key        | 필수 여부 | 설명                     |
|---------------|-------|------------------------|
| Authorization | O     | Bearer ${ACCESS_TOKEN} |
| Content-Type  | O     | application/json       |

### 1-3. 요청 본문

| 변수명                              | 데이터 타입      | 제약 사항 | 필수 여부 | 기본값 | 설명                                                                                                         |
|----------------------------------|-------------|-------|-------|-----|------------------------------------------------------------------------------------------------------------|
| name                             | String      | 128   | O     |     | 샵 이름                                                                                                       |
| contact                          | String      | 13    | O     |     | 샵 대표 연락처                                                                                                   |
| url                              | String      | 1024  | X     |     | SNS 대표 URL                                                                                                 |
| introduction                     | String      | 1024  | X     |     | 소개글                                                                                                        |
| operationIds                     | JSON Array  |       | O     |     | 카테고리 아이디                                                                                                   |
| facilityIds                      | JSON Array  |       | X     |     | 편의시설 아이디                                                                                                   |
| imageFileIds                     | JSON Array  |       | X     |     | [8. Shop 이미지 업로드를 위한 PreSignedUrl 발급 요청](#8-Shop 이미지 등록을 위한 PreSignedUrl 발급) 에서 응답으로 받은 각 이미지에 해당하는 fileId |
| businessTime                     | JSON Object |       | X     |     | 영업 시간                                                                                                      |
| businessTime.openTime            | String      |       | X     |     | 영업 시작 시간                                                                                                   |
| businessTime.closeTime           | String      |       | X     |     | 영업 종료 시간                                                                                                   |
| businessTime.breakBeginTime      | String      |       | X     |     | 휴식 시작 시간                                                                                                   |
| businessTime.breanEndTime        | String      |       | X     |     | 휴식 종료 시간                                                                                                   |
| businessTime.offDayOfWeek        | JSON Array  |       | X     |     | 영업 휴무일                                                                                                     |
| address                          | JSON Object |       | O     |     | 주소 정보                                                                                                      |
| address.dongCode                 | String      |       | O     |     | 법정동코드                                                                                                      |
| address.siDoName                 | String      |       | O     |     | 시도명                                                                                                        |
| address.siGoonGooName            | String      |       | O     |     | 시군구명                                                                                                       |
| address.eubMyungDongName         | String      |       | O     |     | 읍면동명                                                                                                       |
| address.roadNameCode             | String      |       | O     |     | 도로명코드                                                                                                      |
| address.roadName                 | String      |       | O     |     | 도로명                                                                                                        |
| address.underGround              | String      |       | O     |     | 지하여부                                                                                                       |
| address.roadMainNum              | String      |       | O     |     | 건물본번                                                                                                       |
| address.roadSubNum               | String      |       | O     |     | 건물부번                                                                                                       |
| address.siGoonGooBuildingName    | String      |       | O     |     | 시군구건물명                                                                                                     |
| address.zipCode                  | String      |       | O     |     | 기초구역번호(새우편번호)                                                                                              |
| address.apartComplex             | String      |       | O     |     | 공동주택여부                                                                                                     |
| address.eubMyungDongSerialNumber | String      |       | O     |     | 읍면동일련번호                                                                                                    |
| address.latitude                 | String      |       | O     |     | 위도                                                                                                         |
| address.longitude                | String      |       | O     |     | 경도                                                                                                         |

### 1-4. 요청 예시

``` http
POST /v1/shops HTTP/1.1
Authorization: Bearer ${ACCESS_TOKEN}
Content-Type: application/json;

{
  "name": "시술소1",
  "contact": "010-1234-5678",
  "url": "www.naver.com",
  "introduction": "안녕하세요 시술소1 입니다. 두피문신, 눈썹문신, 입술문신 전문입니다.",
  "operationIds": ["2d0a8ad5", "7b603978", "2e933db2"],
  "facilityIds": ["cbc94211", "e652bf54", "1671fc08"],
  "imageFileIds": ["1231a7a9-be6f-46e5-9c91-99c5beb702cc", "74eab2e7-3c6c-455b-a676-fa3dd4367f6b"]
  "businessTime": {
    "openTime": "09:00",
    "closeTime": "18:00",
    "breakBeginTime": "12:00",
    "breakEndTime": "13:00",
    "offDayOfWeek": ["sunday", "monday"]
  },
  "address": {
    "dongCode": "",
    "siDoName": "서울특별시",
    "siGoonGooName": "마포구",
    "eubMyunDongName": "상암동",
    "roadNameCode": "",
    "roadName": "월드컵북로",
    "underGround": "",
    "roadMainNum": "481",
    "roadSubNum": "",
    "siGoonGooBuildingName": "상암 오벨리스크 2차",
    "zipCode": "03902",
    "apartComplex": "1",
    "eubMyungDongSerialNumber": "",
    "latitude": "",
    "longitude": ""
  }
}
```

### 1-5. 응답

``` http
HTTP/1.1 200 OK

{
  "returnValue": {
    "shopId": "732e934"
  }
}
```

- 에러 응답은 [10. 에러](#10-에러) 참고

## 2. 샵 리스트 조회

### 2-1. 기본 정보

| 메서드 | 요청 URI    | 출력 포멧 | 설명       |
|-----|-----------|-------|----------|
| GET | /v1/shops | JSON  | 샵 리스트 조회 |

- 파라미터 (Query String)

  | 파라미터명 | 필수 여부 | 설명                                                         |
      | ---------- | --------- | ------------------------------------------------------------ |
  | type       | O         | 검색 유형<br />- `shopName`: 샵 이름으로 검색<br />- `location`: 지역으로 검색<br />- `like`: 좋아요 개수 순으로 검색<br />- `rate`: 평점 순으로 검색 |
  | page       | X         | 페이지 수<br />- default `0`                                 |
  | count      | X         | 조회 결과 개수<br />- default `10`                           |
  | order      | X         | 정렬조건<br />- `asc`: 오름차순 (default)<br />- `desc`: 내림차순 |

### 2-2. 요청 헤더

| 헤더 key       | 필수 여부 | 설명                                |
|--------------|-------|-----------------------------------|
| Content-Type | O     | application/x-www-form-urlencoded |

### 2-3. 요청 예시

``` http
GET /v1/shops?type=시술소&page=0&count=10&order=asc HTTP/1.1
Content-Type: application/x-www-form-urlencoded
```

### 2-4. 응답

``` http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "returnValue": [
    {
      "id": "2360c169",
      "name": "시술소1",
      "operations": ["두피문신", "눈썹문신", "입술문신"],
      "facilities": ["주차가능", "와이파이", "샤워실"],
      "rate": "4.5",
      "likes": 132,
      "likePushed": false,
      "thumbnailUrl": "www.s3.image1"
    },
    {
      "id": "f4804d31",
      "name": "시술소2",
      "operations": ["타투"],
      "facilities": ["와이파이"],
      "rate": "3.0",
      "likes": 20,
      "likePushed": false,
      "thumbnailUrl": "www.s3.image2"
    },
    ...
  ]
}
```

| 필드명          | 데이터 타입     | 설명          |
|--------------|------------|-------------|
| name         | String     | 샵 이름        |
| operations   | JSON Array | 샵에서 수행하는 시술 |
| facilities   | JSON Array | 편의시설        |
| rate         | String     | 평점          |
| likes        | int        | 좋아요 수       |
| likePushed   | boolean    | 좋아요 클릭 여부   |
| thumbnailUrl | String     | 썸네일 이미지 URL |

- 에러 응답은 [10. 에러](#10-에러) 참고

## 3. 샵 상세 조회

### 3-1. 기본 정보

| 메서드 | 요청 URI                  | 출력 포멧 | 설명      |
|-----|-------------------------|-------|---------|
| GET | /v1/shops/details/${id} | json  | 샵 상세 조회 |

- 경로 변수 (Path Variable)

  | 변수명 | 필수 여부 | 설명                |
    | ------ | --------- | ------------------- |
  | ${id}  | O         | 조회 대상 Shop의 Id |

### 3-2. 요청 헤더

| 헤더 key       | 필수 여부 | 설명                                |
|--------------|-------|-----------------------------------|
| Content-Type | O     | application/x-www-form-urlencoded |

### 3-3. 요청 예시

``` http
GET /v1/shops/details/2360c169 HTTP/1.1
Content-Type: application/x-www-form-urlencoded
```

### 3-4. 응답

``` http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "returnValue": {
    "id": "2360c169",
    "name": "시술소1",
    "contact": "010-1234-5678",
    "url": "www.naver.com",
    "introduction": "안녕하세요 시술소1 입니다. 두피문신, 눈썹문신, 입술문신 전문입니다.",
    "likePushed": false,
    "rate": 4.5,
    "likes": 132,
    "operations": ["두피문신", "눈썹문신", "입술문신"],
    "facilites": ["와이파이", "주차가능", "개인샤워실"],
    "categories": ["머리미용", "피부미용"],
    "businessTime": {
      "openTime": "09:00",
      "closeTime": "18:00",
      "breakBeginTime": "12:00",
      "breanEndTime": "13:00",
      "offDayOfWeek": ["sunday", "monday"]
    },
    "address": {
      "siDoName": "서울특별시",
      "siGoonGooName": "마포구",
      "eubMyunDongName": "상암동",
      "roadName": "월드컵북로",
      "roadMainNum": "481",
      "siGoonGooBuildingName": "상암 오벨리스크 2차",
      "zipCode": "03902",
      "latitude": "", 
      "longitude": ""
    },
    "imageIds": [
      "1231a7a9-be6f-46e5-9c91-99c5beb702cc",
      "74eab2e7-3c6c-455b-a676-fa3dd4367f6b"
    ]
  }
}
```

| 필드명                           | 데이터 타입      | 설명                         |
|-------------------------------|-------------|----------------------------|
| id                            | String      | 샵 ID                       |
| name                          | String      | 샵 이름                       |
| contact                       | String      | 연락처                        |
| url                           | String      | 대표 SNS 주소                  |
| rate                          | String      | 평점                         |
| likes                         | Long        | 좋아요                        |
| likePushed                    | boolean     | 좋아요 버튼 여부                  |
| introduction                  | String      | 소개글                        |
| operations                    | JSON Array  | 지원 시술 목록                   |
| facilities                    | JSON Array  | 이용 편의시설                    |
| categories                    | JSON Array  | 미용 카테고리                    |
| businessTime                  | JSON Object | 영업 시간                      |
| businessTime.openTime         | String      | 영업 시작 시간                   |
| businessTime.closeTime        | String      | 영업 종료 시간                   |
| businessTime.breakBeginTime   | String      | 휴식 시작 시간                   |
| businessTime.breakEndTime     | String      | 휴식 종료 시간                   |
| businessTime.offDayOfWeek     | JSON Array  | 휴무일                        |
| address                       | JSON Object | 주소                         |
| address.siDoName              | String      | 시도명                        |
| address.siGoonGooName         | String      | 시군구명                       |
| address.eubMyunDongName       | String      | 읍면동명                       |
| address.roadName              | String      | 도로명                        |
| address.roadMainNum           | String      | 건물본번                       |
| address.siGoonGooBuildingName | String      | 시군구건물명                     |
| address.zipCode               | String      | 기초구역번호(새우편번호)              |
| address.latitude              | String      | 위도                         |
| address.longitude             | String      | 경도                         |
| imageIds                      | JSON Array  | image 업로드시 응답으로 받았던 fileId |

- 에러 응답은 [10. 에러](#10-에러) 참고

## 4. 샵 수정

### 4-1. 기본정보

| 메서드   | 요청 URI          | 출력 포멧 | 설명   |
|-------|-----------------|-------|------|
| PATCH | /v1/shops/${id} | json  | 샵 수정 |

- 경로 변수 (Path Variable)

  | 변수명 | 필수 여부 | 설명              |
    | ------ | --------- | ----------------- |
  | ${id}  | O         | 수정 대상 샵의 Id |

### 4-2. 요청 헤더

| 헤더 key        | 필수 여부 | 설명                     |
|---------------|-------|------------------------|
| Authorization | O     | Bearer ${ACCESS_TOKEN} |
| Content-Type  | O     | application/json       |

### 4-3. 요청 본문

[1. Shop 등록 API > 1-3. 요청 본문](#1-3-요청-본문) 과 동일한 형태로 수정이 필요한 필드값만 작성하면 됩니다. 단, 좋아요(likes), 평점(rate)은
각 API 통해서만 수정 가능하기 때문에 만약 본문에 필드들을 명시하여 요청을 보내더라도 해당 필드값들은 무시합니다.

배열 타입의 값을 수정하고자 하는 경우, 수정/삭제 대상을 변경/삭제하고 전체 배열을 다시 요청해야 합니다.

### 4-4. 요청 예시

  ``` http
  PATCH /v1/shops/2360c169 HTTP/1.1
  Authorization: Bearer ${ACCESS_TOKEN}
  Content-Type: application/json
  
  {
    "contact": "010-1234-5678",
    "address": {
      "siDoName": "서울특별시",
      "siGoonGooName": "마포구",
      "eubMyunDongName": "상암동",
      "roadName": "월드컵북로",
      "roadMainNum": "482",
      "siGoonGooBuildingName": "상암 오벨리스크 123차",
      "zipCode": "03902",
      "latitude": "", 
      "longitude": ""
    }
  }
  ```

### 4-5. 응답

``` http
HTTP/1.1 200 OK

{
  "returnValue": {
    "shopId": "2360c169"
  }
}
```

- 에러 응답은 [10. 에러](#10-에러) 참고

## 5. 샵 삭제

### 5-1. 기본 정보

| 메서드    | 요청 URI          | 출력 포멧 | 설명   |
|--------|-----------------|-------|------|
| DELETE | /v1/shops/${id} | JSON  | 샵 삭제 |

- 경로 변수 (Path Variable)

  | 변수명 | 필수 여부 | 설명              |
    | ------ | --------- | ----------------- |
  | ${id}  | O         | 삭제 대상 샵의 Id |

### 5-2. 요청 헤더

| 헤더 key        | 필수 여부 | 설명                                |
|---------------|-------|-----------------------------------|
| Authorization | O     | Bearer ${ACCESS_TOKEN}            |
| Content-Type  | O     | application/x-www-form-urlencoded |

### 5-3. 요청 예시

``` http
DELETE /v1/shops/2360c169 HTTP/1.1
Authorization: Bearer ${ACCESS_TOKEN}
Content-Type: application/x-www-form-urlencoded
```

### 5-4. 응답

``` http
HTTP/1.1 204 No Content
```

- 에러 응답은 [10. 에러](#10-에러) 참고

## 6. 샵 좋아요

### 6-1. 기본 정보

| 메서드  | 요청 URI                | 출력 포멧 | 설명    |
|------|-----------------------|-------|-------|
| POST | /v1/shops/likes/${id} | JSON  | 샵 좋아요 |

- 경로 변수 (Path Variable)

  | 변수명 | 필수 여부 | 설명                   |
    | ------ | --------- | ---------------------- |
  | ${id}  | O         | 좋아요 할 대상 샵의 Id |

### 6-2. 요청 헤더

| 헤더 key        | 필수 여부 | 설명                                                     |
|---------------|-------|--------------------------------------------------------|
| Authorization | O     | Bearer ${ACCESS_TOKEN}<br />권한 정보가 포함된 액세스 토큰을 포함하여 요청 |
| Content-Type  | O     | application/x-www-form-urlencoded                      |

### 6-3. 요청 예시

``` http
POST /v1/shops/likes/2360c169 HTTP/1.1
Authorization: Bearer ${ACCESS_TOKEN}
Content-Type: application/x-www-form-urlencoded
```

### 6-4. 응답

``` http
HTTP/1.1 200 OK

{
  "returnValue": {
    "shopId": "2360c169"
  }
}
```

- 에러 응답은 [10. 에러](#10-에러) 참고

## 7. 샵 좋아요 취소

### 7-1. 기본 정보

| 메서드    | 요청 URI                | 출력 포멧 | 설명       |
|--------|-----------------------|-------|----------|
| DELETE | /v1/shops/likes/${id} | json  | 샵 좋아요 취소 |

- 경로 변수 (Path Variable)

  | 변수명 | 필수 여부 | 설명                       |
    | ------ | --------- | -------------------------- |
  | ${id}  | O         | 좋아요 취소할 대상 샵의 Id |

### 7-2. 요청 헤더

| 헤더 key        | 필수 여부 | 설명                                |
|---------------|-------|-----------------------------------|
| Authorization | O     | Bearer ${ACCESS_TOKEN}            |
| Content-Type  | O     | application/x-www-form-urlencoded |

### 7-3. 요청 예시

``` http
DELETE /v1/shops/likes/2360c169 HTTP/1.1
Authorization: Bearer ${ACCESS_TOKEN}
Content-Type: application/x-www-form-urlencoded
```

### 7-4. 응답

``` http
HTTP/1.1 204 No Content
```

- 에러 응답은 [10. 에러](#10-에러) 참고

## 8. Shop 이미지 업로드를 위한 PreSignedUrl 발급

### 8-1. 기본 정보

| 메서드 | 요청 URI                       | 출력 포멧 | 설명                                 |
|-----|------------------------------|-------|------------------------------------|
| GET | /v1/images/presigned-put-url | json  | 이미지 바이너리를 업로드 할 수 있는 URL |

### 8-2. 요청 헤더

| 헤더 key        | 필수 여부 | 설명                                |
|---------------|-------|-----------------------------------|
| Authorization | O     | Bearer ${ACCESS_TOKEN}            |
| Content-Type  | O     | application/x-www-form-urlencoded |

### 8-3. 요청 예시

``` http
GET /v1/images/presigned-put-url
Authorization: Bearer ${ACCESS_TOKEN}
Content-Type: application/x-www-form-urlencoded
```

### 8-4. 응답

``` http
HTTP/1.1 200 OK

{
  "returnValue": {
    "preSignedPutUrl": "https://beautify-project.kr.object.ncloudstorage.com/asasdovbjasoiefjsoeijf",
    "fileId": "1231a7a9-be6f-46e5-9c91-99c5beb702cc"
  }
}
```

- 에러 응답은 [10. 에러](#10-에러) 참고

## 9. Shop 이미지 다운로드를 위한 PreSignedUrl 발급

### 9-1. 기본 정보

| 메서드 | 요청 URI                             | 출력 포멧 | 설명                       |
|-----|------------------------------------|-------|--------------------------|
| GET | /v1/images/presigned-get-url/${id} | json  | 이미지 바이너리를 다운로드할 수 있는 URL |
- 경로 변수

  | 변수명 | 필수 여부 | 설명                  |
  | ------ | --------- |---------------------|
  | ${id}  | O         | 다운로드 대상 이미지의 fileId |


### 9-2. 요청 헤더
| 헤더 key        | 필수 여부 | 설명                                |
|---------------|-------|-----------------------------------|
| Authorization | O     | Bearer ${ACCESS_TOKEN}            |
| Content-Type  | O     | application/x-www-form-urlencoded |


### 9-3. 요청 예시
``` http
GET /v1/images/presigned-get-url
Authorization: Bearer ${ACCESS_TOKEN}
Content-Type: application/x-www-form-urlencoded
```

### 9-4. 응답
``` http
"returnValue": {
    "preSignedGetUrl": "https://beautify-project.kr.object.ncloudstorage.com/asasdovbjasoiefjsoeijf"
  }
```


## 10. 에러

### 10-1. 에러 응답 형식

``` http
HTTP/1.1 404 Not Found
Content-type: application/json

{
  "errorCode": "SH001"
  "errorMessage": "등록되지 않은 미용시술소(Shop id)입니다."
}
```

에러 메시지의 형식은 에러가 발생하는 위치와 관계없이 동일하며, 에러의 성격에 따라 400번대 혹은 500번대 에러 코드가 HTTP 응답 코드로 반환됩니다.

### 10-2. 에러 코드

| HTTP 상태 코드 (에러 유형) | 에러 코드 | 에러 메시지                            | 에러 발생 원인                                            |
|--------------------|-------|-----------------------------------|-----------------------------------------------------|
| 400(잘못된 요청 변수)     | BR001 | 요청 파라미터 '${parameter}' 가 잘못되었습니다. | 필수 요청 변수가 없거나 요청 변수 이름이 잘못된 경우                      |
| 400(형식에 맞지 않는 본문)  | BR002 | 본문(Request Body) 형식이 맞지 않습니다.     | Request Body 형태가 잘못된 경우 (=서버측과 맞지 않는 JSON scheme 등) |
| 401(인증 실패)         | UA001 | ACCESS_TOKEN 이 잘못되었습니다.           | HTTP 헤더에 접근 토큰(ACCESS_TOKEN)이 없는 경우                 |
| 401(인증 실패)         | UA002 | ACCESS_TOKEN 이 만료되었습니다.           | 접근 토큰(ACCESS_TOKEN)이 만료된 경우                         |
| 403(접근 금지)         | FB001 | 해당 API 사용 권한이 없습니다.               | 로그인된 사용자의 접근 권한이 낮을 경우                              |
| 404(존재하지 않는 API)   | NF001 | 요청 URL이 잘못되었습니다.                  | 존재하지 않는 API에 요청을 보낸 경우                              |
| 404(존재하지 않는 리소스)   | SH001 | 등록되지 않은 미용시술소(Shop Id) 입니다.       | 서버상에 존재하지 않는 Shop ID에 관련 기능을 요청한 경우                 |
| 405(메서드 허용 안함)     | MN001 | 허용된 HTTP Method 가 아닙니다.           | GET으로 호출해야 하는 API인데, POST로 호출하는 경우 등등               |
| 500(서버 오류)         | IS001 | 시스템 에러가 발생하였습니다. 관리자에게 문의해주세요.    | 연동된 외부 시스템에서의 에러 혹은 개발 단계에서 식별하지 못했던 에러 발생          |
| 503(비정상적인 서버 상태)   |       |                                   | 서버가 내려가있거나 심각한 장애상황이 발생한 상태                         |

