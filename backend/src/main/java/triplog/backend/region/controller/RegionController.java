package triplog.backend.region.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import triplog.backend.common.exception.ErrorResponse;
import triplog.backend.region.dto.response.RegionResponse.NationwideMapResponse;
import triplog.backend.region.dto.response.RegionResponse.ProvinceMapResponse;
import triplog.backend.region.dto.response.RegionResponse.RegionDetailResponse;
import triplog.backend.region.dto.response.RegionResponse.RegionListResponse;
import triplog.backend.region.service.RegionService;

/**
 * 지역(Region) 관련 API 요청을 처리하는 REST Controller입니다.
 */
@RestController
@RequestMapping("/regions")
@RequiredArgsConstructor
@Tag(name = "Region API", description = "지역 API")
public class RegionController {

    private final RegionService regionService;

    /**
     * 지역 목록을 페이징하여 조회합니다.
     *
     * @param userDetails JWT 인증 사용자 정보
     * @param page        페이지 번호
     * @param size        페이지 크기
     * @return 지역 목록 응답
     */
    @GetMapping
    @Operation(summary = "지역 목록 조회", description = "지역 목록을 페이지 단위로 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "지역 목록 조회에 성공했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RegionListResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "page": 0,
                                      "size": 10,
                                      "totalElements": 229,
                                      "totalPages": 23,
                                      "regions": [
                                        {
                                          "regionId": 101,
                                          "regionName": "수원시",
                                          "regionOverview": "역사와 문화가 있는 지역",
                                          "regionZipcode": "41110",
                                          "visited": false
                                        }
                                      ]
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
            @ApiResponse(responseCode = "404", description = "지역 정보들을 찾을 수 없습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"status\":404,\"message\":\"지역 정보들을 찾을 수 없습니다.\"}"))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류가 발생했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"status\":500,\"message\":\"서버 내부 오류가 발생했습니다.\"}")))
    })
    public ResponseEntity<RegionListResponse> getRegionList(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "페이지 번호(0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "10")
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(regionService.getRegionList(userDetails.getUsername(), page, size));
    }

    /**
     * 전국 지도 현황을 조회합니다.
     *
     * @param userDetails JWT 인증 사용자 정보
     * @return 전국 지도 현황 응답
     */
    @GetMapping("/nationwide/map")
    @Operation(summary = "전국 지도 현황 조회", description = "로그인 사용자의 전국 지도 현황을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "전국 지도 현황 조회에 성공했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = NationwideMapResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "totalRegionCount": 229,
                                      "completedRegionCount": 12,
                                      "visitedRegionCount": 18,
                                      "overallCompletionRate": 5.24,
                                      "regions": [
                                        {
                                          "regionId": 101,
                                          "regionName": "수원시",
                                          "regionZipcode": "41110",
                                          "provinceCode": "41",
                                          "visited": true,
                                          "completed": true,
                                          "completionRate": 100.0
                                        }
                                      ]
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
            @ApiResponse(responseCode = "404", description = "전국 지도 현황 정보를 찾을 수 없습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"status\":404,\"message\":\"전국 지도 현황 정보를 찾을 수 없습니다.\"}"))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류가 발생했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"status\":500,\"message\":\"서버 내부 오류가 발생했습니다.\"}")))
    })
    public ResponseEntity<NationwideMapResponse> getNationwideMap(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(regionService.getNationwideMap(userDetails.getUsername()));
    }

    /**
     * 광역 지도 현황을 조회합니다.
     *
     * @param userDetails  JWT 인증 사용자 정보
     * @param provinceCode 광역 코드
     * @return 광역 지도 현황 응답
     */
    @GetMapping("/provinces/{provinceCode}/map")
    @Operation(summary = "광역 지도 현황 조회", description = "로그인 사용자의 특정 광역 지도 현황을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "광역 지도 현황 조회에 성공했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProvinceMapResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "totalRegionCount": 31,
                                      "completedRegionCount": 4,
                                      "visitedRegionCount": 7,
                                      "provinceCompletionRate": 12.9,
                                      "regions": [
                                        {
                                          "regionId": 101,
                                          "regionName": "수원시",
                                          "regionZipcode": "41110",
                                          "visited": true,
                                          "completed": false,
                                          "completionRate": 70.0
                                        }
                                      ]
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
            @ApiResponse(responseCode = "404", description = "광역 지도 현황 정보를 찾을 수 없습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"status\":404,\"message\":\"광역 지도 현황 정보를 찾을 수 없습니다.\"}"))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류가 발생했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"status\":500,\"message\":\"서버 내부 오류가 발생했습니다.\"}")))
    })
    public ResponseEntity<ProvinceMapResponse> getProvinceMap(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "광역 코드 (두 글자)", required = true, example = "41")
            @PathVariable String provinceCode
    ) {
        return ResponseEntity.ok(regionService.getProvinceMap(userDetails.getUsername(), provinceCode));
    }

    /**
     * 지역 상세 정보를 조회합니다.
     *
     * @param userDetails JWT 인증 사용자 정보
     * @param regionId    지역 ID
     * @return 지역 상세 응답
     */
    @GetMapping("/{regionId}")
    @Operation(summary = "지역 상세 조회", description = "로그인 사용자의 특정 지역 상세 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "지역 상세 조회에 성공했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RegionDetailResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "regionId": 101,
                                      "regionName": "수원시",
                                      "regionOverview": "역사와 문화가 있는 지역",
                                      "regionZipcode": "41110",
                                      "visited": true,
                                      "visitedCount": 3,
                                      "landmarks": [
                                        {
                                          "landmarkId": 301,
                                          "landmarkName": "수원화성",
                                          "contentId": "TOUR-10001",
                                          "landmarkZipcode": "41110",
                                          "acquired": false
                                        }
                                      ]
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
            @ApiResponse(responseCode = "404", description = "지역 상세 정보를 찾을 수 없습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"status\":404,\"message\":\"지역 상세 정보를 찾을 수 없습니다.\"}"))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류가 발생했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"status\":500,\"message\":\"서버 내부 오류가 발생했습니다.\"}")))
    })
    public ResponseEntity<RegionDetailResponse> getRegionDetail(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "지역 ID", required = true, example = "101")
            @PathVariable Long regionId
    ) {
        return ResponseEntity.ok(regionService.getRegionDetail(userDetails.getUsername(), regionId));
    }
}
