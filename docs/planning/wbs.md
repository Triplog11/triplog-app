# 트립로그 MVP 개발 WBS (Work Breakdown Structure)

트립로그 MVP 피처(Priority 1)를 완성하기 위한 전체 개발 마일스톤 및 업무 분할 구조입니다.

---

## 📅 전체 마일스톤 개요

- **기간**: 총 4주 소요 예상
- **목표**: 랜드마크 방문 인증(GPS/사진), 레벨 및 랭킹 시스템, 수집 도감 및 지도 색칠하기 핵심 플로우 구현

---

## 🛠️ 세부 태스크 및 일정 분할

### Phase 1. 기획 및 요구사항 확정 (1주차)
- [x] 서비스 핵심 정책 수립 (`docs/rules/service-policy.md`)
- [x] GitHub 협업 규칙 정의 (`docs/rules/github_rules.md`)
- [x] 페이지 및 5대 탭 구조 기획 (홈 - 도감 - 인증 - 랭킹 - 마이)
- [ ] 기능명세서 상세화 (`docs/planning/feature_spec.md` 기술)

### Phase 2. 백엔드 인프라 및 DB 설계 (1~2주차)
- [x] 데이터베이스 스키마 및 DDL 문서 설계 (`docs/data/data_schema.md`, `erd.md`)
- [ ] Spring Boot 개발 환경 구축 및 로컬 DB 연동
- [ ] JPA Entity 매핑 설계 (사용자, 통계, 정책, 보상, 랜드마크 도메인)

### Phase 3. 핵심 API 기능 개발 (2~3주차)
- [ ] 로그인 및 약관 동의 API 구현
- [ ] 닉네임 유효성 검증 및 중복 체크 API 구현
- [ ] 랜드마크 데이터 조회 및 GPS/사진 기반 방문 인증 API 구현
- [ ] 지역 방문 완료 판정 및 보상(XP/Score) 지급 로직 구현
- [ ] 월간/전체 랭킹 및 동점자 처리 API 구현

### Phase 4. 프론트엔드 UI/UX 구축 및 디자인 시스템 적용 (2~3주차)
- [x] oh-my-design 디자인 시스템 도입 및 `DESIGN.md` 작성
- [x] 디자인 시스템 테마 토큰 파일 구현 (`theme.js` 생성)
- [x] 메인 내비게이션 5대 탭 구조 개편 (`TabNavigator.js` 리팩토링)
- [x] 전국 지도 및 대전 5개 구 확대 SVG 지도 컴포넌트 구현 (`KoreaMap.js`)
- [x] 도감(Collection) 탭 내 세그먼트 스위칭(지도/리스트) 화면 구현 (`CollectionScreen.js`)
- [ ] 랜드마크 방문 인증 및 카메라/GPS 기능 화면 설계 (`VisitCertScreen.js`)

### Phase 5. 백엔드 API 연동 및 통합 검증 (3~4주차)
- [ ] AuthContext 연동을 통한 가입/로그인 온보딩 플로우 결합
- [ ] 홈, 도감, 랭킹 스크린에 실제 API 데이터 연동
- [ ] 인증하기 화면의 위치 센서(GPS) 연동 및 백엔드 인증 테스트
- [ ] 전체 MVP 시나리오 테스트 및 QA 완료
