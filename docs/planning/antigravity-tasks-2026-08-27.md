# Antigravity 위임 작업 (2026-08-27)

> 각 작업은 독립적으로 붙여넣어 실행 가능. 공통 컨텍스트를 먼저 읽게 한 뒤 작업 프롬프트를 준다.
> 저장소: `C:\Users\junsu\Desktop\trip_log` / 브랜치: `feat/full-api-integration` (PR #123) 에서 파생한 새 브랜치 사용.
> **backend/ 는 읽기 전용. frontend/ 만 수정.**

## 공통 컨텍스트 (모든 프롬프트 맨 앞에 붙이기)

```
프로젝트: TripLog(트립로그) — 대한민국 시군구/랜드마크 GPS 방문 인증 수집 앱 (React Native / Expo 54, JS).
디자인 SoT: frontend/assets/DESIGN.md (루트 DESIGN.md는 구버전). 토큰은 frontend/src/theme/theme.js 만 사용, 색상 하드코딩 금지.
팔레트: primary #368FFF, 민트 #0ECEDB, 로고 틸 #00ADBA, 텍스트 #1B1D1F / #7E848A / #ADB1B5, 보더 #E8EAEC, 서피스 #F5F6F7. 그림자 대신 1px 보더의 평면 UI.
카드 등급 색: Common #6B7280, Rare #2563EB, Epic #7C3AED, Legendary #D97706.
톤: 탐험/도감 화면은 친근한 "~해요", 인증/계정 화면은 담백한 "~합니다". 금지어: "오류가 발생했습니다", "데이터가 없습니다", "인증 실패", "불편을 드려 죄송합니다", 영어 "Oops/Sorry".
규칙: 불변 패턴(스프레드), 파일 400줄 이하, 함수 50줄 이하, frontend/ 밖 수정 금지, 완료 주장 전에 `cd frontend && npx expo export --platform android --output-dir ./dist-check` 가 exit 0 인지 반드시 실행하고 dist-check 삭제.
```

---

## A. 이미지 / 아이콘 에셋

저장 위치: `frontend/assets/images/<카테고리>/`. PNG(투명 필요 시) 또는 WebP. 생성 후 `frontend/src/assets/index.js`(없으면 생성)에 `require` 매핑 export.

### A-1. 랜드마크 카드 기본 이미지
```
frontend/assets/images/cards/default-landmark-card.png 를 만들어줘. 4:5 비율 1080x1350.
배경은 #F5F6F7 위에 민트(#0ECEDB)~프라이머리(#368FFF) 그라데이션의 추상적 한국 지도 실루엣, 중앙에 흰색 위치핀 아이콘. 텍스트 없음. 그림자 없음, 플랫.
백엔드 cardUrl 이 null 이거나 로드 실패 시 PhotoPlaceholder(frontend/src/screens/collection/components/PhotoPlaceholder.js)가 이 이미지를 쓰도록 연결해줘.
```

### A-2. 등급별 카드 프레임 4종
```
frontend/assets/images/cards/frame-{common,rare,epic,legendary}.png — 1080x1350 투명 PNG, 안쪽이 비어 있는 테두리 프레임(두께 24px, 모서리 반경 48px).
색: common #6B7280, rare #2563EB, epic #7C3AED, legendary #D97706(금색 하이라이트 허용).
LandmarkCardItem.js / CardDetailModal.js 에서 card.cardTier 에 맞는 프레임을 이미지 위에 오버레이(absolute fill)해줘. tierToGrade 는 frontend/src/data/collection.js 참고.
```

### A-3. 뱃지·칭호 아이콘
```
frontend/assets/images/badges/ 에 512x512 원형 아이콘 PNG 6종, frontend/assets/images/appellations/ 에 4종.
뱃지: first-visit(첫 방문), seoul-conqueror(서울 정복자), card-collector-10(카드 10장), reviewer(기록가), weekend-traveler(주말 여행자), region-explorer(지역 탐험가).
칭호: novice(초보 여행자), explorer(탐험가), collector(수집가), master(마스터).
스타일: 플랫 2톤(프라이머리 #368FFF + 민트 #0ECEDB), 흰 배경 원, 1px #E8EAEC 보더. 텍스트 없음.
BadgeListScreen.js 와 AppellationScreen.js 에서 badgeUrl 이 null 일 때 badgeName 키워드 매칭으로 로컬 아이콘 폴백을 쓰도록 헬퍼(frontend/src/utils/badgeAssets.js)를 만들어 연결해줘.
```

### A-4. 빈 상태 일러스트 3종
```
frontend/assets/images/empty/{collection,travel-log,event}.png — 360x240, 단색 라인 일러스트(#ADB1B5 라인 + #0ECEDB 포인트 1곳), 배경 투명.
collection: 빈 카드 슬롯 3장 / travel-log: 펼쳐진 노트와 펜 / event: 깃발이 꽂힌 달력.
각각 MyCardsTab(도감 카드 없음), ReviewList(여행 기록 없음), EventListScreen(이벤트 없음)의 빈 상태 상단에 배치. 문구는 기존 문구 유지.
```

### A-5. 이벤트 배너 플레이스홀더
```
frontend/assets/images/events/banner-placeholder-{1,2}.png — 1600x900. 1은 민트 그라데이션+지도 라인, 2는 프라이머리 그라데이션+카드 실루엣. 텍스트 없음.
EventListScreen / EventDetailScreen 에서 eventImageUrl 이 null 이거나 onError 일 때 eventId % 2 로 번갈아 사용.
```

### A-6. 앱 아이콘·스플래시 점검 + 잡파일 정리
```
frontend/app.json 의 icon / splash / adaptiveIcon 경로가 실제 파일과 일치하는지 확인하고, 스플래시는 배경 #00ADBA 에 흰 로고(frontend/assets/ 의 로고 파일 사용)로 통일해줘.
frontend/assets/ 안의 firebase.png, kotlin.png, groovy.png, KakaoTalk_*.jpg, DESIGN.md 를 제외한 무관 파일은 frontend/assets/_unused/ 로 옮기고, .gitignore 에 `frontend/assets/_unused/` 와 `frontend/assets/google-services.json` 을 추가해줘 (google-services.json 은 절대 커밋 금지).
```

### A-7. 스토어 스크린샷 프레임
```
docs/design/store/ 에 1080x1920 스크린샷 템플릿 5장(홈/도감/인증/랭킹/마이) 을 HTML+CSS 로 만들어줘. 상단 1/4 에 한 줄 카피(예: "발 닿은 곳이 도감이 돼요"), 아래에 폰 프레임 안에 스크린샷이 들어갈 슬롯. 팔레트 준수. 스크린샷 파일은 B-5 결과를 사용.
```

---

## B. 코드 잡일

### B-1. 금지어 / 톤 점검
```
frontend/src 전체에서 "오류가 발생했습니다", "데이터가 없습니다", "인증 실패", "죄송", "Oops", "Sorry", "에러" 를 grep 해서 DESIGN.md §10/§14 기준 문구로 교체해줘.
원칙: 원인 + 해결 한 문장. 예) 네트워크: "일시적으로 연결이 불안정해요. 잠시 후 다시 시도해 주세요." / 빈 목록: "아직 …가 없어요. …해 보세요!"
인증/계정 화면(record/, auth/)은 "~합니다" 체, 나머지는 "~해요" 체. 변경 목록을 표로 보고해줘.
```

### B-2. 회원가입 주소 드롭다운
```
frontend/src/screens/auth/EmailSignupScreen.js 의 시/도·시군구 텍스트 입력을 선택형으로 바꿔줘.
시/도 목록: frontend/src/utils/provinces.js 의 PROVINCE_CODES. 시군구 목록: src/api/regions.js fetchProvinceMap(provinceCode).regions[].regionName.
선택 UI는 바텀시트(Modal + FlatList) 로, 검색 입력 포함. 선택 결과 → addressSi(시/도명), addressDoGun(도가 없는 광역시는 시/도명 그대로), addressGu(시군구명).
NicknameScreen.js(소셜 가입 추가정보) 도 같은 컴포넌트를 쓰도록 공통화(frontend/src/components/common/RegionPicker.js).
```

### B-3. README 프론트 로컬 실행 문서
```
README.md 에 "프론트 로컬 실행" 섹션을 추가해줘: 필요 도구(Node 20, Android Studio 에뮬), `frontend/.env` 키 5개 설명, 에뮬에서 로컬 백엔드 붙일 때 `EXPO_PUBLIC_API_URL=http://10.0.2.2:8080`, 이메일 로그인/가입 화면이 로컬 QA 용이라는 점, `npx expo start --dev-client`, `npx expo export --platform android` 로 번들 검증하는 법. 기존 README 스타일 유지.
```

### B-4. Jest 세팅 + 유틸 단위 테스트
```
frontend 에 jest-expo 로 테스트 환경을 세팅하고(package.json scripts.test), 다음 유틸에 테스트를 작성해줘:
- src/utils/geo.js: distanceInMeters(서울시청↔경복궁 ≈ 1.6km 오차 5% 이내), formatDistance(null → '--m', 999 → '999m', 1600 → '1.6km')
- src/utils/provinces.js: buildProvinceStats(visited/total 집계), filterProvinceRegions
- src/utils/landmarkIds.js: tourismContentId 우선, 없으면 landmarkId
- src/data/collection.js: tierToGrade 대소문자 무관 매핑, 미지 등급 → null
- src/screens/mypage/utils/format.js: formatDate, getEventStatus(진행중/예정/종료)
`npm test` 가 통과하는 출력까지 첨부해서 보고.
```

### B-5. 전 화면 스크린샷 (한국어 로케일)
```
Android 에뮬레이터(AVD 'triplog')를 켜고 설정 → 시스템 → 언어에서 한국어를 1순위로 바꾼 뒤, 개발 클라이언트로 앱을 실행해 이메일 로그인 후 다음 화면을 1080x2400 PNG 로 docs/design/screens-2026-08-27/ 에 저장해줘:
home, home-mission, province-list, region-collection, collection-cards, card-detail, cert-region, cert-landmark, cert-location, cert-write, cert-success, ranking-total, ranking-monthly, mypage, badges, appellations, events, event-detail, travel-log, verify-history, activity-history, notifications, notification-settings, profile-edit, wishlist, login, email-login, email-signup.
각 화면에서 영어로 뜨는 문구·깨진 아이콘·겹침이 있으면 screenshots-issues.md 에 "화면 / 증상 / 재현 순서" 표로 정리.
```

---

## C. 검증 반복

### C-1. 실서버 API 스모크 스크립트
```
scripts/smoke-api.py 를 만들어줘(레포 루트, Python 표준 라이브러리만).
동작: BASE=https://triplog11.store 에 QA 계정(환경변수 TRIPLOG_QA_EMAIL / TRIPLOG_QA_PASSWORD)으로 POST /auth/oauth {provider:'LOCAL', email, password} 로그인 → accessToken 으로 아래 GET 전부 호출해 [경로, 상태코드, 응답 첫 120자] 표를 마크다운으로 stdout 출력:
/home, /users/mypage, /mypage/activityhistory, /stats/me, /stats/rankings/me, /stats/rankings?rankingType=TOTAL, /stats/rankings?rankingType=MONTHLY, /regions/nationwide/map, /regions?page=0&size=5, /regions/provinces/11/map, /missions/me, /missions, /notifications, /notifications/settings, /landmarks/me, /reviews, /appellations, /badges, /events, /bookmarks
401/5xx 가 하나라도 있으면 exit 1. 한글이 깨지지 않게 UTF-8 로 출력.
```

### C-2. PR #123 실기기 QA
```
https://github.com/Triplog11/triplog-app/pull/123 의 "테스트 계획"과 아래 시나리오를 실기기(또는 에뮬 한국어)에서 수행하고 결과를 docs/planning/qa-2026-08-27.md 에 체크리스트로 기록해줘:
1) 이메일 가입 → 자동 로그인 → 홈 진입 2) 홈 미션 스트립 진행도 표시 3) 도감 지역 탭 → 시군구 → 랜드마크 카드 상세 4) 인증: 지역 → 랜드마크 → 위치 → 기록 → 완료 보상 표시 5) 완료 후 도감/마이 수치 갱신 6) 랭킹 전체/월간 전환 + 당겨서 새로고침 7) 마이: 칭호 선택 / 대표 뱃지 설정 / 이벤트 목록·상세 / 활동 내역 / 여행 기록 상세 8) 알림 설정 토글 왕복 9) 로그아웃 → 재로그인.
실패 항목은 스크린샷 경로 + 재현 순서 + 예상/실제 를 적고, 코드 수정은 하지 말고 보고만.
```

---

## 실행 순서 제안
1. **B-5(스크린샷) + C-1(스모크)** 먼저 — 현재 상태 증거 확보 (회의 자료)
2. **A-1 ~ A-5** 병렬 — 에셋은 서로 독립
3. **B-1, B-3, B-4** — 코드 잡일
4. **B-2** — 유일하게 판단이 조금 필요 (UI 결정), 마지막
5. **C-2** — 모든 것 합친 뒤 최종 QA

각 작업은 별도 브랜치(`chore/antigravity-<작업번호>`)로 PR을 올리고, 머지 판단은 준수(Claude)가 한다.
