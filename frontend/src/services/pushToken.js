import { Platform } from 'react-native';
import * as SecureStore from 'expo-secure-store';
import { registerFcmToken, deleteFcmToken } from '../api/fcmTokens';

const PUSH_TOKEN_KEY = 'triplog.pushToken';

/**
 * 알림 네이티브 모듈을 지연 로드한다.
 * 알림 모듈이 없는 개발 빌드에서도 앱이 죽지 않도록 실패 시 null을 반환한다.
 */
function loadNotificationModules() {
  try {
    return {
      Notifications: require('expo-notifications'),
      Device: require('expo-device'),
    };
  } catch (error) {
    console.warn('알림 모듈을 사용할 수 없어요:', error?.message);
    return null;
  }
}

/**
 * 푸시 알림 권한을 받고 FCM 토큰을 서버에 등록한다.
 * 실패해도 로그인 흐름을 막지 않는다 (알림은 부가 기능).
 */
export async function registerPushToken() {
  const modules = loadNotificationModules();
  if (!modules) return null;
  const { Notifications, Device } = modules;

  try {
    if (!Device.isDevice) return null;

    const { status: existing } = await Notifications.getPermissionsAsync();
    let status = existing;
    if (status !== 'granted') {
      const result = await Notifications.requestPermissionsAsync();
      status = result.status;
    }
    if (status !== 'granted') return null;

    const { data: token } = await Notifications.getDevicePushTokenAsync();
    if (!token) return null;

    await registerFcmToken({
      token,
      deviceType: Platform.OS.toUpperCase(),
      deviceName: Device.deviceName ?? Device.modelName ?? Platform.OS,
    });
    await SecureStore.setItemAsync(PUSH_TOKEN_KEY, token);
    return token;
  } catch (error) {
    // 409(이미 등록됨)를 포함해 등록 실패는 조용히 넘어간다
    console.warn('푸시 토큰 등록 실패:', error?.status, error?.message);
    return null;
  }
}

/** 로그아웃 시 서버에서 푸시 토큰을 제거한다 */
export async function unregisterPushToken() {
  try {
    const token = await SecureStore.getItemAsync(PUSH_TOKEN_KEY);
    if (!token) return;
    await deleteFcmToken(token);
  } catch (error) {
    console.warn('푸시 토큰 삭제 실패:', error?.status, error?.message);
  } finally {
    await SecureStore.deleteItemAsync(PUSH_TOKEN_KEY).catch(() => {});
  }
}
