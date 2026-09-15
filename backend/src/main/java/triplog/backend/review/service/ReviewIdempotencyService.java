package triplog.backend.review.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import triplog.backend.review.dto.request.ReviewRequest.CreateRequest;
import triplog.backend.review.dto.response.ReviewResponse.CreateReviewResponse;
import triplog.backend.review.entity.ReviewIdempotency;
import triplog.backend.review.exception.ReviewErrorCode;
import triplog.backend.review.exception.ReviewException;
import triplog.backend.review.repository.ReviewIdempotencyRepository;
import triplog.backend.stats.service.ActivityRewardInfo;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;

/**
 * 방문 인증 요청을 사용자와 멱등성 키 단위로 한 번만 처리하도록 관리합니다.
 * <p>
 * 멱등성의 범위는 보상 지급에 한정되지 않습니다. 리뷰 생성부터 이미지 저장, 방문 로그,
 * 지역 방문 횟수, 뱃지·칭호·미션 판정, 알림 생성까지 Facade에서 실행하는 전체 흐름을
 * 하나의 요청으로 취급합니다.
 */
@Service
@RequiredArgsConstructor
public class ReviewIdempotencyService {

    private final ReviewIdempotencyRepository reviewIdempotencyRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 요청 키를 원자적으로 선점하거나 이미 완료된 최초 응답을 조회합니다.
     * <p>
     * 반환값이 비어 있으면 현재 호출이 최초 요청의 처리 권한을 얻었다는 의미이므로 호출자는
     * 인증 흐름을 계속 실행해야 합니다. 응답이 있으면 정상적인 재시도이므로 호출자는 어떠한
     * 추후 효과도 다시 실행하지 않고 해당 응답을 즉시 반환해야 합니다.
     * 같은 키라도 요청 지문이 다르면 클라이언트의 키 재사용 오류로 판단하여 409 예외를 던집니다.
     *
     * @param usersId 요청 사용자 식별자
     * @param idempotencyKey 앞뒤 공백을 제거하고 검증한 멱등성 키
     * @param request 방문 인증 요청 DTO
     * @param files 요청에 포함된 이미지 파일 목록
     * @return 최초 요청이면 빈 값, 동일 요청의 재시도이면 저장된 최초 응답
     * @throws ReviewException 같은 키의 요청 내용이 다르거나 저장 응답을 읽을 수 없는 경우
     */
    @Transactional
    public Optional<CreateReviewResponse> claim(
            String usersId,
            String idempotencyKey,
            CreateRequest request,
            List<MultipartFile> files
    ) {
        String requestFingerprint = fingerprint(request, files);
        int inserted = reviewIdempotencyRepository.insertIfAbsent(
                usersId,
                idempotencyKey,
                requestFingerprint,
                LocalDateTime.now()
        );
        if (inserted == 1) {
            return Optional.empty();
        }

        ReviewIdempotency existingRequest = findRequest(usersId, idempotencyKey);
        if (!existingRequest.getRequestFingerprint().equals(requestFingerprint)) {
            throw new ReviewException(ReviewErrorCode.IDEMPOTENCY_KEY_CONFLICT);
        }
        if (existingRequest.getResponsePayload() == null) {
            throw new ReviewException(ReviewErrorCode.IDEMPOTENCY_PROCESSING_FAILED);
        }
        return Optional.of(readResponse(existingRequest.getResponsePayload()));
    }

    /**
     * 최초 처리 결과를 이후 재요청에서 그대로 반환할 수 있도록 저장합니다.
     * Facade 트랜잭션이 커밋되기 전에 호출되므로 응답 저장과 인증 관련 DB 변경은 함께
     * 성공하거나 함께 롤백됩니다.
     *
     * @param usersId 요청 사용자 식별자
     * @param idempotencyKey 완료 처리할 멱등성 키
     * @param response 최초 방문 인증 응답
     */
    @Transactional
    public void complete(
            String usersId,
            String idempotencyKey,
            CreateReviewResponse response
    ) {
        ReviewIdempotency request = findRequest(usersId, idempotencyKey);
        request.complete(writeResponse(response), LocalDateTime.now());
    }

    /** 멱등성 기록을 잠금 조회하고 비정상적인 기록 누락을 서버 오류로 변환합니다. */
    private ReviewIdempotency findRequest(String usersId, String idempotencyKey) {
        return reviewIdempotencyRepository.findByRequestKeyForUpdate(usersId, idempotencyKey)
                .orElseThrow(() -> new ReviewException(
                        ReviewErrorCode.IDEMPOTENCY_PROCESSING_FAILED
                ));
    }

    /**
     * 같은 키에 실제로 같은 요청이 들어왔는지 비교할 SHA-256 키를 생성합니다.
     * DTO의 모든 필드뿐 아니라 이미지 순서, 파일명, MIME 타입, 크기, 실제 바이트까지 포함하므로
     * 키만 같고 첨부 이미지나 여행 기록 내용이 다른 요청을 동일 재시도로 오해할 일이 적습니다.
     */
    private String fingerprint(CreateRequest request, List<MultipartFile> files) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            update(digest, request.getTourismContentId());
            update(digest, request.getLegalRegionCode());
            update(digest, request.getLegalDistrictCode());
            update(digest, request.getReviewTitle());
            update(digest, request.getReviewContent());
            update(digest, request.getReviewScore());

            List<MultipartFile> normalizedFiles = files == null ? List.of() : files;
            update(digest, normalizedFiles.size());
            for (MultipartFile file : normalizedFiles) {
                update(digest, file == null ? null : file.getOriginalFilename());
                update(digest, file == null ? null : file.getContentType());
                update(digest, file == null ? null : file.getSize());
                update(digest, file == null ? null : file.getBytes());
            }
            return HexFormat.of().formatHex(digest.digest());
        } catch (NoSuchAlgorithmException | IOException exception) {
            throw new ReviewException(ReviewErrorCode.IDEMPOTENCY_PROCESSING_FAILED);
        }
    }

    /**
     * 값의 길이와 내용을 함께 해시에 반영합니다.
     * 길이 접두사를 사용해 {@code ["ab", "c"]}와 {@code ["a", "bc"]} 같은 조합이
     * 동일한 바이트 나열로 합쳐지는 모호성을 방지하고 null은 길이 -1로 구분합니다.
     */
    private void update(MessageDigest digest, Object value) {
        if (value == null) {
            digest.update(ByteBuffer.allocate(Integer.BYTES).putInt(-1).array());
            return;
        }
        byte[] bytes = value instanceof byte[] byteValue
                ? byteValue
                : value.toString().getBytes(StandardCharsets.UTF_8);
        digest.update(ByteBuffer.allocate(Integer.BYTES).putInt(bytes.length).array());
        digest.update(bytes);
    }

    /** 공개 응답에서 재전송에 필요한 필드만 저장용 JSON으로 직렬화합니다. */
    private String writeResponse(CreateReviewResponse response) {
        StoredResponse storedResponse = StoredResponse.from(response);
        try {
            return objectMapper.writeValueAsString(storedResponse);
        } catch (JsonProcessingException exception) {
            throw new ReviewException(ReviewErrorCode.IDEMPOTENCY_PROCESSING_FAILED);
        }
    }

    /** 저장된 최초 응답 JSON을 현재 API 응답 DTO로 복원합니다. */
    private CreateReviewResponse readResponse(String responsePayload) {
        try {
            return objectMapper.readValue(responsePayload, StoredResponse.class).toResponse();
        } catch (JsonProcessingException exception) {
            throw new ReviewException(ReviewErrorCode.IDEMPOTENCY_PROCESSING_FAILED);
        }
    }

    /** 이벤트 내부 키를 제외하고 클라이언트에 노출되는 응답 값만 보관하는 저장 모델입니다. (내부 모델은 서비스 내에서 처리하므로 dto 로 처리 안 했습니다.)*/
    private record StoredResponse(
            List<StoredReward> rewards,
            int totalXp,
            int totalScore
    ) {
        private static StoredResponse from(CreateReviewResponse response) {
            List<StoredReward> rewards = response.getRewards().stream()
                    .map(StoredReward::from)
                    .toList();
            return new StoredResponse(rewards, response.getTotalXp(), response.getTotalScore());
        }

        private CreateReviewResponse toResponse() {
            return CreateReviewResponse.toDto(
                    rewards.stream().map(StoredReward::toReward).toList(),
                    totalXp,
                    totalScore
            );
        }
    }

    /** 재요청 응답에 필요한 단일 보상 표시 정보를 보관하는 저장 모델입니다. */
    private record StoredReward(String policyId, String description, int xp, int score) {
        private static StoredReward from(ActivityRewardInfo reward) {
            return new StoredReward(
                    reward.policyId(), reward.description(), reward.xp(), reward.score()
            );
        }

        private ActivityRewardInfo toReward() {
            return new ActivityRewardInfo(null, policyId, description, xp, score);
        }
    }
}
