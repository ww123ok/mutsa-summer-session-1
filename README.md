# mutsa-summer-session-1

# 🛍️ Likelion 14th - 종합 푸드/커머스 장바구니 & 결제 API 서비스

> **멋사 방학 세션 백엔드 팀 프로젝트 과제 리포지토리입니다.**
> Spring Boot 기반의 도메인 주도 설계(DDD)를 바탕으로, JWT 인증/인가, CORS 보안 설정, 동시성을 고려한 크레딧 결제, 그리고 AWS EC2 및 Vercel 실서버 배포까지 완벽하게 연동한 이커머스 REST API 서비스입니다.

---

## 🌟 주요 기술 스택 및 핵심 아키텍처

- **Framework & DB**: Spring Boot 3.x (Java 21), Spring Data JPA, MySQL / H2
- **Security & Auth**: Spring Security 6.x, Stateless JWT (JSON Web Token) 인증/인가 체계
- **Infrastructure & Deploy**: AWS EC2 (Linux Ubuntu), Nginx, Vercel (Frontend Domain CORS 연동)
- **Architecture**: Domain-Driven Design (DDD), DTO Data Embedding 응답 최적화

---

## 👥 팀 구성 및 담당 API (Updated Specification)

---
- **📚 상세 API 명세서 (Notion):** [API 명세서 및 Test Payload 보러가기](https://app.notion.com/p/1-3931d7459f5480078265c71007ffa7df#5bdfdb3a4e94422eb7d6ec038345c7a4)
---

### 배승원 - `회원 / 인증 / 장바구니`
- [x] **`POST /api/auth/signup`** : 회원가입 (BCrypt 비밀번호 암호화 저장)
- [x] **`POST /api/auth/login`** : 로그인 (Bearer Access Token 발급)
- [x] **`GET /api/users/me`** : 내 정보 & 장바구니 담긴 품목 통합 조회 (`Data Embedding`)
- [x] **`POST /api/carts/items`** : 장바구니 상품 및 다중 옵션 담기 (`CartItemOption`)
- [x] **`GET /api/carts`** : 내 장바구니 가게별 그룹화 목록 조회
- [x] **`PATCH /api/carts/items/{id}`** : 장바구니 상품 수량 변경 (`+`, `-` 버튼)
- [x] **`DELETE /api/carts/items/{id}`** : 장바구니 상품 단건 삭제 (`X` 버튼)

### 원지현 - `가게 / 메뉴`
- [x] **`GET /api/stores`** : 전체 가게 목록 조회 (카테고리별 필터링 적용)
- [x] **`GET /api/stores/{storeId}`** : 특정 가게 상세 및 하위 메뉴/옵션 목록 조회 (`isMultiple` 규격 최적화)

### 신현성 - `결제 / 주문`
- [x] **`POST /api/credits/charge`** : 크레딧 충전 (JWT 토큰 기반 이메일 식별 및 잔액 누적)
- [x] **`POST /api/orders`** : 장바구니 최종 결제 및 주문 불변 영수증 스냅샷(`OrderItem`) 생성

---

## 🛡️ 보안, 인증 및 배포 설정 (Security & Deployment)

### 1. 🔐 Spring Security & Stateless JWT 인가 제어 - 원지현
- **토큰 기반 인증 (`JwtAuthenticationFilter`)**: 세션 상태를 유지하지 않는 Stateless 아키텍처를 채택하여, API 요청 헤더(`Authorization: Bearer <Token>`)를 검증하고 `SecurityContextHolder`에 인증 정보를 등록합니다.
- **API 접근 제어 (`authorizeHttpRequests`)**:
  - `permitAll()` : 회원가입(`/api/auth/signup`), 로그인(`/api/auth/login`), 가게 목록 및 상세 조회(`/api/stores/**`)는 비로그인 사용자도 접근 가능합니다.
  - `authenticated()` : 장바구니, 크레딧 충전, 주문 결제, 내 정보 조회 API는 유효한 JWT 토큰을 보유한 사용자만 접근할 수 있습니다.
- **예외 처리 핸들러**: 토큰 만료 및 위조 시 `401 Unauthorized` (`CustomAuthenticationEntryPoint`), 권한 부족 시 `403 Forbidden` (`CustomAccessDeniedHandler`)을 JSON 형태의 공통 응답(`ApiResponse`)으로 반환합니다.

### 2. 🌐 CORS (Cross-Origin Resource Sharing) 설정 - 신현성
프론트엔드 실서버 배포 및 로컬 개발 환경과의 무결점 통신을 위해 `CorsConfigurationSource`를 전역 설정했습니다.
- **허용 Origin**: 
  - 로컬 개발 환경 : `http://localhost:3000`, `http://localhost:5173`
  - Vercel 실서버 운영 도메인 : `https://www.liondelivery.store`, `https://liondelivery.store`
- **Exposed Header**: 프론트엔드 자바스크립트에서 응답 헤더의 토큰을 읽을 수 있도록 `Authorization` 헤더를 노출(`ExposedHeaders`) 설정했습니다.

### 3. 🚀 AWS EC2 실서버 배포 & 환경변수 관리 - 신현성
- **무중단 백그라운드 배포**: AWS EC2(Ubuntu) 클라우드 서버에서 `nohup java -jar ... > app.log 2>&1 &` 명령어를 통해 백그라운드 24시간 실서버(`https://mutsa-food.shop`)를 운영 중입니다.
- **민감 정보 보호 (Environment Variables)**: 데이터베이스 접속 비밀번호(`DB_PASSWORD`) 및 JWT 암호화 시크릿 키(`JWT_SECRET`)는 코드 내부에 하드코딩하지 않고, OS 환경변수 및 IDE 실행 매개변수를 통해 안전하게 주입받도록 설계되었습니다.

---

## 🗂️ 폴더 구조 (Domain-Driven Architecture)

    com.likelion.shopping
     ├── domain
     │    ├── member/    (회원 및 크레딧 관리)
     │    ├── store/     (가게 조회)
     │    ├── menu/      (메뉴 및 메뉴 옵션)
     │    ├── cart/      (장바구니 및 장바구니 항목/옵션)
     │    └── order/     (주문 및 불변 스냅샷 영수증)
     └── global
          ├── config/    (JPA, Auditing 등 전역 설정)
          └── exception/ (공통 예외 처리 및 에러 응답)

---

## 🌲 Git 협업 규칙 (Convention)

본 프로젝트는 `main` 브랜치 직접 작업을 엄격히 금지하며, **Issue ➔ Branch ➔ PR ➔ Code Review ➔ Merge** 흐름을 준수합니다.

### 1. Branch Convention
`type/#이슈번호-기능명` 또는 `type/기능명` 형식을 사용합니다.
- 예시: `feat/#1-store-entity`, `feat/cart-add-api`, `fix/login-error`

| Type | 의미 |
|---|---|
| `feat` | 새로운 기능 개발 |
| `fix` | 버그 수정 |
| `docs` | 문서 수정 (README 등) |
| `refactor` | 코드 리팩토링 |
| `chore` | 빌드 설정, 패키지 및 기타 작업 |

### 2. Commit Convention
`[Type] 작업 내용` 형식을 사용합니다.
- 예시: `[Feat] 장바구니 상품 추가 API 구현`, `[Fix] 크레딧 차감 동시성 예외 처리 수정`
