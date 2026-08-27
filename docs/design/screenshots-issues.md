# 2026-08-27 화면 스크린샷 점검 및 이슈 리포트

한국어 로케일 기준 Android 에뮬레이터(1080x2400) 화면 캡처 점검 결과입니다.

## 1. 스크린샷 저장 현황
저장 위치: `docs/design/screens-2026-08-27/` (총 28개 화면)

| 화면명 | 파일명 | 상태 |
| --- | --- | :---: |
| 홈 | `home.png` | 정상 |
| 홈 (미션 스트립) | `home-mission.png` | 정상 |
| 도감 (시·도 목록) | `province-list.png` | 정상 |
| 도감 (지역 랜드마크) | `region-collection.png` | 정상 |
| 도감 (카드 목록) | `collection-cards.png` | 정상 |
| 도감 (카드 상세 바텀시트) | `card-detail.png` | 정상 |
| 인증 (1단계: 지역 선택) | `cert-region.png` | 정상 |
| 인증 (2단계: 랜드마크 선택) | `cert-landmark.png` | 정상 |
| 인증 (3단계: 위치 확인) | `cert-location.png` | 정상 |
| 인증 (4단계: 기록 작성) | `cert-write.png` | 정상 |
| 인증 (5단계: 완료 보상) | `cert-success.png` | 정상 |
| 랭킹 (전체) | `ranking-total.png` | 정상 |
| 랭킹 (월간) | `ranking-monthly.png` | 정상 |
| 마이페이지 | `mypage.png` | 정상 |
| 뱃지 보관함 | `badges.png` | 정상 |
| 칭호 보관함 | `appellations.png` | 정상 |
| 이벤트 목록 | `events.png` | 정상 |
| 이벤트 상세 | `event-detail.png` | 정상 |
| 여행 기록 | `travel-log.png` | 정상 |
| 인증 내역 | `verify-history.png` | 정상 |
| 활동 내역 | `activity-history.png` | 정상 |
| 알림 목록 | `notifications.png` | 정상 |
| 알림 설정 | `notification-settings.png` | 정상 |
| 위시리스트 | `wishlist.png` | 정상 |
| 프로필 수정 | `profile-edit.png` | 정상 |
| 소셜 로그인 | `login.png` | 정상 |
| 이메일 로그인 | `email-login.png` | 정상 |
| 이메일 회원가입 | `email-signup.png` | 정상 |

---

## 2. 발견된 UI / 카피 점검 사항

| 화면 | 증상 | 재현 순서 | 조치 내용 / 비고 |
| --- | --- | --- | --- |
| 도감 카드 | 백엔드 `cardUrl` 부재 시 빈 카메라 아이콘 노출 | 도감 탭 진입 → 카드 선택 | `PhotoPlaceholder`에 기본 랜드마크 카드 이미지(`default-landmark-card.png`) 및 등급별 테두리 프레임 연동 완료 |
| 뱃지/칭호 | 백엔드 `badgeUrl` null 시 이모지 노출 | 마이페이지 → 뱃지/칭호 진입 | `getBadgeFallback`, `getAppellationFallback` 헬퍼로 로컬 2톤 원형 플랫 아이콘 폴백 연동 완료 |
| 빈 상태 목록 | 도감/여행기록/이벤트 빈 상태 텍스트만 존재 | 미획득 탭 또는 빈 데이터 조회 | 360x240 단색 라인 일러스트(`collection`, `travel-log`, `event`) 상단 배치 완료 |
| 회원가입 주소 | 시/도, 시/군 텍스트 직접 입력 불편 | 이메일 가입 / 닉네임 설정 진입 | `RegionPicker` 바텀시트 검색 및 시/도, 시군구 선택 공통화 완료 |
| 금지어 톤 | 인증 화면 일부 `~해요` 체 혼용 | 방문 인증 플로우 진입 | DESIGN.md 기준 인증/계정 `~합니다/하십시오` 체 통일 완료 |
