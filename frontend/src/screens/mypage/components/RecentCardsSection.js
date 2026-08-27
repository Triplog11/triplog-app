import React from 'react';
import { StyleSheet, View, TouchableOpacity, Image, ActivityIndicator } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import CustomText from '../../../components/common/CustomText';
import theme from '../../../theme/theme';
import { GRADE_CONFIG } from '../../../data/collection';
import { formatDate } from '../utils/format';

/** 백엔드 cardTier 문자열(대소문자 무관)을 GRADE_CONFIG 키로 정규화 */
function toGradeKey(tier) {
  if (!tier) return null;
  const normalized = String(tier).toLowerCase();
  return Object.keys(GRADE_CONFIG).find((key) => key.toLowerCase() === normalized) ?? null;
}

/**
 * 최근 획득 카드 — GET /landmarks/me 최신 6장.
 * @param {{cards: Array, loading: boolean, errorMessage: string|null}} props
 */
export default function RecentCardsSection({ cards, loading, errorMessage, onMorePress }) {
  return (
    <View style={styles.card}>
      <View style={styles.header}>
        <CustomText variant="Heading/H4" color={theme.colors.text} style={styles.title}>
          최근 획득 카드
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
      ) : cards.length === 0 ? (
        <CustomText variant="Body/Small" color={theme.colors.textSecondary} style={styles.stateText}>
          아직 획득한 카드가 없어요. 랜드마크를 인증하면 카드가 담겨요!
        </CustomText>
      ) : (
        <View style={styles.grid}>
          {cards.map((item) => <CardTile key={item.cardId} item={item} />)}
        </View>
      )}
    </View>
  );
}

function CardTile({ item }) {
  const gradeKey = toGradeKey(item.cardTier);
  const grade = gradeKey ? GRADE_CONFIG[gradeKey] : null;
  return (
    <View style={styles.tile}>
      {item.cardUrl ? (
        <Image source={{ uri: item.cardUrl }} style={styles.thumb} resizeMode="cover" />
      ) : (
        <View style={[styles.thumb, styles.thumbPlaceholder]}>
          <Ionicons name="image-outline" size={24} color={theme.colors.textMuted} />
        </View>
      )}
      {grade && (
        <View style={[styles.gradePill, { backgroundColor: grade.color }]}>
          <CustomText variant="Caption" color={theme.colors.white} style={styles.gradeText}>
            {grade.label}
          </CustomText>
        </View>
      )}
      <CustomText variant="Label/Medium" color={theme.colors.text} style={styles.name} numberOfLines={1}>
        {item.cardName ?? item.landmarkName}
      </CustomText>
      <CustomText variant="Caption" color={theme.colors.textSecondary} style={styles.meta} numberOfLines={1}>
        {formatDate(item.acquiredAt) || item.landmarkName || ''}
      </CustomText>
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
  header: {
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
  grid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: theme.spacing.sm,
  },
  tile: {
    width: '31%',
    flexGrow: 1,
    backgroundColor: theme.colors.canvas,
    borderRadius: theme.rounded.md,
    borderWidth: 1,
    borderColor: theme.colors.border,
    overflow: 'hidden',
    paddingBottom: theme.spacing.sm,
  },
  thumb: {
    width: '100%',
    aspectRatio: 1,
    backgroundColor: theme.colors.surfaceDim,
    marginBottom: theme.spacing.sm,
  },
  thumbPlaceholder: {
    justifyContent: 'center',
    alignItems: 'center',
  },
  gradePill: {
    position: 'absolute',
    top: 6,
    left: 6,
    borderRadius: theme.rounded.sm,
    paddingHorizontal: 6,
    paddingVertical: 1,
  },
  gradeText: {
    fontWeight: 'bold',
    fontSize: 10,
  },
  name: {
    fontWeight: 'bold',
    paddingHorizontal: theme.spacing.sm,
  },
  meta: {
    paddingHorizontal: theme.spacing.sm,
  },
});
