import { formatDate, getEventStatus, formatReward } from '../format';

describe('mypage format utils', () => {
  describe('formatDate', () => {
    it('ISO 날짜 문자열을 yyyy.MM.dd 포맷으로 변환한다', () => {
      expect(formatDate('2026-08-27T10:00:00')).toBe('2026.08.27');
      expect(formatDate('2026-12-31')).toBe('2026.12.31');
    });

    it('빈 값이면 빈 문자열을 반환한다', () => {
      expect(formatDate(null)).toBe('');
      expect(formatDate('')).toBe('');
      expect(formatDate(undefined)).toBe('');
    });
  });

  describe('getEventStatus', () => {
    const fixedNow = new Date('2026-08-27T12:00:00Z');

    it('시작일이 현재보다 미래이면 upcoming을 반환한다', () => {
      const status = getEventStatus('2026-09-01T00:00:00Z', '2026-09-10T00:00:00Z', fixedNow);
      expect(status).toBe('upcoming');
    });

    it('종료일이 현재보다 과거이면 ended를 반환한다', () => {
      const status = getEventStatus('2026-08-01T00:00:00Z', '2026-08-20T00:00:00Z', fixedNow);
      expect(status).toBe('ended');
    });

    it('현재가 시작일과 종료일 사이이면 ongoing을 반환한다', () => {
      const status = getEventStatus('2026-08-01T00:00:00Z', '2026-08-30T00:00:00Z', fixedNow);
      expect(status).toBe('ongoing');
    });
  });

  describe('formatReward', () => {
    it('XP와 점수가 모두 있으면 조합하여 반환한다', () => {
      expect(formatReward(40, 80)).toBe('+40 XP · +80점');
    });

    it('XP만 있으면 XP만 반환한다', () => {
      expect(formatReward(50, null)).toBe('+50 XP');
    });

    it('점수만 있으면 점수만 반환한다', () => {
      expect(formatReward(null, 100)).toBe('+100점');
    });

    it('둘 다 없으면 null을 반환한다', () => {
      expect(formatReward(null, null)).toBeNull();
      expect(formatReward(0, 0)).toBeNull();
    });
  });
});
