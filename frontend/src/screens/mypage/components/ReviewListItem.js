import React from 'react';
import { StyleSheet, View, TouchableOpacity, Image } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import CustomText from '../../../components/common/CustomText';
import theme from '../../../theme/theme';
import { formatDate, formatReward } from '../utils/format';

/** 여행 기록/인증 내역 목록의 한 줄 — GET /reviews items 한 건 */
export default function ReviewListItem({ item, onPress }) {
  const title = item.reviewTitle || item.contentTitle || '여행 기록';
  const reward = formatReward(item.acquiredXp, item.acquiredScore);
  const date = formatDate(item.createdAt);

  return (
    <TouchableOpacity style={styles.card} onPress={() => onPress?.(item)} activeOpacity={0.8}>
      {item.imageUrl ? (
        <Image source={{ uri: item.imageUrl }} style={styles.thumb} resizeMode="cover" />
      ) : (
        <View style={[styles.thumb, styles.thumbPlaceholder]}>
          <Ionicons name="image-outline" size={20} color={theme.colors.textMuted} />
        </View>
      )}
      <View style={styles.body}>
        <CustomText variant="Label/Medium" color={theme.colors.text} style={styles.title} numberOfLines={1}>
          {title}
        </CustomText>
        {item.contentTitle && item.contentTitle !== title ? (
          <CustomText variant="Caption" color={theme.colors.textSecondary} numberOfLines={1}>
            {item.contentTitle}
          </CustomText>
        ) : null}
        <CustomText variant="Caption" color={theme.colors.textSecondary} numberOfLines={1}>
          {[item.regionName, date].filter(Boolean).join(' · ')}
        </CustomText>
        {reward ? (
          <CustomText variant="Caption" color={theme.colors.primary} style={styles.reward}>
            {reward}
          </CustomText>
        ) : null}
      </View>
      <Ionicons name="chevron-forward" size={16} color={theme.colors.textMuted} />
    </TouchableOpacity>
  );
}

const styles = StyleSheet.create({
  card: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: theme.spacing.md,
    backgroundColor: theme.colors.canvas,
    borderRadius: theme.rounded.card,
    borderWidth: 1,
    borderColor: theme.colors.border,
    padding: theme.spacing.md,
  },
  thumb: {
    width: 64,
    height: 64,
    borderRadius: theme.rounded.md,
    backgroundColor: theme.colors.surfaceDim,
  },
  thumbPlaceholder: {
    justifyContent: 'center',
    alignItems: 'center',
  },
  body: {
    flex: 1,
    gap: 2,
  },
  title: {
    fontWeight: 'bold',
  },
  reward: {
    fontWeight: 'bold',
    marginTop: 2,
  },
});
