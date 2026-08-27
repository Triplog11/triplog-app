# Antigravity 위임 작업 2차 — 실서버 QA + 영상 녹화 (2026-08-27)

> 목표: 프로덕션 서버(https://triplog11.store) 기준으로 앱을 "사용자 관점"으로 끝까지 써보고, 전 과정을 **영상으로 녹화해 `C:\Users\junsu\Downloads\triplog-qa\`** 에 넣는다. 코드 수정은 **버그 수정만**, 기능 추가/리팩터링 금지. 판단이 필요한 건 `docs/planning/qa-2-2026-08-27.md` 에 적고 멈춘다.
> 저장소 `C:\Users\junsu\Desktop\trip_log`, 브랜치 `feat/full-api-integration` 에서 `qa/antigravity-2` 브랜치를 새로 딴다. **backend/ 수정 금지.**

## 0. 공통 컨텍스트 (모든 프롬프트 앞에 붙이기)
```
프로젝트: TripLog(트립로그) — 시군구/랜드마크 GPS 방문 인증 수집 앱 (React Native / Expo 54, JS, dev client 빌드 사용).
디자인 SoT frontend/assets/DESIGN.md, 토큰 frontend/src/theme/theme.js. 인증/계정 화면은 "~합니다" 체, 나머지는 "~해요" 체.
규칙: frontend/ 만 수정, 불변 패턴, 파일 400줄 이하. 완료 주장 전 `cd frontend && npm test` 와 `npx expo export --platform android --output-dir ./dist-check`(후 삭제) 가 모두 성공해야 한다. 증거(출력) 없이 "됐다"고 쓰지 않는다.
```

## 1. 에뮬레이터·앱 실행 방법 (그대로 따라 하기)
```
사전: Android SDK = %LOCALAPPDATA%\Android\Sdk, AVD 이름 'triplog' (Google Play 이미지). 앱 패키지 store.triplog11.app (Expo dev client가 이미 설치돼 있음).
1) 에뮬 부팅:  "%LOCALAPPDATA%\Android\Sdk\emulator\emulator.exe" -avd triplog -no-snapshot-load -no-boot-anim   (별도 터미널, 백그라운드)
   부팅 확인:  adb shell getprop sys.boot_completed  → 1
2) 한국어 로케일: 설정 앱 → 시스템 → 언어 → 한국어 추가 후 맨 위로 (위치명이 영어로 뜨는 건 로케일 문제).
3) 프론트 env:  frontend/.env 의 EXPO_PUBLIC_API_URL=https://triplog11.store (실서버). 로컬 서버 쓸 땐 http://10.0.2.2:8080.
4) Metro:  cd frontend && npx expo start --dev-client --port 8081   (별도 터미널)
   adb reverse tcp:8081 tcp:8081
5) 앱 실행(딥링크):  adb shell am start -a android.intent.action.VIEW -d "triplog://expo-development-client/?url=http%3A%2F%2Flocalhost%3A8081"
   첫 화면에서 dev 메뉴가 뜨면 Continue.
6) 초기화가 필요하면:  adb shell pm clear store.triplog11.app
7) 스크린샷:  adb exec-out screencap -p > shot.png
8) 한글 입력: ADBKeyboard IME 가 설치돼 있음. adb shell ime set com.android.adbkeyboard/.AdbIME 후
   adb shell am broadcast -a ADB_INPUT_TEXT --es msg "한글텍스트"   (영문은 adb shell input text 'abc')
   끝나면 adb shell ime reset
9) GPS 위치 지정(인증 테스트): adb emu geo fix 126.977041 37.579617   (경도 위도 순, 경복궁)
10) 로그: Metro 터미널 + adb logcat -s ReactNativeJS:V
```

## 2. 영상 녹화 방법
```
녹화 시작(최대 3분/파일, 필요하면 여러 파일로):
  adb shell screenrecord --bit-rate 6000000 --time-limit 180 /sdcard/qa-<이름>.mp4   (별도 터미널에서 실행, 끝낼 땐 Ctrl+C)
가져오기:
  mkdir C:\Users\junsu\Downloads\triplog-qa 2>nul
  adb pull /sdcard/qa-<이름>.mp4 C:\Users\junsu\Downloads\triplog-qa\
파일명 규칙: qa-01-signup.mp4, qa-02-home.mp4 … 시나리오 번호와 동일. 각 영상 첫 2초에 무엇을 테스트하는지 알 수 있도록 해당 화면에서 시작.
최종적으로 C:\Users\junsu\Downloads\triplog-qa\INDEX.md 에 파일명 / 시나리오 / 결과(PASS·FAIL) / 이슈 링크 표를 만든다.
```

## 3. 실서버 QA 시나리오 (전부 녹화)
QA 계정: 이메일로 새로 가입 (`qa+<오늘날짜>@triplog.test`, 비번은 INDEX.md 에 기록). 구글/네이버 로그인은 에뮬에 계정이 없으면 "버튼 눌러 브라우저 열리는지"까지만.

| # | 시나리오 | PASS 기준 |
|---|---|---|
| 01 | 이메일 가입 (주소 RegionPicker 사용) → 자동 로그인 → 홈 | 가입 200, 홈 진입, 전국 %·방문 수 숫자 표시 |
| 02 | 홈: 위치 허용 → 현재 위치 칩 한국어 → 미션 스트립 진행도 | 영어 지명 없음, 0/N 진행바 |
| 03 | 홈 지도: 시도 탭 → 시군구 목록 → 지역 도감 → 카드 상세(미획득 잠금) | 실데이터, 잠금 카드 ??? |
| 04 | 인증 5단계: 종로구 → 경복궁 → 위치 확인(geo fix) → 별점+제목+내용 → 인증하기 | 완료 화면 + 보상 행 + 합계. **실패 시 서버 응답 본문을 이슈에 기록** |
| 05 | 완료 → "도감 보러 가기" → 카드 획득 반영 → 마이 탭 수치 갱신 | 도감 1/N, 마이 인증 1회·카드 1장·레벨/XP 변화 |
| 06 | 같은 랜드마크 재인증 | 보상 없음 안내가 담백하게 표시, 에러 아님 |
| 07 | 도감 카드 목록 탭: 무한 스크롤·등급 필터·카드 상세 프레임 | 서버 cardUrl 또는 기본 이미지, 등급 프레임 |
| 08 | 랭킹 전체/월간 전환, 당겨서 새로고침, 내 랭킹 카드 | 순위·다음 티어 문구 |
| 09 | 마이: 칭호 선택 → 대표 반영 / 뱃지 목록 → 대표 뱃지 설정 / 활동 내역 / 여행 기록 상세 / 이벤트 목록·상세 | 각 화면 실데이터, 빈 상태 일러스트 |
| 10 | 알림 설정 토글 왕복 → 알림 목록 → 읽음 처리 | PATCH 200, 읽음 상태 유지 |
| 11 | 프로필 편집(닉네임 중복확인·주소 변경) 저장 | PATCH 200, 마이 반영 |
| 12 | 찜(Wishlist) 추가/삭제 | 목록 반영 |
| 13 | FCM: 로그인 직후 logcat/Metro에 POST /fcm-tokens 200 확인 → 인증 1회 → 상단 알림 도착 캡처 | 푸시 수신 영상. 미수신이면 토큰 등록 로그·google-services.json 위치(frontend/ 루트) 확인해 이슈 기록 |
| 14 | 로그아웃 → 재로그인 → 상태 유지 | 토큰 재발급 정상, 401 루프 없음 |
| 15 | 네트워크 끊고(에뮬 설정 → 비행기 모드) 각 탭 진입 | 네트워크 배너/재시도, 앱 크래시 없음 |
| 16 | 백그라운드 30분 후 복귀(액세스 토큰 만료 시뮬레이션은 에뮬 시간 앞당기기: adb shell date 로 불가하면 생략) | 자동 재발급 |

## 4. 버그 처리 규칙
- **수정 허용**: 크래시, 잘못된 API 필드명, 빈 상태/에러 문구 누락, 겹침/잘림, 영어 하드코딩, 로딩 시 0 표시(`--` 여야 함).
- **수정 금지 → 이슈만**: 백엔드 계약 문제(예: `tourismContentId`·좌표 미노출, 404 vs 200), 새 화면/기능, 디자인 변경, 의존성 추가.
- 이슈 형식 (`docs/planning/qa-2-2026-08-27.md`): `#번호 | 시나리오 | 증상 | 재현 순서 | 예상/실제 | 스크린샷 경로 | 영상 파일+타임스탬프 | 수정 커밋(있으면)`.
- 수정은 시나리오별 작은 커밋, 마지막에 `qa/antigravity-2` 를 push 하고 PR 은 만들지 않는다(검수는 준수가 한다).

## 5. 완료 보고 형식
1. INDEX.md 표 (16개 시나리오 PASS/FAIL)
2. 수정한 파일 목록 + `npm test` / `expo export` 출력
3. 백엔드에 넘겨야 할 이슈 목록 (한 줄씩)
4. "사용자 배포 가능 여부"에 대한 본인 판단 한 줄 + 근거
