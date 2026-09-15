CREATE TABLE review_idempotency (
    review_idempotency_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '방문 인증 멱등성 요청 식별자',
    users_id VARCHAR(36) NOT NULL COMMENT '유저 식별자',
    idempotency_key VARCHAR(100) NOT NULL COMMENT '클라이언트 요청 멱등성 키',
    request_fingerprint CHAR(64) NOT NULL COMMENT '요청 내용 SHA-256 해시',
    response_payload TEXT NULL COMMENT '최초 요청 응답 JSON',
    created_at DATETIME(6) NOT NULL COMMENT '처리 시작 시각',
    completed_at DATETIME(6) NULL COMMENT '처리 완료 시각',
    PRIMARY KEY (review_idempotency_id),
    UNIQUE KEY uk_review_idempotency_users_key (users_id, idempotency_key),
    CONSTRAINT fk_review_idempotency_users FOREIGN KEY (users_id)
        REFERENCES users (users_id) ON DELETE CASCADE
) COMMENT='방문 인증 요청 멱등성 처리 기록';
