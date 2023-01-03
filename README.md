# MutsaSNS

## 미션 요구사항 분석 & 체크리스트

---

### 필수과제(6/6)

- [x] 회원가입
- [x] Swagger
- [x] AWS EC2에 Docker 배포
- [x] Gitlab CI & Crontab CD
- [x] 로그인
- [x] 포스트 작성, 수정, 삭제, 리스트

### 도전과제(2.25/3)

- [x] 화면 UI 개발
    - [x] 회원가입
    - [ ] 로그인
    - [ ] 글쓰기
    - [x] 조회
- [x] ADMIN 회원으로 등급업하는 기능

- [x] ADMIN 회원이 로그인 시 자신이 쓴 글이 아닌 글과 댓글에 수정, 삭제를 할 수 있는 기능

---

## 1주차 미션 요약

### [접근 방법]
#### 구현 중점
필수 과제는 빠짐없이 구현하기

강의 자료나 인터넷에서 복사를 하더라도 코드가 무슨 내용인지 이해하고 복사하기

그렇지 못할 경우 따로 파둔 프로젝트에서 연습하기

tdd를 최대한 활용하기
#### 참고 자료
수업시간 코드와 강사님 유튜브 자료

테스트 작성을 위해 mockito 관련 블로그 글을 많이 읽었습니다.

#### 결과물
필수 과제로 나왔던 모든 기능이 작동하고 부족하지만 도전과제도 어느 정도 해냈다.

### [특이 사항]

#### 아쉬웠던 점, 궁금한 점

**필수 과제 - 예외 처리**

jwt 토큰을 예외처리 할 때 토큰 만료와 같은 예외는 spring 내부까지 들어오지 못하고
밖에서 처리되는 것 같다 

securityConfig 쪽을 설정 하면 spring 밖의 예외 또한 핸들링 하는 방법이 있을텐데
해당 부분을 하지 못해서 아쉽다 

해당 예외는 어떤식으로 처리해야 할까?


**도전 과제 - 세션 처리**

나는 ui 도전 과제 개발을 수업시간에 배운 mustache를 활용 했다

조회와 회원가입은 어떻게 수업시간에 한 자료로 해결 할 수 있었다

그러나 포스트 작성,수정과 같은 기능은 로그인에서 받은 token을 통해 인증을 해야 진행 할 수 있는데
로그인 처리를 하고 클라이언트에 토큰을 넘기는 방법을 검색하다 시간이 부족해서 아쉽다.

검색해보니 httpsession과 관련된 내용을 활용하면 될 것 같으니 나중에 refactoring 할 때 더 해봐야 알 것 같다

또한 내가 nodejs나 react를 활용할 줄 알았더라면

mvc 방식이 아니라 기존에 만들어놓은 api 기능들만 가지고 fe서버를 새로 만들거나 했으면

controller를 따로 만들지 않고 깔끔 하게 해결 할 수 있었을 것 같다

나중에 시간이 날 때 node와 같은 내용들도 한번 공부 해봐야겠다.


## URL
http://ec2-13-124-16-45.ap-northeast-2.compute.amazonaws.com:8080/

Admin
> userName : admin
>
> password : 1q2w3e4r!

## ERD
![img.png](img.png)

## Endpoint

## Users

### 회원가입
`(POST) /api/v1/users/join`

**Request Body**
```json
{
"userName": "String",
"password": "String",
}
```

**Response Body**
```json
{
    "resultCode": "SUCCESS",
    "result": {
        "userId": Long,
        "userName": "String"
    }
}
```
<br>

### 로그인
`(POST) /api/v1/users/login`

**RequestBody**
```json
{
"userName": "String",
"password": "String"
}
```

**Response Body**
```json
{
  "jwt": "String"
}
```

### 권한 수정 : admin계정으로만 가능
`(POST) /api/v1/users/{id}/role/change`

**RequestBody**
```json
{
  "role" : "String"
}
```

**Response Body**
```json
{
  "resultCode": "SUCCESS",
  "result": {
    "message" : "권한 수정 완료", 
    "userId": Long,
  }
}
```
<br>

## Posts
### 게시글 전체 조회
`(GET) /api/v1/posts`

**Response Body**
```json
{
  "resultCode": "SUCCESS",
  "result": {
    "content": OnePostResponse[],
    "pageable": Pageable,
    "last": boolean,
    "totalElements": Number,
    "totalPages": Number,
    "size": Number,
    "number": Number,
    "first": boolean,
    "sort": Sort,
    "numberOfElements": Number,
    "empty": boolean
  }
}
```

### 한 개 조회
`(GET) /api/v1/posts/{id}`

**Response Body**
```json
{
  "resultCode": "SUCCESS",
  "result": {
    "id": Long,
    "title" : "String",
    "body" : "String",
    "userName": "String",
    "createdAt" : LocalDateTime,
    "lastModifiedAt" : LocalDateTime,
  }
}
```

### 게시글 작성
`(POST) /api/v1/posts`

**Request Body**
```json
{
    "title": "String",
    "body": "String"
}
```

**Response Body**
```json
{
    "resultCode": "SUCCESS",
    "result": {
        "message": "포스트 등록 완료",
        "postId": Long
    }
}
```

### 수정(user가 동일하거나 admin만 가능)
`(PUT) /api/v1/posts/{id}`

**Request Body**
```json
{
    "title": "String",
    "body": "String"
}
```

**Response Body**
```json
{
    "resultCode": "SUCCESS",
    "result": {
        "message": "포스트 수정 완료",
        "postId": Long
    }
}
```

#### 삭제(user가 동일하거나 admin만 가능)
`(DELETE) /api/v1/posts/{id}`

**Response Body**
```json
{
    "resultCode": "SUCCESS",
    "result": {
        "message": "포스트 삭제 완료",
        "postId": Long
    }
}
```

## 도전과제 - 화면 UI 개발

### 전체 글 조회
http://ec2-13-124-16-45.ap-northeast-2.compute.amazonaws.com:8080/posts

### 게시글 한 개 조회
http://ec2-13-124-16-45.ap-northeast-2.compute.amazonaws.com:8080/posts/50

### 회원 가입
http://ec2-13-124-16-45.ap-northeast-2.compute.amazonaws.com:8080/users/join


