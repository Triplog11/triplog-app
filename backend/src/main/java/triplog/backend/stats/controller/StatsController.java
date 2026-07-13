package triplog.backend.stats.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 사용자 통계(Stats)와 관련된 API 요청을 처리하는 Controller입니다.
 * <p>
 * 사용자 통계 정보 조회 및 수정, 점수 및 레벨 관리 등
 * Stats 도메인과 관련된 HTTP 요청을 처리합니다.
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "Stats API", description = "사용자 통계 API")
@RequestMapping("/stats")
@Slf4j
public class StatsController {
}