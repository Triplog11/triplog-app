import React, { createContext, useState, useContext, useEffect, useRef, useCallback } from 'react';
import { oauthLogin, submitAdditionalInfo, logoutRequest } from '../api/auth';
import { saveTokens, getTokens, clearTokens } from '../utils/tokenStorage';

export const AUTH_STATUS = {
  BOOTSTRAPPING: 'bootstrapping',
  LOGGED_OUT: 'loggedOut',
  NEEDS_ADDITIONAL_INFO: 'needsAdditionalInfo',
  LOGGED_IN: 'loggedIn',
};

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [status, setStatus] = useState(AUTH_STATUS.BOOTSTRAPPING);
  const [user, setUser] = useState(null);
  const [temporaryToken, setTemporaryToken] = useState(null);
  const tempTokenTimerRef = useRef(null);

  // 앱 시작 시 저장된 토큰 복원 (토큰 검증 API 부재 — 존재하면 로그인 상태로 간주, 이후 401 시 로그아웃)
  useEffect(() => {
    let mounted = true;
    (async () => {
      try {
        const tokens = await getTokens();
        if (!mounted) return;
        setStatus(tokens ? AUTH_STATUS.LOGGED_IN : AUTH_STATUS.LOGGED_OUT);
      } catch (error) {
        console.error('토큰 복원 실패:', error);
        if (mounted) setStatus(AUTH_STATUS.LOGGED_OUT);
      }
    })();
    return () => {
      mounted = false;
      if (tempTokenTimerRef.current) clearTimeout(tempTokenTimerRef.current);
    };
  }, []);

  const clearTempTokenTimer = () => {
    if (tempTokenTimerRef.current) {
      clearTimeout(tempTokenTimerRef.current);
      tempTokenTimerRef.current = null;
    }
  };

  const applyLoginSuccess = useCallback(async (response) => {
    const { accessToken, refreshToken, ...profile } = response;
    await saveTokens({ accessToken, refreshToken });
    setUser(profile);
    setTemporaryToken(null);
    clearTempTokenTimer();
    setStatus(AUTH_STATUS.LOGGED_IN);
  }, []);

  /**
   * 소셜/자체 로그인 요청.
   * @returns {'loggedIn'|'needsAdditionalInfo'} 후속 화면 분기용 결과
   */
  const signInWithProvider = useCallback(async ({ provider, code, state, email, password }) => {
    const response = await oauthLogin({ provider, code, state, email, password });

    if (response?.accessToken) {
      await applyLoginSuccess(response);
      return AUTH_STATUS.LOGGED_IN;
    }

    // 신규 회원 — 임시 토큰으로 추가정보 입력 필요 (expiresIn초 후 만료)
    setTemporaryToken(response.temporaryToken);
    setStatus(AUTH_STATUS.NEEDS_ADDITIONAL_INFO);
    clearTempTokenTimer();
    const expiresInMs = (response.expiresIn ?? 300) * 1000;
    tempTokenTimerRef.current = setTimeout(() => {
      setTemporaryToken(null);
      setStatus(AUTH_STATUS.LOGGED_OUT);
    }, expiresInMs);
    return AUTH_STATUS.NEEDS_ADDITIONAL_INFO;
  }, [applyLoginSuccess]);

  /**
   * 신규 소셜 회원 가입 완료 (닉네임/주소/알림 동의 제출).
   */
  const completeSignup = useCallback(async (additionalInfo) => {
    const response = await submitAdditionalInfo(temporaryToken, additionalInfo);
    await applyLoginSuccess(response);
  }, [temporaryToken, applyLoginSuccess]);

  const logout = useCallback(async () => {
    try {
      const tokens = await getTokens();
      if (tokens) {
        await logoutRequest(tokens.accessToken, tokens.refreshToken);
      }
    } catch (error) {
      // 서버 로그아웃이 실패해도 로컬 세션은 정리한다
      console.error('서버 로그아웃 실패:', error);
    } finally {
      await clearTokens();
      setUser(null);
      setTemporaryToken(null);
      clearTempTokenTimer();
      setStatus(AUTH_STATUS.LOGGED_OUT);
    }
  }, []);

  /** 임시 토큰 만료 등으로 가입 플로우를 중단하고 로그인 화면으로 복귀 */
  const resetToLoggedOut = useCallback(() => {
    setTemporaryToken(null);
    clearTempTokenTimer();
    setStatus(AUTH_STATUS.LOGGED_OUT);
  }, []);

  const value = {
    status,
    user,
    temporaryToken,
    isLoggedIn: status === AUTH_STATUS.LOGGED_IN,
    signInWithProvider,
    completeSignup,
    logout,
    resetToLoggedOut,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}
