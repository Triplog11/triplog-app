import React, { useState } from 'react';
import {
  StyleSheet,
  View,
  TouchableOpacity,
  Alert,
  KeyboardAvoidingView,
  Platform,
  ScrollView,
} from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import CustomText from '../../components/common/CustomText';
import theme from '../../theme/theme';
import { useAuth, AUTH_STATUS } from '../../context/AuthContext';
import AuthField from './email/AuthField';
import AuthSubmitButton from './email/AuthSubmitButton';
import { isValidEmail } from './email/validation';

/** 이메일 로그인 — 계정 화면이므로 합니다 체, 위트 없음 (DESIGN §10) */
export default function EmailLoginScreen({ navigation }) {
  const { signInWithProvider } = useAuth();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [emailTouched, setEmailTouched] = useState(false);
  const [submitting, setSubmitting] = useState(false);

  const trimmedEmail = email.trim();
  const emailValid = isValidEmail(trimmedEmail);
  const canSubmit = emailValid && password.length > 0 && !submitting;

  const emailHelper =
    emailTouched && trimmedEmail.length > 0 && !emailValid
      ? { state: 'error', message: '이메일 형식이 올바르지 않습니다.' }
      : null;

  const handleLogin = async () => {
    if (!canSubmit) return;
    setSubmitting(true);
    try {
      const nextStatus = await signInWithProvider({
        provider: 'LOCAL',
        email: trimmedEmail,
        password,
      });
      // LOGGED_IN이면 AppNavigator가 메인으로 전환한다
      if (nextStatus === AUTH_STATUS.NEEDS_ADDITIONAL_INFO) {
        navigation.navigate('Terms');
      }
    } catch (error) {
      Alert.alert('로그인', error.message);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <SafeAreaView style={styles.container} edges={['bottom']}>
      <KeyboardAvoidingView
        style={styles.flex}
        behavior={Platform.OS === 'ios' ? 'padding' : undefined}
      >
        <ScrollView contentContainerStyle={styles.scroll} keyboardShouldPersistTaps="handled">
          <View style={styles.header}>
            <CustomText variant="Heading/H2" color={theme.colors.text}>
              이메일로 로그인
            </CustomText>
            <CustomText variant="Body/Small" color={theme.colors.textSecondary} style={styles.subtitle}>
              가입하신 이메일과 비밀번호를 입력해 주십시오.
            </CustomText>
          </View>

          <AuthField
            label="이메일"
            placeholder="example@triplog.kr"
            keyboardType="email-address"
            textContentType="emailAddress"
            autoComplete="email"
            value={email}
            onChangeText={setEmail}
            onBlur={() => setEmailTouched(true)}
            helper={emailHelper}
            returnKeyType="next"
          />
          <AuthField
            label="비밀번호"
            placeholder="비밀번호"
            secureTextEntry
            textContentType="password"
            autoComplete="password"
            value={password}
            onChangeText={setPassword}
            returnKeyType="done"
            onSubmitEditing={handleLogin}
            style={styles.field}
          />
        </ScrollView>

        <View style={styles.footer}>
          <AuthSubmitButton
            label="로그인"
            onPress={handleLogin}
            disabled={!canSubmit}
            loading={submitting}
          />
          <TouchableOpacity
            style={styles.signupLink}
            onPress={() => navigation.navigate('EmailSignup')}
            disabled={submitting}
            activeOpacity={0.7}
            accessibilityRole="link"
          >
            <CustomText variant="Body/Small" color={theme.colors.textSecondary}>
              아직 계정이 없으신가요?{' '}
            </CustomText>
            <CustomText variant="Body/Small" color={theme.colors.primary} style={styles.linkText}>
              이메일로 가입하기
            </CustomText>
          </TouchableOpacity>
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
  footer: {
    padding: theme.spacing.xl,
    gap: theme.spacing.base,
  },
  signupLink: {
    flexDirection: 'row',
    justifyContent: 'center',
    alignItems: 'center',
    minHeight: 44,
  },
  linkText: {
    fontWeight: 'bold',
  },
});
