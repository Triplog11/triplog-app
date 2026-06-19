
# 트립로그 GitHub 협업 규칙

이 문서는 트립로그 프로젝트의 GitHub 협업 규칙을 정리한 문서입니다.

모든 팀원은 작업 전 이 문서를 확인하고, 아래 규칙을 기준으로 작업합니다.

## 1. 기본 작업 흐름

트립로그 프로젝트의 기본 작업 흐름은 다음과 같습니다.

```text
Issue 생성
→ develop 브랜치 기준으로 작업 브랜치 생성
→ 작업 진행
→ 커밋
→ develop 브랜치로 Pull Request 생성
→ 리뷰
→ merge
```

## 2. 브랜치 전략

### 기본 브랜치

| 브랜치     | 역할           |
| ------- | ------------ |
| main    | 배포 가능한 안정 버전 |
| develop | 개발 통합 브랜치    |

### 작업 브랜치

작업 브랜치는 `develop` 브랜치에서 생성합니다.

브랜치명은 아래 형식을 사용합니다.

```text
타입/이슈번호-작업명
```

예시:

```text
feature/10-triplog-home
feature/13-visit-auth
feature/20-badge-list
fix/25-gps-error
docs/1-github-rules
chore/2-project-setting
```

### 브랜치 타입

| 타입       | 설명             |
| -------- | -------------- |
| feature  | 기능 개발          |
| fix      | 버그 수정          |
| docs     | 문서 수정          |
| style    | UI 스타일 수정      |
| refactor | 리팩토링           |
| chore    | 설정, 패키지, 빌드 작업 |
| hotfix   | 긴급 수정          |

## 3. 브랜치 운영 규칙

* `main` 브랜치에 직접 push하지 않습니다.
* `develop` 브랜치에 직접 push하지 않습니다.
* 모든 작업은 별도 작업 브랜치에서 진행합니다.
* 작업 완료 후 `develop` 브랜치로 Pull Request를 생성합니다.
* `main` 브랜치는 배포 가능한 안정 버전으로만 유지합니다.
* 배포 또는 제출 시점에만 `develop`에서 `main`으로 merge합니다.

## 4. 커밋 메시지 규칙

커밋 메시지는 아래 형식을 사용합니다.

```text
type: 작업 내용 (#이슈번호)
```

예시:

```text
feat: 트립로그 홈 화면 구현 (#10)
feat: 방문 인증 플로우 구현 (#13)
fix: GPS 인증 실패 처리 오류 수정 (#25)
docs: GitHub 협업 규칙 추가 (#1)
chore: React Native 프로젝트 초기 세팅 (#2)
```

### 커밋 타입

| 타입       | 설명             |
| -------- | -------------- |
| feat     | 새로운 기능 추가      |
| fix      | 버그 수정          |
| docs     | 문서 수정          |
| style    | UI 스타일 수정      |
| refactor | 리팩토링           |
| test     | 테스트 코드         |
| chore    | 설정, 패키지, 빌드 작업 |
| design   | 디자인 반영         |
| rename   | 파일명 변경         |
| remove   | 파일 삭제          |

### 좋지 않은 커밋 예시

```text
수정
최종
진짜 최종
오류 고침
```

### 좋은 커밋 예시

```text
feat: 랜드마크 상세 화면 구현 (#18)
fix: 찜 해제 시 상태가 갱신되지 않는 오류 수정 (#22)
docs: 데이터 스키마 문서 추가 (#5)
```

## 5. Issue 규칙

모든 작업은 Issue를 먼저 생성한 뒤 진행합니다.

### Issue 제목 형식

```text
[파트] 작업 내용
```

예시:

```text
[PM] 트립로그 MVP 범위 확정
[FE] 홈 화면 구현
[BE] 방문 인증 API 구현
[DESIGN] 마이페이지 화면 보완
[DOCS] ERD 문서 추가
```

### Issue 작성 기준

Issue에는 아래 내용을 작성합니다.

```md
## 작업 내용
-

## 상세 내용
-

## 완료 조건
- [ ]
- [ ]

## 참고 자료
- 피그마:
- 기능명세서:
- API 문서:
```

## 6. Pull Request 규칙

Pull Request는 `develop` 브랜치로 생성합니다.

### PR 제목 형식

```text
[파트] 작업 내용
```

예시:

```text
[FE] 방문 인증 화면 구현
[BE] 찜 등록 API 구현
[DOCS] GitHub 협업 규칙 추가
```

### PR 작성 기준

PR에는 아래 내용을 작성합니다.

```md
## 작업 내용
-

## 변경 사항
-

## 확인 방법
1.
2.

## 체크리스트
- [ ] 로컬에서 정상 실행 확인
- [ ] 관련 이슈 연결
- [ ] 충돌 없음
- [ ] 불필요한 console.log 제거
- [ ] 기능명세서와 동작 일치 확인
- [ ] 피그마 화면과 큰 차이 없음

## 관련 이슈
<!--
아래 예시 중 현재 PR에 맞는 한 가지를 작성해주세요.

이 PR로 이슈가 완전히 해결되는 경우:
Closes #이슈번호

이슈의 일부만 작업한 경우:
Part of #이슈번호

단순 관련 이슈인 경우:
Related to #이슈번호
-->
```

## 7. 코드 리뷰 규칙

* 최소 1명 이상의 리뷰 또는 확인 후 merge합니다.
* 리뷰는 가능하면 24시간 이내에 진행합니다.
* 기능 동작, 명세 일치 여부, 충돌 여부, 예외 처리를 중심으로 확인합니다.
* 단순 취향 차이보다는 서비스 안정성과 기능 완성도를 우선합니다.
* 리뷰 코멘트는 공격적이지 않게 작성합니다.

### 리뷰 코멘트 예시

```text
이 부분은 인증 실패 케이스에서도 동작해야 할 것 같은데 확인 부탁드립니다.
```

```text
기능명세서 기준으로는 리뷰 작성 후 인증 결과 화면으로 이동해야 할 것 같습니다.
```

## 8. Merge 규칙

* PR은 `develop` 브랜치로 merge합니다.
* `main` 브랜치에는 직접 merge하지 않습니다.
* merge 방식은 `Squash and merge`를 기본으로 합니다.
* 배포 가능한 상태가 되었을 때 `develop`에서 `main`으로 PR을 생성합니다.

## 9. 환경변수 및 보안 규칙

* `.env` 파일은 GitHub에 올리지 않습니다.
* API Key, 인증키, Secret Key는 GitHub에 올리지 않습니다.
* `.env.example` 파일에는 키 이름만 작성합니다.
* 실제 키 값은 별도 안전한 채널에서 공유합니다.
* 실수로 키가 올라간 경우 즉시 공유하고 키를 재발급합니다.

### `.env.example` 예시

```env
API_BASE_URL=
TOUR_API_SERVICE_KEY=
KAKAO_NATIVE_APP_KEY=
```

## 10. GitHub Project 상태 규칙

작업 상태는 아래 기준으로 관리합니다.

| 상태          | 의미      |
| ----------- | ------- |
| Backlog     | 추후 작업   |
| Todo        | 해야 할 작업 |
| In Progress | 진행 중    |
| Review      | PR 리뷰 중 |
| Done        | 완료      |
| Hold        | 보류      |

작업 흐름은 다음과 같습니다.

```text
Issue 생성
→ Todo
→ In Progress
→ Review
→ Done
```

## 11. Label 규칙

Issue와 PR에는 필요한 Label을 붙입니다.

| Label    | 의미         |
| -------- | ---------- |
| FE       | 프론트엔드      |
| BE       | 백엔드        |
| DESIGN   | 디자인        |
| PM       | 기획, 문서, 일정 |
| DOCS     | 문서         |
| FEATURE  | 기능         |
| BUG      | 버그         |
| API      | API 관련     |
| DB       | 데이터베이스     |
| MVP      | MVP 필수     |
| 2차       | 후순위        |
| QUESTION | 논의 필요      |
| REFACTORING | 리팩토링      |
| URGENT   | 긴급         |
| HOLD     | 보류         |
| DUPLICATE     | 이미 있던 이슈(이슈 중복)        |
| 작성자     | (작성자 이름)        |
