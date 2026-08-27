/** ISO 날짜 문자열 → 'yyyy.MM.dd' (파싱 실패 시 원본, 빈 값이면 '') */
export function formatDate(iso) {
  if (!iso) return '';
  if (typeof iso === 'string' && iso.length >= 10) {
    return iso.slice(0, 10).replace(/-/g, '.');
  }
  return String(iso);
}

/** 이벤트 기간 상태 — 'upcoming' | 'ongoing' | 'ended' */
export function getEventStatus(eventStart, eventEnd, now = new Date()) {
  const start = eventStart ? new Date(eventStart) : null;
  const end = eventEnd ? new Date(eventEnd) : null;
  if (start && now < start) return 'upcoming';
  if (end && now > end) return 'ended';
  return 'ongoing';
}

export const EVENT_STATUS_LABEL = {
  upcoming: '예정',
  ongoing: '진행중',
  ended: '종료',
};

/** 보상 표기 — '+40 XP · +80점' (둘 다 없으면 null) */
export function formatReward(xp, score) {
  const parts = [];
  if (xp) parts.push(`+${xp} XP`);
  if (score) parts.push(`+${score}점`);
  return parts.length > 0 ? parts.join(' · ') : null;
}
