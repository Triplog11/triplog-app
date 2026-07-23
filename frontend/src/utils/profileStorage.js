import * as SecureStore from 'expo-secure-store';

const PROFILE_KEY = 'triplog.userProfile';

/** 로그인 응답의 유저 프로필(nickname/level/xp/tier)을 저장한다 */
export async function saveProfile(profile) {
  await SecureStore.setItemAsync(PROFILE_KEY, JSON.stringify(profile));
}

export async function getProfile() {
  const raw = await SecureStore.getItemAsync(PROFILE_KEY);
  if (!raw) return null;
  try {
    return JSON.parse(raw);
  } catch (error) {
    return null;
  }
}

export async function clearProfile() {
  await SecureStore.deleteItemAsync(PROFILE_KEY);
}
