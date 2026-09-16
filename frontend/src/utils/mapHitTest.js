/**
 * 지도 탭 위치로 어느 지역을 눌렀는지 계산한다.
 *
 * SVG Path 의 onPress 는 확대·이동 제스처(react-native-gesture-handler)와 겹치면
 * 안드로이드에서 터치를 빼앗겨 호출되지 않는다. 그래서 탭 제스처로 좌표만 받고,
 * 화면 좌표 → viewBox 좌표로 되돌린 뒤 도형 안에 있는지 직접 판정한다.
 */

const TOKEN = /[MLZ]|-?\d*\.?\d+(?:e[-+]?\d+)?/gi;

/** M/L/Z 로만 이루어진 경로 문자열을 링(꼭짓점 배열)의 배열로 바꾼다. */
export function parsePathPolygons(d) {
  const tokens = String(d ?? '').match(TOKEN) ?? [];
  const polygons = [];
  let ring = [];
  let i = 0;
  while (i < tokens.length) {
    const token = tokens[i].toUpperCase();
    if (token === 'M' || token === 'Z') {
      if (ring.length) polygons.push(ring);
      ring = [];
      i += 1;
    } else if (token === 'L') {
      i += 1;
    } else {
      ring = [...ring, [Number(tokens[i]), Number(tokens[i + 1])]];
      i += 2;
    }
  }
  if (ring.length) polygons.push(ring);
  return polygons;
}

/** 링 여러 개를 evenodd 규칙으로 판정한다(구멍 안쪽은 밖으로 본다). */
export function isPointInPolygons(polygons, x, y) {
  let inside = false;
  polygons.forEach((ring) => {
    for (let a = 0, b = ring.length - 1; a < ring.length; b = a, a += 1) {
      const [xa, ya] = ring[a];
      const [xb, yb] = ring[b];
      const crosses = ya > y !== yb > y && x < ((xb - xa) * (y - ya)) / (yb - ya) + xa;
      if (crosses) inside = !inside;
    }
  });
  return inside;
}

/**
 * 제스처가 받은 화면 좌표를 SVG viewBox 좌표로 되돌린다.
 * 지도는 화면 중심을 기준으로 translate → rotate → scale 순서로 변형되고,
 * SVG 는 preserveAspectRatio="xMidYMid meet" 로 가운데 맞춤되어 있다.
 */
export function stagePointToViewBox(point, { stageW, stageH, viewW, viewH, tx, ty, rotation, scale }) {
  const cx = stageW / 2;
  const cy = stageH / 2;
  const dx = point.x - cx - tx;
  const dy = point.y - cy - ty;
  const cos = Math.cos(-rotation);
  const sin = Math.sin(-rotation);
  const localX = (cos * dx - sin * dy) / scale + cx;
  const localY = (sin * dx + cos * dy) / scale + cy;

  const fit = Math.min(stageW / viewW, stageH / viewH);
  const offsetX = (stageW - viewW * fit) / 2;
  const offsetY = (stageH - viewH * fit) / 2;
  return { x: (localX - offsetX) / fit, y: (localY - offsetY) / fit };
}

/** regions: [{name, polygons}] 중 점을 포함하는 첫 지역 이름. 없으면 null. */
export function findRegionAt(regions, x, y) {
  const hit = regions.find((region) => isPointInPolygons(region.polygons, x, y));
  return hit ? hit.name : null;
}
