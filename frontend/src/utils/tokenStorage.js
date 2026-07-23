import * as SecureStore from 'expo-secure-store';

const ACCESS_TOKEN_KEY = 'triplog.accessToken';
const REFRESH_TOKEN_KEY = 'triplog.refreshToken';

export async function saveTokens({ accessToken, refreshToken }) {
  await Promise.all([
    SecureStore.setItemAsync(ACCESS_TOKEN_KEY, accessToken),
    SecureStore.setItemAsync(REFRESH_TOKEN_KEY, refreshToken),
  ]);
}

export async function getTokens() {
  const [accessToken, refreshToken] = await Promise.all([
    SecureStore.getItemAsync(ACCESS_TOKEN_KEY),
    SecureStore.getItemAsync(REFRESH_TOKEN_KEY),
  ]);
  if (!accessToken || !refreshToken) {
    return null;
  }
  return { accessToken, refreshToken };
}

export async function clearTokens() {
  await Promise.all([
    SecureStore.deleteItemAsync(ACCESS_TOKEN_KEY),
    SecureStore.deleteItemAsync(REFRESH_TOKEN_KEY),
  ]);
}
