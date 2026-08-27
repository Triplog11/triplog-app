import { useState, useCallback } from 'react';
import { Alert } from 'react-native';

/**
 * 이메일/닉네임 중복확인 상태 훅.
 * @param {(value: string) => Promise<{available: boolean, message: string}>} checkFn
 * @param {string} title Alert 제목 (에러 시)
 */
export default function useAvailabilityCheck(checkFn, title) {
  // state: idle | checking | available | unavailable
  const [check, setCheck] = useState({ state: 'idle', value: '', message: '' });

  const reset = useCallback(() => {
    setCheck({ state: 'idle', value: '', message: '' });
  }, []);

  const run = useCallback(async (value) => {
    setCheck({ state: 'checking', value, message: '' });
    try {
      const result = await checkFn(value);
      setCheck({
        state: result?.available ? 'available' : 'unavailable',
        value,
        message: result?.message ?? '',
      });
    } catch (error) {
      setCheck({ state: 'idle', value: '', message: '' });
      Alert.alert(title, error.message);
    }
  }, [checkFn, title]);

  /** 확인 완료된 값이 현재 입력값과 같을 때만 '사용 가능'으로 인정 */
  const isAvailableFor = (value) => check.state === 'available' && check.value === value;

  const helper = (() => {
    if (check.state === 'available') return { state: 'success', message: check.message };
    if (check.state === 'unavailable') return { state: 'error', message: check.message };
    return null;
  })();

  return { check, run, reset, isAvailableFor, helper };
}
