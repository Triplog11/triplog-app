
# DDL 문서

이 문서는 트립로그 서비스에서 사용하는 데이터베이스 테이블을 만드는 DDL 문서입니다.

ERD를 기준으로 작성되었습니다.

```sql
CREATE DATABASE IF NOT EXISTS `triplog`
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE `triplog`;

-- =========================================================
-- 1. 사용자 도메인
-- =========================================================

CREATE TABLE IF NOT EXISTS `users` (
  `users_id` VARCHAR(36) NOT NULL COMMENT '유저 식별자',
  `login_type` ENUM('NAVER', 'GOOGLE', 'LOCAL') NOT NULL COMMENT '로그인 타입',
  `nickname` VARCHAR(12) NOT NULL COMMENT '닉네임',
  `profile_url` VARCHAR(2048) NOT NULL COMMENT '프로필 이미지',
  `email` VARCHAR(320) NOT NULL COMMENT '이메일',
  `password` VARCHAR(255) NULL COMMENT '비밀번호',
  CONSTRAINT `pk_users` PRIMARY KEY (`users_id`),
  CONSTRAINT `uk_users_email` UNIQUE (`email`),
  CONSTRAINT `uk_users_nickname` UNIQUE (`nickname`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='사용자 계정 정보';

CREATE TABLE IF NOT EXISTS `stats` (
  `stats_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '상태 식별자',
  `users_id` VARCHAR(36) NOT NULL COMMENT '유저 식별자',
  `address_si` VARCHAR(20) NOT NULL COMMENT '시',
  `address_do_gun` VARCHAR(20) NOT NULL COMMENT '도 / 군',
  `address_gu` VARCHAR(30) NOT NULL COMMENT '구',
  `overall_score` INT NOT NULL COMMENT '누적 스코어',
  `month_score` INT NOT NULL COMMENT '월간 스코어',
  `current_tier` VARCHAR(10) NOT NULL COMMENT '현재 티어',
  `stats_level` INT NOT NULL COMMENT '현재 레벨',
  `stats_xp` INT NOT NULL COMMENT '현재 경험치',
  CONSTRAINT `pk_stats` PRIMARY KEY (`stats_id`),
  CONSTRAINT `uk_stats_users_id` UNIQUE (`users_id`),
  CONSTRAINT `fk_stats_users` FOREIGN KEY (`users_id`) REFERENCES `users` (`users_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='사용자 활동 통계 정보';

-- =========================================================
-- 2. 정책 도메인
-- =========================================================

CREATE TABLE IF NOT EXISTS `activity_policy` (
  `activity_policy_id` VARCHAR(36) NOT NULL COMMENT '활동 정책 식별자',
  `upper_policy_id` VARCHAR(36) NULL COMMENT '활동 정책 상위 policy_id',
  `policy_xp` INT NOT NULL COMMENT '활동 정책 경험치',
  `policy_score` INT NOT NULL COMMENT '활동 정책 점수',
  `policy_description` VARCHAR(2048) NOT NULL COMMENT '활동 정책 설명',
  CONSTRAINT `pk_activity_policy` PRIMARY KEY (`activity_policy_id`),
  INDEX `idx_activity_policy_upper` (`upper_policy_id`),
  CONSTRAINT `fk_activity_policy_upper` FOREIGN KEY (`upper_policy_id`) REFERENCES `activity_policy` (`activity_policy_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='활동 보상 정책';

CREATE TABLE IF NOT EXISTS `level_policy` (
  `level_policy_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '레벨 정책 식별자',
  `level_policy_number` INT NOT NULL COMMENT '레벨',
  `level_policy_condition` INT NOT NULL COMMENT '레벨업 조건',
  CONSTRAINT `pk_level_policy` PRIMARY KEY (`level_policy_id`),
  CONSTRAINT `uk_level_policy_number` UNIQUE (`level_policy_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='레벨업 조건 정책';

CREATE TABLE IF NOT EXISTS `role` (
  `role_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '권한 식별자',
  `role_name` VARCHAR(10) NOT NULL COMMENT '권한 이름',
  `role_description` VARCHAR(255) NOT NULL COMMENT '권한 설명',
  CONSTRAINT `pk_role` PRIMARY KEY (`role_id`),
  CONSTRAINT `uk_role_name` UNIQUE (`role_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='사용자 권한 정보';

CREATE TABLE IF NOT EXISTS `level_policy_role` (
  `level_policy_role_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '레벨 정책 권한 식별자',
  `role_id` BIGINT NOT NULL COMMENT '권한 식별자',
  `level_policy_id` BIGINT NOT NULL COMMENT '레벨 식별자',
  CONSTRAINT `pk_level_policy_role` PRIMARY KEY (`level_policy_role_id`),
  CONSTRAINT `uk_level_policy_role` UNIQUE (`role_id`, `level_policy_id`),
  INDEX `idx_lpr_level_policy` (`level_policy_id`),
  CONSTRAINT `fk_lpr_role` FOREIGN KEY (`role_id`) REFERENCES `role` (`role_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_lpr_level_policy` FOREIGN KEY (`level_policy_id`) REFERENCES `level_policy` (`level_policy_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='레벨 정책과 권한 연결 중간 테이블';

CREATE TABLE IF NOT EXISTS `rank_policy` (
  `rank_policy_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '랭크 정책 식별자',
  `rank_policy_tier` VARCHAR(10) NOT NULL COMMENT '랭크 정책 티어',
  `rank_policy_condition` INT NOT NULL COMMENT '랭크 정책 조건',
  CONSTRAINT `pk_rank_policy` PRIMARY KEY (`rank_policy_id`),
  CONSTRAINT `uk_rank_policy_tier` UNIQUE (`rank_policy_tier`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='랭크 및 티어 정책';

-- =========================================================
-- 3. 보상 도메인
-- =========================================================

CREATE TABLE IF NOT EXISTS `badge` (
  `badge_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '뱃지 식별자',
  `badge_url` VARCHAR(2048) NOT NULL COMMENT '뱃지 이미지',
  `badge_name` VARCHAR(100) NOT NULL COMMENT '뱃지 이름',
  `badge_group` INT NULL COMMENT '뱃지 그룹',
  `badge_type` VARCHAR(50) NOT NULL COMMENT '뱃지 타입',
  `badge_target` VARCHAR(50) NOT NULL COMMENT '뱃지 타겟',
  `badge_operator` VARCHAR(10) NOT NULL COMMENT '뱃지 연산자',
  `badge_value` INT NULL COMMENT '뱃지 값',
  `badge_filter` JSON NOT NULL COMMENT '뱃지 상세 조건',
  CONSTRAINT `pk_badge` PRIMARY KEY (`badge_id`),
  CONSTRAINT `uk_badge_name` UNIQUE (`badge_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='뱃지 정보';

CREATE TABLE IF NOT EXISTS `users_badge` (
  `users_badge_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '유저 뱃지 식별자',
  `users_id` VARCHAR(36) NOT NULL COMMENT '유저 식별자',
  `badge_id` BIGINT NOT NULL COMMENT '뱃지 식별자',
  CONSTRAINT `pk_users_badge` PRIMARY KEY (`users_badge_id`),
  CONSTRAINT `uk_users_badge` UNIQUE (`users_id`, `badge_id`),
  INDEX `idx_users_badge_badge` (`badge_id`),
  CONSTRAINT `fk_users_badge_users` FOREIGN KEY (`users_id`) REFERENCES `users` (`users_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_users_badge_badge` FOREIGN KEY (`badge_id`) REFERENCES `badge` (`badge_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='사용자와 뱃지 연결 중간 테이블';

CREATE TABLE IF NOT EXISTS `users_badge_log` (
  `users_badge_log_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '유저 뱃지 로그 식별자',
  `users_badge_id` BIGINT NOT NULL COMMENT '유저 뱃지 식별자',
  `users_badge_log_created_at` DATETIME NOT NULL COMMENT '유저 뱃지 로그 생성일자',
  `users_badge_content` VARCHAR(500) NOT NULL COMMENT '유저 뱃지 로그 내용',
  CONSTRAINT `pk_users_badge_log` PRIMARY KEY (`users_badge_log_id`),
  INDEX `idx_ubl_users_badge` (`users_badge_id`),
  CONSTRAINT `fk_ubl_users_badge` FOREIGN KEY (`users_badge_id`) REFERENCES `users_badge` (`users_badge_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='사용자 뱃지 획득 로그';

CREATE TABLE IF NOT EXISTS `appellation` (
  `appellation_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '칭호 식별자',
  `appellation_name` VARCHAR(100) NOT NULL COMMENT '칭호 이름',
  `appellation_group` INT NULL COMMENT '칭호 그룹',
  `appellation_type` VARCHAR(50) NOT NULL COMMENT '칭호 타입',
  `appellation_target` VARCHAR(50) NOT NULL COMMENT '칭호 타겟',
  `appellation_operator` VARCHAR(10) NOT NULL COMMENT '칭호 연산자',
  `appellation_value` INT NULL COMMENT '칭호 값',
  `appellation_filter` JSON NOT NULL COMMENT '칭호 상세 조건',
  CONSTRAINT `pk_appellation` PRIMARY KEY (`appellation_id`),
  CONSTRAINT `uk_appellation_name` UNIQUE (`appellation_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='칭호 정보';

CREATE TABLE IF NOT EXISTS `users_appellation` (
  `users_appellation_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '유저 칭호 식별자',
  `users_id` VARCHAR(36) NOT NULL COMMENT '유저 식별자',
  `appellation_id` BIGINT NOT NULL COMMENT '칭호 식별자',
  CONSTRAINT `pk_users_appellation` PRIMARY KEY (`users_appellation_id`),
  CONSTRAINT `uk_users_appellation` UNIQUE (`users_id`, `appellation_id`),
  INDEX `idx_ua_appellation` (`appellation_id`),
  CONSTRAINT `fk_ua_users` FOREIGN KEY (`users_id`) REFERENCES `users` (`users_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_ua_appellation` FOREIGN KEY (`appellation_id`) REFERENCES `appellation` (`appellation_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='사용자와 칭호 연결 중간 테이블';

-- =========================================================
-- 4. 알림 도메인
-- =========================================================

CREATE TABLE IF NOT EXISTS `notification_policy` (
  `notification_policy_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '알림 정책 식별자',
  `notification_type` VARCHAR(50) NOT NULL COMMENT '알림 유형',
  `notification_policy_name` VARCHAR(100) NOT NULL COMMENT '알림 정책 이름',
  `trigger_event` VARCHAR(50) NOT NULL COMMENT '트리거 이벤트',
  `title_template` VARCHAR(100) NOT NULL COMMENT '제목 템플릿',
  `content_template` VARCHAR(500) NOT NULL COMMENT '내용 템플릿',
  `default_enable` BOOLEAN NOT NULL COMMENT '기본 수신 여부',
  `is_active` BOOLEAN NOT NULL COMMENT '알림 정책 활성화 여부',
  CONSTRAINT `pk_notification_policy` PRIMARY KEY (`notification_policy_id`),
  CONSTRAINT `uk_notification_policy_type` UNIQUE (`notification_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='알림 발송 정책';

CREATE TABLE IF NOT EXISTS `notification` (
  `notification_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '알림 식별자',
  `users_id` VARCHAR(36) NOT NULL COMMENT '유저 식별자',
  `notification_policy_id` BIGINT NOT NULL COMMENT '알림 정책 식별자',
  `notification_type` VARCHAR(50) NOT NULL COMMENT '알림 유형',
  `notification_title` VARCHAR(100) NOT NULL COMMENT '알림 제목',
  `notification_content` VARCHAR(500) NOT NULL COMMENT '알림 내용',
  `notification_identifier` BIGINT NOT NULL COMMENT '관련 엔티티 ID',
  `target_type` VARCHAR(50) NOT NULL COMMENT '타겟 유형',
  `notification_data` JSON NOT NULL COMMENT '알림 추가 정보',
  `is_read` BOOLEAN NOT NULL COMMENT '읽음 여부',
  `notification_created_at` DATETIME NOT NULL COMMENT '알림 생성 날짜',
  `read_at` DATETIME NULL COMMENT '알림 읽은 날짜',
  CONSTRAINT `pk_notification` PRIMARY KEY (`notification_id`),
  INDEX `idx_notification_users` (`users_id`),
  INDEX `idx_notification_policy` (`notification_policy_id`),
  CONSTRAINT `fk_notification_users` FOREIGN KEY (`users_id`) REFERENCES `users` (`users_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_notification_policy` FOREIGN KEY (`notification_policy_id`) REFERENCES `notification_policy` (`notification_policy_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='사용자 알림 데이터';

CREATE TABLE IF NOT EXISTS `fcm_token` (
  `fcm_token_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '푸시 토큰 식별자',
  `users_id` VARCHAR(36) NOT NULL COMMENT '유저 식별자',
  `token` VARCHAR(512) NOT NULL COMMENT '디바이스 토큰',
  `device_type` VARCHAR(50) NOT NULL COMMENT '디바이스 유형',
  `device_name` VARCHAR(100) NOT NULL COMMENT '디바이스 이름',
  `fcm_token_created_at` DATETIME NOT NULL COMMENT '토큰 생성일자',
  CONSTRAINT `pk_fcm_token` PRIMARY KEY (`fcm_token_id`),
  CONSTRAINT `uk_fcm_token_token` UNIQUE (`token`),
  INDEX `idx_fcm_token_users` (`users_id`),
  CONSTRAINT `fk_fcm_token_users` FOREIGN KEY (`users_id`) REFERENCES `users` (`users_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='FCM 디바이스 토큰';

-- =========================================================
-- 5. 지역 및 랜드마크 도메인
-- =========================================================

CREATE TABLE IF NOT EXISTS `region` (
  `region_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '지역 식별자',
  `users_id` VARCHAR(36) NOT NULL COMMENT '유저 식별자',
  `region_is_visited` BOOLEAN NOT NULL COMMENT '지역 방문여부',
  `region_overview` TEXT NOT NULL COMMENT '지역 설명',
  `region_zipcode` VARCHAR(255) NOT NULL COMMENT '지역 법정동 코드',
  CONSTRAINT `pk_region` PRIMARY KEY (`region_id`),
  INDEX `idx_region_users` (`users_id`),
  CONSTRAINT `fk_region_users` FOREIGN KEY (`users_id`) REFERENCES `users` (`users_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='지역 정보';

CREATE TABLE IF NOT EXISTS `landmark` (
  `landmark_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '랜드마크 식별자',
  `region_id` BIGINT NOT NULL COMMENT '지역 식별자',
  `landmark_is_visited` BOOLEAN NOT NULL COMMENT '랜드마크 방문 여부',
  `content_id` VARCHAR(255) NOT NULL COMMENT 'tour api 식별자',
  `landmark_zipcode` VARCHAR(255) NOT NULL COMMENT '랜드마크 법정동 코드',
  CONSTRAINT `pk_landmark` PRIMARY KEY (`landmark_id`),
  CONSTRAINT `uk_landmark_content_id` UNIQUE (`content_id`),
  INDEX `idx_landmark_region` (`region_id`),
  CONSTRAINT `fk_landmark_region` FOREIGN KEY (`region_id`) REFERENCES `region` (`region_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='랜드마크 정보';

-- =========================================================
-- 6. 이벤트 및 북마크 도메인
-- =========================================================

CREATE TABLE IF NOT EXISTS `event` (
  `event_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '이벤트 식별자',
  `event_start` DATETIME NULL COMMENT '이벤트 시작일자',
  `event_end` DATETIME NULL COMMENT '이벤트 종료일자',
  `event_title` VARCHAR(100) NOT NULL COMMENT '이벤트 제목',
  `event_content` VARCHAR(500) NOT NULL COMMENT '이벤트 내용',
  `event_image_url1` VARCHAR(2048) NOT NULL COMMENT '이벤트 이미지 url 1',
  `event_image_url2` VARCHAR(2048) NOT NULL COMMENT '이벤트 이미지 url 2',
  CONSTRAINT `pk_event` PRIMARY KEY (`event_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='이벤트 정보';

CREATE TABLE IF NOT EXISTS `bookmark` (
  `bookmark_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '북마크 식별자',
  `users_id` VARCHAR(36) NOT NULL COMMENT '유저 식별자',
  `bookmark_type` ENUM('EVENT', 'REGION', 'LANDMARK') NOT NULL COMMENT '북마크 타입',
  `bookmark_identifier` BIGINT NOT NULL COMMENT '북마크 타입 식별자',
  CONSTRAINT `pk_bookmark` PRIMARY KEY (`bookmark_id`),
  CONSTRAINT `uk_bookmark_users_target` UNIQUE (`users_id`, `bookmark_type`, `bookmark_identifier`),
  CONSTRAINT `fk_bookmark_users` FOREIGN KEY (`users_id`) REFERENCES `users` (`users_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='사용자 북마크 정보';

-- =========================================================
-- 7. 리뷰 도메인
-- =========================================================

CREATE TABLE IF NOT EXISTS `review` (
  `review_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '리뷰 식별자',
  `users_id` VARCHAR(36) NOT NULL COMMENT '유저 식별자',
  `region_id` BIGINT NOT NULL COMMENT '지역 식별자',
  `event_id` BIGINT NOT NULL COMMENT '이벤트 식별자',
  `review_title` VARCHAR(100) NOT NULL COMMENT '리뷰 제목',
  `review_content` VARCHAR(500) NOT NULL COMMENT '리뷰 내용',
  `review_score` FLOAT NOT NULL COMMENT '만족도',
  `review_point` INT NOT NULL COMMENT '리뷰 점수',
  CONSTRAINT `pk_review` PRIMARY KEY (`review_id`),
  INDEX `idx_review_users` (`users_id`),
  INDEX `idx_review_region` (`region_id`),
  INDEX `idx_review_event` (`event_id`),
  CONSTRAINT `fk_review_users` FOREIGN KEY (`users_id`) REFERENCES `users` (`users_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_review_region` FOREIGN KEY (`region_id`) REFERENCES `region` (`region_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_review_event` FOREIGN KEY (`event_id`) REFERENCES `event` (`event_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='리뷰 정보';

CREATE TABLE IF NOT EXISTS `review_log` (
  `review_log_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '리뷰 로그 식별자',
  `review_id` BIGINT NOT NULL COMMENT '리뷰 식별자',
  `review_created_at` DATETIME NOT NULL COMMENT '리뷰 생성일자',
  `review_log_content` VARCHAR(500) NOT NULL COMMENT '리뷰 로그 내용',
  CONSTRAINT `pk_review_log` PRIMARY KEY (`review_log_id`),
  INDEX `idx_review_log_review` (`review_id`),
  CONSTRAINT `fk_review_log_review` FOREIGN KEY (`review_id`) REFERENCES `review` (`review_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='리뷰 작성 및 변경 로그';

CREATE TABLE IF NOT EXISTS `image` (
  `image_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '이미지 식별자',
  `review_id` BIGINT NOT NULL COMMENT '리뷰 식별자',
  `original_name` VARCHAR(255) NOT NULL COMMENT '실제 파일명',
  `saved_name` VARCHAR(255) NOT NULL COMMENT '난수화된 파일명',
  `image_url` VARCHAR(2048) NOT NULL COMMENT '이미지 url',
  `file_size` INT NOT NULL COMMENT '파일 용량',
  `image_created_at` DATETIME NOT NULL COMMENT '이미지 생성일자',
  CONSTRAINT `pk_image` PRIMARY KEY (`image_id`),
  INDEX `idx_image_review` (`review_id`),
  CONSTRAINT `fk_image_review` FOREIGN KEY (`review_id`) REFERENCES `review` (`review_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='리뷰 첨부 이미지';

-- =========================================================
-- 8. 미션 도메인
-- =========================================================

CREATE TABLE IF NOT EXISTS `mission` (
  `mission_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '미션 식별자',
  `mission_name` VARCHAR(100) NOT NULL COMMENT '미션 이름',
  `mission_group` INT NULL COMMENT '미션 그룹',
  `mission_type` VARCHAR(50) NOT NULL COMMENT '미션 타입',
  `mission_target` VARCHAR(50) NOT NULL COMMENT '미션 타겟',
  `mission_operator` VARCHAR(10) NOT NULL COMMENT '미션 연산자',
  `mission_value` INT NULL COMMENT '미션 값',
  `mission_filter` JSON NOT NULL COMMENT '미션 상세 조건',
  `mission_week_start` DATETIME NOT NULL COMMENT '주 간 미션 시작 날짜',
  `mission_week_end` DATETIME NOT NULL COMMENT '주간 미션 종료 날짜',
  `mission_score` INT NOT NULL COMMENT '미션 점수',
  `mission_xp` INT NOT NULL COMMENT '미션 경험치',
  CONSTRAINT `pk_mission` PRIMARY KEY (`mission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='미션 정보';

CREATE TABLE IF NOT EXISTS `users_mission` (
  `users_mission_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '유저 미션 식별자',
  `users_id` VARCHAR(36) NOT NULL COMMENT '유저 식별자',
  `mission_id` BIGINT NOT NULL COMMENT '미션 식별자',
  `users_mission_created_at` DATETIME NOT NULL COMMENT '유저 미션 생성 날짜',
  CONSTRAINT `pk_users_mission` PRIMARY KEY (`users_mission_id`),
  CONSTRAINT `uk_users_mission` UNIQUE (`users_id`, `mission_id`),
  INDEX `idx_users_mission_mission` (`mission_id`),
  CONSTRAINT `fk_users_mission_users` FOREIGN KEY (`users_id`) REFERENCES `users` (`users_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_users_mission_mission` FOREIGN KEY (`mission_id`) REFERENCES `mission` (`mission_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='사용자와 미션 연결 중간 테이블';

```