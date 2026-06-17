# ERD 설계 문서

이 문서는 트립로그 서비스에서 사용하는 데이터베이스 테이블 구조를 정리한 문서입니다.

각 테이블은 사용자, 성장 정책, 뱃지, 칭호, 알림, 리뷰, 지역, 랜드마크, 이벤트, 북마크, 미션 기능을 기준으로 설계되었습니다.

## 테이블 목록

| 구분      | 테이블                                                                           |
| ------- | ----------------------------------------------------------------------------- |
| 사용자     | `users`, `stats`                                                              |
| 정책      | `activity_policy`, `level_policy`, `rank_policy`, `role`, `level_policy_role` |
| 보상      | `badge`, `users_badge`, `users_badge_log`, `appellation`, `users_appellation` |
| 알림      | `notification`, `notification_policy`, `fcm_token`                            |
| 리뷰      | `review`, `review_log`, `image`                                               |
| 지역/랜드마크 | `region`, `landmark`                                                          |
| 이벤트/북마크 | `event`, `bookmark`                                                           |
| 미션      | `mission`, `users_mission`                                                    |

## 중간 테이블 목록

| 중간 테이블              | 연결 관계                     | 설명                     |
| ------------------- | ------------------------- | ---------------------- |
| `level_policy_role` | `level_policy` N:M `role` | 레벨 정책과 권한을 연결하는 중간 테이블 |
| `users_badge`       | `users` N:M `badge`       | 사용자와 뱃지를 연결하는 중간 테이블   |
| `users_appellation` | `users` N:M `appellation` | 사용자와 칭호를 연결하는 중간 테이블   |
| `users_mission`     | `users` N:M `mission`     | 사용자와 미션을 연결하는 중간 테이블   |

---

# 1. 사용자 도메인

## users

사용자 계정 정보를 저장하는 테이블입니다.

소셜 로그인 또는 자체 회원가입을 통해 가입한 사용자의 기본 정보를 관리하며 사용자 식별자, 로그인 타입, 닉네임, 이메일, 프로필 이미지, 비밀번호 정보를 저장합니다.

| 컬럼명           | 설명      | 타입            | 제약조건                               |
| ------------- | ------- | ------------- | ---------------------------------- |
| users_id (PK) | 유저 식별자  | VARCHAR(36)   | `NOT NULL`, `UNIQUE`, 서버 자체 UUID   |
| login_type    | 로그인 타입  | ENUM          | `NOT NULL`, 네이버, 구글, 우리 서비스        |
| nickname      | 닉네임     | VARCHAR(12)   | `NOT NULL`, `UNIQUE`, 2자 이상 12자 이하 |
| profile_url   | 프로필 이미지 | VARCHAR(2048) | `NOT NULL`                         |
| email         | 이메일     | VARCHAR(320)  | `NOT NULL`, `UNIQUE`               |
| password      | 비밀번호    | VARCHAR(255)  | `NULL`                             |

### 관계

* `users`는 `stats`와 1:1 관계를 가집니다.
* `users`는 `users_badge` 중간 테이블을 통해 `badge`와 N:M 관계를 가집니다.
* `users`는 `users_appellation` 중간 테이블을 통해 `appellation`과 N:M 관계를 가집니다.
* `users`는 `users_mission` 중간 테이블을 통해 `mission`과 N:M 관계를 가집니다.
* `users`는 `notification`, `fcm_token`, `review`, `bookmark`와 1:N 관계를 가집니다.

---

## stats

사용자의 활동 통계 정보를 저장하는 테이블입니다.

사용자의 누적 점수, 월간 점수, 현재 티어, 레벨, 경험치를 관리합니다.

| 컬럼명            | 설명     | 타입          | 제약조건                                   |
| -------------- | ------ | ----------- | -------------------------------------- |
| stats_id (PK)  | 상태 식별자 | BIGINT      | `NOT NULL`, `UNIQUE`, `AUTO_INCREMENT` |
| users_id (FK)  | 유저 식별자 | VARCHAR(36) | `NOT NULL`, `UNIQUE`                  |
| address_si     | 시      | VARCHAR(20) | `NOT NULL`                             |
| address_do_gun | 도 / 군  | VARCHAR(20) | `NOT NULL`                             |
| address_gu     | 구      | VARCHAR(30) | `NOT NULL`                             |
| overall_score  | 누적 스코어 | INT         | `NOT NULL`                             |
| month_score    | 월간 스코어 | INT         | `NOT NULL`                             |
| current_tier   | 현재 티어  | VARCHAR(10) | `NOT NULL`                             |
| stats_level    | 현재 레벨  | INT         | `NOT NULL`                             |
| stats_xp       | 현재 경험치 | INT         | `NOT NULL`                             |

### 관계

* `stats.users_id`는 `users.users_id`를 참조합니다.
* `stats.users_id`에 `UNIQUE` 제약조건을 부여하여 한 사용자당 하나의 통계 행만 생성되도록 합니다.
* 한 사용자는 하나의 통계 정보만 가질 수 있습니다.

---

# 2. 정책 도메인

## activity_policy

사용자 활동에 따라 지급되는 경험치와 점수 정책을 저장하는 테이블입니다.

방문 인증, 리뷰 작성, 미션 완료, 뱃지 획득 등 특정 활동이 발생했을 때 부여할 경험치와 점수를 관리합니다.

| 컬럼명                     | 설명                 | 타입            | 제약조건                 |
| ----------------------- | ------------------ | ------------- | -------------------- |
| activity_policy_id (PK) | 활동 정책 식별자          | VARCHAR(36)   | `NOT NULL`, `UNIQUE` |
| upper_policy_id         | 활동 정책 상위 policy_id | VARCHAR(36)   | `NULL`               |
| policy_xp               | 활동 정책 경험치          | INT           | `NOT NULL`           |
| policy_score            | 활동 정책 점수           | INT           | `NOT NULL`           |
| policy_description      | 활동 정책 설명           | VARCHAR(2048) | `NOT NULL`           |

### 관계

* `upper_policy_id`를 통해 상위 활동 정책과 하위 활동 정책을 계층적으로 관리할 수 있습니다.
* 특정 활동 정책은 점수 및 경험치 지급 기준으로 사용됩니다.

---

## level_policy

사용자 레벨업 조건을 저장하는 테이블입니다.

각 레벨에 도달하기 위해 필요한 경험치 또는 조건 값을 관리합니다.

| 컬럼명                    | 설명        | 타입     | 제약조건                                   |
| ---------------------- | --------- | ------ | -------------------------------------- |
| level_policy_id (PK)   | 레벨 정책 식별자 | BIGINT | `NOT NULL`, `UNIQUE`, `AUTO_INCREMENT` |
| level_policy_number    | 레벨        | INT    | `NOT NULL`                             |
| level_policy_condition | 레벨업 조건    | INT    | `NOT NULL`                             |

### 관계

* `level_policy`는 `level_policy_role` 중간 테이블을 통해 `role`과 N:M 관계를 가집니다.
* 레벨별 권한 지급 기준으로 사용됩니다.

---

## role

사용자에게 부여할 수 있는 권한 정보를 저장하는 테이블입니다.

레벨 정책에 따라 특정 권한을 지급하거나, 서비스 내 기능 접근 권한을 관리하기 위해 사용합니다.

| 컬럼명              | 설명     | 타입           | 제약조건                                   |
| ---------------- | ------ | ------------ | -------------------------------------- |
| role_id (PK)     | 권한 식별자 | BIGINT       | `NOT NULL`, `UNIQUE`, `AUTO_INCREMENT` |
| role_name        | 권한 이름  | VARCHAR(10)  | `NOT NULL`                             |
| role_description | 권한 설명  | VARCHAR(255) | `NOT NULL`                             |

### 관계

* `role`은 `level_policy_role` 중간 테이블을 통해 `level_policy`와 N:M 관계를 가집니다.
* 하나의 권한은 여러 레벨 정책에 연결될 수 있습니다.

---

## level_policy_role - 중간 테이블

레벨 정책과 권한의 N:M 관계를 연결하는 중간 테이블입니다.

특정 레벨에 도달했을 때 어떤 권한을 부여할지 정의합니다.

| 컬럼명                       | 설명           | 타입     | 제약조건                                   |
| ------------------------- | ------------ | ------ | -------------------------------------- |
| level_policy_role_id (PK) | 레벨 정책 권한 식별자 | BIGINT | `NOT NULL`, `UNIQUE`, `AUTO_INCREMENT` |
| role_id (FK)              | 권한 식별자       | BIGINT | `NOT NULL`                             |
| level_policy_id (FK)      | 레벨 식별자       | BIGINT | `NOT NULL`                             |

### 관계

* `level_policy_role.role_id`는 `role.role_id`를 참조합니다.
* `level_policy_role.level_policy_id`는 `level_policy.level_policy_id`를 참조합니다.
* `level_policy`와 `role`의 N:M 관계를 연결합니다.

---

## rank_policy

사용자 랭크 또는 티어 조건을 저장하는 테이블입니다.

사용자의 누적 점수 또는 월간 점수에 따라 랭크를 결정하는 기준으로 사용합니다.

| 컬럼명                   | 설명        | 타입          | 제약조건                                   |
| --------------------- | --------- | ----------- | -------------------------------------- |
| rank_policy_id (PK)   | 랭크 정책 식별자 | BIGINT      | `NOT NULL`, `UNIQUE`, `AUTO_INCREMENT` |
| rank_policy_tier      | 랭크 정책 티어  | VARCHAR(10) | `NOT NULL`                             |
| rank_policy_condition | 랭크 정책 조건  | INT         | `NOT NULL`                             |

### 관계

* `stats.current_tier`를 결정할 때 기준 정책으로 사용할 수 있습니다.
* 사용자의 점수가 `rank_policy_condition`을 만족하면 해당 티어로 갱신됩니다.

---

# 3. 뱃지 및 칭호 도메인

## badge

서비스에서 제공하는 뱃지 정보를 저장하는 테이블입니다.

뱃지 이름, 이미지, 획득 조건, 조건 타입, 상세 필터를 관리합니다.

| 컬럼명            | 설명       | 타입            | 제약조건                                   |
| -------------- | -------- | ------------- | -------------------------------------- |
| badge_id (PK)  | 뱃지 식별자   | BIGINT        | `NOT NULL`, `UNIQUE`, `AUTO_INCREMENT` |
| badge_url      | 뱃지 이미지   | VARCHAR(2048) | `NOT NULL`                             |
| badge_name     | 뱃지 이름    | VARCHAR(100)  | `NOT NULL`                             |
| badge_group    | 뱃지 그룹    | INT           | `NULL`                                 |
| badge_type     | 뱃지 타입    | VARCHAR(50)   | `NOT NULL`                             |
| badge_target   | 뱃지 타겟    | VARCHAR(50)   | `NOT NULL`                             |
| badge_operator | 뱃지 연산자   | VARCHAR(10)   | `NOT NULL`                             |
| badge_value    | 뱃지 값     | INT           | `NULL`                                 |
| badge_filter   | 뱃지 상세 조건 | JSON          | `NOT NULL`                             |

### 관계

* `badge`는 `users_badge` 중간 테이블을 통해 `users`와 N:M 관계를 가집니다.
* 하나의 뱃지는 여러 사용자에게 지급될 수 있습니다.

---

## users_badge - 중간 테이블

사용자와 뱃지의 N:M 관계를 연결하는 중간 테이블입니다.

사용자가 획득한 뱃지 목록을 관리합니다.

| 컬럼명                 | 설명        | 타입          | 제약조건                                   |
| ------------------- | --------- | ----------- | -------------------------------------- |
| users_badge_id (PK) | 유저 뱃지 식별자 | BIGINT      | `NOT NULL`, `UNIQUE`, `AUTO_INCREMENT` |
| users_id (FK)       | 유저 식별자    | VARCHAR(36) | `NOT NULL`                             |
| badge_id (FK)       | 뱃지 식별자    | BIGINT      | `NOT NULL`                             |

### 관계

* `users_badge.users_id`는 `users.users_id`를 참조합니다.
* `users_badge.badge_id`는 `badge.badge_id`를 참조합니다.
* `users`와 `badge`의 N:M 관계를 연결합니다.
* `users_badge`는 `users_badge_log`와 1:N 관계를 가집니다.

---

## users_badge_log

사용자의 뱃지 획득 로그를 저장하는 테이블입니다.

뱃지를 획득한 시점과 획득 내용을 기록합니다.

| 컬럼명                        | 설명            | 타입           | 제약조건                                   |
| -------------------------- | ------------- | ------------ | -------------------------------------- |
| users_badge_log_id (PK)    | 유저 뱃지 로그 식별자  | BIGINT       | `NOT NULL`, `UNIQUE`, `AUTO_INCREMENT` |
| users_badge_id (FK)        | 유저 뱃지 식별자     | BIGINT       | `NOT NULL`                             |
| users_badge_log_created_at | 유저 뱃지 로그 생성일자 | DATETIME     | `NOT NULL`                             |
| users_badge_content        | 유저 뱃지 로그 내용   | VARCHAR(500) | `NOT NULL`                             |

### 관계

* `users_badge_log.users_badge_id`는 `users_badge.users_badge_id`를 참조합니다.
* 하나의 유저 뱃지에 대해 여러 획득 로그를 기록할 수 있습니다.

---

## appellation

서비스에서 제공하는 칭호 정보를 저장하는 테이블입니다.

칭호 이름과 획득 조건을 관리하며, 조건 타입과 상세 필터를 통해 다양한 칭호 정책을 표현합니다.

| 컬럼명                  | 설명       | 타입           | 제약조건                                   |
| -------------------- | -------- | ------------ | -------------------------------------- |
| appellation_id (PK)  | 칭호 식별자   | BIGINT       | `NOT NULL`, `UNIQUE`, `AUTO_INCREMENT` |
| appellation_name     | 칭호 이름    | VARCHAR(100) | `NOT NULL`                             |
| appellation_group    | 칭호 그룹    | INT          | `NULL`                                 |
| appellation_type     | 칭호 타입    | VARCHAR(50)  | `NOT NULL`                             |
| appellation_target   | 칭호 타겟    | VARCHAR(50)  | `NOT NULL`                             |
| appellation_operator | 칭호 연산자   | VARCHAR(10)  | `NOT NULL`                             |
| appellation_value    | 칭호 값     | INT          | `NULL`                                 |
| appellation_filter   | 칭호 상세 조건 | JSON         | `NOT NULL`                             |

### 관계

* `appellation`은 `users_appellation` 중간 테이블을 통해 `users`와 N:M 관계를 가집니다.
* 하나의 칭호는 여러 사용자에게 지급될 수 있습니다.

---

## users_appellation - 중간 테이블

사용자와 칭호의 N:M 관계를 연결하는 중간 테이블입니다.

사용자가 획득한 칭호 목록을 관리합니다.

| 컬럼명                       | 설명        | 타입          | 제약조건                                   |
| ------------------------- | --------- | ----------- | -------------------------------------- |
| users_appellation_id (PK) | 유저 칭호 식별자 | BIGINT      | `NOT NULL`, `UNIQUE`, `AUTO_INCREMENT` |
| users_id (FK)             | 유저 식별자    | VARCHAR(36) | `NOT NULL`                             |
| appellation_id (FK)       | 칭호 식별자    | BIGINT      | `NOT NULL`                             |

### 관계

* `users_appellation.users_id`는 `users.users_id`를 참조합니다.
* `users_appellation.appellation_id`는 `appellation.appellation_id`를 참조합니다.
* `users`와 `appellation`의 N:M 관계를 연결합니다.

---

# 4. 알림 도메인

## notification_policy

알림 발송 정책을 저장하는 테이블입니다.

알림 유형, 트리거 이벤트, 제목 및 내용 템플릿, 기본 수신 여부, 활성화 여부를 관리합니다.

| 컬럼명                         | 설명           | 타입           | 제약조건                                   |
| --------------------------- | ------------ | ------------ | -------------------------------------- |
| notification_policy_id (PK) | 알림 정책 식별자    | BIGINT       | `NOT NULL`, `UNIQUE`, `AUTO_INCREMENT` |
| notification_type           | 알림 유형        | VARCHAR(50)  | `NOT NULL`, `UNIQUE`                   |
| notification_policy_name    | 알림 정책 이름     | VARCHAR(100) | `NOT NULL`                             |
| trigger_event               | 트리거 이벤트      | VARCHAR(50)  | `NOT NULL`                             |
| title_template              | 제목 템플릿       | VARCHAR(100) | `NOT NULL`                             |
| content_template            | 내용 템플릿       | VARCHAR(500) | `NOT NULL`                             |
| default_enable              | 기본 수신 여부     | BOOLEAN      | `NOT NULL`                             |
| is_active                   | 알림 정책 활성화 여부 | BOOLEAN      | `NOT NULL`                             |

### 알림 타입 예시

| notification_type          | notification_name | trigger_event              |
| -------------------------- | ----------------- | -------------------------- |
| `LEVEL_UP`                 | 레벨업 알림            | `USER_LEVEL_UP`            |
| `RANK_UP`                  | 랭크 상승 알림          | `USER_RANK_UP`             |
| `BADGE_ACQUIRED`           | 배지 획득 알림          | `BADGE_ACQUIRED`           |
| `CARD_ACQUIRED`            | 카드 획득 알림          | `LANDMARK_CARD_ACQUIRED`   |
| `REGION_COMPLETED`         | 지역 방문 완료 알림       | `REGION_VISIT_COMPLETED`   |
| `LANDMARK_VERIFIED`        | 랜드마크 방문 인증 알림     | `LANDMARK_VISIT_VERIFIED`  |
| `WEEKLY_MISSION_COMPLETED` | 주간 미션 완료 알림       | `WEEKLY_MISSION_COMPLETED` |

### 관계

* `notification_policy`는 `notification`과 1:N 관계를 가집니다.
* 하나의 알림 정책은 여러 알림 생성에 사용될 수 있습니다.

---

## notification

사용자에게 생성된 실제 알림 데이터를 저장하는 테이블입니다.

알림 제목, 내용, 읽음 여부, 관련 엔티티 ID, 추가 데이터를 저장합니다.

| 컬럼명                         | 설명        | 타입           | 제약조건                                   |
| --------------------------- | --------- | ------------ | -------------------------------------- |
| notification_id (PK)        | 알림 식별자    | BIGINT       | `NOT NULL`, `UNIQUE`, `AUTO_INCREMENT` |
| users_id (FK)               | 유저 식별자    | VARCHAR(36)  | `NOT NULL`                             |
| notification_policy_id (FK) | 알림 정책 식별자 | BIGINT       | `NOT NULL`                             |
| notification_type           | 알림 유형     | VARCHAR(50)  | `NOT NULL`                             |
| notification_title          | 알림 제목     | VARCHAR(100) | `NOT NULL`                             |
| notification_content        | 알림 내용     | VARCHAR(500) | `NOT NULL`                             |
| notification_identifier     | 관련 엔티티 ID | BIGINT       | `NOT NULL`                             |
| target_type                 | 타겟 유형     | VARCHAR(50)  | `NOT NULL`                             |
| notification_data           | 알림 추가 정보  | JSON         | `NOT NULL`                             |
| is_read                     | 읽음 여부     | BOOLEAN      | `NOT NULL`                             |
| notification_created_at     | 알림 생성 날짜  | DATETIME     | `NOT NULL`                             |
| read_at                     | 알림 읽은 날짜  | DATETIME     | `NULL`                                 |

### 관계

* `notification.users_id`는 `users.users_id`를 참조합니다.
* `notification.notification_policy_id`는 `notification_policy.notification_policy_id`를 참조합니다.

---

## fcm_token

사용자의 푸시 알림 발송을 위한 FCM 토큰을 저장하는 테이블입니다.

사용자별 디바이스 토큰, 디바이스 타입, 디바이스 이름을 관리합니다.

| 컬럼명                  | 설명        | 타입           | 제약조건                                   |
| -------------------- | --------- | ------------ | -------------------------------------- |
| fcm_token_id (PK)    | 푸시 토큰 식별자 | BIGINT       | `NOT NULL`, `UNIQUE`, `AUTO_INCREMENT` |
| users_id (FK)        | 유저 식별자    | VARCHAR(36)  | `NOT NULL`                             |
| token                | 디바이스 토큰   | VARCHAR(512) | `NOT NULL`                             |
| device_type          | 디바이스 유형   | VARCHAR(50)  | `NOT NULL`                             |
| device_name          | 디바이스 이름   | VARCHAR(100) | `NOT NULL`                             |
| fcm_token_created_at | 토큰 생성일자   | DATETIME     | `NOT NULL`                             |

### 관계

* `fcm_token.users_id`는 `users.users_id`를 참조합니다.
* 한 사용자는 여러 디바이스의 FCM 토큰을 가질 수 있습니다.

---

# 5. 리뷰 도메인

## review

사용자가 지역 또는 이벤트에 대해 작성한 리뷰 정보를 저장하는 테이블입니다.

리뷰 제목, 내용, 만족도, 리뷰 점수를 관리합니다.

| 컬럼명            | 설명      | 타입           | 제약조건                                   |
| -------------- | ------- | ------------ | -------------------------------------- |
| review_id (PK) | 리뷰 식별자  | BIGINT       | `NOT NULL`, `UNIQUE`, `AUTO_INCREMENT` |
| users_id (FK)  | 유저 식별자  | VARCHAR(36)  | `NOT NULL`                             |
| region_id (FK) | 지역 식별자  | BIGINT       | `NOT NULL`                             |
| event_id (FK)  | 이벤트 식별자 | BIGINT       | `NOT NULL`                             |
| review_title   | 리뷰 제목   | VARCHAR(100) | `NOT NULL`                             |
| review_content | 리뷰 내용   | VARCHAR(500) | `NOT NULL`                             |
| review_score   | 만족도     | FLOAT        | `NOT NULL`                             |
| review_point   | 리뷰 점수   | INT          | `NOT NULL`                             |

### 관계

* `review.users_id`는 `users.users_id`를 참조합니다.
* `review.region_id`는 `region.region_id`를 참조합니다.
* `review.event_id`는 `event.event_id`를 참조합니다.
* `review`는 `image`, `review_log`와 1:N 관계를 가집니다.

---

## review_log

리뷰 작성 또는 변경 이력을 저장하는 테이블입니다.

리뷰 생성 시점과 로그 내용을 기록합니다.

| 컬럼명                | 설명        | 타입           | 제약조건                                   |
| ------------------ | --------- | ------------ | -------------------------------------- |
| review_log_id (PK) | 리뷰 로그 식별자 | BIGINT       | `NOT NULL`, `UNIQUE`, `AUTO_INCREMENT` |
| review_id (FK)     | 리뷰 식별자    | BIGINT       | `NOT NULL`                             |
| review_created_at  | 리뷰 생성일자   | DATETIME     | `NOT NULL`                             |
| review_log_content | 리뷰 로그 내용  | VARCHAR(500) | `NOT NULL`                             |

### 관계

* `review_log.review_id`는 `review.review_id`를 참조합니다.
* 하나의 리뷰는 여러 로그를 가질 수 있습니다.

---

## image

리뷰에 첨부된 이미지 정보를 저장하는 테이블입니다.

원본 파일명, 저장 파일명, 이미지 URL, 파일 크기, 생성일자를 관리합니다.

| 컬럼명              | 설명       | 타입            | 제약조건                                   |
| ---------------- | -------- | ------------- | -------------------------------------- |
| image_id (PK)    | 이미지 식별자  | BIGINT        | `NOT NULL`, `UNIQUE`, `AUTO_INCREMENT` |
| review_id (FK)   | 리뷰 식별자   | BIGINT        | `NOT NULL`                             |
| original_name    | 실제 파일명   | VARCHAR(255)  | `NOT NULL`                             |
| saved_name       | 난수화된 파일명 | VARCHAR(255)  | `NOT NULL`                             |
| image_url        | 이미지 URL  | VARCHAR(2048) | `NOT NULL`                             |
| file_size        | 파일 용량    | INT           | `NOT NULL`                             |
| image_created_at | 이미지 생성일자 | DATETIME      | `NOT NULL`                             |

### 관계

* `image.review_id`는 `review.review_id`를 참조합니다.
* 하나의 리뷰는 여러 이미지를 가질 수 있습니다.

---

# 6. 지역 및 랜드마크 도메인

## region

서비스에서 관리하는 지역 정보를 저장하는 테이블입니다.

지역 방문 여부, 지역 설명, 법정동 코드를 관리합니다.

| 컬럼명               | 설명        | 타입           | 제약조건                                   |
| ----------------- | --------- | ------------ | -------------------------------------- |
| region_id (PK)    | 지역 식별자    | BIGINT       | `NOT NULL`, `UNIQUE`, `AUTO_INCREMENT` |
| users_id (FK)     | 유저 식별자    | VARCHAR(36)  | `NOT NULL`                             |
| region_is_visited | 지역 방문 여부  | BOOLEAN      | `NOT NULL`                             |
| region_overview   | 지역 설명     | TEXT         | `NOT NULL`                             |
| region_zipcode    | 지역 법정동 코드 | VARCHAR(255) | `NOT NULL`                             |

### 관계

* `region.users_id`는 `users.users_id`를 참조합니다.
* `region`은 `landmark`, `review`와 1:N 관계를 가집니다.
* 지역 내 랜드마크를 모두 방문하면 지역 방문 여부를 갱신할 수 있습니다.

---

## landmark

지역에 속한 랜드마크 정보를 저장하는 테이블입니다.

Tour API 식별자, 방문 여부, 법정동 코드를 관리합니다.

| 컬럼명                 | 설명           | 타입           | 제약조건                                   |
| ------------------- | ------------ | ------------ | -------------------------------------- |
| landmark_id (PK)    | 랜드마크 식별자     | BIGINT       | `NOT NULL`, `UNIQUE`, `AUTO_INCREMENT` |
| region_id (FK)      | 지역 식별자       | BIGINT       | `NOT NULL`                             |
| landmark_is_visited | 랜드마크 방문 여부   | BOOLEAN      | `NOT NULL`                             |
| content_id          | Tour API 식별자 | VARCHAR(255) | `NOT NULL`, `UNIQUE`                   |
| landmark_zipcode    | 랜드마크 법정동 코드  | VARCHAR(255) | `NOT NULL`                             |

### 관계

* `landmark.region_id`는 `region.region_id`를 참조합니다.
* 하나의 지역은 여러 랜드마크를 가질 수 있습니다.

---

# 7. 이벤트 및 북마크 도메인

## event

서비스에서 제공하는 이벤트 정보를 저장하는 테이블입니다.

이벤트 기간, 제목, 내용, 이미지 URL을 관리합니다.

| 컬럼명              | 설명            | 타입            | 제약조건                                   |
| ---------------- | ------------- | ------------- | -------------------------------------- |
| event_id (PK)    | 이벤트 식별자       | BIGINT        | `NOT NULL`, `UNIQUE`, `AUTO_INCREMENT` |
| event_start      | 이벤트 시작일자      | DATETIME      | `NULL`                                 |
| event_end        | 이벤트 종료일자      | DATETIME      | `NULL`                                 |
| event_title      | 이벤트 제목        | VARCHAR(100)  | `NOT NULL`                             |
| event_content    | 이벤트 내용        | VARCHAR(500)  | `NOT NULL`                             |
| event_image_url1 | 이벤트 이미지 URL 1 | VARCHAR(2048) | `NOT NULL`                             |
| event_image_url2 | 이벤트 이미지 URL 2 | VARCHAR(2048) | `NOT NULL`                             |

### 관계

* `event`는 `review`와 1:N 관계를 가질 수 있습니다.
* 이벤트는 북마크 대상이 될 수 있습니다.

---

## bookmark

사용자가 저장한 북마크 정보를 관리하는 테이블입니다.

이벤트, 지역, 랜드마크를 하나의 북마크 테이블에서 관리하기 위해 북마크 타입과 식별자를 함께 저장합니다.

| 컬럼명                 | 설명         | 타입          | 제약조건                                      |
| ------------------- | ---------- | ----------- | ----------------------------------------- |
| bookmark_id (PK)    | 북마크 식별자    | BIGINT      | `NOT NULL`, `UNIQUE`, `AUTO_INCREMENT`    |
| users_id (FK)       | 유저 식별자     | VARCHAR(36) | `NOT NULL`                                |
| bookmark_type       | 북마크 타입     | ENUM        | `NOT NULL`, `EVENT`, `REGION`, `LANDMARK` |
| bookmark_identifier | 북마크 타입 식별자 | BIGINT      | `NOT NULL`                                |

### 관계

* `bookmark.users_id`는 `users.users_id`를 참조합니다.
* `bookmark_type`과 `bookmark_identifier`를 통해 이벤트, 지역, 랜드마크 중 하나를 참조합니다.
* `bookmark`는 여러 대상 타입을 하나의 테이블에서 관리하는 구조이므로 일반적인 N:M 중간 테이블과는 구분됩니다.

---

# 8. 미션 도메인

## mission

서비스에서 제공하는 미션 정보를 저장하는 테이블입니다.

미션 이름, 조건, 상세 필터, 주간 미션 기간, 보상 점수와 경험치를 관리합니다.

| 컬럼명                | 설명          | 타입           | 제약조건                                   |
| ------------------ | ----------- | ------------ | -------------------------------------- |
| mission_id (PK)    | 미션 식별자      | BIGINT       | `NOT NULL`, `UNIQUE`, `AUTO_INCREMENT` |
| mission_name       | 미션 이름       | VARCHAR(100) | `NOT NULL`                             |
| mission_group      | 미션 그룹       | INT          | `NULL`                                 |
| mission_type       | 미션 타입       | VARCHAR(50)  | `NOT NULL`                             |
| mission_target     | 미션 타겟       | VARCHAR(50)  | `NOT NULL`                             |
| mission_operator   | 미션 연산자      | VARCHAR(10)  | `NOT NULL`                             |
| mission_value      | 미션 값        | INT          | `NULL`                                 |
| mission_filter     | 미션 상세 조건    | JSON         | `NOT NULL`                             |
| mission_week_start | 주간 미션 시작 날짜 | DATETIME     | `NOT NULL`                             |
| mission_week_end   | 주간 미션 종료 날짜 | DATETIME     | `NOT NULL`                             |
| mission_score      | 미션 점수       | INT          | `NOT NULL`                             |
| mission_xp         | 미션 경험치      | INT          | `NOT NULL`                             |

### 관계

* `mission`은 `users_mission` 중간 테이블을 통해 `users`와 N:M 관계를 가집니다.
* 하나의 미션은 여러 사용자에게 부여될 수 있습니다.

---

## users_mission - 중간 테이블

사용자와 미션의 N:M 관계를 연결하는 중간 테이블입니다.

사용자에게 부여되었거나 완료된 미션 정보를 관리합니다.

| 컬럼명                      | 설명          | 타입          | 제약조건                                   |
| ------------------------ | ----------- | ----------- | -------------------------------------- |
| users_mission_id (PK)    | 유저 미션 식별자   | BIGINT      | `NOT NULL`, `UNIQUE`, `AUTO_INCREMENT` |
| users_id (FK)            | 유저 식별자      | VARCHAR(36) | `NOT NULL`                             |
| mission_id (FK)          | 미션 식별자      | BIGINT      | `NOT NULL`                             |
| users_mission_created_at | 유저 미션 생성 날짜 | DATETIME    | `NOT NULL`                             |

### 관계

* `users_mission.users_id`는 `users.users_id`를 참조합니다.
* `users_mission.mission_id`는 `mission.mission_id`를 참조합니다.
* `users`와 `mission`의 N:M 관계를 연결합니다.

---

# 9. 주요 관계 요약

| 관계                                       | 설명                                               |
| ---------------------------------------- | ------------------------------------------------ |
| `users` 1:1 `stats`                      | 한 사용자는 하나의 통계 정보만 가질 수 있습니다.                    |
| `users` N:M `badge`                      | `users_badge` 중간 테이블을 통해 사용자와 뱃지를 연결합니다.         |
| `users_badge` 1:N `users_badge_log`      | 사용자의 뱃지 획득 로그를 기록합니다.                            |
| `users` N:M `appellation`                | `users_appellation` 중간 테이블을 통해 사용자와 칭호를 연결합니다.   |
| `users` 1:N `notification`               | 한 사용자는 여러 알림을 받을 수 있습니다.                         |
| `notification_policy` 1:N `notification` | 하나의 알림 정책은 여러 알림 생성에 사용됩니다.                      |
| `users` 1:N `fcm_token`                  | 한 사용자는 여러 디바이스 토큰을 가질 수 있습니다.                    |
| `users` 1:N `review`                     | 한 사용자는 여러 리뷰를 작성할 수 있습니다.                        |
| `review` 1:N `image`                     | 하나의 리뷰는 여러 이미지를 가질 수 있습니다.                       |
| `review` 1:N `review_log`                | 하나의 리뷰는 여러 로그를 가질 수 있습니다.                        |
| `region` 1:N `landmark`                  | 하나의 지역은 여러 랜드마크를 가질 수 있습니다.                      |
| `region` 1:N `review`                    | 하나의 지역은 여러 리뷰를 가질 수 있습니다.                        |
| `event` 1:N `review`                     | 하나의 이벤트는 여러 리뷰를 가질 수 있습니다.                       |
| `users` 1:N `bookmark`                   | 한 사용자는 여러 북마크를 가질 수 있습니다.                        |
| `users` N:M `mission`                    | `users_mission` 중간 테이블을 통해 사용자와 미션을 연결합니다.       |
| `level_policy` N:M `role`                | `level_policy_role` 중간 테이블을 통해 레벨 정책과 권한을 연결합니다. |

---

# 10. 설계 참고 사항

## UUID 사용

`users.users_id`는 서버에서 생성한 UUID 값을 사용합니다.

사용자 식별자는 외부에 노출될 가능성이 있으므로 자동 증가 숫자 ID 대신 UUID를 사용하여 예측 가능성을 낮춥니다.

## users와 stats의 1:1 관계

`users`와 `stats`는 1:1 관계입니다.

따라서 `stats.users_id`는 `users.users_id`를 참조하는 FK이면서, 동시에 `UNIQUE` 제약조건을 가져야 합니다.

이를 통해 하나의 사용자 계정에 여러 통계 데이터가 중복 생성되는 것을 방지합니다.

## JSON 컬럼 사용

`badge_filter`, `appellation_filter`, `mission_filter`, `notification_data`는 JSON 타입을 사용합니다.

조건이 단순 컬럼만으로 표현하기 어려운 경우가 있기 때문에 상세 조건을 유연하게 저장하기 위한 목적입니다.

## N:M 관계 처리

사용자와 뱃지, 사용자와 칭호, 사용자와 미션, 레벨 정책과 권한은 다대다 관계이므로 각각 중간 테이블을 사용합니다.

* `users_badge`: 사용자와 뱃지를 연결하는 중간 테이블
* `users_appellation`: 사용자와 칭호를 연결하는 중간 테이블
* `users_mission`: 사용자와 미션을 연결하는 중간 테이블
* `level_policy_role`: 레벨 정책과 권한을 연결하는 중간 테이블

## 북마크 구조

북마크는 이벤트, 지역, 랜드마크를 모두 대상으로 할 수 있으므로 `bookmark_type`과 `bookmark_identifier`를 함께 저장합니다.

이를 통해 하나의 북마크 테이블에서 여러 타입의 대상을 관리할 수 있습니다.

## 알림 구조

알림은 정책 테이블과 실제 알림 테이블을 분리합니다.

* `notification_policy`: 알림 생성 기준과 템플릿 관리
* `notification`: 사용자에게 실제로 생성된 알림 관리
* `fcm_token`: 사용자 디바이스 푸시 토큰 관리

이 구조를 통해 알림 정책 변경과 실제 알림 데이터를 분리해서 관리할 수 있습니다.
