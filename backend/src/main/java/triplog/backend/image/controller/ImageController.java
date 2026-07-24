package triplog.backend.image.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import triplog.backend.common.exception.ErrorResponse;
import triplog.backend.image.dto.response.ImageResponse.ImageUploadResponse;
import triplog.backend.image.service.ImageService;

import java.util.Arrays;

/**
 * 이미지 업로드 API 요청을 처리하는 컨트롤러입니다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/images")
@Tag(name = "Image API", description = "이미지 업로드 API")
public class ImageController {

    /**
     * 이미지 업로드 비즈니스 로직을 처리하는 서비스입니다.
     */
    private final ImageService imageService;

    /**
     * 전달받은 한 개 이상의 이미지 파일을 Cloudinary에 업로드합니다.
     *
     * @param files 업로드할 이미지 파일 배열
     * @return 업로드된 이미지 URL 목록을 포함한 응답
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "이미지 업로드", description = "한 개 이상의 이미지를 Cloudinary에 업로드하고 이미지 URL 목록을 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "이미지 업로드에 성공했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ImageUploadResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 이미지 파일입니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"status\":400,\"message\":\"유효한 이미지 파일이 필요합니다.\"}"))),
            @ApiResponse(responseCode = "401", description = "로그인이 필요한 기능입니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"status\":401,\"message\":\"로그인이 필요한 기능입니다.\"}"))),
            @ApiResponse(responseCode = "500", description = "이미지 업로드에 실패했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"status\":500,\"message\":\"이미지 업로드에 실패했습니다.\"}")))
    })
    public ResponseEntity<ImageUploadResponse> upload(@RequestPart("files") MultipartFile[] files) {
        return ResponseEntity.ok(imageService.upload(Arrays.asList(files)));
    }
}
