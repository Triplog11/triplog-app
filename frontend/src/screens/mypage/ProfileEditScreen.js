import React, { useState } from 'react';
import {
  StyleSheet,
  View,
  TextInput,
  TouchableOpacity,
  SafeAreaView,
  Alert,
  ScrollView,
} from 'react-native';
import { useAuth } from '../../context/AuthContext';
import { checkNickname, updateProfile } from '../../api/users';
import CustomText from '../../components/common/CustomText';
import RegionPicker from '../../components/common/RegionPicker';
import theme from '../../theme/theme';

const NICKNAME_MIN = 2;
const NICKNAME_MAX = 12;

/**
 * 프로필 수정 — PATCH /users/profile.
 * 입력한 필드만 전송한다 (비워두면 기존 값 유지).
 */
export default function ProfileEditScreen({ navigation }) {
  const { user, updateUser } = useAuth();
  const [nickname, setNickname] = useState(user?.nickname ?? '');
  const [nicknameCheck, setNicknameCheck] = useState({ state: 'idle', message: '' });
  const [addressDoGun, setAddressDoGun] = useState('');
  const [addressSi, setAddressSi] = useState('');
  const [addressGu, setAddressGu] = useState('');
  const [submitting, setSubmitting] = useState(false);

  const trimmedNickname = nickname.trim();
  const isNicknameValid =
    trimmedNickname.length >= NICKNAME_MIN && trimmedNickname.length <= NICKNAME_MAX;
  const nicknameChanged = trimmedNickname !== (user?.nickname ?? '');
  const addressEntered = !!(addressSi.trim() || addressDoGun.trim() || addressGu.trim());

  const [nicknameFocused, setNicknameFocused] = useState(false);

  // 닉네임을 바꾼 경우에만 중복확인 필요
  const canSubmit =
    !submitting &&
    (nicknameChanged || addressEntered) &&
    (!nicknameChanged || (isNicknameValid && nicknameCheck.state === 'available'));

  const handleNicknameChange = (text) => {
    setNickname(text);
    setNicknameCheck({ state: 'idle', message: '' });
  };

  const handleSelectRegion = (addr) => {
    setAddressDoGun(addr.addressDoGun ?? '');
    setAddressSi(addr.addressSi ?? '');
    setAddressGu(addr.addressGu ?? '');
  };

  const handleCheckNickname = async () => {
    if (!isNicknameValid || nicknameCheck.state === 'checking') return;
    setNicknameCheck({ state: 'checking', message: '' });
    try {
      const result = await checkNickname(trimmedNickname);
      setNicknameCheck({
        state: result.available ? 'available' : 'unavailable',
        message: result.message,
      });
    } catch (error) {
      setNicknameCheck({ state: 'idle', message: '' });
      Alert.alert('닉네임 확인', error.message);
    }
  };

  const handleSubmit = async () => {
    if (!canSubmit) return;
    setSubmitting(true);
    try {
      const changes = {};
      if (nicknameChanged) changes.nickname = trimmedNickname;
      if (addressSi.trim()) changes.addressSi = addressSi.trim();
      if (addressDoGun.trim()) changes.addressDoGun = addressDoGun.trim();
      if (addressGu.trim()) changes.addressGu = addressGu.trim();

      const result = await updateProfile(changes);
      await updateUser({ nickname: result?.nickname ?? trimmedNickname });
      Alert.alert('프로필', '프로필이 수정되었어요.', [
        { text: '확인', onPress: () => navigation.goBack() },
      ]);
    } catch (error) {
      Alert.alert('프로필 수정', error.message);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <SafeAreaView style={styles.container}>
      <ScrollView contentContainerStyle={styles.scrollContent} keyboardShouldPersistTaps="handled">
        <View style={styles.content}>
          <CustomText variant="Label/Medium" color={theme.colors.textSecondary} style={styles.label}>
            닉네임
          </CustomText>
          <View style={styles.nicknameRow}>
            <TextInput
              style={[
                styles.input,
                styles.nicknameInput,
                nicknameFocused && styles.inputFocused,
              ]}
              placeholder={`한글, 영문, 숫자 조합 ${NICKNAME_MIN}~${NICKNAME_MAX}자`}
              placeholderTextColor={theme.colors.textMuted}
              value={nickname}
              onChangeText={handleNicknameChange}
              onFocus={() => setNicknameFocused(true)}
              onBlur={() => setNicknameFocused(false)}
              maxLength={NICKNAME_MAX}
            />
            <TouchableOpacity
              style={[
                styles.checkBtn,
                (!nicknameChanged || !isNicknameValid || nicknameCheck.state === 'checking') &&
                  styles.checkBtnDisabled,
              ]}
              disabled={!nicknameChanged || !isNicknameValid || nicknameCheck.state === 'checking'}
              onPress={handleCheckNickname}
              activeOpacity={0.85}
            >
              <CustomText
                variant="UI/Button/Small"
                color={nicknameChanged && isNicknameValid ? '#FFFFFF' : theme.colors.textMuted}
                style={styles.checkBtnText}
              >
                {nicknameCheck.state === 'checking' ? '확인 중...' : '중복확인'}
              </CustomText>
            </TouchableOpacity>
          </View>
          {nicknameCheck.state === 'available' && (
            <CustomText variant="Body/Small" color={theme.colors.success} style={styles.helperText}>
              ✓ {nicknameCheck.message}
            </CustomText>
          )}
          {nicknameCheck.state === 'unavailable' && (
            <CustomText variant="Body/Small" color={theme.colors.error} style={styles.helperText}>
              {nicknameCheck.message} 다른 닉네임으로 시도해 주세요.
            </CustomText>
          )}

          <CustomText
            variant="Label/Medium"
            color={theme.colors.textSecondary}
            style={[styles.label, styles.sectionGap]}
          >
            거주 지역 (변경할 때만 선택)
          </CustomText>
          <RegionPicker
            value={{ addressDoGun, addressSi, addressGu }}
            onSelect={handleSelectRegion}
            placeholder="거주 지역을 선택해 주세요"
          />
        </View>
      </ScrollView>

      <View style={styles.footer}>
        <TouchableOpacity
          style={[styles.submitBtn, !canSubmit && styles.submitBtnDisabled]}
          disabled={!canSubmit}
          onPress={handleSubmit}
          activeOpacity={0.9}
        >
          <CustomText
            variant="UI/Button"
            color={canSubmit ? '#FFFFFF' : theme.colors.textMuted}
            style={styles.submitText}
          >
            {submitting ? '저장하는 중...' : '저장하기'}
          </CustomText>
        </TouchableOpacity>
      </View>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: theme.colors.canvas,
  },
  scrollContent: {
    flexGrow: 1,
  },
  content: {
    paddingHorizontal: 24,
    paddingTop: 24,
  },
  label: {
    marginBottom: 8,
    fontWeight: '600',
  },
  sectionGap: {
    marginTop: 28,
  },
  input: {
    height: 56,
    backgroundColor: theme.colors.surface,
    borderRadius: theme.rounded.card,
    paddingHorizontal: 16,
    color: theme.colors.text,
    fontSize: 15,
    fontFamily: 'Pretendard-Regular',
    borderWidth: 1,
    borderColor: theme.colors.border,
  },
  inputFocused: {
    borderColor: theme.colors.primary,
    backgroundColor: theme.colors.canvas,
  },
  helperText: {
    marginTop: 8,
  },
  nicknameRow: {
    flexDirection: 'row',
    gap: 8,
  },
  nicknameInput: {
    flex: 1,
  },
  checkBtn: {
    width: 92,
    height: 56,
    backgroundColor: theme.colors.primary,
    borderRadius: theme.rounded.card,
    justifyContent: 'center',
    alignItems: 'center',
  },
  checkBtnDisabled: {
    backgroundColor: theme.colors.surfaceDim,
  },
  checkBtnText: {
    fontWeight: 'bold',
  },
  footer: {
    padding: 24,
  },
  submitBtn: {
    height: 56,
    backgroundColor: theme.colors.primary,
    borderRadius: theme.rounded.cta,
    justifyContent: 'center',
    alignItems: 'center',
  },
  submitBtnDisabled: {
    backgroundColor: theme.colors.surfaceDim,
  },
  submitText: {
    fontWeight: 'bold',
  },
});
