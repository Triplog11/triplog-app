package triplog.backend.users.controller;

import io.swagger.v3.oas.annotations.Operation;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import triplog.backend.common.exception.ErrorResponse;
import triplog.backend.users.dto.request.UsersRequest.EmailCheckRequest;
import triplog.backend.users.dto.request.UsersRequest.NicknameCheckRequest;
import triplog.backend.users.dto.response.UsersResponse.EmailCheckResponse;
import triplog.backend.users.dto.response.UsersResponse.NicknameCheckResponse;
import triplog.backend.users.service.UsersService;

/**
 * 사용자(User)와 관련된 API 요청을 처리하는 Controller입니다.
 * <p>
 * 사용자 정보 조회 및 수정 등 사용자 도메인과 관련된 HTTP 요청을 처리합니다.
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "Users API", description = "유저 API")
@RequestMapping("/users")
@Slf4j
public class UsersController {

    private final UsersService usersService;

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
}

