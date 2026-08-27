import { tierToGrade, GRADE_CONFIG, formatAcquiredDate, toCardModel } from '../collection';

describe('collection data utils', () => {
  describe('tierToGrade', () => {
    it('대문자 서버 티어를 정상 매핑한다', () => {
      expect(tierToGrade('COMMON')).toBe('Common');
      expect(tierToGrade('RARE')).toBe('Rare');
      expect(tierToGrade('EPIC')).toBe('Epic');
      expect(tierToGrade('LEGENDARY')).toBe('Legendary');
    });

    it('소문자/혼합 대소문자 티어도 정상 매핑한다', () => {
      expect(tierToGrade('common')).toBe('Common');
      expect(tierToGrade('rare')).toBe('Rare');
      expect(tierToGrade('Epic')).toBe('Epic');
      expect(tierToGrade('legendary')).toBe('Legendary');
    });

    it('미지의 등급이나 null/undefined는 null을 반환한다', () => {
      expect(tierToGrade('UNKNOWN')).toBeNull();
      expect(tierToGrade('MYTHIC')).toBeNull();
      expect(tierToGrade(null)).toBeNull();
      expect(tierToGrade(undefined)).toBeNull();
      expect(tierToGrade('')).toBeNull();
    });
  });

  describe('formatAcquiredDate', () => {
    it('ISO 날짜 문자열을 YYYY.MM.DD 형식으로 변환한다', () => {
      expect(formatAcquiredDate('2026-08-27T10:30:00.000Z')).toBe('2026.08.27');
      expect(formatAcquiredDate('2026-01-05')).toBe('2026.01.05');
    });

    it('null이나 빈 문자열이면 null을 반환한다', () => {
      expect(formatAcquiredDate(null)).toBeNull();
      expect(formatAcquiredDate('')).toBeNull();
    });
  });

  describe('toCardModel', () => {
    it('API 아이템을 UI 카드 모델 구조로 올바르게 변환한다', () => {
      const item = {
        cardId: 10,
        landmarkId: 100,
        cardName: 'N서울타워 카드',
        landmarkName: 'N서울타워',
        cardTier: 'RARE',
        cardUrl: 'https://triplog11.store/cards/10.png',
        acquiredAt: '2026-08-27T09:00:00Z',
      };

      const card = toCardModel(item);
      expect(card).toEqual({
        id: 10,
        landmarkId: 100,
        name: 'N서울타워 카드',
        landmarkName: 'N서울타워',
        grade: 'Rare',
        imageUrl: 'https://triplog11.store/cards/10.png',
        obtained: true,
        date: '2026.08.27',
        region: null,
      });
    });
  });
});
