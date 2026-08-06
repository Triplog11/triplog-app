import React from 'react';
import { StyleSheet, View, TouchableOpacity } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import CustomText from '../../../components/common/CustomText';
import theme from '../../../theme/theme';

/**
 * 나의 여행 기록 — 가장 최근 하루치 인증 내역 타임라인.
 * 정책(서팍 피그마 디스크립션 #3):
 * - 좌측에 총 인증 장소 개수 표시
 * - 리뷰는 높이 36px 고정으로 잘라서 미리보기
 * - 사진 인증 시 우측에 사진 미리보기(+N)
 * - 카드 획득 장소는 장소명 옆에 카드 랭크 아이콘
 */
export default function TravelLogSection({ log, onMorePress }) {
  return (
    <View style={styles.card}>
      <View style={styles.headerRow}>
        <View style={styles.titleRow}>
          <CustomText variant="Heading/H4" color={theme.colors.text} style={styles.title}>
            나의 여행 기록
          </CustomText>
          <View style={styles.samplePill}>
            <CustomText variant="Caption" color={theme.colors.textSecondary}>예시</CustomText>
          </View>
        </View>
        <TouchableOpacity style={styles.moreRow} onPress={onMorePress} activeOpacity={0.7}>
          <CustomText variant="Body/Small" color={theme.colors.textSecondary}>더보기</CustomText>
          <Ionicons name="chevron-forward" size={14} color={theme.colors.textSecondary} />
        </TouchableOpacity>
      </View>

      <View style={styles.timelineRow}>
        {/* 좌측 타임라인 축 */}
        <View style={styles.axis}>
          <View style={styles.axisDot} />
          <View style={styles.axisLine} />
          <View style={styles.axisCountWrap}>
            <Ionicons name="checkmark-circle" size={16} color={theme.colors.textMuted} />
            <CustomText variant="Caption" color={theme.colors.textMuted}>{log.totalCount}개</CustomText>
          </View>
          <View style={styles.axisLineShort} />
        </View>

        {/* 우측 내역 */}
        <View style={styles.entries}>
          <CustomText variant="Label/Medium" color={theme.colors.text} style={styles.date}>
            {log.date}
          </CustomText>
          {log.entries.map((entry) => (
            <View key={entry.id} style={styles.entry}>
              <View style={styles.entryHeader}>
                <CustomText variant="Body/Medium" color={theme.colors.text} style={styles.place}>
                  {entry.place}
                </CustomText>
                {entry.cardGrade && (
                  <View style={styles.gradeBadge}>
                    <CustomText variant="Caption" color="#FFFFFF" style={styles.gradeText}>
                      {entry.cardGrade}
                    </CustomText>
                  </View>
                )}
                <View style={styles.regionTag}>
                  <CustomText variant="Caption" color={theme.colors.textSecondary}>
                    {entry.region}
                  </CustomText>
                </View>
              </View>
              {(entry.review || entry.photoCount > 0) && (
                <View style={styles.entryBody}>
                  {entry.review ? (
                    <View style={styles.reviewClip}>
                      <CustomText variant="Body/Small" color={theme.colors.textSecondary}>
                        {entry.review}
                      </CustomText>
                    </View>
                  ) : (
                    <View style={styles.reviewSpacer} />
                  )}
                  {entry.photoCount > 0 && (
                    <View style={styles.photoPreview}>
                      <View style={styles.photoThumb}>
                        <Ionicons name="image" size={14} color={theme.colors.primary} />
                      </View>
                      {entry.photoCount > 1 && (
                        <View style={[styles.photoThumb, styles.photoThumbOverlap]}>
                          <CustomText variant="Caption" color={theme.colors.primary} style={styles.photoMore}>
                            +{entry.photoCount - 1}
                          </CustomText>
                        </View>
                      )}
                    </View>
                  )}
                </View>
              )}
            </View>
          ))}
        </View>
      </View>
    </View>
  );
}

const REVIEW_CLIP_HEIGHT = 36;

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
  titleRow: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: theme.spacing.sm,
  },
  samplePill: {
    backgroundColor: theme.colors.surfaceDim,
    borderRadius: theme.rounded.pill ?? 9999,
    paddingHorizontal: 8,
    paddingVertical: 2,
  },
  moreRow: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 2,
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
    flexWrap: 'wrap',
  },
  place: {
    fontWeight: 'bold',
  },
  gradeBadge: {
    width: 18,
    height: 18,
    borderRadius: 4,
    backgroundColor: theme.colors.accentGold,
    justifyContent: 'center',
    alignItems: 'center',
  },
  gradeText: {
    fontWeight: 'bold',
    fontSize: 10,
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
  reviewClip: {
    flex: 1,
    height: REVIEW_CLIP_HEIGHT,
    overflow: 'hidden',
  },
  reviewSpacer: {
    flex: 1,
  },
  photoPreview: {
    flexDirection: 'row',
    marginLeft: theme.spacing.sm,
  },
  photoThumb: {
    width: 32,
    height: 32,
    borderRadius: 8,
    backgroundColor: theme.colors.primarySoft,
    borderWidth: 1,
    borderColor: theme.colors.border,
    justifyContent: 'center',
    alignItems: 'center',
  },
  photoThumbOverlap: {
    marginLeft: -8,
  },
  photoMore: {
    fontWeight: 'bold',
  },
});
