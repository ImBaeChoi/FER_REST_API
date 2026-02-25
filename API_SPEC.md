# FER API 명세서

## 공통 사항

### Base URL
```
http://localhost:8080
```

### 인증
- JWT Bearer 토큰 방식
- 인증이 필요한 API는 요청 헤더에 포함
```
Authorization: Bearer {accessToken}
```

### 공통 응답 형식

**성공 (data 있음)**
```json
{
  "success": true,
  "data": { ... },
  "error": null
}
```

**성공 (data 없음)**
```json
{
  "success": true,
  "data": null,
  "error": null
}
```

**실패**
```json
{
  "success": false,
  "data": null,
  "error": {
    "status": 400,
    "message": "오류 메시지"
  }
}
```

> **주의:** 운전 세션·이벤트 API의 성공 응답은 `ApiResponse` 래핑 없이 JSON 객체를 직접 반환합니다.

### HTTP 상태 코드
| 코드 | 설명 |
|------|------|
| 200 | 성공 |
| 400 | 잘못된 요청 (유효성 검사 실패, 잘못된 형식 등) |
| 401 | 인증 실패 (비밀번호 불일치, 토큰 만료/폐기 등) |
| 403 | 권한 없음 (타인의 리소스 접근) |
| 404 | 리소스 없음 |
| 409 | 충돌 (중복 데이터) |
| 500 | 서버 내부 오류 |

---

## 1. 인증 API

### 1-1. 회원가입

```
POST /api/auth/signup
```

**인증 불필요**

**Request Body**
```json
{
  "userId": "string",     // 필수, 최대 100자
  "password": "string",  // 필수, 최소 4자
  "name": "string"       // 필수, 최대 100자
}
```

**Response** `200 OK`
```json
{
  "success": true,
  "data": null,
  "error": null
}
```

**오류**
| 상태 코드 | 사유 |
|-----------|------|
| 400 | 필수 필드 누락 또는 유효성 검사 실패 |
| 409 | 이미 존재하는 userId |

---

### 1-2. 로그인

```
POST /api/auth/login
```

**인증 불필요**

**Request Body**
```json
{
  "userId": "string",   // 필수
  "password": "string" // 필수
}
```

**Response** `200 OK`
```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
  },
  "error": null
}
```

**오류**
| 상태 코드 | 사유 |
|-----------|------|
| 400 | 필수 필드 누락 |
| 401 | userId 없음 또는 비밀번호 불일치 |

---

### 1-3. 토큰 갱신

```
POST /api/auth/refresh
```

**인증 불필요**

**Request Body**
```json
{
  "refreshToken": "string"  // 필수
}
```

**Response** `200 OK`
```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
  },
  "error": null
}
```

> Refresh Token Rotation 방식: 갱신 시 기존 refreshToken은 폐기되고 새 refreshToken이 발급됩니다.

**오류**
| 상태 코드 | 사유 |
|-----------|------|
| 400 | 필수 필드 누락 |
| 401 | 유효하지 않은 토큰 타입 |
| 401 | DB에 존재하지 않는 refreshToken |
| 401 | 만료되었거나 폐기된 refreshToken |

---

### 1-4. 로그아웃

```
POST /api/auth/logout
```

**인증 불필요**

**Request Body**
```json
{
  "refreshToken": "string"  // 필수
}
```

**Response** `200 OK`
```json
{
  "success": true,
  "data": null,
  "error": null
}
```

**오류**
| 상태 코드 | 사유 |
|-----------|------|
| 400 | 필수 필드 누락 |

---

## 2. 사용자 API

> 모든 엔드포인트에 JWT 인증 필요

### 2-1. 내 정보 조회

```
GET /api/users/me
```

**Response** `200 OK`
```json
{
  "success": true,
  "data": {
    "userId": "string",
    "name": "string",
    "createdAt": "2026-01-01T00:00:00",
    "updatedAt": "2026-01-01T00:00:00"
  },
  "error": null
}
```

**오류**
| 상태 코드 | 사유 |
|-----------|------|
| 401 | 인증 토큰 없음 또는 만료 |
| 404 | 유저 없음 |

---

### 2-2. 이름 변경

```
PATCH /api/users/me
```

**Request Body**
```json
{
  "name": "string"  // 필수, 최대 100자
}
```

**Response** `200 OK`
```json
{
  "success": true,
  "data": null,
  "error": null
}
```

**오류**
| 상태 코드 | 사유 |
|-----------|------|
| 400 | 필수 필드 누락 또는 유효성 검사 실패 |
| 401 | 인증 토큰 없음 또는 만료 |
| 404 | 유저 없음 |

---

### 2-3. 비밀번호 변경

```
PATCH /api/users/me/password
```

> 비밀번호 변경 시 해당 유저의 **모든 refreshToken이 자동 폐기**됩니다.

**Request Body**
```json
{
  "currentPassword": "string",  // 필수
  "newPassword": "string"       // 필수, 최소 4자
}
```

**Response** `200 OK`
```json
{
  "success": true,
  "data": null,
  "error": null
}
```

**오류**
| 상태 코드 | 사유 |
|-----------|------|
| 400 | 필수 필드 누락 또는 유효성 검사 실패 |
| 401 | 인증 토큰 없음 또는 만료 |
| 401 | 현재 비밀번호 불일치 |
| 404 | 유저 없음 |

---

### 2-4. 회원 탈퇴

```
DELETE /api/users/me
```

**Request Body**
```json
{
  "password": "string"  // 필수
}
```

**Response** `200 OK`
```json
{
  "success": true,
  "data": null,
  "error": null
}
```

**오류**
| 상태 코드 | 사유 |
|-----------|------|
| 400 | 필수 필드 누락 |
| 401 | 인증 토큰 없음 또는 만료 |
| 401 | 비밀번호 불일치 |
| 404 | 유저 없음 |

---

## 3. 운전 세션 API

> 모든 엔드포인트에 JWT 인증 필요
> 성공 응답은 `ApiResponse` 래핑 없이 JSON 객체를 직접 반환

### 3-1. 운전 시작

```
POST /api/drives
```

**Request Body**
```json
{
  "deviceId": 1  // 필수 (Long)
}
```

**Response** `200 OK`
```json
{
  "driveId": 1,
  "startedAt": "2026-01-01T09:00:00"
}
```

**오류**
| 상태 코드 | 사유 |
|-----------|------|
| 400 | 필수 필드 누락 |
| 401 | 인증 토큰 없음 또는 만료 |
| 404 | 유저 없음 |
| 404 | 디바이스 없음 |

---

### 3-2. 운전 종료

```
PATCH /api/drives/{driveId}/end
```

**Path Variable**
| 이름 | 타입 | 설명 |
|------|------|------|
| driveId | Long | 운전 세션 ID |

**Request Body** 없음

**Response** `200 OK`
```json
{
  "driveId": 1,
  "startedAt": "2026-01-01T09:00:00",
  "endedAt": "2026-01-01T10:30:00",
  "durationSeconds": 5400
}
```

**오류**
| 상태 코드 | 사유 |
|-----------|------|
| 401 | 인증 토큰 없음 또는 만료 |
| 404 | 활성 상태의 운전 세션 없음 (이미 종료되었거나 본인 소유가 아닌 경우 포함) |

---

### 3-3. 운전 상세 조회

```
GET /api/drives/{driveId}
```

**Path Variable**
| 이름 | 타입 | 설명 |
|------|------|------|
| driveId | Long | 운전 세션 ID |

**Response** `200 OK`
```json
{
  "driveId": 1,
  "deviceId": 1,
  "startedAt": "2026-01-01T09:00:00",
  "endedAt": "2026-01-01T10:30:00",
  "durationSec": 5400,
  "emotionCount": 120,
  "reportCount": 3,
  "emotionDistribution": [
    {
      "emotionTypeId": 1,
      "emotionName": "Neutral",
      "count": 80,
      "ratio": 0.667
    },
    {
      "emotionTypeId": 2,
      "emotionName": "Angry",
      "count": 40,
      "ratio": 0.333
    }
  ],
  "latestReport": {
    "reportId": 5,
    "windowStart": "2026-01-01T09:00:00",
    "windowEnd": "2026-01-01T09:05:00",
    "generatedAt": "2026-01-01T09:05:01",
    "summary": { ... }
  }
}
```

> - `endedAt`, `durationSec`은 운전 중인 경우 `null`
> - `latestReport`는 리포트가 없는 경우 `null`

**오류**
| 상태 코드 | 사유 |
|-----------|------|
| 401 | 인증 토큰 없음 또는 만료 |
| 403 | 본인의 운전 세션이 아님 |
| 404 | 운전 세션 없음 |

---

## 4. 이벤트 배치 업로드 API

> JWT 인증 필요
> 성공 응답은 `ApiResponse` 래핑 없이 JSON 객체를 직접 반환

### 4-1. 감정/생체신호 배치 업로드

```
POST /api/drives/{driveId}/events:batch
```

**Path Variable**
| 이름 | 타입 | 설명 |
|------|------|------|
| driveId | Long | 운전 세션 ID |

**Request Body**
```json
{
  "emotions": [
    {
      "deviceId": 1,                        // 필수 (Long)
      "emotionTypeId": 1,                   // 필수 (Short)
      "source": "Vision",                   // 필수 ("Vision" | "Bio" | "Fusion")
      "modelVersion": "v1.0",              // 필수
      "occurredAt": "2026-01-01T09:00:00", // 필수 (ISO-8601, yyyy-MM-ddTHH:mm:ss)
      "confidence": "0.778",               // 필수 (소수점 문자열)
      "payload": { ... }                   // 선택 (JSON)
    }
  ],
  "biosignals": [
    {
      "deviceId": 1,                        // 필수 (Long)
      "signalType": "HRV",                 // 선택 ("HRV", "GSR" 등)
      "value": 72.5,                       // 필수 (Double)
      "unit": "bpm",                       // 필수
      "measuredAt": "2026-01-01T09:00:00", // 필수 (ISO-8601, yyyy-MM-ddTHH:mm:ss)
      "qualityFlag": "GOOD",               // 선택
      "attr": { ... }                      // 선택 (JSON)
    }
  ]
}
```

> - `emotions`, `biosignals` 모두 선택 필드이나 최소 하나는 포함 권장
> - `emotions` 최대 2,000건
> - `biosignals` 최대 5,000건

**Response** `200 OK`
```json
{
  "savedEmotions": 10,
  "savedBiosignals": 25
}
```

**오류**
| 상태 코드 | 사유 |
|-----------|------|
| 400 | 유효성 검사 실패 |
| 400 | 존재하지 않는 deviceId 또는 emotionTypeId |
| 400 | 잘못된 날짜 형식 (occurredAt, measuredAt) |
| 401 | 인증 토큰 없음 또는 만료 |
| 403 | 본인의 운전 세션이 아님 |
| 403 | 본인 소유가 아닌 디바이스 |
| 404 | 유저 없음 |
| 404 | 운전 세션 없음 |

---

## 오류 응답 예시

**400 Bad Request** (유효성 검사 실패)
```json
{
  "success": false,
  "data": null,
  "error": {
    "status": 400,
    "message": "password: 최소 4자 이상이어야 합니다"
  }
}
```

**401 Unauthorized**
```json
{
  "success": false,
  "data": null,
  "error": {
    "status": 401,
    "message": "Invalid userId or password"
  }
}
```

**403 Forbidden**
```json
{
  "success": false,
  "data": null,
  "error": {
    "status": 403,
    "message": "Drive does not belong to user"
  }
}
```

**404 Not Found**
```json
{
  "success": false,
  "data": null,
  "error": {
    "status": 404,
    "message": "Active drive not found"
  }
}
```

**409 Conflict**
```json
{
  "success": false,
  "data": null,
  "error": {
    "status": 409,
    "message": "UserId already exists"
  }
}
```

**500 Internal Server Error**
```json
{
  "success": false,
  "data": null,
  "error": {
    "status": 500,
    "message": "Internal server error"
  }
}
```
