import { getTourismContentId } from '../landmarkIds';

describe('landmarkIds utils', () => {
  it('tourismContentId가 있으면 landmarkId보다 우선 반환한다', () => {
    const landmark = {
      landmarkId: 101,
      tourismContentId: 'TOUR_9999',
    };
    expect(getTourismContentId(landmark)).toBe('TOUR_9999');
  });

  it('tourismContentId가 없으면 landmarkId를 반환한다', () => {
    const landmark = {
      landmarkId: 202,
      landmarkName: '경복궁',
    };
    expect(getTourismContentId(landmark)).toBe(202);
  });

  it('landmark 객체가 없거나 id 필드가 모두 없으면 null을 반환한다', () => {
    expect(getTourismContentId(null)).toBeNull();
    expect(getTourismContentId(undefined)).toBeNull();
    expect(getTourismContentId({})).toBeNull();
  });
});
