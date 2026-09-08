import React, { useState } from 'react';
import {
  StyleSheet, View, ScrollView, TextInput, TouchableOpacity, ActivityIndicator,
} from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import CustomText from '../../components/common/CustomText';
import theme from '../../theme/theme';
import { withdrawAccount } from '../../api/users';
import { useAuth } from '../../context/AuthContext';

/** 탈퇴 시 함께 사라지는 항목. 되돌릴 수 없다는 사실을 구체적으로 알린다. */
const DELETED_ITEMS = [
  '수집한 랜드마크 카드와 도감 기록',
  '작성한 여행 기록과 방문 인증 이력',
  '획득한 뱃지와 칭호, 레벨과 점수',
  '랭킹 기록과 미션 진행 상황',
];

const EMAIL_PATTERN = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

/**
 * 회원 탈퇴 화면입니다.
 * 본인 확인을 위해 가입한 이메일을 직접 입력받고, 동의 체크까지 마쳐야 진행할 수 있습니다.
 */
export default function WithdrawScreen({ navigation }) {
  const { logout } = useAuth();
  const [email, setEmail] = useState('');
  const [agreed, setAgreed] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [errorMessage, setErrorMessage] = useState(null);

  const emailValid = EMAIL_PATTERN.test(email.trim());
  const canSubmit = emailValid && agreed && !submitting;

  const handleWithdraw = async () => {
    if (!canSubmit) return;
    setSubmitting(true);
    setErrorMessage(null);
    try {
      await withdrawAccount(email.trim());
      // 서버 계정이 사라졌으므로 로그아웃 요청 없이 로컬 세션만 정리한다.
      await logout({ skipServer: true });
    } catch (error) {
      setSubmitting(false);
      setErrorMessage(
        error?.message
          ?? '지금은 탈퇴 처리를 할 수 없습니다. 잠시 후 다시 시도해 주십시오.',
      );
    }
  };

  return (
    <ScrollView
      style={styles.container}
      contentContainerStyle={styles.content}
      keyboardShouldPersistTaps="handled"
      showsVerticalScrollIndicator={false}
    >
      <CustomText variant="Heading/H4" color={theme.colors.text} style={styles.title}>
        정말 탈퇴하시겠습니까?
      </CustomText>
      <CustomText variant="Body/Small" color={theme.colors.textSecondary} style={styles.subtitle}>
        탈퇴하시면 아래 정보가 모두 삭제되며 복구할 수 없습니다.
      </CustomText>

      <View style={styles.noticeBox}>
        {DELETED_ITEMS.map((item) => (
          <View key={item} style={styles.noticeRow}>
            <Ionicons name="close-circle" size={16} color={theme.colors.error} />
            <CustomText variant="Body/Small" color={theme.colors.textBody} style={styles.noticeText}>
              {item}
            </CustomText>
          </View>
        ))}
      </View>

      <CustomText variant="Body/Small" color={theme.colors.textMuted} style={styles.retentionNote}>
        관계 법령에 따라 분쟁 처리 기록과 접속 기록은 정해진 기간 동안 보관한 뒤 파기합니다.
        자세한 내용은 개인정보 처리방침에서 확인하실 수 있습니다.
      </CustomText>

      <View style={styles.field}>
        <CustomText variant="Label/Medium" color={theme.colors.text} style={styles.label}>
          가입하신 이메일
        </CustomText>
        <TextInput
          style={[styles.input, errorMessage ? styles.inputError : null]}
          value={email}
          onChangeText={(text) => {
            setEmail(text);
            if (errorMessage) setErrorMessage(null);
          }}
          placeholder="example@triplog.kr"
          placeholderTextColor={theme.colors.textMuted}
          keyboardType="email-address"
          autoCapitalize="none"
          autoCorrect={false}
          editable={!submitting}
        />
        <CustomText variant="Body/Small" color={theme.colors.textMuted} style={styles.hint}>
          본인 확인을 위해 가입하신 이메일 주소를 입력해 주십시오.
        </CustomText>
      </View>

      <TouchableOpacity
        style={styles.agreeRow}
        onPress={() => setAgreed((prev) => !prev)}
        activeOpacity={0.8}
        disabled={submitting}
      >
        <Ionicons
          name={agreed ? 'checkbox' : 'square-outline'}
          size={22}
          color={agreed ? theme.colors.primary : theme.colors.textMuted}
        />
        <CustomText variant="Body/Small" color={theme.colors.textBody} style={styles.agreeText}>
          위 내용을 모두 확인했으며, 데이터가 삭제되는 것에 동의합니다.
        </CustomText>
      </TouchableOpacity>

      {errorMessage ? (
        <CustomText variant="Body/Small" color={theme.colors.error} style={styles.errorText}>
          {errorMessage}
        </CustomText>
      ) : null}

      <TouchableOpacity
        style={[styles.withdrawButton, !canSubmit && styles.buttonDisabled]}
        onPress={handleWithdraw}
        disabled={!canSubmit}
        activeOpacity={0.9}
      >
        {submitting ? (
          <ActivityIndicator color={theme.colors.white} />
        ) : (
          <CustomText variant="UI/Button" color={theme.colors.white} style={styles.buttonLabel}>
            탈퇴합니다
          </CustomText>
        )}
      </TouchableOpacity>

      <TouchableOpacity
        style={styles.cancelButton}
        onPress={() => navigation.goBack()}
        disabled={submitting}
        activeOpacity={0.8}
      >
        <CustomText variant="UI/Button" color={theme.colors.textSecondary} style={styles.buttonLabel}>
          돌아가기
        </CustomText>
      </TouchableOpacity>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: theme.colors.white,
  },
  content: {
    paddingHorizontal: theme.spacing.lg,
    paddingTop: theme.spacing.lg,
    paddingBottom: 104,
  },
  title: {
    fontWeight: 'bold',
  },
  subtitle: {
    marginTop: 6,
  },
  noticeBox: {
    marginTop: theme.spacing.lg,
    padding: theme.spacing.base,
    backgroundColor: theme.colors.surface,
    borderRadius: theme.rounded.card,
    borderWidth: 1,
    borderColor: theme.colors.border,
  },
  noticeRow: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: theme.spacing.sm,
    paddingVertical: 6,
  },
  noticeText: {
    flex: 1,
  },
  retentionNote: {
    marginTop: theme.spacing.base,
    lineHeight: 20,
  },
  field: {
    marginTop: theme.spacing.xl,
  },
  label: {
    fontWeight: 'bold',
    marginBottom: theme.spacing.sm,
  },
  input: {
    height: 52,
    borderWidth: 1,
    borderColor: theme.colors.border,
    borderRadius: theme.rounded.md,
    paddingHorizontal: theme.spacing.base,
    fontSize: 15,
    color: theme.colors.text,
    backgroundColor: theme.colors.canvas,
  },
  inputError: {
    borderColor: theme.colors.error,
    borderWidth: 2,
  },
  hint: {
    marginTop: 6,
  },
  agreeRow: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: theme.spacing.sm,
    marginTop: theme.spacing.lg,
  },
  agreeText: {
    flex: 1,
  },
  errorText: {
    marginTop: theme.spacing.base,
  },
  withdrawButton: {
    height: 52,
    borderRadius: theme.rounded.cta,
    backgroundColor: theme.colors.error,
    justifyContent: 'center',
    alignItems: 'center',
    marginTop: theme.spacing.xl,
  },
  buttonDisabled: {
    backgroundColor: theme.colors.border,
  },
  cancelButton: {
    height: 48,
    justifyContent: 'center',
    alignItems: 'center',
    marginTop: theme.spacing.sm,
  },
  buttonLabel: {
    fontWeight: 'bold',
  },
});
