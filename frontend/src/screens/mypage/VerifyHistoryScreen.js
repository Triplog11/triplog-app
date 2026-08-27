import React from 'react';
import { StyleSheet, View } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import CustomText from '../../components/common/CustomText';
import theme from '../../theme/theme';
import ReviewList from './components/ReviewList';

/** 인증 내역 — GET /reviews 실연동. 상단에 총 인증 수 요약을 보여준다. */
export default function VerifyHistoryScreen() {
  const renderHeader = (totalElements) => (
    <View style={styles.summary}>
      <CustomText variant="Caption" color={theme.colors.textSecondary}>총 인증</CustomText>
      <CustomText variant="Heading/H4" color={theme.colors.text} style={styles.bold}>
        {totalElements != null ? `${totalElements}회` : '--'}
      </CustomText>
    </View>
  );

  return (
    <SafeAreaView style={styles.container} edges={['bottom']}>
      <ReviewList
        renderHeader={renderHeader}
        emptyText="아직 인증 내역이 없어요. 첫 번째 랜드마크를 인증해 보세요!"
      />
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: theme.colors.surface,
  },
  summary: {
    alignItems: 'center',
    gap: 3,
    backgroundColor: theme.colors.canvas,
    borderRadius: theme.rounded.card,
    borderWidth: 1,
    borderColor: theme.colors.border,
    paddingVertical: theme.spacing.base,
    marginBottom: theme.spacing.sm,
  },
  bold: {
    fontWeight: 'bold',
  },
});
