package triplog.backend.common.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import triplog.backend.bookmark.exception.BookmarkException;
import triplog.backend.common.auth.exception.AuthException;
import triplog.backend.badge.exception.BadgeException;
import triplog.backend.common.exception.CommonErrorCode;
import triplog.backend.common.exception.ErrorResponse;
import triplog.backend.fcmtoken.exception.FcmTokenException;
import triplog.backend.event.exception.EventException;
import triplog.backend.image.exception.ImageException;
import triplog.backend.landmark.exception.LandmarkException;
import triplog.backend.mission.exception.MissionException;
import triplog.backend.notification.exception.NotificationException;
import triplog.backend.rankpolicy.exception.RankPolicyException;
import triplog.backend.region.exception.RegionException;
import triplog.backend.review.exception.ReviewException;
import triplog.backend.stats.exception.StatsException;
import triplog.backend.users.exception.UsersException;

import static triplog.backend.common.exception.ErrorResponse.toResponseEntity;

/**
 * 전역(Global)에서 발생하는 예외를 중앙에서 처리하는 핸들러 클래스입니다.
 * <p>
 * 도메인 비즈니스 예외, 요청 데이터 검증 예외, 잘못된 요청 예외를
 * 공통 에러 응답 형식인 {@link ErrorResponse}로 변환하여 응답합니다.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 이벤트 도메인 비즈니스 예외를 공통 오류 응답으로 변환합니다.
     *
     * @param e 이벤트 도메인에서 발생한 예외
     * @return 이벤트 오류 코드에 해당하는 HTTP 상태와 메시지를 포함한 응답
     */
    @ExceptionHandler(EventException.class)
    protected ResponseEntity<ErrorResponse> handleEventException(EventException e) {
        log.warn("이벤트 도메인 예외 발생: {}", e.getErrorCode());
        return toResponseEntity(e.getErrorCode());
    }

    /**
     * 배지(Badge) 도메인 비즈니스 로직에서 발생하는 {@link BadgeException}을 처리합니다.
     *
     * @param e 배지 도메인에서 발생한 예외
     * @return 배지 에러 코드에 해당하는 HTTP 상태 코드와 에러 메시지를 포함한 응답
     */
    @ExceptionHandler(BadgeException.class)
    protected ResponseEntity<ErrorResponse> handleBadgeException(BadgeException e) {
        log.warn("배지 도메인 예외 발생: {}", e.getErrorCode());
        return toResponseEntity(e.getErrorCode());
    }

    /**
     * 북마크 처리 중 발생한 {@link BookmarkException}을 공통 오류 응답으로 변환합니다.
     *
     * @param e 북마크 도메인에서 발생한 예외
     * @return 북마크 오류 코드에 해당하는 HTTP 상태와 메시지를 포함한 응답
     */
    @ExceptionHandler(BookmarkException.class)
    protected ResponseEntity<ErrorResponse> handleBookmarkException(BookmarkException e) {
        log.warn("북마크 도메인 예외 발생: {}", e.getErrorCode());
        return toResponseEntity(e.getErrorCode());
    }

    /**
     * 사용자(Users) 도메인 비즈니스 로직에서 발생하는 {@link UsersException}을 처리합니다.
     *
     * @param e 사용자 도메인에서 발생한 예외
     * @return 사용자 에러 코드에 해당하는 HTTP 상태 코드와 에러 메시지를 포함한 응답
     */
    @ExceptionHandler(UsersException.class)
    protected ResponseEntity<ErrorResponse> handleUsersException(UsersException e) {
        log.warn("사용자 도메인 예외 발생: {}", e.getErrorCode());
        return toResponseEntity(e.getErrorCode());
    }

    /**
     * 인증(Auth) 도메인 비즈니스 로직에서 발생하는 {@link AuthException}을 처리합니다.
     * @param e 인증 도메인에서 발생한 예외
     * @return 인증 에러 코드에 해당하는 HTTP 상태 코드와 에러 메시지를 포함한 응답
     */
    @ExceptionHandler(AuthException.class)
    protected ResponseEntity<ErrorResponse> handleAuthException(AuthException e) {
        log.warn("인증 도메인 예외 발생: {}", e.getErrorCode());
        return toResponseEntity(e.getErrorCode());
    }

    /**
     * 사용자 통계(Stats) 도메인 비즈니스 로직에서 발생하는
     * {@link StatsException}을 처리합니다.
     *
     * @param e 사용자 통계 도메인에서 발생한 예외
     * @return 사용자 통계 에러 코드에 해당하는 HTTP 상태 코드와
     * 에러 메시지를 포함한 응답
     */
    @ExceptionHandler(StatsException.class)
    protected ResponseEntity<ErrorResponse> handleStatsException(StatsException e) {
        log.warn("사용자 통계 도메인 예외 발생: {}", e.getErrorCode());
        return toResponseEntity(e.getErrorCode());
    }

    /**
     * FCM 푸시 토큰 도메인 비즈니스 로직에서 발생하는 {@link FcmTokenException}을 처리합니다.
     *
     * @param e FCM 푸시 토큰 도메인에서 발생한 예외
     * @return FCM 푸시 토큰 에러 코드에 해당하는 HTTP 상태 코드와 에러 메시지를 포함한 응답
     */
    @ExceptionHandler(FcmTokenException.class)
    protected ResponseEntity<ErrorResponse> handleFcmTokenException(FcmTokenException e) {
        log.warn("FCM 푸시 토큰 도메인 예외 발생: {}", e.getErrorCode());
        return toResponseEntity(e.getErrorCode());
    }

    /**
     * 이미지 업로드 처리 중 발생한 {@link ImageException}을 공통 오류 응답으로 변환합니다.
     *
     * @param e 이미지 도메인에서 발생한 예외
     * @return 이미지 오류 코드에 해당하는 HTTP 상태와 메시지를 포함한 응답
     */
    @ExceptionHandler(ImageException.class)
    protected ResponseEntity<ErrorResponse> handleImageException(ImageException e) {
        log.warn("이미지 도메인 예외 발생: {}", e.getErrorCode());
        return toResponseEntity(e.getErrorCode());
    }

    /**
     * 알림 처리 중 발생한 {@link NotificationException}을 공통 오류 응답으로 변환합니다.
     *
     * @param e 알림 도메인에서 발생한 예외
     * @return 알림 오류 코드에 해당하는 HTTP 상태와 메시지를 포함한 응답
     */
    @ExceptionHandler(NotificationException.class)
    protected ResponseEntity<ErrorResponse> handleNotificationException(NotificationException e) {
        log.warn("알림 도메인 예외 발생: {}", e.getErrorCode());
        return toResponseEntity(e.getErrorCode());
    }

    /**
     * 랭크 정책 처리 중 발생한 {@link RankPolicyException}을 공통 오류 응답으로 변환합니다.
     *
     * @param e 랭크 정책 도메인에서 발생한 예외
     * @return 랭크 정책 오류 코드에 해당하는 HTTP 상태와 메시지를 포함한 응답
     */
    @ExceptionHandler(RankPolicyException.class)
    protected ResponseEntity<ErrorResponse> handleRankPolicyException(RankPolicyException e) {
        log.warn("랭크 정책 도메인 예외 발생: {}", e.getErrorCode());
        return toResponseEntity(e.getErrorCode());
    }

    /**
     * 미션 처리 중 발생한 {@link MissionException}을 공통 오류 응답으로 변환합니다.
     *
     * @param e 미션 도메인에서 발생한 예외
     * @return 미션 오류 코드에 해당하는 HTTP 상태와 메시지를 포함한 응답
     */
    @ExceptionHandler(MissionException.class)
    protected ResponseEntity<ErrorResponse> handleMissionException(MissionException e) {
        log.warn("미션 도메인 예외 발생: {}", e.getErrorCode());
        return toResponseEntity(e.getErrorCode());
    }

    /**
     * 지역(Region) 도메인 비즈니스 로직에서 발생하는 {@link RegionException}을 처리합니다.
     *
     * @param e 지역 도메인에서 발생한 예외
     * @return 지역 에러 코드에 해당하는 HTTP 상태 코드와 에러 메시지를 포함한 응답
     */
    @ExceptionHandler(RegionException.class)
    protected ResponseEntity<ErrorResponse> handleRegionException(RegionException e) {
        log.warn("지역 도메인 예외 발생: {}", e.getErrorCode());
        return toResponseEntity(e.getErrorCode());
    }

    /**
     * 랜드마크(Landmark) 도메인 비즈니스 로직에서 발생하는 {@link LandmarkException}을 처리합니다.
     *
     * @param e 랜드마크 도메인에서 발생한 예외
     * @return 랜드마크 에러 코드에 해당하는 HTTP 상태 코드와 에러 메시지를 포함한 응답
     */
    @ExceptionHandler(LandmarkException.class)
    protected ResponseEntity<ErrorResponse> handleLandmarkException(LandmarkException e) {
        log.warn("랜드마크 도메인 예외 발생: {}", e.getErrorCode());
        return toResponseEntity(e.getErrorCode());
    }

    /**
     * 리뷰(Review) 도메인 비즈니스 로직에서 발생하는 {@link ReviewException}을 처리합니다.
     *
     * @param e 리뷰 도메인에서 발생한 예외
     * @return 리뷰 에러 코드에 해당하는 HTTP 상태 코드와 에러 메시지를 포함한 응답
     */
    @ExceptionHandler(ReviewException.class)
    protected ResponseEntity<ErrorResponse> handleReviewException(ReviewException e) {
        log.warn("리뷰 도메인 예외 발생: {}", e.getErrorCode());
        return toResponseEntity(e.getErrorCode());
    }

    /**
     * {@code @Valid} 어노테이션을 통한 요청 데이터 유효성 검증 실패 예외를 처리합니다.
     * <p>
     * 검증 실패 필드의 기본 메시지를 우선 사용하고, 존재하지 않는 경우 공통 잘못된 요청 메시지를 사용합니다.
     *
     * @param e 요청 데이터 유효성 검증 실패 예외
     * @return 400 잘못된 요청 상태 코드와 검증 실패 메시지를 포함한 응답
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        FieldError fieldError = bindingResult.getFieldError();
        String errorMessage = fieldError != null ? fieldError.getDefaultMessage() : CommonErrorCode.INVALID_PARAMETER.getMessage();

        log.warn("요청 데이터 검증 실패: {}", errorMessage);
        return toResponseEntity(CommonErrorCode.INVALID_PARAMETER, errorMessage);
    }

    /**
     * 요청 파라미터 타입 불일치, JSON 파싱 오류, 필수 파라미터 누락 등
     * 잘못된 요청 관련 예외를 처리합니다.
     *
     * @param e 잘못된 요청으로 인해 발생한 예외
     * @return 400 잘못된 요청 상태 코드와 공통 에러 메시지를 포함한 응답
     */
    @ExceptionHandler({
            MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class,
            MissingServletRequestParameterException.class,
            MissingServletRequestPartException.class
    })
    protected ResponseEntity<ErrorResponse> handleBadRequestException(Exception e) {
        log.warn("잘못된 요청 예외 발생: {}", e.getMessage());
        return toResponseEntity(CommonErrorCode.INVALID_PARAMETER);
    }

    /**
     * 별도로 처리되지 않은 모든 예외를 처리합니다.
     * <p>
     * 예상하지 못한 서버 내부 오류를 공통 500 응답으로 변환합니다.
     *
     * @param e 처리되지 않은 예외
     * @return 500 서버 내부 오류 상태 코드와 공통 에러 메시지를 포함한 응답
     */
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("서버 내부 오류 발생: {}", e.getMessage(), e);
        return toResponseEntity(CommonErrorCode.INTERNAL_SERVER_ERROR);
    }
}
