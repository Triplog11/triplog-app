import {
  averageCompletionRate,
  countRatioColor,
  normalizeRate,
  SCALE_COLORS,
  SCALE_LOCATIONS,
  UNVISITED_COLOR,
  visitRateColor,
} from '../mapColor';
import theme from '../../theme/theme';

describe('mapColor', () => {
  describe('normalizeRate', () => {
    it('0~1 비율은 그대로 유지한다', () => {
      expect(normalizeRate(0.42)).toBeCloseTo(0.42);
    });

    it('1보다 큰 값은 0~100 퍼센트로 보고 100으로 나눈다', () => {
      expect(normalizeRate(42)).toBeCloseTo(0.42);
      expect(normalizeRate(100)).toBe(1);
    });

    it('1은 100%로 해석한다', () => {
      expect(normalizeRate(1)).toBe(1);
    });

    it('숫자가 아니거나 음수면 0으로 본다', () => {
      expect(normalizeRate(null)).toBe(0);
      expect(normalizeRate(undefined)).toBe(0);
      expect(normalizeRate('abc')).toBe(0);
      expect(normalizeRate(-5)).toBe(0);
    });

    it('100을 넘는 값은 1로 자른다', () => {
      expect(normalizeRate(140)).toBe(1);
    });
  });

  describe('visitRateColor', () => {
    it('방문률 0%는 미방문 회색이다', () => {
      expect(visitRateColor(0)).toBe(UNVISITED_COLOR);
      expect(visitRateColor(null)).toBe(UNVISITED_COLOR);
    });

    it('방문률 100%는 primary 파랑이다', () => {
      expect(visitRateColor(1)).toBe(theme.colors.primary.toUpperCase());
      expect(visitRateColor(100)).toBe(theme.colors.primary.toUpperCase());
    });

    it('스케일에 정의된 지점은 그 색을 그대로 돌려준다', () => {
      theme.mapScale.forEach((stop) => {
        expect(visitRateColor(stop.at)).toBe(stop.color.toUpperCase());
      });
    });

    it('스톱 사이 구간은 두 색 사이로 보간한다', () => {
      // 0.25(#C9E0FA)와 0.5(#96C6F7)의 중간
      expect(visitRateColor(0.375)).toBe('#B0D3F9');
    });

    it('방문률이 올라갈수록 색이 파랑에 가까워진다(빨강 채널 감소)', () => {
      const red = (hex) => parseInt(hex.slice(1, 3), 16);
      const samples = [0, 0.2, 0.4, 0.6, 0.8, 1].map((rate) => red(visitRateColor(rate)));
      samples.slice(1).forEach((value, index) => {
        expect(value).toBeLessThan(samples[index]);
      });
    });
  });

  describe('countRatioColor', () => {
    it('전체가 0이면 미방문 회색이다', () => {
      expect(countRatioColor(0, 0)).toBe(UNVISITED_COLOR);
      expect(countRatioColor(3, 0)).toBe(UNVISITED_COLOR);
    });

    it('방문 수가 0이면 미방문 회색이다', () => {
      expect(countRatioColor(0, 25)).toBe(UNVISITED_COLOR);
    });

    it('전부 방문하면 primary 파랑이다', () => {
      expect(countRatioColor(25, 25)).toBe(theme.colors.primary.toUpperCase());
    });

    it('절반을 방문하면 스케일 중간 색이다', () => {
      expect(countRatioColor(5, 10)).toBe(visitRateColor(0.5));
    });
  });

  describe('averageCompletionRate', () => {
    it('completionRate의 평균을 0~1로 돌려준다', () => {
      const regions = [{ completionRate: 0.5 }, { completionRate: 1 }, { completionRate: 0 }];
      expect(averageCompletionRate(regions)).toBeCloseTo(0.5);
    });

    it('0~100 퍼센트로 와도 0~1로 정규화한 뒤 평균을 낸다', () => {
      expect(averageCompletionRate([{ completionRate: 50 }, { completionRate: 100 }])).toBeCloseTo(
        0.75
      );
    });

    it('completionRate가 없으면 visited 여부로 대신한다', () => {
      expect(averageCompletionRate([{ visited: true }, { visited: false }])).toBeCloseTo(0.5);
    });

    it('목록이 비었거나 배열이 아니면 null이다', () => {
      expect(averageCompletionRate([])).toBeNull();
      expect(averageCompletionRate(undefined)).toBeNull();
    });
  });

  describe('범례 그라데이션', () => {
    it('범례 막대와 지도가 같은 스케일 배열을 쓴다', () => {
      expect(SCALE_COLORS).toEqual(theme.mapScale.map((stop) => stop.color));
      expect(SCALE_LOCATIONS).toEqual(theme.mapScale.map((stop) => stop.at));
    });

    it('색과 위치의 개수가 같고 위치는 0에서 1까지 오름차순이다', () => {
      expect(SCALE_COLORS).toHaveLength(SCALE_LOCATIONS.length);
      expect(SCALE_LOCATIONS[0]).toBe(0);
      expect(SCALE_LOCATIONS[SCALE_LOCATIONS.length - 1]).toBe(1);
      SCALE_LOCATIONS.slice(1).forEach((value, index) => {
        expect(value).toBeGreaterThan(SCALE_LOCATIONS[index]);
      });
    });
  });
});
