package triplog.backend.bookmark.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import triplog.backend.bookmark.dto.request.BookmarkRequest.CreateRequest;
import triplog.backend.bookmark.dto.response.BookmarkResponse.CreateResponse;
import triplog.backend.bookmark.dto.response.BookmarkResponse.DeleteResponse;
import triplog.backend.bookmark.service.BookmarkService;
import triplog.backend.common.exception.ErrorResponse;

/**
 * 북마크(Bookmark)와 관련된 API 요청을 처리하는 Controller입니다.
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "Bookmark API", description = "북마크 API")
@RequestMapping("/bookmarks")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    /**
     * 로그인 사용자의 북마크를 해제합니다.
     *
     * @param userDetails JWT 인증 사용자 정보
     * @param bookmarkId 해제할 북마크 ID
     * @return 북마크 해제 결과
     */
    @DeleteMapping("/{bookmarkId}")
    @Operation(summary = "북마크 해제", description = "로그인 사용자의 북마크를 해제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "북마크 해제에 성공했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DeleteResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "isDeleted": true
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"status\":400,\"message\":\"잘못된 요청입니다.\"}"))),
            @ApiResponse(responseCode = "401", description = "로그인이 필요한 기능입니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"status\":401,\"message\":\"로그인이 필요한 기능입니다.\"}"))),
            @ApiResponse(responseCode = "403", description = "북마크를 해제할 권한이 없습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"status\":403,\"message\":\"북마크를 해제할 권한이 없습니다.\"}"))),
            @ApiResponse(responseCode = "404", description = "북마크 정보를 찾을 수 없습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"status\":404,\"message\":\"북마크 정보를 찾을 수 없습니다.\"}"))),
            @ApiResponse(responseCode = "409", description = "북마크가 이미 해제된 항목입니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"status\":409,\"message\":\"북마크가 이미 해제된 항목입니다.\"}"))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류가 발생했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"status\":500,\"message\":\"서버 내부 오류가 발생했습니다.\"}")))
    })
    public ResponseEntity<DeleteResponse> deleteBookmark(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "북마크 ID", example = "1")
            @PathVariable Long bookmarkId
    ) {
        return ResponseEntity.ok(bookmarkService.deleteBookmark(userDetails.getUsername(), bookmarkId));
    }

    /**
     * 로그인 사용자의 북마크를 등록합니다.
     *
     * @param userDetails JWT 인증 사용자 정보
     * @param request 북마크 등록 요청
     * @return 북마크 등록 결과
     */
    @PostMapping
    @Operation(summary = "북마크 등록", description = "로그인 사용자의 북마크를 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "북마크 등록에 성공했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CreateResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "bookmarkId": 9001,
                                      "bookmarkType": "REGION",
                                      "bookmarkIdentifier": 101,
                                      "bookmarked": true
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"status\":400,\"message\":\"잘못된 요청입니다.\"}"))),
            @ApiResponse(responseCode = "401", description = "로그인이 필요한 기능입니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"status\":401,\"message\":\"로그인이 필요한 기능입니다.\"}"))),
            @ApiResponse(responseCode = "403", description = "북마크 등록을 할 권한이 없습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"status\":403,\"message\":\"북마크 등록을 할 권한이 없습니다.\"}"))),
            @ApiResponse(responseCode = "404", description = "북마크 할 대상을 찾을 수 없습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"status\":404,\"message\":\"북마크 할 대상을 찾을 수 없습니다.\"}"))),
            @ApiResponse(responseCode = "409", description = "이미 북마크 지정된 항목입니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"status\":409,\"message\":\"이미 북마크 지정된 항목입니다.\"}"))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류가 발생했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"status\":500,\"message\":\"서버 내부 오류가 발생했습니다.\"}")))
    })
    public ResponseEntity<CreateResponse> createBookmark(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateRequest request
    ) {
        return ResponseEntity.ok(bookmarkService.createBookmark(userDetails.getUsername(), request));
    }

    /**
     * 로그인 사용자의 북마크 목록을 타입별로 조회합니다.
     *
     * @param userDetails JWT 인증 사용자 정보
     * @param bookmarkType 북마크 타입 (EVENT, LANDMARK)
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return 타입에 맞는 북마크 목록
     */
    @GetMapping
    @Operation(summary = "북마크 목록 조회", description = "로그인 사용자의 북마크 목록을 타입별로 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "북마크 목록 조회에 성공했습니다.",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "EVENT", value = """
                                            {
                                              "page": 0,
                                              "size": 10,
                                              "totalElements": 4,
                                              "totalPages": 1,
                                              "bookmarks": [
                                                {
                                                  "bookmarkId": 9101,
                                                  "eventId": 201,
                                                  "eventTitle": "수원 문화 행사",
                                                  "eventImageUrl": "event-main.png",
                                                  "eventStart": "2026-07-01",
                                                  "eventEnd": "2026-07-07"
                                                }
                                              ]
                                            }
                                            """),
                                    @ExampleObject(name = "LANDMARK", value = """
                                            {
                                              "page": 0,
                                              "size": 10,
                                              "totalElements": 12,
                                              "totalPages": 2,
                                              "bookmarks": [
                                                {
                                                  "bookmarkId": 9011,
                                                  "landmarkId": 301,
                                                  "landmarkName": "수원화성",
                                                  "regionId": 101,
                                                  "regionName": "수원시",
                                                  "contentId": "TOUR-10001"
                                                }
                                              ]
                                            }
                                            """),
                                    @ExampleObject(name = "REGION", value = """
                                            {
                                              "page": 0,
                                              "size": 10,
                                              "totalElements": 3,
                                              "totalPages": 1,
                                              "bookmarks": [
                                                {
                                                  "bookmarkId": 9201,
                                                  "regionId": 101,
                                                  "regionName": "서울특별시 강남구"
                                                }
                                              ]
                                            }
                                            """)
                            })),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"status\":400,\"message\":\"잘못된 요청입니다.\"}"))),
            @ApiResponse(responseCode = "401", description = "로그인이 필요한 기능입니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"status\":401,\"message\":\"로그인이 필요한 기능입니다.\"}"))),
            @ApiResponse(responseCode = "404", description = "북마크 정보들을 찾을 수 없습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"status\":404,\"message\":\"북마크 정보들을 찾을 수 없습니다.\"}"))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류가 발생했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"status\":500,\"message\":\"서버 내부 오류가 발생했습니다.\"}")))
    })
    public ResponseEntity<Object> getBookmarks(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "북마크 타입 (EVENT, LANDMARK)", example = "EVENT")
            @RequestParam String bookmarkType,
            @Parameter(description = "페이지 번호(0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "10")
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(bookmarkService.getBookmarks(userDetails.getUsername(), bookmarkType, page, size));
    }
}
