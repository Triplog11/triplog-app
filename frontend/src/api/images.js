import { authedMultipartRequest } from './client';

/**
 * 로컬 이미지 uri를 RN FormData 파일 파트로 변환한다.
 * @param {string} uri  로컬 파일 uri (예: expo-image-picker 결과)
 * @param {number} index 파일 순번 (이름 생성용)
 */
function toFilePart(uri, index) {
  const match = /\.(\w+)$/.exec(uri);
  const ext = (match?.[1] ?? 'jpg').toLowerCase();
  const mime = ext === 'png' ? 'image/png' : 'image/jpeg';
  return { uri, name: `image_${index}.${ext}`, type: mime };
}

/**
 * 이미지 여러 장 업로드.
 * @param {string[]} uris 로컬 이미지 uri 배열
 * @returns {imageUrls: string[]} Cloudinary https URL 목록
 */
export function uploadImages(uris) {
  const formData = new FormData();
  uris.forEach((uri, index) => {
    formData.append('files', toFilePart(uri, index));
  });
  return authedMultipartRequest('/images', formData);
}
