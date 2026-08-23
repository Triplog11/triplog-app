package triplog.backend.event.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import triplog.backend.common.exception.ErrorResponse;
import triplog.backend.event.dto.response.EventResponse.EventDetailResponse;
import triplog.backend.event.dto.response.EventResponse.EventListResponse;
import triplog.backend.event.exception.EventErrorCode;
import triplog.backend.event.exception.EventException;
import triplog.backend.event.service.EventService;

/**
 * 이벤트(Event) 관련 API 요청을 처리하는 REST Controller입니다.
 */
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Tag(name = "Event API", description = "이벤트 API")
public class EventController {

    private final EventService eventService;

    /**
     * 이벤트 목록을 페이지 단위로 조회합니다.
     *
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return 이벤트 목록 응답
     */
    @GetMapping
    @Operation(summary = "이벤트 목록 조회", description = "이벤트를 시작일 순으로 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "이벤트 목록 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = EventListResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 페이지 요청",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "로그인 필요",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<EventListResponse> getEvents(
            @Parameter(description = "페이지 번호(0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "10")
            @RequestParam(defaultValue = "10") int size
    ) {
        if (page < 0 || size < 1) {
            throw new EventException(EventErrorCode.INVALID_PAGE_REQUEST);
        }
        Sort sort = Sort.by(
                Sort.Order.asc("eventStartDate"),
                Sort.Order.asc("eventId")
        );
        return ResponseEntity.ok(eventService.getEvents(PageRequest.of(page, size, sort)));
    }

    /**
     * 이벤트 상세 정보를 조회합니다.
     *
     * @param eventId 이벤트 ID
     * @return 이벤트 상세 응답
     */
    @GetMapping("/{eventId}")
    @Operation(summary = "이벤트 상세 조회", description = "특정 이벤트의 상세 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "이벤트 상세 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = EventDetailResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "eventId": 201,
                                      "eventTitle": "수원 문화 행사",
                                      "eventContent": "지역 이벤트 안내",
                                      "eventImageUrl1": "https://example.com/event-main.png",
                                      "eventImageUrl2": "https://example.com/event-sub.png",
                                      "eventStart": "2026-07-01T10:00:00",
                                      "eventEnd": "2026-07-07T18:00:00"
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "로그인 필요",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "이벤트 상세 정보를 찾을 수 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"status\":404,\"message\":\"이벤트 상세 정보를 찾을 수 없습니다.\"}"))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<EventDetailResponse> getEventDetail(
            @Parameter(description = "이벤트 ID", required = true, example = "201")
            @PathVariable Long eventId
    ) {
        return ResponseEntity.ok(eventService.getEventDetail(eventId));
    }
}
