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
    landmark_id BIGINT NOT NULL AUTO_INCREMENT,
    tourism_content_id BIGINT NOT NULL,
    landmark_name VARCHAR(100),
    PRIMARY KEY (landmark_id),
    UNIQUE KEY uk_landmark_tourism_content (tourism_content_id),
    CONSTRAINT fk_landmark_content FOREIGN KEY (tourism_content_id) REFERENCES tourism_content (tourism_content_id)
);

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
    users_region_id BIGINT NOT NULL AUTO_INCREMENT,
    users_id VARCHAR(36) NOT NULL,
    region_id BIGINT NOT NULL,
    first_visited_at DATETIME(6) NOT NULL,
    PRIMARY KEY (users_region_id),
    UNIQUE KEY uk_users_region (users_id, region_id),
    CONSTRAINT fk_users_region_users FOREIGN KEY (users_id) REFERENCES users (users_id),
    CONSTRAINT fk_users_region_region FOREIGN KEY (region_id) REFERENCES region (region_id)
);

CREATE TABLE users_card_landmark (
    users_card_landmark_id BIGINT NOT NULL AUTO_INCREMENT,
    users_id VARCHAR(36) NOT NULL,
    landmark_id BIGINT NOT NULL,
    acquired_at DATETIME(6) NOT NULL,
    PRIMARY KEY (users_card_landmark_id),
    UNIQUE KEY uk_users_card_landmark (users_id, landmark_id),
    CONSTRAINT fk_users_card_landmark_users FOREIGN KEY (users_id) REFERENCES users (users_id),
    CONSTRAINT fk_users_card_landmark_landmark FOREIGN KEY (landmark_id) REFERENCES landmark (landmark_id)
);

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
