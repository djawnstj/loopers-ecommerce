# Loopers E-commerce 프로젝트 가이드

## 프로젝트 개요
Spring Boot + Kotlin 기반의 멀티모듈 이커머스 백엔드 프로젝트입니다.

## 프로젝트 구조

### 멀티모듈 구조
```
Root
├── apps (실행 가능한 Spring Boot Application)
│   └── commerce-api (메인 API 서버)
├── modules (재사용 가능한 설정 모듈)
│   └── jpa (JPA 설정 및 공통 기능)
└── supports (부가 기능 모듈)
    ├── jackson (JSON 직렬화 설정)
    ├── logging (로깅 설정)
    └── monitoring (모니터링 설정)
```

### 패키지 구조 (apps/commerce-api)
```
com.loopers
├── presentation/ (컨트롤러 레이어)
│   ├── auth/ (인증 관련)
│   ├── user/ (사용자 API)
│   └── point/ (포인트 API)
├── application/ (파사드 레이어)
│   ├── user/ (사용자 비즈니스 로직 조합)
│   └── point/ (포인트 비즈니스 로직 조합)
├── domain/ (도메인 레이어)
│   ├── user/ (사용자 도메인)
│   └── point/ (포인트 도메인)
├── infrastructure/ (인프라스트럭처 레이어)
│   ├── user/ (사용자 저장소 구현)
│   └── point/ (포인트 저장소 구현)
└── support/ (공통 기능)
    ├── error/ (예외 처리)
    └── presentation/ (API 응답 포맷)
```

## 도메인 구조

### User 도메인
- **Entity**: `User.kt` - 사용자 엔티티 (테이블명: member)
- **Value Objects**:
  - `LoginId.kt` - 로그인 ID (6-10자 영숫자)
  - `Email.kt` - 이메일 주소
  - `BirthDay.kt` - 생년월일
  - `GenderType.kt` - 성별 (ENUM)
- **Service**: `UserService.kt` - 사용자 도메인 로직
- **Repository**: `UserRepository.kt` - 사용자 저장소 인터페이스

### Point 도메인
- **Entity**: `UserPoint.kt` - 사용자 포인트 엔티티 (테이블명: user_point)
- **Value Objects**:
  - `Point.kt` - 포인트 값 (BigDecimal)
- **Service**: `UserPointService.kt` - 포인트 도메인 로직
- **Repository**: `UserPointRepository.kt` - 포인트 저장소 인터페이스

### 파사드 패턴
- `UserFacade.kt` - 사용자 관련 비즈니스 로직 조합
- `UserPointFacade.kt` - 포인트 관련 비즈니스 로직 조합

## 테스트 구조

### 테스트 계층별 분류
1. **Unit Tests** (`*Test.kt`)
   - 도메인 로직 단위 테스트
   - Mock 객체 사용 (MockK, Mockito)
   - 예시: `UserServiceImplTest.kt`, `UserTest.kt`

2. **Integration Tests** (`*IntegrationTest.kt`)
   - `IntegrationTestSupport` 상속
   - Spring Context 로드
   - 실제 DB 연동 (TestContainers)
   - 예시: `UserServiceIntegrationTest.kt`

3. **E2E Tests** (`*E2ETest.kt`)
   - `E2ETestSupport` 상속
   - REST API 전체 흐름 테스트
   - TestRestTemplate 사용
   - 예시: `UserV1ControllerE2ETest.kt`

### 테스트 지원 도구
- **TestContainers**: MySQL 컨테이너를 사용한 통합 테스트
- **DatabaseCleanUp**: 테스트 후 DB 초기화
- **Fixture**: 테스트 데이터 생성 (`UserFixture.kt`, `UserPointFixture.kt`)
- **Fake Repository**: 메모리 기반 테스트 저장소

### 테스트 명명 규칙
- 한글로 테스트 의도 명확히 표현
- Nested 클래스로 시나리오별 그룹화
- Given-When-Then 패턴 적용

## 주요 기술 스택

### 백엔드
- **Language**: Kotlin
- **Framework**: Spring Boot 3.x
- **JVM**: Java 21
- **Database**: MySQL (JPA/QueryDSL)
- **Build Tool**: Gradle (Kotlin DSL)

### 테스트
- **Framework**: JUnit 5
- **Mock**: MockK, Mockito-Kotlin
- **Assertion**: AssertJ
- **DB**: TestContainers (MySQL)
- **Coverage**: Jacoco

### 개발 도구
- **Code Style**: ktlint
- **Pre-commit**: Husky + ktlint
- **API Docs**: SpringDoc OpenAPI
- **Monitoring**: Prometheus + Grafana

## 빌드 및 실행

### 환경 설정
```bash
# 프로젝트 초기화 (pre-commit 설치)
make init

# 인프라 실행 (MySQL)
docker-compose -f ./docker/infra-compose.yml up

# 모니터링 실행 (Prometheus + Grafana)
docker-compose -f ./docker/monitoring-compose.yml up
```

### 빌드 명령어
```bash
# 전체 빌드
./gradlew build

# 테스트 실행
./gradlew test

# 코드 스타일 검사
./gradlew ktlintCheck

# 애플리케이션 실행
./gradlew :apps:commerce-api:bootRun
```

## API 엔드포인트

### 사용자 API
- `POST /api/v1/users` - 회원가입
- `GET /api/v1/users/me` - 내 정보 조회

### 포인트 API
- `GET /api/v1/users/me/point` - 포인트 조회
- `PATCH /api/v1/users/me/point` - 포인트 충전

## 에러 처리
- `CoreException` - 도메인 예외
- `ErrorType` - 에러 타입 정의
- `ApiControllerAdvice` - 전역 예외 처리

## 인증/인가
- `@LoginId` - 커스텀 어노테이션으로 사용자 ID 주입
- `UserIdArgumentResolver` - 인증된 사용자 ID 추출

## 참고사항
- Clean Architecture 구조 적용
- DDD(Domain Driven Design) 패턴 사용
- Value Object로 타입 안전성 확보
- 테스트 커버리지 유지 필수
- 한글 주석 및 테스트 메서드명 사용