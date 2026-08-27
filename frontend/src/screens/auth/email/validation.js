/**
 * 이메일 로그인/가입 입력 검증.
 * 백엔드 규칙: 이메일 @Email, 비밀번호 @NotBlank, 닉네임 2~12자, 주소 3필드 @NotBlank.
 * 비밀번호 최소 길이는 백엔드에 규칙이 없어 프론트에서 8자로 정한다.
 */
export const NICKNAME_MIN = 2;
export const NICKNAME_MAX = 12;
export const PASSWORD_MIN = 8;

const EMAIL_PATTERN = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

export function isValidEmail(email) {
  return EMAIL_PATTERN.test((email ?? '').trim());
}

export function isValidPassword(password) {
  return typeof password === 'string' && password.length >= PASSWORD_MIN && !/\s/.test(password);
}

export function isValidNickname(nickname) {
  const length = (nickname ?? '').trim().length;
  return length >= NICKNAME_MIN && length <= NICKNAME_MAX;
}

/**
 * 주소 3필드를 백엔드 형식으로 정규화한다.
 * 도가 없는 지역(서울특별시 등)은 addressDoGun에 시/도명을 다시 넣는다 (백엔드 @NotBlank 대응).
 */
export function normalizeAddress({ addressDoGun, addressSi, addressGu }) {
  const si = (addressSi ?? '').trim();
  const doGun = (addressDoGun ?? '').trim() || si;
  const gu = (addressGu ?? '').trim();
  return { addressSi: si, addressDoGun: doGun, addressGu: gu };
}

export function isValidAddress(address) {
  const { addressSi, addressDoGun, addressGu } = normalizeAddress(address);
  return !!(addressSi && addressDoGun && addressGu);
}
