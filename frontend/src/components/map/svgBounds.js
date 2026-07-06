/**
 * SVG path d 문자열의 바운딩박스 계산 (절대/상대 커맨드 지원).
 * 곡선은 제어점 포함 근사 — 지도 도형 중심점 계산 용도로 충분.
 */
export function getPathBounds(d) {
  const re = /([MmLlHhVvCcSsQqTtAaZz])|(-?\d*\.?\d+(?:[eE][-+]?\d+)?)/g;
  const tokens = [];
  let m;
  while ((m = re.exec(d))) {
    tokens.push(m[1] ? m[1] : parseFloat(m[2]));
  }

  let i = 0;
  let cmd = 'M';
  let x = 0;
  let y = 0;
  let startX = 0;
  let startY = 0;
  let minX = Infinity;
  let minY = Infinity;
  let maxX = -Infinity;
  let maxY = -Infinity;

  const addPoint = (px, py) => {
    if (px < minX) minX = px;
    if (px > maxX) maxX = px;
    if (py < minY) minY = py;
    if (py > maxY) maxY = py;
  };
  const num = () => tokens[i++];

  while (i < tokens.length) {
    if (typeof tokens[i] === 'string') {
      cmd = tokens[i];
      i += 1;
      if (cmd === 'Z' || cmd === 'z') {
        x = startX;
        y = startY;
        continue;
      }
    }

    const rel = cmd === cmd.toLowerCase();
    switch (cmd.toUpperCase()) {
      case 'M': {
        const nx = num();
        const ny = num();
        x = rel ? x + nx : nx;
        y = rel ? y + ny : ny;
        startX = x;
        startY = y;
        addPoint(x, y);
        cmd = rel ? 'l' : 'L'; // 연속 좌표는 lineto로 처리
        break;
      }
      case 'L': {
        const nx = num();
        const ny = num();
        x = rel ? x + nx : nx;
        y = rel ? y + ny : ny;
        addPoint(x, y);
        break;
      }
      case 'H': {
        const nx = num();
        x = rel ? x + nx : nx;
        addPoint(x, y);
        break;
      }
      case 'V': {
        const ny = num();
        y = rel ? y + ny : ny;
        addPoint(x, y);
        break;
      }
      case 'C': {
        for (let k = 0; k < 2; k += 1) {
          const cx = num();
          const cy = num();
          addPoint(rel ? x + cx : cx, rel ? y + cy : cy);
        }
        const nx = num();
        const ny = num();
        x = rel ? x + nx : nx;
        y = rel ? y + ny : ny;
        addPoint(x, y);
        break;
      }
      case 'S':
      case 'Q': {
        const cx = num();
        const cy = num();
        addPoint(rel ? x + cx : cx, rel ? y + cy : cy);
        const nx = num();
        const ny = num();
        x = rel ? x + nx : nx;
        y = rel ? y + ny : ny;
        addPoint(x, y);
        break;
      }
      case 'T': {
        const nx = num();
        const ny = num();
        x = rel ? x + nx : nx;
        y = rel ? y + ny : ny;
        addPoint(x, y);
        break;
      }
      case 'A': {
        num(); // rx
        num(); // ry
        num(); // rotation
        num(); // large-arc
        num(); // sweep
        const nx = num();
        const ny = num();
        x = rel ? x + nx : nx;
        y = rel ? y + ny : ny;
        addPoint(x, y);
        break;
      }
      default:
        i += 1;
    }
  }

  return {
    minX,
    minY,
    maxX,
    maxY,
    cx: (minX + maxX) / 2,
    cy: (minY + maxY) / 2,
  };
}
