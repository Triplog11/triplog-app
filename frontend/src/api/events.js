import { authedRequest } from './client';

/**
 * 이벤트 목록 — 페이징.
 * @returns {page, size, totalElements, totalPages,
 *           items: [{eventId, eventTitle, eventContent, eventImageUrl, eventStart, eventEnd}]}
 */
export function fetchEvents({ page = 0, size = 10 } = {}) {
  const params = new URLSearchParams({ page: String(page), size: String(size) });
  return authedRequest(`/events?${params.toString()}`);
}

/**
 * 이벤트 상세.
 * @returns {eventId, eventTitle, eventContent, eventImageUrl1, eventImageUrl2, eventStart, eventEnd}
 */
export function fetchEventDetail(eventId) {
  return authedRequest(`/events/${eventId}`);
}
