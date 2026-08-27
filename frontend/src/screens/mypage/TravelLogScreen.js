import React from 'react';
import { StyleSheet } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import theme from '../../theme/theme';
import ReviewList from './components/ReviewList';

/** 여행 기록 목록 — GET /reviews 실연동 (무한 스크롤 + 상세 시트) */
export default function TravelLogScreen() {
  return (
    <SafeAreaView style={styles.container} edges={['bottom']}>
      <ReviewList emptyText="아직 남긴 기록이 없어요. 인증을 마치면 기록이 쌓여요!" />
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: theme.colors.surface,
  },
});
