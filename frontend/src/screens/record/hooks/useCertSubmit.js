import { useState, useRef, useCallback } from 'react';
import { submitReview, createIdempotencyKey } from '../../../api/reviews';
import { getTourismContentId } from '../../../utils/landmarkIds';

/**
 * 방문 인증 전송 상태 관리.
 * 인증 시도 1건당 멱등 키 1개를 발급해 두고, 실패 후 재시도에는 같은 키를 재사용한다.
 * (네트워크 오류로 서버에는 이미 반영됐어도 중복 등록되지 않는다)
 */
export default function useCertSubmit() {
  const [submitting, setSubmitting] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');
  const [result, setResult] = useState(null);
  const idempotencyKeyRef = useRef(null);

  const reset = useCallback(() => {
    idempotencyKeyRef.current = null;
    setSubmitting(false);
    setErrorMessage('');
    setResult(null);
  }, []);

  /**
   * @param landmark {landmarkId, tourismContentId?, legalRegionCode, legalDistrictCode}
   * @param review {reviewTitle, reviewContent, reviewScore}
   * @returns 성공 시 응답, 실패 시 null (메시지는 errorMessage로 노출)
   */
  const submit = useCallback(async (landmark, review) => {
    if (!idempotencyKeyRef.current) {
      idempotencyKeyRef.current = createIdempotencyKey();
    }
    setSubmitting(true);
    setErrorMessage('');
    try {
      const response = await submitReview(
        {
          tourismContentId: getTourismContentId(landmark),
          legalRegionCode: landmark.legalRegionCode,
          legalDistrictCode: landmark.legalDistrictCode,
          ...review,
        },
        [],
        { idempotencyKey: idempotencyKeyRef.current },
      );
      setResult(response);
      return response;
    } catch (error) {
      console.error('방문 인증 전송 실패:', error);
      // 400/404/409는 서버 메시지를 그대로, 네트워크(0)/5xx는 client.js의 공통 메시지를 노출
      setErrorMessage(error?.message ?? '인증을 전송하지 못했어요. 잠시 후 다시 시도해 주세요.');
      return null;
    } finally {
      setSubmitting(false);
    }
  }, []);

  return { submit, submitting, errorMessage, result, reset };
}
