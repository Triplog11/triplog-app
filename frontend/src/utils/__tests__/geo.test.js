import { distanceInMeters, formatDistance } from '../geo';

describe('geo utils', () => {
  describe('distanceInMeters', () => {
    it('서울시청 ↔ 경복궁 거리가 약 1.4~1.6km (오차 5% 이내)로 계산된다', () => {
      const seoulCityHall = { lat: 37.566535, lng: 126.977969 };
      const gyeongbokgung = { lat: 37.579617, lng: 126.977041 };
      
      const distance = distanceInMeters(seoulCityHall, gyeongbokgung);
      // 직선 거리 약 1,455m (1.45km ~ 1.5km)
      expect(distance).toBeGreaterThanOrEqual(1400);
      expect(distance).toBeLessThanOrEqual(1600);
    });

    it('동일한 위치 사이의 거리는 0m이다', () => {
      const point = { lat: 37.5665, lng: 126.9780 };
      expect(distanceInMeters(point, point)).toBe(0);
    });
  });

  describe('formatDistance', () => {
    it('null 또는 undefined일 때 --m을 반환한다', () => {
      expect(formatDistance(null)).toBe('--m');
      expect(formatDistance(undefined)).toBe('--m');
    });

    it('1000m 미만일 때 m 단위로 반환한다', () => {
      expect(formatDistance(999)).toBe('999m');
      expect(formatDistance(50)).toBe('50m');
      expect(formatDistance(0)).toBe('0m');
    });

    it('1000m 이상일 때 km 단위로 소수점 1자리까지 반환한다', () => {
      expect(formatDistance(1000)).toBe('1.0km');
      expect(formatDistance(1600)).toBe('1.6km');
      expect(formatDistance(12345)).toBe('12.3km');
    });
  });
});
