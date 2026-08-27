import React, { useState } from 'react';
import {
  StyleSheet,
  View,
  Alert,
  Switch,
  KeyboardAvoidingView,
  Platform,
  ScrollView,
  TextInput,
} from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import CustomText from '../../components/common/CustomText';
import theme from '../../theme/theme';
import { useAuth } from '../../context/AuthContext';
import { signup } from '../../api/auth';
import { checkEmail, checkNickname } from '../../api/users';
import AuthField from './email/AuthField';
import AuthSubmitButton from './email/AuthSubmitButton';
import TermsAgreement, { EMPTY_AGREEMENT, isAllAgreed } from './email/TermsAgreement';
import useAvailabilityCheck from './email/useAvailabilityCheck';
import RegionPicker from '../../components/common/RegionPicker';
import {
  NICKNAME_MIN,
  NICKNAME_MAX,
  PASSWORD_MIN,
  isValidEmail,
  isValidPassword,
  isValidNickname,
  isValidAddress,
  normalizeAddress,
} from './email/validation';

const INITIAL_FORM = {
  email: '',
  password: '',
  passwordConfirm: '',
  nickname: '',
  addressDoGun: '',
  addressSi: '',
  addressGu: '',
  isNotification: true,
};

/** 이메일 회원가입 — 계정 화면이므로 합니다 체 (DESIGN §10) */
export default function EmailSignupScreen() {
  const { signInWithProvider } = useAuth();
  const [form, setForm] = useState(INITIAL_FORM);
  const [agreement, setAgreement] = useState(EMPTY_AGREEMENT);
  const [submitting, setSubmitting] = useState(false);
  const emailCheck = useAvailabilityCheck(checkEmail, '이메일 확인');
  const nicknameCheck = useAvailabilityCheck(checkNickname, '닉네임 확인');

  const setField = (key) => (value) => setForm((prev) => ({ ...prev, [key]: value }));

  const trimmedEmail = form.email.trim();
  const trimmedNickname = form.nickname.trim();
  const emailValid = isValidEmail(trimmedEmail);
  const passwordValid = isValidPassword(form.password);
  const passwordMatches = form.password === form.passwordConfirm;
  const nicknameValid = isValidNickname(trimmedNickname);
  const addressValid = isValidAddress(form);

  const canSubmit =
    emailValid &&
    emailCheck.isAvailableFor(trimmedEmail) &&
    passwordValid &&
    passwordMatches &&
    nicknameValid &&
    nicknameCheck.isAvailableFor(trimmedNickname) &&
    addressValid &&
    isAllAgreed(agreement) &&
    !submitting;

  const handleEmailChange = (text) => {
    setField('email')(text);
    emailCheck.reset();
  };
  const handleNicknameChange = (text) => {
    setField('nickname')(text);
    nicknameCheck.reset();
  };

  const handleSignup = async () => {
    if (!canSubmit) return;
    setSubmitting(true);
    try {
      const address = normalizeAddress(form);
      const result = await signup({
        nickname: trimmedNickname,
        profileUrl: null,
        ...address,
        email: trimmedEmail,
        password: form.password,
        isNotification: form.isNotification,
      });
      if (!result?.isRegister) {
        throw new Error('회원가입이 완료되지 않았습니다. 잠시 후 다시 시도해 주십시오.');
      }
      // 가입 직후 자체 로그인 → LOGGED_IN 전환 시 AppNavigator가 메인으로 이동
      await signInWithProvider({ provider: 'LOCAL', email: trimmedEmail, password: form.password });
    } catch (error) {
      Alert.alert('회원가입', error.message);
      setSubmitting(false);
    }
  };

  const emailHelper = emailCheck.helper
    ?? (trimmedEmail.length > 0 && !emailValid
      ? { state: 'error', message: '이메일 형식이 올바르지 않습니다.' }
      : null);
  const passwordHelper = form.password.length > 0 && !passwordValid
    ? { state: 'error', message: `비밀번호는 공백 없이 ${PASSWORD_MIN}자 이상 입력해 주십시오.` }
    : null;
  const confirmHelper = form.passwordConfirm.length > 0 && !passwordMatches
    ? { state: 'error', message: '비밀번호가 일치하지 않습니다.' }
    : null;
  const nicknameHelper = nicknameCheck.helper
    ?? (form.nickname.length > 0 && !nicknameValid
      ? { state: 'error', message: `닉네임은 ${NICKNAME_MIN}자 이상 ${NICKNAME_MAX}자 이하로 입력해 주십시오.` }
      : null);

  return (
    <SafeAreaView style={styles.container} edges={['bottom']}>
      <KeyboardAvoidingView
        style={styles.flex}
        behavior={Platform.OS === 'ios' ? 'padding' : undefined}
      >
        <ScrollView contentContainerStyle={styles.scroll} keyboardShouldPersistTaps="handled">
          <View style={styles.header}>
            <CustomText variant="Heading/H2" color={theme.colors.text}>
              이메일로 가입
            </CustomText>
            <CustomText variant="Body/Small" color={theme.colors.textSecondary} style={styles.subtitle}>
              계정 정보와 프로필을 입력해 주십시오.
            </CustomText>
          </View>

          <AuthField
            label="이메일"
            placeholder="example@triplog.kr"
            keyboardType="email-address"
            textContentType="emailAddress"
            autoComplete="email"
            value={form.email}
            onChangeText={handleEmailChange}
            helper={emailHelper}
            action={{
              label: emailCheck.check.state === 'checking' ? '확인 중' : '중복확인',
              disabled: !emailValid || emailCheck.check.state === 'checking',
              onPress: () => emailCheck.run(trimmedEmail),
            }}
          />
          <AuthField
            label="비밀번호"
            placeholder={`${PASSWORD_MIN}자 이상`}
            secureTextEntry
            textContentType="newPassword"
            autoComplete="new-password"
            value={form.password}
            onChangeText={setField('password')}
            helper={passwordHelper}
            style={styles.field}
          />
          <AuthField
            label="비밀번호 확인"
            placeholder="비밀번호를 한 번 더 입력"
            secureTextEntry
            textContentType="newPassword"
            autoComplete="new-password"
            value={form.passwordConfirm}
            onChangeText={setField('passwordConfirm')}
            helper={confirmHelper}
            style={styles.field}
          />
          <AuthField
            label="닉네임"
            placeholder={`한글, 영문, 숫자 조합 ${NICKNAME_MIN}~${NICKNAME_MAX}자`}
            maxLength={NICKNAME_MAX}
            value={form.nickname}
            onChangeText={handleNicknameChange}
            helper={nicknameHelper}
            style={styles.field}
            action={{
              label: nicknameCheck.check.state === 'checking' ? '확인 중' : '중복확인',
              disabled: !nicknameValid || nicknameCheck.check.state === 'checking',
              onPress: () => nicknameCheck.run(trimmedNickname),
            }}
          />

          <CustomText variant="Label/Medium" color={theme.colors.textSecondary} style={[styles.label, styles.field]}>
            거주 지역
          </CustomText>
          <RegionPicker
            value={{
              addressDoGun: form.addressDoGun,
              addressSi: form.addressSi,
              addressGu: form.addressGu,
            }}
            onSelect={(selected) => {
              setForm((prev) => ({
                ...prev,
                addressDoGun: selected.addressDoGun,
                addressSi: selected.addressSi,
                addressGu: selected.addressGu,
              }));
            }}
          />

          <View style={styles.notificationRow}>
            <View style={styles.notificationTextGroup}>
              <CustomText variant="Label/Medium" color={theme.colors.textSecondary}>
                알림 받기
              </CustomText>
              <CustomText variant="Body/Small" color={theme.colors.textMuted} style={styles.notificationDescription}>
                새로운 뱃지와 이벤트 소식을 알려드립니다.
              </CustomText>
            </View>
            <Switch
              value={form.isNotification}
              onValueChange={setField('isNotification')}
              trackColor={{ false: theme.colors.border, true: theme.colors.primary }}
              thumbColor="#FFFFFF"
            />
          </View>

          <View style={styles.field}>
            <TermsAgreement agreement={agreement} onChange={setAgreement} />
          </View>
        </ScrollView>

        <View style={styles.footer}>
          <AuthSubmitButton
            label="가입하고 시작하기"
            onPress={handleSignup}
            disabled={!canSubmit}
            loading={submitting}
          />
        </View>
      </KeyboardAvoidingView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: theme.colors.canvas,
  },
  flex: { flex: 1 },
  scroll: {
    paddingHorizontal: theme.spacing.xl,
    paddingTop: theme.spacing.xl,
    paddingBottom: theme.spacing.lg,
  },
  header: {
    marginBottom: theme.spacing.xl,
  },
  subtitle: {
    marginTop: 8,
    fontWeight: '500',
  },
  field: {
    marginTop: theme.spacing.lg,
  },
  label: {
    marginBottom: 8,
    fontWeight: '600',
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
  addressRow: {
    flexDirection: 'row',
    gap: 8,
  },
  addressInput: {
    flex: 1,
  },
  addressBottomInput: {
    marginTop: 8,
  },
  notificationRow: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    marginTop: theme.spacing.lg,
    paddingVertical: 4,
  },
  notificationTextGroup: {
    flex: 1,
    paddingRight: 16,
  },
  notificationDescription: {
    marginTop: 4,
  },
  footer: {
    padding: theme.spacing.xl,
  },
});
