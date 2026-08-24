package triplog.backend.users.controller;

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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import triplog.backend.common.exception.ErrorResponse;
import triplog.backend.users.dto.request.UsersRequest.EmailCheckRequest;
import triplog.backend.users.dto.request.UsersRequest.NicknameCheckRequest;
import triplog.backend.users.dto.request.UsersRequest.ProfileUpdateRequest;
import triplog.backend.users.dto.response.UsersResponse.EmailCheckResponse;
import triplog.backend.users.dto.response.UsersResponse.NicknameCheckResponse;
import triplog.backend.users.dto.response.UsersResponse.MyPageInfoResponse;
import triplog.backend.users.dto.response.UsersResponse.ProfileUpdateResponse;
import triplog.backend.users.service.MyPageFacadeService;
import triplog.backend.users.service.UsersService;

/**
 * 사용자(User)와 관련된 API 요청을 처리하는 Controller입니다.
 * <p>
 * 사용자 정보 조회 및 수정, 사용자 닉네임과 관련된 HTTP 요청을 처리합니다.
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "Users API", description = "유저 API")
@RequestMapping("/users")
@Slf4j
public class UsersController {

    private final UsersService usersService;
    private final MyPageFacadeService myPageFacadeService;

    /**
     * 로그인 사용자의 마이페이지 정보를 조회합니다.
     *
     * @param userDetails JWT 인증 사용자 정보
     * @return 기본 프로필 및 활동 요약 정보
     */
    @GetMapping("/mypage")
    @Operation(summary = "마이페이지 정보 조회", description = "내 기본 프로필 및 활동 요약 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "마이페이지 정보 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MyPageInfoResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "nickname": "여행자",
                                      "profileUrl": "https://example.com/profile.png",
                                      "level": 3,
                                      "xp": 240,
                                      "tier": "SILVER",
                                      "overallScore": 1250,
                                      "monthScore": 220,
                                      "totalCertificationCount": 12,
                                      "visitedRegionCount": 5,
                                      "acquiredBadgeCount": 4,
                                      "collectedCardCount": 8,
                                      "representativeAppellation": {
                                        "appellationId": 2,
                                        "appellationName": "랜드마크 탐험가"
                                      },
                                      "representativeBadge": {
                                        "badgeId": 1,
                                        "badgeName": "첫 발자국",
                                        "badgeUrl": "https://cdn.triplog.com/badges/first-step.png"
                                      }
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "로그인 필요",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자 또는 통계 정보를 찾을 수 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<MyPageInfoResponse> getMyPageInfo(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(myPageFacadeService.getMyPageInfo(userDetails.getUsername()));
    }

    /**
     * 닉네임 중복 확인 요청을 처리합니다.
     *
     * @param request 닉네임 중복 확인 요청 DTO
     * @return 닉네임 사용 가능 여부와 결과 메시지
     */
    @Operation(
            summary = "닉네임 중복 확인",
            description = "사용자가 입력한 닉네임이 이미 사용 중인지 확인합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "닉네임 중복 확인 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = NicknameCheckResponse.class),
                            examples = {
                                    @ExampleObject(name = "사용 가능", value = "{\"available\":true,\"message\":\"사용 가능한 닉네임입니다.\"}"),
                                    @ExampleObject(name = "사용 불가", value = "{\"available\":false,\"message\":\"중복된 닉네임입니다.\"}")
                            })),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(name = "닉네임 누락", value = "{\"status\":400,\"message\":\"닉네임은 필수입니다.\"}"),
                                    @ExampleObject(name = "닉네임 길이 오류", value = "{\"status\":400,\"message\":\"닉네임은 2자 이상 12자 이하로 입력해야 합니다.\"}")
                            })),
            @ApiResponse(responseCode = "429", description = "요청을 너무 많이 보냈습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"status\":429,\"message\":\"요청을 너무 많이 보냈습니다.\"}"))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류가 발생했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"status\":500,\"message\":\"서버 내부 오류가 발생했습니다.\"}")))
    })
    @PostMapping("/nickname/check")
    public ResponseEntity<NicknameCheckResponse> checkNickname(
            @Valid @RequestBody NicknameCheckRequest request
    ) {
        log.info("닉네임 중복 확인 요청 수신: nickName={}", request.getNickName());
        return ResponseEntity.ok(usersService.checkNickname(request.getNickName()));
    }

    /**
     * 이메일 중복 확인 요청을 처리합니다.
     *
     * @param request 이메일 중복 확인 요청 DTO
     * @return 이메일 사용 가능 여부와 결과 메시지
     */
    @Operation(
            summary = "이메일 중복 확인",
            description = "사용자가 입력한 이메일이 이미 사용 중인지 확인합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이메일 중복 확인 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = EmailCheckResponse.class),
                            examples = {
                                    @ExampleObject(name = "사용 가능", value = "{\"available\":true,\"message\":\"사용 가능한 이메일입니다.\"}"),
                                    @ExampleObject(name = "사용 불가", value = "{\"available\":false,\"message\":\"중복된 이메일입니다.\"}")
                            })),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(name = "이메일 누락", value = "{\"status\":400,\"message\":\"이메일은 필수입니다.\"}"),
                                    @ExampleObject(name = "이메일 형식 오류", value = "{\"status\":400,\"message\":\"이메일 형식이 올바르지 않습니다.\"}")
                            })),
            @ApiResponse(responseCode = "429", description = "요청을 너무 많이 보냈습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"status\":429,\"message\":\"요청을 너무 많이 보냈습니다.\"}"))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류가 발생했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"status\":500,\"message\":\"서버 내부 오류가 발생했습니다.\"}")))
    })
    @PostMapping("/email/check")
    public ResponseEntity<EmailCheckResponse> checkEmail(
            @Valid @RequestBody EmailCheckRequest request
    ) {
        log.info("이메일 중복 확인 요청 수신: email={}", request.getEmail());
        return ResponseEntity.ok(usersService.checkEmail(request.getEmail()));
    }

    /**
     * 로그인한 사용자의 프로필 수정 요청을 처리합니다.
     *
     * @param userDetails JWT 인증 사용자 정보
     * @param request 프로필 수정 요청 DTO
     * @return 수정된 사용자 프로필 정보
     */
    @Operation(
            summary = "프로필 수정",
            description = "로그인한 사용자의 닉네임, 주소, 프로필 이미지 정보를 수정합니다. 요청에서 전달되지 않은 필드는 기존 값을 유지합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "프로필 수정에 성공했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProfileUpdateResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "usersId": "550e8400-e29b-41d4-a716-446655440000",
                                      "nickname": "여행자",
                                      "addressSi": "수원시",
                                      "addressDoGun": "경기도",
                                      "addressGu": "팔달구",
                                      "profileUrl": "https://example.com/profile.png"
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(name = "닉네임 길이 오류", value = "{\"status\":400,\"message\":\"닉네임은 2자 이상 12자 이하로 입력해야 합니다.\"}"),
                                    @ExampleObject(name = "시 길이 오류", value = "{\"status\":400,\"message\":\"시는 1자 이상 20자 이하로 입력해야 합니다.\"}"),
                                    @ExampleObject(name = "도/군 길이 오류", value = "{\"status\":400,\"message\":\"도/군은 1자 이상 20자 이하로 입력해야 합니다.\"}"),
                                    @ExampleObject(name = "구 길이 오류", value = "{\"status\":400,\"message\":\"구는 1자 이상 30자 이하로 입력해야 합니다.\"}"),
                                    @ExampleObject(name = "프로필 이미지 URL 길이 오류", value = "{\"status\":400,\"message\":\"프로필 이미지 URL은 1자 이상 2048자 이하로 입력해야 합니다.\"}")
                            })),
            @ApiResponse(responseCode = "401", description = "로그인이 필요한 기능입니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"status\":401,\"message\":\"로그인이 필요한 기능입니다.\"}"))),
            @ApiResponse(responseCode = "409", description = "이미 사용 중인 닉네임입니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"status\":409,\"message\":\"이미 사용 중인 닉네임입니다.\"}"))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류가 발생했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"status\":500,\"message\":\"서버 내부 오류가 발생했습니다.\"}")))
    })
    @PatchMapping("/profile")
    public ResponseEntity<ProfileUpdateResponse> updateProfile(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ProfileUpdateRequest request
    ) {
        log.info("프로필 수정 요청 수신: usersId={}", userDetails.getUsername());
        return ResponseEntity.ok(usersService.updateProfile(userDetails.getUsername(), request));
    }
}
