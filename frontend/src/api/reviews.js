import { authedMultipartRequest } from './client';

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
 * 방문 인증(리뷰) 등록 — multipart/form-data.
 * 백엔드는 `request`(JSON) 파트 + `files`(이미지, 선택) 파트를 받는다.
 *
 * ⚠️ RN에서 @RequestPart("request") JSON 파트는 Content-Type이 명시되지 않으면
 * 서버가 거부할 수 있다. 디바이스 검증에서 400/415가 나오면 이 파트 전송 방식을
 * 조정해야 한다(백엔드 @RequestPart content-type 허용 또는 blob 전송).
 *
 * @param {{landmarkId, legalRegionCode, legalDistrictCode, reviewTitle,
 *          reviewContent, reviewScore, reviewPoint}} review
 * @param {string[]} [imageUris] 첨부 이미지 로컬 uri 배열
 * @returns {isVerified: boolean}
 */
export function submitReview(review, imageUris = []) {
  const formData = new FormData();
  formData.append('request', JSON.stringify(review));
  imageUris.forEach((uri, index) => {
    formData.append('files', toFilePart(uri, index));
  });
  return authedMultipartRequest('/reviews', formData);
}
