import React from 'react';
import { StyleSheet, View, TouchableOpacity } from 'react-native';
import CustomText from '../../../components/common/CustomText';
import theme from '../../../theme/theme';

export const TERMS_ITEMS = [
  { key: 'terms', label: '[필수] 서비스 이용약관 동의', document: 'terms', title: '서비스 이용약관' },
  { key: 'privacy', label: '[필수] 개인정보 수집 및 이용 동의', document: 'privacy', title: '개인정보 처리방침' },
];

export const EMPTY_AGREEMENT = { terms: false, privacy: false };

export function isAllAgreed(agreement) {
  return TERMS_ITEMS.every((item) => agreement[item.key]);
}

/**
 * 가입 화면 인라인 약관 동의 (TermsScreen 패턴 축약판).
 * @param {{terms: boolean, privacy: boolean}} agreement
 * @param {(next: object) => void} onChange 새 객체를 넘긴다 (불변)
 * @param {(params: {document: string, title: string}) => void} onViewDocument 약관 전문 열기
 */
export default function TermsAgreement({ agreement, onChange, onViewDocument }) {
  const allChecked = isAllAgreed(agreement);

  const toggleAll = () => {
    const next = !allChecked;
    onChange(Object.fromEntries(TERMS_ITEMS.map((item) => [item.key, next])));
  };

  const toggleOne = (key) => {
    onChange({ ...agreement, [key]: !agreement[key] });
  };

  return (
    <View>
      <TouchableOpacity
        style={[styles.allRow, allChecked && styles.allRowActive]}
        onPress={toggleAll}
        activeOpacity={0.8}
        accessibilityRole="checkbox"
        accessibilityState={{ checked: allChecked }}
      >
        <Check checked={allChecked} />
        <CustomText
          variant="UI/Button"
          color={allChecked ? theme.colors.primaryDark : theme.colors.textSecondary}
          style={styles.bold}
        >
          전체 약관에 동의합니다
        </CustomText>
      </TouchableOpacity>

      {TERMS_ITEMS.map((item) => (
        <TouchableOpacity
          key={item.key}
          style={styles.itemRow}
          onPress={() => toggleOne(item.key)}
          activeOpacity={0.7}
          accessibilityRole="checkbox"
          accessibilityState={{ checked: !!agreement[item.key] }}
        >
          <Check checked={!!agreement[item.key]} />
          <CustomText variant="Body/Small" color={theme.colors.text} style={styles.itemLabel}>
            {item.label}
          </CustomText>
          {onViewDocument ? (
            <TouchableOpacity
              onPress={() => onViewDocument({ document: item.document, title: item.title })}
              hitSlop={{ top: 8, bottom: 8, left: 8, right: 8 }}
              accessibilityRole="button"
              accessibilityLabel={`${item.title} 전문 보기`}
            >
              <CustomText variant="Body/Small" color={theme.colors.textMuted} style={styles.viewLink}>
                보기
              </CustomText>
            </TouchableOpacity>
          ) : null}
        </TouchableOpacity>
      ))}
    </View>
  );
}

function Check({ checked }) {
  return (
    <View style={[styles.circle, checked && styles.circleActive]}>
      {checked && <CustomText variant="Label/Small" color="#FFFFFF">✓</CustomText>}
    </View>
  );
}

const styles = StyleSheet.create({
  bold: { fontWeight: 'bold' },
  itemLabel: { flex: 1 },
  viewLink: { textDecorationLine: 'underline' },
  allRow: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingVertical: 14,
    paddingHorizontal: 16,
    borderRadius: theme.rounded.card,
    backgroundColor: theme.colors.surface,
    borderWidth: 1,
    borderColor: theme.colors.border,
    marginBottom: 8,
    gap: 12,
  },
  allRowActive: {
    backgroundColor: theme.colors.primarySoft,
    borderColor: theme.colors.primary,
  },
  itemRow: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 12,
    paddingHorizontal: 16,
    minHeight: 44,
  },
  circle: {
    width: 22,
    height: 22,
    borderRadius: 11,
    borderWidth: 2,
    borderColor: theme.colors.borderStrong,
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: theme.colors.white,
  },
  circleActive: {
    borderColor: theme.colors.primary,
    backgroundColor: theme.colors.primary,
  },
});
