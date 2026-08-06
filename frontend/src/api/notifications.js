import { authedRequest } from './client';

/**
 * 알림 목록 조회.
 * @param {{page?: number, size?: number, unreadOnly?: boolean}} opts
 * @returns {page, size, totalElements, totalPages,
 *           notifications: [{notificationId, title, content, notificationType, isRead, createdAt}]}
 */
export function fetchNotifications({ page = 0, size = 10, unreadOnly = false } = {}) {
  const params = new URLSearchParams({
    page: String(page),
    size: String(size),
    unreadOnly: String(unreadOnly),
  });
  return authedRequest(`/notifications?${params.toString()}`);
}

/**
 * 알림 읽음 처리.
 * @returns {notificationId, isRead, readAt}
 */
export function markNotificationRead(notificationId) {
  return authedRequest(`/notifications/${notificationId}/read`, { method: 'PATCH' });
}

/**
 * 알림 설정 조회.
 * @returns {isLevelUp, isRankUp, isBadgeAcquired, isCardAcquired,
 *           isRegionCompleted, isLandmarkVerified, isWeeklyMissonCompleted}
 *          (isWeeklyMissonCompleted는 백엔드 필드명 오타를 그대로 따름)
 */
export function fetchNotificationSettings() {
  return authedRequest('/notifications/settings');
}

/**
 * 알림 설정 수정 — 7개 플래그 전부 필수.
 * @param {{isLevelUp, isRankUp, isBadgeAcquired, isCardAcquired,
 *          isRegionCompleted, isLandmarkVerified, isWeeklyMissonCompleted}} settings
 * @returns 수정된 설정 (조회와 동일 shape)
 */
export function updateNotificationSettings(settings) {
  return authedRequest('/notifications/settings', {
    method: 'PATCH',
    body: settings,
  });
}
