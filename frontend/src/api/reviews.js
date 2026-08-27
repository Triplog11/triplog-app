import * as Crypto from 'expo-crypto';
import { authedRequest, authedMultipartRequest } from './client';

/**
 * 로컬 이미지 uri를 RN FormData 파일 파트로 변환한다.
 */
function toFilePart(uri, index) {
  const match = /\.(\w+)$/.exec(uri);
  const ext = (match?.[1] ?? 'jpg').toLowerCase();
  const mime = ext === 'png' ? 'image/png' : 'image/jpeg';
  return { uri, name: `review_${index}.${ext}`, type: mime };
}

/**
 * 방문 인증(여행 기록) 등록 — multipart/form-data.
 * 백엔드는 `request`(JSON) 파트 + `files`(이미지, 선택) 파트와
 * 중복 제출 방지용 `Idempotency-Key` 헤더(필수)를 받는다.
 *
 * @param {{tourismContentId: number, legalRegionCode: string, legalDistrictCode: string,
 *          reviewTitle?: string, reviewContent?: string, reviewScore?: number}} review
 *        reviewScore는 1.0~5.0, 소수점 첫째 자리까지
 * @param {string[]} [imageUris] 첨부 이미지 로컬 uri 배열
 * @param {{idempotencyKey?: string}} [opts] 재시도 시 같은 키를 넘기면 중복 등록되지 않는다
 * @returns {isVerified: boolean, rewards: [{policyId, description, xp, score}],
 *           totalXp: number, totalScore: number}
 */
export function submitReview(review, imageUris = [], { idempotencyKey } = {}) {
  const formData = new FormData();
  // RN FormData는 문자열 파트에 Content-Type을 붙이지 않아 서버(@RequestPart)가
  // application/octet-stream으로 거부한다 → {string, type} 형태로 JSON 타입을 명시한다.
  formData.append('request', { string: JSON.stringify(review), type: 'application/json' });
  imageUris.forEach((uri, index) => {
    formData.append('files', toFilePart(uri, index));
  });
  return authedMultipartRequest('/reviews', formData, {
    headers: { 'Idempotency-Key': idempotencyKey ?? createIdempotencyKey() },
  });
}

/** 방문 인증 1건당 1개 발급해 재시도에 재사용하는 멱등 키 */
export function createIdempotencyKey() {
  return Crypto.randomUUID();
}

/**
 * 내 여행 기록(인증 내역) 목록 — 최신순 페이징.
 * @returns {page, size, totalElements, totalPages,
 *           items: [{reviewId, tourismContentId, contentTitle, reviewTitle, regionId,
 *                    regionName, imageUrl, acquiredXp, acquiredScore, createdAt}]}
 */
export function fetchMyReviews({ page = 0, size = 20 } = {}) {
  const params = new URLSearchParams({ page: String(page), size: String(size) });
  return authedRequest(`/reviews?${params.toString()}`);
}

/**
 * 여행 기록 상세.
 * @returns {reviewId, landmarkId, landmarkName, regionId, regionName, imageUrl,
 *           acquiredXp, acquiredScore, createdAt}
 */
export function fetchReviewDetail(reviewId) {
  return authedRequest(`/reviews/${reviewId}/detail`);
}
