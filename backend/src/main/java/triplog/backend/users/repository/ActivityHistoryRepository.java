package triplog.backend.users.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import triplog.backend.users.service.ActivityHistoryRecord;

import java.sql.Timestamp;
import java.util.List;

/**
 * 통합 사용자 활동 로그를 조회하고 중복 없이 기록합니다.
 */
@Repository
@RequiredArgsConstructor
public class ActivityHistoryRepository {

    private static final String ACTIVITY_HISTORY_QUERY = """
            SELECT activity_id,
                   activity_type,
                   title,
                   content,
                   score,
                   xp,
                   created_at
            FROM (
                SELECT users_activity_log_id AS activity_id,
                       activity_type,
                       activity_title AS title,
                       activity_content AS content,
                       activity_gain_score AS score,
                       activity_gain_xp AS xp,
                       activity_created_at AS created_at,
                       display_order
                FROM users_activity_log
                WHERE users_id = :usersId
            ) activity
            ORDER BY created_at DESC, display_order ASC, activity_id ASC
            LIMIT :limit OFFSET :offset
            """;

    private static final String ACTIVITY_HISTORY_COUNT_QUERY = """
            SELECT COUNT(*)
            FROM users_activity_log
            WHERE users_id = :usersId
            """;

    private static final String INSERT_ACTIVITY_QUERY = """
            INSERT INTO users_activity_log (
                users_id,
                activity_type,
                source_type,
                source_id,
                event_key,
                activity_title,
                activity_content,
                activity_gain_xp,
                activity_gain_score,
                display_order,
                activity_created_at
            ) VALUES (
                :usersId,
                :activityType,
                :sourceType,
                :sourceId,
                :eventKey,
                :title,
                :content,
                :xp,
                :score,
                :displayOrder,
                :createdAt
            )
            ON DUPLICATE KEY UPDATE event_key = users_activity_log.event_key
            """;

    private final NamedParameterJdbcTemplate jdbcTemplate;

    /**
     * 이벤트 키가 중복되지 않은 경우 활동 로그를 저장합니다.
     *
     * @param record 저장할 활동 정보
     * @return 저장된 행 수
     */
    public int insertIfAbsent(ActivityHistoryRecord record) {
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("usersId", record.usersId())
                .addValue("activityType", record.activityType())
                .addValue("sourceType", record.sourceType())
                .addValue("sourceId", record.sourceId())
                .addValue("eventKey", record.eventKey())
                .addValue("title", record.title())
                .addValue("content", record.content())
                .addValue("xp", record.xp())
                .addValue("score", record.score())
                .addValue("displayOrder", record.displayOrder())
                .addValue("createdAt", record.createdAt());
        return jdbcTemplate.update(INSERT_ACTIVITY_QUERY, parameters);
    }

    /**
     * 로그인 사용자의 활동 히스토리를 최신순으로 조회합니다.
     *
     * @param usersId 사용자 식별자
     * @param pageable 페이지 정보
     * @return 활동 히스토리 페이지
     */
    public Page<ActivityHistoryQueryResult> findByUsersId(String usersId, Pageable pageable) {
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("usersId", usersId)
                .addValue("limit", pageable.getPageSize())
                .addValue("offset", pageable.getOffset());

        List<ActivityHistoryQueryResult> activities = jdbcTemplate.query(
                ACTIVITY_HISTORY_QUERY,
                parameters,
                (resultSet, rowNumber) -> {
                    Timestamp createdAt = resultSet.getTimestamp("created_at");
                    return new ActivityHistoryQueryResult(
                            resultSet.getLong("activity_id"),
                            resultSet.getString("activity_type"),
                            resultSet.getString("title"),
                            resultSet.getString("content"),
                            resultSet.getObject("score", Integer.class),
                            resultSet.getObject("xp", Integer.class),
                            createdAt.toLocalDateTime()
                    );
                }
        );

        Long totalElements = jdbcTemplate.queryForObject(
                ACTIVITY_HISTORY_COUNT_QUERY,
                new MapSqlParameterSource("usersId", usersId),
                Long.class
        );

        return new PageImpl<>(
                activities,
                pageable,
                totalElements == null ? 0L : totalElements
        );
    }
}
