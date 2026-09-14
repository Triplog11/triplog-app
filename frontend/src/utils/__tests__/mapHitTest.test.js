import { parsePathPolygons, isPointInPolygons, stagePointToViewBox, findRegionAt } from '../mapHitTest';

const SQUARE = 'M0,0L10,0L10,10L0,10Z';
// 바깥 사각형 안에 구멍(hole)이 있는 도형 — evenodd 규칙이면 구멍은 바깥으로 본다
const DONUT = 'M0,0L30,0L30,30L0,30ZM10,10L20,10L20,20L10,20Z';

describe('parsePathPolygons', () => {
  it('M/L/Z 경로를 꼭짓점 배열로 나눈다', () => {
    expect(parsePathPolygons(DONUT)).toEqual([
      [[0, 0], [30, 0], [30, 30], [0, 30]],
      [[10, 10], [20, 10], [20, 20], [10, 20]],
    ]);
  });

  it('공백으로 구분한 좌표와 음수도 읽는다', () => {
    expect(parsePathPolygons('M -5 -5 L 5 -5 L 5 5 Z')).toEqual([[[-5, -5], [5, -5], [5, 5]]]);
  });
});

describe('isPointInPolygons', () => {
  it('도형 안쪽 점은 true, 바깥은 false', () => {
    const polygons = parsePathPolygons(SQUARE);
    expect(isPointInPolygons(polygons, 5, 5)).toBe(true);
    expect(isPointInPolygons(polygons, 15, 5)).toBe(false);
  });

  it('구멍 안쪽은 도형 밖으로 판정한다', () => {
    const polygons = parsePathPolygons(DONUT);
    expect(isPointInPolygons(polygons, 5, 5)).toBe(true);
    expect(isPointInPolygons(polygons, 15, 15)).toBe(false);
  });
});

describe('stagePointToViewBox', () => {
  const base = { stageW: 200, stageH: 400, viewW: 100, viewH: 200 };

  it('변형이 없으면 화면 비율만큼 나눈다', () => {
    const p = stagePointToViewBox({ x: 100, y: 200 }, { ...base, tx: 0, ty: 0, rotation: 0, scale: 1 });
    expect(p.x).toBeCloseTo(50);
    expect(p.y).toBeCloseTo(100);
  });

  it('가로가 남는 화면에서는 가운데 정렬 여백을 뺀다', () => {
    // 300x400 화면에 100x200 도형 → 배율 2, 좌우 여백 50
    const p = stagePointToViewBox(
      { x: 50, y: 0 },
      { ...base, stageW: 300, tx: 0, ty: 0, rotation: 0, scale: 1 },
    );
    expect(p.x).toBeCloseTo(0);
    expect(p.y).toBeCloseTo(0);
  });

  it('확대·이동한 상태를 되돌려 원래 좌표를 구한다', () => {
    // 중심(100,200) 기준 2배 확대 후 오른쪽으로 20 이동 → 화면 (140,200)은 원래 (110,200)
    const p = stagePointToViewBox({ x: 140, y: 200 }, { ...base, tx: 20, ty: 0, rotation: 0, scale: 2 });
    expect(p.x).toBeCloseTo(55);
    expect(p.y).toBeCloseTo(100);
  });

  it('회전한 상태를 되돌린다', () => {
    // 90도 회전: 원래 중심 오른쪽 10 지점은 화면에서 중심 아래 10 지점에 보인다
    const p = stagePointToViewBox({ x: 100, y: 210 }, { ...base, tx: 0, ty: 0, rotation: Math.PI / 2, scale: 1 });
    expect(p.x).toBeCloseTo(55);
    expect(p.y).toBeCloseTo(100);
  });
});

describe('findRegionAt', () => {
  const regions = [
    { name: 'A', polygons: parsePathPolygons(SQUARE) },
    { name: 'B', polygons: parsePathPolygons('M20,0L30,0L30,10L20,10Z') },
  ];

  it('점이 들어 있는 지역 이름을 돌려준다', () => {
    expect(findRegionAt(regions, 25, 5)).toBe('B');
  });

  it('어느 지역에도 속하지 않으면 null', () => {
    expect(findRegionAt(regions, 15, 5)).toBeNull();
  });
});

describe('실제 전국 지도 데이터', () => {
  // eslint-disable-next-line @typescript-eslint/no-var-requires
  const { KOREA_PROVINCES } = require('../../components/map/koreaProvinces');
  const { getPathBounds } = require('../../components/map/svgBounds');
  const regions = KOREA_PROVINCES.regions.map((r) => ({ name: r.name, polygons: parsePathPolygons(r.d) }));

  it('서울 도형의 중심을 누르면 서울특별시로 판정한다', () => {
    const seoul = KOREA_PROVINCES.regions.find((r) => r.name === '서울특별시');
    const { cx, cy } = getPathBounds(seoul.d);
    expect(findRegionAt(regions, cx, cy)).toBe('서울특별시');
  });

  it('바다(지도 왼쪽 위 모서리)는 어느 지역도 아니다', () => {
    expect(findRegionAt(regions, 1, 1)).toBeNull();
  });
});
