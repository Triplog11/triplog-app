CREATE TABLE attraction_visit_log (
    attraction_visit_log_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '일반 관광지 방문 로그 식별자',
    users_id VARCHAR(36) NOT NULL COMMENT '유저 식별자',
    attraction_id BIGINT NOT NULL COMMENT '일반 관광지 식별자',
    visited_at DATETIME(6) NOT NULL COMMENT '방문 시각',
    PRIMARY KEY (attraction_visit_log_id),
    KEY idx_attraction_visit_log_users_attraction (users_id, attraction_id, visited_at),
    CONSTRAINT fk_attraction_visit_log_users FOREIGN KEY (users_id) REFERENCES users (users_id),
    CONSTRAINT fk_attraction_visit_log_attraction FOREIGN KEY (attraction_id) REFERENCES attraction (attraction_id)
) COMMENT='일반 관광지 방문 로그';

ALTER TABLE users_mission
    ADD CONSTRAINT uk_users_mission_users_mission UNIQUE (users_id, mission_id);

ALTER TABLE mission
    ADD CONSTRAINT uk_mission_week_name UNIQUE (mission_week_start, mission_name);
