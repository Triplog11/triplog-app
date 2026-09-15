package triplog.backend.review.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import triplog.backend.review.dto.request.ReviewRequest.CreateRequest;
import triplog.backend.review.dto.response.ReviewResponse.CreateReviewResponse;
import triplog.backend.review.entity.ReviewIdempotency;
import triplog.backend.review.exception.ReviewErrorCode;
import triplog.backend.review.exception.ReviewException;
import triplog.backend.review.repository.ReviewIdempotencyRepository;
import triplog.backend.stats.service.ActivityRewardInfo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/** {@link ReviewIdempotencyService}의 요청 선점과 최초 응답 재사용을 검증합니다. */
@ExtendWith(MockitoExtension.class)
class ReviewIdempotencyServiceTest {

    private static final String USERS_ID = "user-id";
    private static final String REQUEST_KEY = "review-request-1";

    @Mock
    private ReviewIdempotencyRepository reviewIdempotencyRepository;

    @Mock
    private ReviewIdempotency reviewIdempotency;

    private ReviewIdempotencyService reviewIdempotencyService;

    @BeforeEach
    void setUp() {
        reviewIdempotencyService = new ReviewIdempotencyService(reviewIdempotencyRepository);
    }

    @Test
    @DisplayName("동일 요청을 재전송하면 최초 처리 응답을 반환한다")
    void claim_ReturnsStoredResponseForSameRequest() {
        // Given
        CreateRequest request = request("방문 완료");
        CreateReviewResponse firstResponse = CreateReviewResponse.toDto(
                List.of(new ActivityRewardInfo(
                        null, "REVIEW_CREATE", "여행 기록 작성 보상", 15, 0
                )),
                15,
                0
        );
        given(reviewIdempotencyRepository.insertIfAbsent(
                anyString(), anyString(), anyString(), any(LocalDateTime.class)
        )).willReturn(1, 0);

        Optional<CreateReviewResponse> firstClaim = reviewIdempotencyService.claim(
                USERS_ID, REQUEST_KEY, request, null
        );
        ArgumentCaptor<String> fingerprintCaptor = ArgumentCaptor.forClass(String.class);
        verify(reviewIdempotencyRepository).insertIfAbsent(
                org.mockito.ArgumentMatchers.eq(USERS_ID),
                org.mockito.ArgumentMatchers.eq(REQUEST_KEY),
                fingerprintCaptor.capture(),
                any(LocalDateTime.class)
        );
        given(reviewIdempotencyRepository.findByRequestKeyForUpdate(
                USERS_ID, REQUEST_KEY
        )).willReturn(Optional.of(reviewIdempotency));
        given(reviewIdempotency.getRequestFingerprint())
                .willReturn(fingerprintCaptor.getValue());

        reviewIdempotencyService.complete(USERS_ID, REQUEST_KEY, firstResponse);
        ArgumentCaptor<String> responseCaptor = ArgumentCaptor.forClass(String.class);
        verify(reviewIdempotency).complete(
                responseCaptor.capture(), any(LocalDateTime.class)
        );
        given(reviewIdempotency.getResponsePayload()).willReturn(responseCaptor.getValue());

        // When
        Optional<CreateReviewResponse> replayedResponse = reviewIdempotencyService.claim(
                USERS_ID, REQUEST_KEY, request, null
        );

        // Then
        assertThat(firstClaim).isEmpty();
        assertThat(replayedResponse).isPresent();
        assertThat(replayedResponse.orElseThrow().getTotalXp()).isEqualTo(15);
        assertThat(replayedResponse.orElseThrow().getTotalScore()).isZero();
        assertThat(replayedResponse.orElseThrow().getRewards())
                .singleElement()
                .satisfies(reward -> {
                    assertThat(reward.policyId()).isEqualTo("REVIEW_CREATE");
                    assertThat(reward.description()).isEqualTo("여행 기록 작성 보상");
                    assertThat(reward.xp()).isEqualTo(15);
                });
    }

    @Test
    @DisplayName("동일한 멱등성 키를 다른 요청에 사용하면 충돌 예외를 던진다")
    void claim_RejectsDifferentRequestWithSameKey() {
        // Given
        given(reviewIdempotencyRepository.insertIfAbsent(
                anyString(), anyString(), anyString(), any(LocalDateTime.class)
        )).willReturn(0);
        given(reviewIdempotencyRepository.findByRequestKeyForUpdate(
                USERS_ID, REQUEST_KEY
        )).willReturn(Optional.of(reviewIdempotency));
        given(reviewIdempotency.getRequestFingerprint()).willReturn("different-fingerprint");

        // When & Then
        assertThatThrownBy(() -> reviewIdempotencyService.claim(
                USERS_ID, REQUEST_KEY, request("다른 요청"), null
        ))
                .isInstanceOf(ReviewException.class)
                .extracting(exception -> ((ReviewException) exception).getErrorCode())
                .isEqualTo(ReviewErrorCode.IDEMPOTENCY_KEY_CONFLICT);
    }

    private CreateRequest request(String reviewContent) {
        return new CreateRequest(
                1L, "41", "110", "수원화성 방문", reviewContent, 5.0F
        );
    }
}
