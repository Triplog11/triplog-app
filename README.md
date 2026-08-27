# 트립로그 Triplog
2026 관광데이터 활용 공모전 트립로그 프로젝트 
: 여행 기록 및 방문 인증 기반 게이미피케이션 앱 트립로그

## 협업 규칙

GitHub 협업 규칙은 `CONTRIBUTING.md`를 참고합니다.

기본 원칙은 다음과 같습니다.

* `main`, `develop` 브랜치 직접 push 금지
* 모든 작업은 Issue 생성 후 진행
* 작업 브랜치는 `feature/이슈번호-작업명` 형식 사용
* 작업 완료 후 `develop` 브랜치로 Pull Request 생성
* 최소 1명 이상 리뷰 후 merge
* API Key, 인증키, `.env` 파일은 절대 커밋 금지

## 문서

* 상세 메뉴트리: `docs/planning/menu-tree.md`
* 기능명세서: `docs/planning/feature-spec.md`
* WBS: `docs/planning/wbs.md`
* 데이터 스키마: `docs/data/data-schema.md`
* ERD: `docs/data/erd.md`
* GitHub 협업 규칙: `docs/rules/github-rules.md`

## 프론트 로컬 실행

### 1. 사전 요구 도구
* **Node.js**: v20 이상 (LTS 권장)
* **Android Studio**: Android SDK 및 AVD 에뮬레이터 (권장 AVD: `triplog`)

### 2. 환경변수 설정 (`frontend/.env`)
`frontend/.env.example`을 복사하여 `frontend/.env`를 생성하고 5개 키 값을 설정합니다.
* `EXPO_PUBLIC_API_URL`: 백엔드 API 베이스 URL (Android 에뮬레이터에서 로컬 백엔드 연결 시 `http://10.0.2.2:8080`, 배포 서버: `https://triplog11.store`)
* `EXPO_PUBLIC_GOOGLE_CLIENT_ID`: Google OAuth 웹 클라이언트 ID
* `EXPO_PUBLIC_GOOGLE_REDIRECT_URI`: Google 인가코드 redirect URI
* `EXPO_PUBLIC_NAVER_CLIENT_ID`: Naver OAuth 클라이언트 ID
* `EXPO_PUBLIC_NAVER_REDIRECT_URI`: Naver 콜백 redirect URI

> **참고**: 이메일 로그인/회원가입 화면은 소셜 OAuth 인증 외에 로컬 개발 및 QA 테스트를 위해 지원되는 기능입니다.

### 3. 개발 서버 실행 및 번들 검증
```bash
# 프론트엔드 디렉터리 이동 및 의존성 설치
cd frontend
npm install

# 개발 클라이언트 실행 (Android)
npx expo start --dev-client

# Android 프로덕션 번들 빌드 사전 검증
npx expo export --platform android --output-dir ./dist-check
```

## 주의사항

> API Key, 인증키, `.env` 파일은 절대 GitHub에 올리지 않습니다.
> 환경변수는 `.env.example`에 키 이름만 작성하고, 실제 값은 별도로 공유합니다.
