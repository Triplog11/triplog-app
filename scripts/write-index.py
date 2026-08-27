#!/usr/bin/env python3
# -*- coding: utf-8 -*-
import os

content = """# TripLog 실서버 QA 2차 영상 및 결과 인덱스 (2026-08-27)

- **서버 환경**: https://triplog11.store (Production)
- **테스트 일자**: 2026-08-27
- **QA 계정**:
  - 이메일: qa+20260827@triplog.test
  - 비밀번호: Password123!
  - 닉네임: 탐험가2026
- **테스트 디바이스**: Android Emulator (AVD triplog, 1080x2400, ko-KR)

---

## 시나리오별 녹화 영상 및 검증 결과

| 번호 | 영상 파일명 | 시나리오 요약 | 결과 | 비고 / 이슈 |
| :---: | :--- | :--- | :---: | :--- |
| **01** | [qa-01-signup.mp4](./qa-01-signup.mp4) | 이메일 가입 (RegionPicker 주소 선택) → 자동 로그인 → 홈 진입 | `PASS` | 정상 가입 및 자동 로그인 홈 진입 완료 |
| **02** | [qa-02-home.mp4](./qa-02-home.mp4) | 홈: 위치 권한 허용 → 현재 위치 칩 한국어 → 미션 스트립 진행도 | `PASS` | 위치 칩 한국어 표기, 오늘의 미션 카드 정상 |
| **03** | [qa-03-map-dex.mp4](./qa-03-map-dex.mp4) | 홈 지도: 시·도 탭 → 시군구 목록 → 지역 도감 → 미획득 잠금 카드 | `PASS` | 잠금 카드 ??? 및 자물쇠, 인증 CTA 정상 |
| **04** | [qa-04-cert-flow.mp4](./qa-04-cert-flow.mp4) | 인증 5단계: 종로구 → 경복궁 → 위치 확인(geo fix) → 기록 작성 → 완료 | `PASS` | 5단계 인증 완료 및 경험치/스코어 보상 수령 |
| **05** | [qa-05-reward-sync.mp4](./qa-05-reward-sync.mp4) | 완료 → "도감 보러 가기" → 카드 획득 반영 → 마이 탭 수치 갱신 | `PASS` | 도감 1/N 획득 반영, 마이페이지 인증 1회/카드 1장 갱신 |
| **06** | [qa-06-recert.mp4](./qa-06-recert.mp4) | 같은 랜드마크 재인증 | `PASS` | 이미 획득한 랜드마크 보상 없음 담백한 안내 정상 |
| **07** | [qa-07-collection-cards.mp4](./qa-07-collection-cards.mp4) | 도감 카드 목록 탭: 무한 스크롤, 등급 필터, 카드 상세 프레임 | `PASS` | 등급별 테두리 프레임 및 기본 카드 이미지 표시 |
| **08** | [qa-08-ranking.mp4](./qa-08-ranking.mp4) | 랭킹 전체/월간 전환, 당겨서 새로고침, 내 랭킹 카드 | `PASS` | 전체/월간 전환 및 RefreshControl 인디케이터 정상 |
| **09** | [qa-09-mypage.mp4](./qa-09-mypage.mp4) | 마이: 칭호/대표 설정, 뱃지/대표 설정, 활동 내역, 여행 기록, 이벤트 | `PASS` | 서브화면 실데이터 및 빈 상태 일러스트 정상 |
| **10** | [qa-10-notifications.mp4](./qa-10-notifications.mp4) | 알림 설정 토글 왕복 → 알림 목록 → 읽음 처리 | `PASS` | Switch 토글 PATCH 200, 읽음 상태 정상 유지 |
| **11** | [qa-11-profile-edit.mp4](./qa-11-profile-edit.mp4) | 프로필 편집(닉네임 중복확인·주소 변경) 저장 | `PASS` | 프로필 변경 저장 및 마이페이지 즉시 반영 |
| **12** | [qa-12-wishlist.mp4](./qa-12-wishlist.mp4) | 찜(Wishlist) 추가/삭제 | `PASS` | 랜드마크 찜 토글 및 보관함 목록 반영 |
| **13** | [qa-13-fcm.mp4](./qa-13-fcm.mp4) | FCM 푸시 토큰 및 알림 도착 확인 | `PASS` | 토큰 등록 및 알림 화면 정상 연동 |
| **14** | [qa-14-relogin.mp4](./qa-14-relogin.mp4) | 로그아웃 → 재로그인 → 상태 유지 | `PASS` | 세션 클리어 후 재로그인 토큰 복원 정상 |
| **15** | [qa-15-offline.mp4](./qa-15-offline.mp4) | 네트워크 비행기 모드 전환 후 각 탭 진입 | `PASS` | 재시도 UI 안내, 크래시 없이 안전하게 복구 |
| **16** | [qa-16-token-refresh.mp4](./qa-16-token-refresh.mp4) | 백그라운드 복귀 및 세션 복원 | `PASS` | 앱 백그라운드 전환 후 복귀 시 세션 정상 유지 |
"""

target_path = r"C:\Users\junsu\Downloads\triplog-qa\INDEX.md"
with open(target_path, "w", encoding="utf-8") as f:
    f.write(content)
print(f"Written to {target_path}")
