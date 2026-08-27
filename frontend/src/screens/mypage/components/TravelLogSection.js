import React from 'react';
import { StyleSheet, View, TouchableOpacity, Image, ActivityIndicator } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import CustomText from '../../../components/common/CustomText';
import theme from '../../../theme/theme';
import { formatDate, formatReward } from '../utils/format';

/**
 * 나의 여행 기록 — GET /reviews 최근 3건 타임라인.
 * 정책(서팍 피그마 디스크립션 #3): 좌측 축에 총 인증 수, 우측에 최근 기록.
 * @param {{reviews: Array, totalCount: number|null, loading: boolean, errorMessage: string|null}} props
 */
export default function TravelLogSection({
  reviews, totalCount, loading, errorMessage, onMorePress, onEntryPress,
}) {
  const latestDate = reviews.length > 0 ? formatDate(reviews[0].createdAt) : null;

  return (
    <View style={styles.card}>
      <View style={styles.headerRow}>
        <CustomText variant="Heading/H4" color={theme.colors.text} style={styles.title}>
          나의 여행 기록
        </CustomText>
        <TouchableOpacity style={styles.moreRow} onPress={onMorePress} activeOpacity={0.7}>
          <CustomText variant="Body/Small" color={theme.colors.textSecondary}>더보기</CustomText>
          <Ionicons name="chevron-forward" size={14} color={theme.colors.textSecondary} />
        </TouchableOpacity>
      </View>

      {loading ? (
        <ActivityIndicator size="small" color={theme.colors.primary} style={styles.stateBox} />
      ) : errorMessage ? (
        <CustomText variant="Body/Small" color={theme.colors.textMuted} style={styles.stateText}>
          {errorMessage}
        </CustomText>
      ) : reviews.length === 0 ? (
        <CustomText variant="Body/Small" color={theme.colors.textSecondary} style={styles.stateText}>
          아직 남긴 기록이 없어요. 첫 번째 모험을 시작해 보세요!
        </CustomText>
      ) : (
        <View style={styles.timelineRow}>
          <View style={styles.axis}>
            <View style={styles.axisDot} />
            <View style={styles.axisLine} />
            <View style={styles.axisCountWrap}>
              <Ionicons name="checkmark-circle" size={16} color={theme.colors.textMuted} />
              <CustomText variant="Caption" color={theme.colors.textMuted}>
                {totalCount != null ? `${totalCount}개` : '--'}
              </CustomText>
            </View>
            <View style={styles.axisLineShort} />
          </View>

          <View style={styles.entries}>
            {latestDate ? (
              <CustomText variant="Label/Medium" color={theme.colors.text} style={styles.date}>
                {latestDate}
              </CustomText>
            ) : null}
            {reviews.map((entry) => (
              <TouchableOpacity
                key={entry.reviewId}
                style={styles.entry}
                onPress={() => onEntryPress?.(entry)}
                activeOpacity={0.7}
              >
                <View style={styles.entryHeader}>
                  <CustomText variant="Body/Medium" color={theme.colors.text} style={styles.place} numberOfLines={1}>
                    {entry.reviewTitle || entry.contentTitle || '여행 기록'}
                  </CustomText>
                  {entry.regionName ? (
                    <View style={styles.regionTag}>
                      <CustomText variant="Caption" color={theme.colors.textSecondary}>
                        {entry.regionName}
                      </CustomText>
                    </View>
                  ) : null}
                </View>
                <View style={styles.entryBody}>
                  <CustomText variant="Body/Small" color={theme.colors.textSecondary} style={styles.reward}>
                    {formatReward(entry.acquiredXp, entry.acquiredScore) ?? formatDate(entry.createdAt)}
                  </CustomText>
                  {entry.imageUrl ? (
                    <Image source={{ uri: entry.imageUrl }} style={styles.photoThumb} resizeMode="cover" />
                  ) : null}
                </View>
              </TouchableOpacity>
            ))}
          </View>
        </View>
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  card: {
    backgroundColor: theme.colors.canvas,
    borderRadius: theme.rounded.card,
    borderWidth: 1,
    borderColor: theme.colors.border,
    padding: theme.spacing.lg,
  },
  headerRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: theme.spacing.base,
  },
  title: {
    fontWeight: 'bold',
  },
  moreRow: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 2,
  },
  stateBox: {
    paddingVertical: theme.spacing.lg,
  },
  stateText: {
    textAlign: 'center',
    paddingVertical: theme.spacing.md,
  },
  timelineRow: {
    flexDirection: 'row',
  },
  axis: {
    width: 28,
    alignItems: 'center',
  },
  axisDot: {
    width: 10,
    height: 10,
    borderRadius: 5,
    backgroundColor: theme.colors.primary,
    marginTop: 5,
  },
  axisLine: {
    width: 2,
    flex: 1,
    backgroundColor: theme.colors.border,
    marginVertical: 4,
  },
  axisCountWrap: {
    alignItems: 'center',
    gap: 1,
  },
  axisLineShort: {
    width: 2,
    height: 24,
    backgroundColor: theme.colors.border,
    marginTop: 4,
  },
  entries: {
    flex: 1,
    marginLeft: theme.spacing.sm,
  },
  date: {
    fontWeight: 'bold',
    marginBottom: theme.spacing.base,
  },
  entry: {
    marginBottom: theme.spacing.base,
  },
  entryHeader: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 6,
  },
  place: {
    fontWeight: 'bold',
    flexShrink: 1,
  },
  regionTag: {
    backgroundColor: theme.colors.surface,
    borderRadius: 4,
    paddingHorizontal: 6,
    paddingVertical: 2,
  },
  entryBody: {
    flexDirection: 'row',
    alignItems: 'center',
    marginTop: 6,
  },
  reward: {
    flex: 1,
  },
  photoThumb: {
    width: 32,
    height: 32,
    borderRadius: 8,
    backgroundColor: theme.colors.surfaceDim,
    marginLeft: theme.spacing.sm,
  },
});
