## 1. Shop 이미지 업로드를 위한 PreSignedUrl 발급

### 1-1. 기본 정보

| 메서드 | 요청 URI                       | 출력 포멧 | 설명                                 |
|-----|------------------------------|-------|------------------------------------|
| GET | /v1/images/presigned-put-url | json  | 이미지 바이너리를 업로드 할 수 있는 URL |

### 1-2. 요청 헤더

| 헤더 key        | 필수 여부 | 설명                                |
|---------------|-------|-----------------------------------|
| Authorization | O     | Bearer ${ACCESS_TOKEN}            |
| Content-Type  | O     | application/x-www-form-urlencoded |

### 1-3. 요청 예시

``` http
GET /v1/images/presigned-put-url
Authorization: Bearer ${ACCESS_TOKEN}
Content-Type: application/x-www-form-urlencoded
```

### 1-4. 응답

``` http
HTTP/1.1 200 OK

{
  "returnValue": {
    "preSignedPutUrl": "https://beautify-project.kr.object.ncloudstorage.com/asasdovbjasoiefjsoeijf",
    "fileId": "1231a7a9-be6f-46e5-9c91-99c5beb702cc"
  }
}
```

## 2. Shop 이미지 다운로드를 위한 PreSignedUrl 발급

### 2-1. 기본 정보

| 메서드 | 요청 URI                             | 출력 포멧 | 설명                       |
|-----|------------------------------------|-------|--------------------------|
| GET | /v1/images/presigned-get-url/${id} | json  | 이미지 바이너리를 다운로드할 수 있는 URL |
- 경로 변수

  | 변수명 | 필수 여부 | 설명                  |
    | ------ | --------- |---------------------|
  | ${id}  | O         | 다운로드 대상 이미지의 fileId |


### 2-2. 요청 헤더
| 헤더 key        | 필수 여부 | 설명                                |
|---------------|-------|-----------------------------------|
| Authorization | O     | Bearer ${ACCESS_TOKEN}            |
| Content-Type  | O     | application/x-www-form-urlencoded |


### 2-3. 요청 예시
``` http
GET /v1/images/presigned-get-url/1231a7a9-be6f-46e5-9c91-99c5beb702cc
Authorization: Bearer ${ACCESS_TOKEN}
Content-Type: application/x-www-form-urlencoded
```

### 2-4. 응답
``` http
"returnValue": {
    "preSignedGetUrl": "https://beautify-project.kr.object.ncloudstorage.com/asasdovbjasoiefjsoeijf"
  }
```
