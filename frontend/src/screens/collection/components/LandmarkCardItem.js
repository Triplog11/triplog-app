import React from 'react';
import { StyleSheet, View, TouchableOpacity } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import CustomText from '../../../components/common/CustomText';
import theme from '../../../theme/theme';
import { GRADE_CONFIG } from '../../../data/collection';
import PhotoPlaceholder from './PhotoPlaceholder';

/**
 * 도감 그리드의 랜드마크 카드 한 장.
 * 획득: 사진 + 등급 보더/뱃지 + 획득일 / 미획득: ??? + 자물쇠 (DESIGN.md §14 잠금 카드)
 */
export default function LandmarkCardItem({ card, wishlisted, onPress }) {
  const grade = GRADE_CONFIG[card.grade];
  const { obtained } = card;

  return (
    <TouchableOpacity
      style={[styles.card, { borderColor: obtained ? grade.border : theme.colors.border }]}
      onPress={onPress}
      activeOpacity={0.85}
    >
      <View style={styles.thumbWrap}>
        {obtained ? (
          <PhotoPlaceholder tint={grade.soft} icon="camera-outline" />
        ) : (
          <View style={styles.lockedThumb}>
            <Ionicons name="lock-closed" size={24} color={theme.colors.textMuted} />
          </View>
        )}
        <View style={[styles.gradeBadge, { backgroundColor: grade.soft }]}>
          <CustomText variant="Caption" color={grade.color} style={styles.gradeText}>
            {card.grade}
          </CustomText>
        </View>
        {wishlisted && (
          <View style={styles.heart}>
            <Ionicons name="heart" size={14} color={theme.colors.error} />
          </View>
        )}
      </View>

      <View style={[styles.body, !obtained && styles.bodyLocked]}>
        <CustomText
          variant="Label/Medium"
          color={obtained ? theme.colors.text : theme.colors.textMuted}
          numberOfLines={1}
          style={styles.name}
        >
          {obtained ? card.name : '???'}
        </CustomText>
        <CustomText variant="Caption" color={theme.colors.textSecondary} numberOfLines={1}>
          {obtained ? card.region : '미발견 지역'}
        </CustomText>
        <View style={styles.metaRow}>
          <Ionicons
            name={obtained ? 'checkmark-circle' : 'lock-closed'}
            size={11}
            color={obtained ? theme.colors.success : theme.colors.textMuted}
          />
          <CustomText variant="Caption" color={theme.colors.textMuted} style={styles.metaText}>
            {obtained ? card.date : '방문 후 획득'}
          </CustomText>
        </View>
      </View>
    </TouchableOpacity>
  );
}

const styles = StyleSheet.create({
  card: {
    flex: 1,
    borderWidth: 2,
    borderRadius: theme.rounded.card,
    overflow: 'hidden',
    backgroundColor: theme.colors.canvas,
  },
  thumbWrap: {
    height: 104,
  },
  lockedThumb: {
    flex: 1,
    backgroundColor: theme.colors.surfaceDim,
    justifyContent: 'center',
    alignItems: 'center',
  },
  gradeBadge: {
    position: 'absolute',
    top: 8,
    right: 8,
    borderRadius: theme.rounded.pill,
    paddingHorizontal: 8,
    paddingVertical: 2,
  },
  gradeText: {
    fontWeight: 'bold',
    fontSize: 10,
  },
  heart: {
    position: 'absolute',
    top: 8,
    left: 8,
  },
  body: {
    padding: 10,
    gap: 2,
  },
  bodyLocked: {
    backgroundColor: theme.colors.surface,
  },
  name: {
    fontWeight: 'bold',
  },
  metaRow: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 4,
    marginTop: 4,
  },
  metaText: {
    fontSize: 10,
  },
});
