import React, { useState, useEffect } from 'react';
import {
  StyleSheet,
  View,
  TextInput,
  TouchableOpacity,
  SafeAreaView,
  Alert,
  Switch,
  ScrollView,
} from 'react-native';
import { useAuth, AUTH_STATUS } from '../../context/AuthContext';
import CustomText from '../../components/common/CustomText';
import theme from '../../theme/theme';

const NICKNAME_MIN = 2;
const NICKNAME_MAX = 12;

export default function NicknameScreen({ navigation }) {
  const { completeSignup, temporaryToken, status } = useAuth();
  const [nickname, setNickname] = useState('');
  const [addressDoGun, setAddressDoGun] = useState('');
  const [addressSi, setAddressSi] = useState('');
  const [addressGu, setAddressGu] = useState('');
  const [isNotification, setIsNotification] = useState(true);
  const [submitting, setSubmitting] = useState(false);

  // 임시 토큰 만료(5분) 시 로그인 화면으로 복귀
  // (가입 성공 시에도 temporaryToken이 비워지므로 LOGGED_IN 전환과는 구분한다)
  useEffect(() => {
    if (!temporaryToken && status !== AUTH_STATUS.LOGGED_IN) {
      Alert.alert('로그인', '로그인 유효 시간이 지났어요. 다시 로그인해 주세요.', [
        { text: '확인', onPress: () => navigation.popToTop() },
      ]);
    }
  }, [temporaryToken, status, navigation]);

  const trimmedNickname = nickname.trim();
  const isNicknameValid =
    trimmedNickname.length >= NICKNAME_MIN && trimmedNickname.length <= NICKNAME_MAX;
  const isAddressValid = !!(addressDoGun.trim() && addressSi.trim() && addressGu.trim());
  const canSubmit = isNicknameValid && isAddressValid && !submitting && !!temporaryToken;

  const handleComplete = async () => {
    if (!canSubmit) return;
    setSubmitting(true);
    try {
      await completeSignup({
        nickname: trimmedNickname,
        addressDoGun: addressDoGun.trim(),
        addressSi: addressSi.trim(),
        addressGu: addressGu.trim(),
        isNotification,
      });
      // 성공 시 status가 loggedIn으로 바뀌며 AppNavigator가 메인으로 전환
    } catch (error) {
      Alert.alert('회원가입', error.message);
      setSubmitting(false);
    }
  };

  return (
    <SafeAreaView style={styles.container}>
      <ScrollView contentContainerStyle={styles.scrollContent} keyboardShouldPersistTaps="handled">
        <View style={styles.header}>
          <CustomText variant="Heading/H2" color={theme.colors.text}>
            프로필 설정
          </CustomText>
          <CustomText variant="Body/Small" color={theme.colors.textSecondary} style={styles.headerSubtitle}>
            트립로그에서 사용할 이름과 거주 지역을 알려주세요.
          </CustomText>
        </View>

        <View style={styles.content}>
          <CustomText variant="Label/Medium" color={theme.colors.textSecondary} style={styles.label}>
            닉네임
          </CustomText>
          <TextInput
            style={styles.input}
            placeholder={`한글, 영문, 숫자 조합 ${NICKNAME_MIN}~${NICKNAME_MAX}자`}
            placeholderTextColor={theme.colors.textMuted}
            value={nickname}
            onChangeText={setNickname}
            maxLength={NICKNAME_MAX}
          />
          {nickname.length > 0 && !isNicknameValid && (
            <CustomText variant="Body/Small" color={theme.colors.error} style={styles.helperText}>
              닉네임은 {NICKNAME_MIN}자 이상 {NICKNAME_MAX}자 이하로 입력해 주세요.
            </CustomText>
          )}

          <CustomText variant="Label/Medium" color={theme.colors.textSecondary} style={[styles.label, styles.sectionGap]}>
            거주 지역
          </CustomText>
          <View style={styles.addressRow}>
            <TextInput
              style={[styles.input, styles.addressInput]}
              placeholder="도 (예: 경기도)"
              placeholderTextColor={theme.colors.textMuted}
              value={addressDoGun}
              onChangeText={setAddressDoGun}
            />
            <TextInput
              style={[styles.input, styles.addressInput]}
              placeholder="시 (예: 수원시)"
              placeholderTextColor={theme.colors.textMuted}
              value={addressSi}
              onChangeText={setAddressSi}
            />
          </View>
          <TextInput
            style={[styles.input, styles.addressBottomInput]}
            placeholder="구/군 (예: 팔달구)"
            placeholderTextColor={theme.colors.textMuted}
            value={addressGu}
            onChangeText={setAddressGu}
          />

          <View style={styles.notificationRow}>
            <View style={styles.notificationTextGroup}>
              <CustomText variant="Label/Medium" color={theme.colors.textSecondary}>
                알림 받기
              </CustomText>
              <CustomText variant="Body/Small" color={theme.colors.textMuted} style={styles.notificationDescription}>
                새로운 뱃지와 이벤트 소식을 알려드려요.
              </CustomText>
            </View>
            <Switch
              value={isNotification}
              onValueChange={setIsNotification}
              trackColor={{ false: theme.colors.border, true: theme.colors.primary }}
              thumbColor="#FFFFFF"
            />
          </View>
        </View>
      </ScrollView>

      <View style={styles.footer}>
        <TouchableOpacity
          style={[styles.completeBtn, !canSubmit && styles.completeBtnDisabled]}
          disabled={!canSubmit}
          onPress={handleComplete}
          activeOpacity={0.9}
        >
          <CustomText
            variant="UI/Button"
            color={canSubmit ? '#FFFFFF' : theme.colors.textMuted}
            style={styles.completeText}
          >
            {submitting ? '가입하는 중...' : '가입 완료'}
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
  header: {
    paddingHorizontal: 24,
    paddingTop: 30,
    paddingBottom: 20,
  },
  headerSubtitle: {
    marginTop: 8,
    fontWeight: '500',
  },
  content: {
    paddingHorizontal: 24,
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
  helperText: {
    marginTop: 8,
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
    marginTop: 28,
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
    padding: 24,
  },
  completeBtn: {
    height: 56,
    backgroundColor: theme.colors.primary,
    borderRadius: theme.rounded.cta,
    justifyContent: 'center',
    alignItems: 'center',
  },
  completeBtnDisabled: {
    backgroundColor: theme.colors.surfaceDim,
    shadowOpacity: 0,
    elevation: 0,
  },
  completeText: {
    fontWeight: 'bold',
  },
});
