# mutsa-summer-session-1

# 🛍️ Likelion 14th - 종합 푸드/커머스 장바구니 & 결제 API 서비스

멋사 방학 세션 백엔드 팀 프로젝트 과제 리포지토리입니다.

---

## 👥 팀 구성 및 담당 API

### 신현성 - `결제 / 주문`
- [ ] **`POST /api/credits/charge`** : 크레딧 충전 (동시성 안전 연산 적용)
- [ ] **`POST /api/orders`** : 장바구니 결제 및 주문 영수증 스냅샷(`OrderItem`) 생성

### 원지현 - `가게 / 메뉴`
- [ ] **`GET /api/stores`** : 전체 가게 목록 조회 (카테고리 필터링 적용)
- [ ] **`GET /api/stores/{storeId}`** : 특정 가게 상세 및 하위 메뉴/옵션 목록 조회

### 배승원 - `장바구니`
- [ ] **`POST /api/carts/items`** : 장바구니 상품 및 다중 옵션 담기 (`CartItemOption`)
- [ ] **`GET /api/carts`** : 내 장바구니 가게별 그룹화 조회
- [ ] **`PATCH /api/carts/items/{id}`** : 장바구니 상품 수량 변경 (`+`, `-`)
- [ ] **`DELETE /api/carts/items/{id}`** : 장바구니 상품 단건 삭제 (`X`)

- **📚 상세 API 명세서 (Notion):** [상세 API 명세서 보러가기](https://app.notion.com/p/1-3931d7459f5480078265c71007ffa7df#5bdfdb3a4e94422eb7d6ec038345c7a4)

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
