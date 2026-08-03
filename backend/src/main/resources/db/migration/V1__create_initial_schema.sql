CREATE TABLE users (
                       users_id VARCHAR(36) NOT NULL,
                       login_type VARCHAR(30) NOT NULL,
                       nickname VARCHAR(12) NOT NULL,
                       profile_url VARCHAR(2048) NOT NULL,
                       email VARCHAR(320) NOT NULL,
                       password VARCHAR(255),
                       PRIMARY KEY (users_id),
                       UNIQUE KEY uk_users_nickname (nickname),
                       UNIQUE KEY uk_users_email (email)
);

CREATE TABLE stats (
                       stats_id INT NOT NULL AUTO_INCREMENT,
                       users_id VARCHAR(36) NOT NULL,
                       address_si VARCHAR(20) NOT NULL,
                       address_do_gun VARCHAR(20) NOT NULL,
                       address_gu VARCHAR(30) NOT NULL,
                       overall_score INT NOT NULL,
                       month_score INT NOT NULL,
                       quarter_score INT NOT NULL,
                       current_tier VARCHAR(10) NOT NULL,
                       stats_level INT NOT NULL,
                       stats_xp INT NOT NULL,
                       PRIMARY KEY (stats_id),
                       UNIQUE KEY uk_stats_users (users_id),
                       CONSTRAINT fk_stats_users FOREIGN KEY (users_id) REFERENCES users (users_id)
);

CREATE TABLE badge (
                       badge_id BIGINT NOT NULL AUTO_INCREMENT,
                       badge_name VARCHAR(100) NOT NULL,
                       badge_url VARCHAR(2048) NOT NULL,
                       badge_group INT,
                       badge_type VARCHAR(50) NOT NULL,
                       badge_target VARCHAR(50) NOT NULL,
                       badge_operator VARCHAR(10) NOT NULL,
                       badge_value INT,
                       badge_filter JSON NOT NULL,
                       PRIMARY KEY (badge_id)
);

CREATE TABLE users_badge (
                             users_badge_id BIGINT NOT NULL AUTO_INCREMENT,
                             users_id VARCHAR(36) NOT NULL,
                             badge_id BIGINT NOT NULL,
                             is_representative BOOLEAN NOT NULL DEFAULT FALSE,
                             PRIMARY KEY (users_badge_id),
                             KEY idx_users_badge_users (users_id),
                             KEY idx_users_badge_badge (badge_id),
                             CONSTRAINT fk_users_badge_users FOREIGN KEY (users_id) REFERENCES users (users_id),
                             CONSTRAINT fk_users_badge_badge FOREIGN KEY (badge_id) REFERENCES badge (badge_id)
);

CREATE TABLE region (
                        region_id BIGINT NOT NULL AUTO_INCREMENT,
                        region_name VARCHAR(100) NOT NULL,
                        region_overview TEXT,
                        legal_region_code VARCHAR(10) NOT NULL,
                        legal_district_code VARCHAR(10) NOT NULL,
                        PRIMARY KEY (region_id),
                        UNIQUE KEY uk_region_legal_code (legal_region_code, legal_district_code)
);

CREATE TABLE tourism_content (
                                 tourism_content_id BIGINT NOT NULL AUTO_INCREMENT,
                                 region_id BIGINT NOT NULL,
                                 external_content_id VARCHAR(32) NOT NULL,
                                 previous_external_content_id VARCHAR(32),
                                 content_type_id VARCHAR(10) NOT NULL,
                                 title VARCHAR(255) NOT NULL,
                                 overview TEXT,
                                 address VARCHAR(500),
                                 detail_address VARCHAR(500),
                                 postal_code VARCHAR(20),
                                 telephone VARCHAR(255),
                                 homepage TEXT,
                                 longitude DECIMAL(11,8),
                                 latitude DECIMAL(10,8),
                                 map_level INT,
                                 legal_region_code VARCHAR(10),
                                 legal_district_code VARCHAR(10),
                                 classification_depth1 VARCHAR(20),
                                 classification_depth2 VARCHAR(20),
                                 classification_depth3 VARCHAR(20),
                                 primary_image_url VARCHAR(2048),
                                 thumbnail_image_url VARCHAR(2048),
                                 copyright_type VARCHAR(20),
                                 is_active BOOLEAN NOT NULL DEFAULT TRUE,
                                 sync_status VARCHAR(20) NOT NULL,
                                 consecutive_failure_count INT NOT NULL DEFAULT 0,
                                 consecutive_missing_count INT NOT NULL DEFAULT 0,
                                 provider_created_at DATETIME(6),
                                 provider_modified_at DATETIME(6),
                                 last_synced_at DATETIME(6),
                                 last_sync_failure_at DATETIME(6),
                                 PRIMARY KEY (tourism_content_id),
                                 UNIQUE KEY uk_tourism_content_external (external_content_id),
                                 KEY idx_tourism_content_region_type (region_id, content_type_id, is_active),
                                 KEY idx_tourism_content_modified (provider_modified_at),
                                 CONSTRAINT fk_tourism_content_region FOREIGN KEY (region_id) REFERENCES region (region_id)
);

CREATE TABLE landmark (
                          landmark_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '랜드마크 식별자',
                          tourism_content_id BIGINT NOT NULL COMMENT 'TourAPI 공통 관광 콘텐츠 식별자',
                          landmark_name VARCHAR(100) COMMENT '랜드마크 표시명',
                          PRIMARY KEY (landmark_id),
                          UNIQUE KEY uk_landmark_tourism_content (tourism_content_id),
                          CONSTRAINT fk_landmark_content FOREIGN KEY (tourism_content_id) REFERENCES tourism_content (tourism_content_id)
) COMMENT='서비스에서 선정한 랜드마크';

CREATE TABLE attraction (
                            attraction_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '일반 관광지 식별자',
                            tourism_content_id BIGINT NOT NULL COMMENT 'TourAPI 공통 관광 콘텐츠 식별자',
                            PRIMARY KEY (attraction_id),
                            UNIQUE KEY uk_attraction_tourism_content (tourism_content_id),
                            CONSTRAINT fk_attraction_content FOREIGN KEY (tourism_content_id)
                                REFERENCES tourism_content (tourism_content_id)
) COMMENT='서비스에서 선정한 일반 관광지';

CREATE TABLE event (
                       event_id BIGINT NOT NULL AUTO_INCREMENT,
                       tourism_content_id BIGINT NOT NULL,
                       event_start_date DATE,
                       event_end_date DATE,
                       event_place VARCHAR(500),
                       play_time VARCHAR(500),
                       age_limit VARCHAR(255),
                       usage_fee TEXT,
                       sponsor_name VARCHAR(255),
                       sponsor_telephone VARCHAR(255),
                       progress_type VARCHAR(100),
                       festival_type VARCHAR(100),
                       event_detail_data JSON,
                       PRIMARY KEY (event_id),
                       UNIQUE KEY uk_event_tourism_content (tourism_content_id),
                       CONSTRAINT fk_event_content FOREIGN KEY (tourism_content_id) REFERENCES tourism_content (tourism_content_id)
);

CREATE TABLE tourism_content_image (
                                       tourism_content_image_id BIGINT NOT NULL AUTO_INCREMENT,
                                       tourism_content_id BIGINT NOT NULL,
                                       external_serial_number VARCHAR(50) NOT NULL,
                                       image_name VARCHAR(255),
                                       original_image_url VARCHAR(2048) NOT NULL,
                                       thumbnail_image_url VARCHAR(2048),
                                       copyright_type VARCHAR(20),
                                       is_active BOOLEAN NOT NULL DEFAULT TRUE,
                                       PRIMARY KEY (tourism_content_image_id),
                                       UNIQUE KEY uk_tourism_content_image_serial (tourism_content_id, external_serial_number),
                                       KEY idx_tourism_content_image_content (tourism_content_id),
                                       CONSTRAINT fk_tourism_content_image_content FOREIGN KEY (tourism_content_id) REFERENCES tourism_content (tourism_content_id)
);

CREATE TABLE tourism_sync_failure (
                                      tourism_sync_failure_id BIGINT NOT NULL AUTO_INCREMENT,
                                      sync_type VARCHAR(30) NOT NULL,
                                      external_content_id VARCHAR(32) NOT NULL,
                                      legal_region_code VARCHAR(10),
                                      legal_district_code VARCHAR(10),
                                      error_code VARCHAR(50) NOT NULL,
                                      error_message VARCHAR(500) NOT NULL,
                                      retry_count INT NOT NULL DEFAULT 0,
                                      sync_failure_status VARCHAR(20) NOT NULL,
                                      failed_at DATETIME(6) NOT NULL,
                                      last_retried_at DATETIME(6),
                                      resolved_at DATETIME(6),
                                      PRIMARY KEY (tourism_sync_failure_id),
                                      UNIQUE KEY uk_tourism_sync_failure_target (sync_type, external_content_id),
                                      KEY idx_tourism_sync_failure_retry (sync_failure_status, last_retried_at)
);

CREATE TABLE tourism_sync_checkpoint (
                                         tourism_sync_checkpoint_id BIGINT NOT NULL AUTO_INCREMENT,
                                         sync_type VARCHAR(30) NOT NULL,
                                         last_succeeded_at DATETIME(6) NOT NULL,
                                         PRIMARY KEY (tourism_sync_checkpoint_id),
                                         UNIQUE KEY uk_tourism_sync_checkpoint_type (sync_type)
);

CREATE TABLE users_region (
                              users_region_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '유저 지역 식별자',
                              region_id BIGINT NOT NULL COMMENT '지역 식별자',
                              users_id VARCHAR(36) NOT NULL COMMENT '유저 식별자',
                              users_region_visited_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '방문일자',
                              users_region_visited_count INT NOT NULL DEFAULT 0 COMMENT '방문 횟수',
                              PRIMARY KEY (users_region_id),
                              KEY idx_users_region_region (region_id),
                              KEY idx_users_region_users (users_id),
                              CONSTRAINT fk_users_region_region FOREIGN KEY (region_id) REFERENCES region (region_id) ON DELETE CASCADE,
                              CONSTRAINT fk_users_region_users FOREIGN KEY (users_id) REFERENCES users (users_id) ON DELETE CASCADE
) COMMENT='사용자 지역 방문 정보';

CREATE TABLE card (
                      card_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '카드 식별자',
                      card_name VARCHAR(100) NOT NULL COMMENT '카드 이름',
                      card_tier VARCHAR(10) NOT NULL COMMENT '카드 등급',
                      card_url VARCHAR(2048) NOT NULL COMMENT '카드 이미지',
                      PRIMARY KEY (card_id)
) COMMENT='카드 정보';

CREATE TABLE users_card_landmark (
                                     users_card_landmark_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '유저 카드 랜드마크 식별자',
                                     landmark_id BIGINT NOT NULL COMMENT '랜드마크 식별자',
                                     card_id BIGINT NOT NULL COMMENT '카드 식별자',
                                     users_id VARCHAR(36) NOT NULL COMMENT '유저 식별자',
                                     users_card_landmark_visited_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '유저 카드 랜드마크 방문 일자',
                                     users_card_landmark_count INT NOT NULL DEFAULT 0 COMMENT '유저 카드 랜드마크 방문 횟수',
                                     PRIMARY KEY (users_card_landmark_id),
                                     KEY idx_ucl_landmark (landmark_id),
                                     KEY idx_ucl_card (card_id),
                                     KEY idx_ucl_users (users_id),
                                     CONSTRAINT fk_ucl_landmark FOREIGN KEY (landmark_id) REFERENCES landmark (landmark_id) ON DELETE CASCADE,
                                     CONSTRAINT fk_ucl_card FOREIGN KEY (card_id) REFERENCES card (card_id) ON DELETE CASCADE,
                                     CONSTRAINT fk_ucl_users FOREIGN KEY (users_id) REFERENCES users (users_id) ON DELETE CASCADE
) COMMENT='사용자 랜드마크 카드 획득 정보';

CREATE TABLE region_visit_log (
                                  region_visit_log_id BIGINT NOT NULL AUTO_INCREMENT,
                                  users_id VARCHAR(36) NOT NULL,
                                  region_id BIGINT NOT NULL,
                                  visited_at DATETIME(6) NOT NULL,
                                  PRIMARY KEY (region_visit_log_id),
                                  KEY idx_region_visit_log_users_region (users_id, region_id, visited_at),
                                  CONSTRAINT fk_region_visit_log_users FOREIGN KEY (users_id) REFERENCES users (users_id),
                                  CONSTRAINT fk_region_visit_log_region FOREIGN KEY (region_id) REFERENCES region (region_id)
);

CREATE TABLE landmark_visit_log (
                                    landmark_visit_log_id BIGINT NOT NULL AUTO_INCREMENT,
                                    users_id VARCHAR(36) NOT NULL,
                                    landmark_id BIGINT NOT NULL,
                                    visited_at DATETIME(6) NOT NULL,
                                    PRIMARY KEY (landmark_visit_log_id),
                                    KEY idx_landmark_visit_log_users_landmark (users_id, landmark_id, visited_at),
                                    CONSTRAINT fk_landmark_visit_log_users FOREIGN KEY (users_id) REFERENCES users (users_id),
                                    CONSTRAINT fk_landmark_visit_log_landmark FOREIGN KEY (landmark_id) REFERENCES landmark (landmark_id)
);

CREATE TABLE users_level_log (
                                 level_log_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '레벨 로그 식별자',
                                 users_id VARCHAR(36) NOT NULL COMMENT '유저 식별자',
                                 level_log_created_at DATETIME NOT NULL COMMENT '레벨 로그 생성일자',
                                 users_level_log_gain_xp INT NOT NULL COMMENT '레벨 로그 받은 경험치',
                                 users_level_content VARCHAR(500) NOT NULL COMMENT '레벨 로그 내용',
                                 PRIMARY KEY (level_log_id),
                                 KEY idx_users_level_log_users (users_id),
                                 CONSTRAINT fk_users_level_log_users FOREIGN KEY (users_id) REFERENCES users (users_id) ON DELETE CASCADE
) COMMENT='사용자 레벨 변경 로그';

CREATE TABLE activity_policy (
                                 activity_policy_id VARCHAR(36) NOT NULL COMMENT '활동 정책 식별자',
                                 upper_policy_id VARCHAR(36) COMMENT '활동 정책 상위 policy_id',
                                 policy_xp INT NOT NULL COMMENT '활동 정책 경험치',
                                 policy_score INT NOT NULL COMMENT '활동 정책 점수',
                                 policy_description VARCHAR(2048) NOT NULL COMMENT '활동 정책 설명',
                                 PRIMARY KEY (activity_policy_id),
                                 KEY idx_activity_policy_upper (upper_policy_id),
                                 CONSTRAINT fk_activity_policy_upper FOREIGN KEY (upper_policy_id)
                                     REFERENCES activity_policy (activity_policy_id) ON DELETE SET NULL
) COMMENT='활동 보상 정책';

CREATE TABLE level_policy (
                              level_policy_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '레벨 정책 식별자',
                              level_policy_number INT NOT NULL COMMENT '레벨',
                              level_policy_condition INT NOT NULL COMMENT '레벨업 조건',
                              PRIMARY KEY (level_policy_id)
) COMMENT='레벨업 조건 정책';

CREATE TABLE `role` (
                        role_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '권한 식별자',
                        role_name VARCHAR(10) NOT NULL COMMENT '권한 이름',
                        role_description VARCHAR(255) NOT NULL COMMENT '권한 설명',
                        PRIMARY KEY (role_id)
) COMMENT='사용자 권한 정보';

CREATE TABLE level_policy_role (
                                   level_policy_role_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '레벨 정책 권한 식별자',
                                   role_id BIGINT NOT NULL COMMENT '권한 식별자',
                                   level_policy_id BIGINT NOT NULL COMMENT '레벨 식별자',
                                   PRIMARY KEY (level_policy_role_id),
                                   KEY idx_lpr_role (role_id),
                                   KEY idx_lpr_level_policy (level_policy_id),
                                   CONSTRAINT fk_lpr_role FOREIGN KEY (role_id) REFERENCES `role` (role_id) ON DELETE CASCADE,
                                   CONSTRAINT fk_lpr_level_policy FOREIGN KEY (level_policy_id)
                                       REFERENCES level_policy (level_policy_id) ON DELETE CASCADE
) COMMENT='레벨 정책과 권한 연결 중간 테이블';

CREATE TABLE rank_policy (
                             rank_policy_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '랭크 정책 식별자',
                             rank_policy_tier VARCHAR(10) NOT NULL COMMENT '랭크 정책 티어',
                             rank_policy_condition INT NOT NULL COMMENT '랭크 정책 조건',
                             PRIMARY KEY (rank_policy_id)
) COMMENT='랭크 및 티어 정책';

CREATE TABLE users_badge_log (
                                 users_badge_log_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '유저 뱃지 로그 식별자',
                                 users_badge_id BIGINT NOT NULL COMMENT '유저 뱃지 식별자',
                                 users_badge_log_created_at DATETIME NOT NULL COMMENT '유저 뱃지 로그 생성일자',
                                 users_badge_content VARCHAR(500) NOT NULL COMMENT '유저 뱃지 로그 내용',
                                 users_badge_gain_xp INT NOT NULL COMMENT '받은 경험치',
                                 PRIMARY KEY (users_badge_log_id),
                                 KEY idx_ubl_users_badge (users_badge_id),
                                 CONSTRAINT fk_ubl_users_badge FOREIGN KEY (users_badge_id)
                                     REFERENCES users_badge (users_badge_id) ON DELETE CASCADE
) COMMENT='사용자 뱃지 획득 로그';

CREATE TABLE appellation (
                             appellation_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '칭호 식별자',
                             appellation_name VARCHAR(100) NOT NULL COMMENT '칭호 이름',
                             appellation_group INT COMMENT '칭호 그룹',
                             appellation_type VARCHAR(50) NOT NULL COMMENT '칭호 타입',
                             appellation_target VARCHAR(50) NOT NULL COMMENT '칭호 타겟',
                             appellation_operator VARCHAR(10) NOT NULL COMMENT '칭호 연산자',
                             appellation_value INT COMMENT '칭호 값',
                             appellation_filter JSON NOT NULL COMMENT '칭호 상세 조건',
                             PRIMARY KEY (appellation_id)
) COMMENT='칭호 정보';

CREATE TABLE users_appellation (
                                   users_appellation_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '유저 칭호 식별자',
                                   users_id VARCHAR(36) NOT NULL COMMENT '유저 식별자',
                                   appellation_id BIGINT NOT NULL COMMENT '칭호 식별자',
                                   PRIMARY KEY (users_appellation_id),
                                   KEY idx_ua_users (users_id),
                                   KEY idx_ua_appellation (appellation_id),
                                   CONSTRAINT fk_ua_users FOREIGN KEY (users_id) REFERENCES users (users_id) ON DELETE CASCADE,
                                   CONSTRAINT fk_ua_appellation FOREIGN KEY (appellation_id)
                                       REFERENCES appellation (appellation_id) ON DELETE CASCADE
) COMMENT='사용자와 칭호 연결 중간 테이블';

CREATE TABLE notification_policy (
                                     notification_policy_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '알림 정책 식별자',
                                     notification_type VARCHAR(50) NOT NULL COMMENT '알림 유형',
                                     notification_policy_name VARCHAR(100) NOT NULL COMMENT '알림 정책 이름',
                                     trigger_event VARCHAR(50) NOT NULL COMMENT '트리거 이벤트',
                                     title_template VARCHAR(100) NOT NULL COMMENT '제목 템플릿',
                                     content_template VARCHAR(500) NOT NULL COMMENT '내용 템플릿',
                                     default_enable BOOLEAN NOT NULL COMMENT '기본 수신 여부',
                                     is_active BOOLEAN NOT NULL COMMENT '알림 정책 활성화 여부',
                                     PRIMARY KEY (notification_policy_id),
                                     UNIQUE KEY uk_notification_policy_notification_type (notification_type)
) COMMENT='알림 발송 정책';

CREATE TABLE notification (
                              notification_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '알림 식별자',
                              users_id VARCHAR(36) NOT NULL COMMENT '유저 식별자',
                              notification_policy_id BIGINT NOT NULL COMMENT '알림 정책 식별자',
                              notification_type VARCHAR(50) NOT NULL COMMENT '알림 유형',
                              notification_title VARCHAR(100) NOT NULL COMMENT '알림 제목',
                              notification_content VARCHAR(500) NOT NULL COMMENT '알림 내용',
                              notification_identifier BIGINT NOT NULL COMMENT '관련 엔티티 ID',
                              target_type VARCHAR(50) NOT NULL COMMENT '타겟 유형',
                              notification_data JSON NOT NULL COMMENT '알림 추가 정보',
                              is_read BOOLEAN NOT NULL COMMENT '읽음 여부',
                              notification_created_at DATETIME NOT NULL COMMENT '알림 생성 날짜',
                              read_at DATETIME COMMENT '알림 읽은 날짜',
                              PRIMARY KEY (notification_id),
                              KEY idx_notification_users (users_id),
                              KEY idx_notification_policy (notification_policy_id),
                              CONSTRAINT fk_notification_users FOREIGN KEY (users_id) REFERENCES users (users_id) ON DELETE CASCADE,
                              CONSTRAINT fk_notification_policy FOREIGN KEY (notification_policy_id)
                                  REFERENCES notification_policy (notification_policy_id)
) COMMENT='사용자 알림 데이터';

CREATE TABLE fcm_token (
                           fcm_token_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '푸시 토큰 식별자',
                           users_id VARCHAR(36) NOT NULL COMMENT '유저 식별자',
                           token VARCHAR(512) NOT NULL COMMENT '디바이스 토큰',
                           device_type VARCHAR(50) NOT NULL COMMENT '디바이스 유형',
                           device_name VARCHAR(100) NOT NULL COMMENT '디바이스 이름',
                           fcm_token_created_at DATETIME NOT NULL COMMENT '토큰 생성일자',
                           PRIMARY KEY (fcm_token_id),
                           KEY idx_fcm_token_users (users_id),
                           CONSTRAINT fk_fcm_token_users FOREIGN KEY (users_id) REFERENCES users (users_id) ON DELETE CASCADE
) COMMENT='FCM 디바이스 토큰';

CREATE TABLE users_region_log (
                                  users_region_log_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '유저 지역 로그 식별자',
                                  users_region_id BIGINT NOT NULL COMMENT '유저 지역 식별자',
                                  users_region_log_created_at DATETIME NOT NULL COMMENT '유저 지역 로그 생성일자',
                                  users_region_log_content VARCHAR(500) NOT NULL COMMENT '유저 지역 로그 내용',
                                  users_region_log_xp INT NOT NULL COMMENT '유저 지역 로그 받은 경험치',
                                  PRIMARY KEY (users_region_log_id),
                                  KEY idx_users_region_log_users_region (users_region_id),
                                  CONSTRAINT fk_users_region_log_users_region FOREIGN KEY (users_region_id)
                                      REFERENCES users_region (users_region_id) ON DELETE CASCADE
) COMMENT='사용자 지역 방문 로그';

CREATE TABLE users_card_landmark_log (
                                         users_card_landmark_log_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '유저 카드 랜드마크 로그 식별자',
                                         users_card_landmark_id BIGINT NOT NULL COMMENT '유저 카드 랜드마크 식별자',
                                         users_card_landmark_log_created_at DATETIME NOT NULL COMMENT '유저 카드 랜드마크 로그 생성일자',
                                         users_card_landmark_log_content VARCHAR(500) NOT NULL COMMENT '유저 카드 랜드마크 로그 내용',
                                         users_card_landmark_log_gain_xp INT NOT NULL COMMENT '유저 카드 랜드마크 받은 경험치',
                                         PRIMARY KEY (users_card_landmark_log_id),
                                         KEY idx_ucl_log_ucl (users_card_landmark_id),
                                         CONSTRAINT fk_ucl_log_ucl FOREIGN KEY (users_card_landmark_id)
                                             REFERENCES users_card_landmark (users_card_landmark_id) ON DELETE CASCADE
) COMMENT='사용자 랜드마크 카드 획득 로그';

CREATE TABLE bookmark (
                          bookmark_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '북마크 식별자',
                          users_id VARCHAR(36) NOT NULL COMMENT '유저 식별자',
                          bookmark_type ENUM('EVENT', 'REGION', 'LANDMARK') NOT NULL COMMENT '북마크 타입',
                          bookmark_identifier BIGINT NOT NULL COMMENT '북마크 타입 식별자',
                          PRIMARY KEY (bookmark_id),
                          KEY idx_bookmark_users (users_id),
                          KEY idx_bookmark_target (bookmark_type, bookmark_identifier),
                          CONSTRAINT fk_bookmark_users FOREIGN KEY (users_id) REFERENCES users (users_id) ON DELETE CASCADE
) COMMENT='사용자 북마크 정보';

CREATE TABLE review (
                        review_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '리뷰 식별자',
                        users_id VARCHAR(36) NOT NULL COMMENT '유저 식별자',
                        tourism_content_id BIGINT NOT NULL COMMENT '리뷰 대상 관광 콘텐츠 식별자',
                        review_title VARCHAR(100) NOT NULL COMMENT '리뷰 제목',
                        review_content VARCHAR(500) NOT NULL COMMENT '리뷰 내용',
                        review_score FLOAT NOT NULL COMMENT '만족도',
                        review_point INT NOT NULL COMMENT '리뷰 점수',
                        PRIMARY KEY (review_id),
                        KEY idx_review_users (users_id),
                        KEY idx_review_tourism_content (tourism_content_id),
                        CONSTRAINT fk_review_users FOREIGN KEY (users_id) REFERENCES users (users_id) ON DELETE CASCADE,
                        CONSTRAINT fk_review_tourism_content FOREIGN KEY (tourism_content_id)
                            REFERENCES tourism_content (tourism_content_id)
) COMMENT='랜드마크 또는 축제 리뷰';

CREATE TABLE review_log (
                            review_log_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '리뷰 로그 식별자',
                            review_id BIGINT NOT NULL COMMENT '리뷰 식별자',
                            review_created_at DATETIME NOT NULL COMMENT '리뷰 생성일자',
                            review_log_content VARCHAR(500) NOT NULL COMMENT '리뷰 로그 내용',
                            review_gain_xp INT NOT NULL COMMENT '리뷰 받은 경험치',
                            PRIMARY KEY (review_log_id),
                            KEY idx_review_log_review (review_id),
                            CONSTRAINT fk_review_log_review FOREIGN KEY (review_id) REFERENCES review (review_id) ON DELETE CASCADE
) COMMENT='리뷰 작성 및 변경 로그';

CREATE TABLE image (
                       image_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '이미지 식별자',
                       review_id BIGINT NOT NULL COMMENT '리뷰 식별자',
                       original_name VARCHAR(255) NOT NULL COMMENT '실제 파일명',
                       saved_name VARCHAR(255) NOT NULL COMMENT '난수화된 파일명',
                       image_url VARCHAR(2048) NOT NULL COMMENT '이미지 url',
                       file_size INT NOT NULL COMMENT '파일 용량',
                       image_created_at DATETIME NOT NULL COMMENT '이미지 생성일자',
                       PRIMARY KEY (image_id),
                       KEY idx_image_review (review_id),
                       CONSTRAINT fk_image_review FOREIGN KEY (review_id) REFERENCES review (review_id) ON DELETE CASCADE
) COMMENT='리뷰 첨부 이미지';

CREATE TABLE mission (
                         mission_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '미션 식별자',
                         mission_name VARCHAR(100) NOT NULL COMMENT '미션 이름',
                         mission_group INT COMMENT '미션 그룹',
                         mission_type VARCHAR(50) NOT NULL COMMENT '미션 타입',
                         mission_target VARCHAR(50) NOT NULL COMMENT '미션 타겟',
                         mission_operator VARCHAR(10) NOT NULL COMMENT '미션 연산자',
                         mission_value INT COMMENT '미션 값',
                         mission_filter JSON NOT NULL COMMENT '미션 상세 조건',
                         mission_week_start DATETIME NOT NULL COMMENT '주간 미션 시작 날짜',
                         mission_week_end DATETIME NOT NULL COMMENT '주간 미션 종료 날짜',
                         mission_score INT NOT NULL COMMENT '미션 점수',
                         mission_xp INT NOT NULL COMMENT '미션 경험치',
                         PRIMARY KEY (mission_id)
) COMMENT='미션 정보';

CREATE TABLE users_mission (
                               users_mission_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '유저 미션 식별자',
                               users_id VARCHAR(36) NOT NULL COMMENT '유저 식별자',
                               mission_id BIGINT NOT NULL COMMENT '미션 식별자',
                               users_mission_created_at DATETIME NOT NULL COMMENT '유저 미션 생성 날짜',
                               PRIMARY KEY (users_mission_id),
                               KEY idx_users_mission_users (users_id),
                               KEY idx_users_mission_mission (mission_id),
                               CONSTRAINT fk_users_mission_users FOREIGN KEY (users_id) REFERENCES users (users_id) ON DELETE CASCADE,
                               CONSTRAINT fk_users_mission_mission FOREIGN KEY (mission_id) REFERENCES mission (mission_id) ON DELETE CASCADE
) COMMENT='사용자와 미션 연결 중간 테이블';

CREATE TABLE BATCH_JOB_INSTANCE (
                                    JOB_INSTANCE_ID BIGINT NOT NULL PRIMARY KEY,
                                    VERSION BIGINT,
                                    JOB_NAME VARCHAR(100) NOT NULL,
                                    JOB_KEY VARCHAR(32) NOT NULL,
                                    CONSTRAINT JOB_INST_UN UNIQUE (JOB_NAME, JOB_KEY)
);

CREATE TABLE BATCH_JOB_EXECUTION (
                                     JOB_EXECUTION_ID BIGINT NOT NULL PRIMARY KEY,
                                     VERSION BIGINT,
                                     JOB_INSTANCE_ID BIGINT NOT NULL,
                                     CREATE_TIME DATETIME(6) NOT NULL,
                                     START_TIME DATETIME(6),
                                     END_TIME DATETIME(6),
                                     STATUS VARCHAR(10),
                                     EXIT_CODE VARCHAR(2500),
                                     EXIT_MESSAGE VARCHAR(2500),
                                     LAST_UPDATED DATETIME(6),
                                     CONSTRAINT JOB_INST_EXEC_FK FOREIGN KEY (JOB_INSTANCE_ID) REFERENCES BATCH_JOB_INSTANCE (JOB_INSTANCE_ID)
);

CREATE TABLE BATCH_JOB_EXECUTION_PARAMS (
                                            JOB_EXECUTION_ID BIGINT NOT NULL,
                                            PARAMETER_NAME VARCHAR(100) NOT NULL,
                                            PARAMETER_TYPE VARCHAR(100) NOT NULL,
                                            PARAMETER_VALUE VARCHAR(2500),
                                            IDENTIFYING CHAR(1) NOT NULL,
                                            CONSTRAINT JOB_EXEC_PARAMS_FK FOREIGN KEY (JOB_EXECUTION_ID) REFERENCES BATCH_JOB_EXECUTION (JOB_EXECUTION_ID)
);

CREATE TABLE BATCH_STEP_EXECUTION (
                                      STEP_EXECUTION_ID BIGINT NOT NULL PRIMARY KEY,
                                      VERSION BIGINT NOT NULL,
                                      STEP_NAME VARCHAR(100) NOT NULL,
                                      JOB_EXECUTION_ID BIGINT NOT NULL,
                                      CREATE_TIME DATETIME(6) NOT NULL,
                                      START_TIME DATETIME(6),
                                      END_TIME DATETIME(6),
                                      STATUS VARCHAR(10),
                                      COMMIT_COUNT BIGINT,
                                      READ_COUNT BIGINT,
                                      FILTER_COUNT BIGINT,
                                      WRITE_COUNT BIGINT,
                                      READ_SKIP_COUNT BIGINT,
                                      WRITE_SKIP_COUNT BIGINT,
                                      PROCESS_SKIP_COUNT BIGINT,
                                      ROLLBACK_COUNT BIGINT,
                                      EXIT_CODE VARCHAR(2500),
                                      EXIT_MESSAGE VARCHAR(2500),
                                      LAST_UPDATED DATETIME(6),
                                      CONSTRAINT JOB_EXEC_STEP_FK FOREIGN KEY (JOB_EXECUTION_ID) REFERENCES BATCH_JOB_EXECUTION (JOB_EXECUTION_ID)
);

CREATE TABLE BATCH_STEP_EXECUTION_CONTEXT (
                                              STEP_EXECUTION_ID BIGINT NOT NULL PRIMARY KEY,
                                              SHORT_CONTEXT VARCHAR(2500) NOT NULL,
                                              SERIALIZED_CONTEXT TEXT,
                                              CONSTRAINT STEP_EXEC_CTX_FK FOREIGN KEY (STEP_EXECUTION_ID) REFERENCES BATCH_STEP_EXECUTION (STEP_EXECUTION_ID)
);

CREATE TABLE BATCH_JOB_EXECUTION_CONTEXT (
                                             JOB_EXECUTION_ID BIGINT NOT NULL PRIMARY KEY,
                                             SHORT_CONTEXT VARCHAR(2500) NOT NULL,
                                             SERIALIZED_CONTEXT TEXT,
                                             CONSTRAINT JOB_EXEC_CTX_FK FOREIGN KEY (JOB_EXECUTION_ID) REFERENCES BATCH_JOB_EXECUTION (JOB_EXECUTION_ID)
);

CREATE TABLE BATCH_STEP_EXECUTION_SEQ (
                                          ID BIGINT NOT NULL,
                                          UNIQUE_KEY CHAR(1) NOT NULL,
                                          CONSTRAINT UK_BATCH_STEP_EXECUTION_SEQ UNIQUE (UNIQUE_KEY)
);
INSERT INTO BATCH_STEP_EXECUTION_SEQ (ID, UNIQUE_KEY) VALUES (0, '0');

CREATE TABLE BATCH_JOB_EXECUTION_SEQ (
                                         ID BIGINT NOT NULL,
                                         UNIQUE_KEY CHAR(1) NOT NULL,
                                         CONSTRAINT UK_BATCH_JOB_EXECUTION_SEQ UNIQUE (UNIQUE_KEY)
);
INSERT INTO BATCH_JOB_EXECUTION_SEQ (ID, UNIQUE_KEY) VALUES (0, '0');

CREATE TABLE BATCH_JOB_INSTANCE_SEQ (
                                        ID BIGINT NOT NULL,
                                        UNIQUE_KEY CHAR(1) NOT NULL,
                                        CONSTRAINT UK_BATCH_JOB_INSTANCE_SEQ UNIQUE (UNIQUE_KEY)
);
INSERT INTO BATCH_JOB_INSTANCE_SEQ (ID, UNIQUE_KEY) VALUES (0, '0');
