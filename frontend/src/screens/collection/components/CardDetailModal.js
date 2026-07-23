import React from 'react';
import { StyleSheet, View, Modal, Pressable, TouchableOpacity, ScrollView } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import CustomText from '../../../components/common/CustomText';
import theme from '../../../theme/theme';
import { GRADE_CONFIG } from '../../../data/collection';
import PhotoPlaceholder from './PhotoPlaceholder';

const MAX_STARS = 4;

/**
 * 카드 상세 바텀시트 — 프로토타입 CardDetailModal의 RN 구현.
 * 미획득 카드는 정보를 가리고 "방문 인증하러 가기" CTA를 노출한다.
 */
export default function CardDetailModal({ card, onClose, onVerifyPress }) {
  if (!card) return null;
  const grade = GRADE_CONFIG[card.grade];
  const { obtained } = card;

  return (
    <Modal visible transparent animationType="slide" onRequestClose={onClose}>
      <Pressable style={styles.backdrop} onPress={onClose}>
        <Pressable style={styles.sheet} onPress={() => {}}>
          <TouchableOpacity style={styles.closeBtn} onPress={onClose} hitSlop={8}>
            <Ionicons name="close" size={18} color="#FFFFFF" />
          </TouchableOpacity>

          {/* 히어로 이미지 */}
          <View style={styles.hero}>
            {obtained ? (
              <>
                <PhotoPlaceholder tint={grade.soft} icon="camera-outline" size={44} />
                <View style={styles.heroTextWrap}>
                  <View style={[styles.gradePill, { backgroundColor: theme.colors.canvas }]}>
                    <CustomText variant="Caption" color={grade.color} style={styles.gradePillText}>
                      {card.grade}
                    </CustomText>
                  </View>
                  <CustomText variant="Heading/H3" color={theme.colors.text} style={styles.heroName}>
                    {card.name}
                  </CustomText>
                </View>
              </>
            ) : (
              <View style={styles.heroLocked}>
                <Ionicons name="lock-closed" size={40} color={theme.colors.textMuted} />
                <CustomText variant="Body/Medium" color={theme.colors.textMuted} style={styles.lockedLabel}>
                  미발견 카드
                </CustomText>
              </View>
            )}
          </View>

          <ScrollView contentContainerStyle={styles.content} showsVerticalScrollIndicator={false}>
            {/* 별점 + 희귀도 + 수집 상태 */}
            <View style={styles.ratingRow}>
              <View style={styles.starRow}>
                {Array.from({ length: MAX_STARS }).map((_, i) => (
                  <Ionicons
                    key={i}
                    name={i < grade.stars ? 'star' : 'star-outline'}
                    size={14}
                    color={i < grade.stars ? theme.colors.warning : theme.colors.textMuted}
                  />
                ))}
                <View style={[styles.gradePill, styles.rarityPill, { backgroundColor: grade.soft }]}>
                  <CustomText variant="Caption" color={grade.color} style={styles.gradePillText}>
                    {grade.label}
                  </CustomText>
                </View>
              </View>
              {obtained && (
                <View style={styles.collectedRow}>
                  <Ionicons name="checkmark-circle" size={14} color={theme.colors.success} />
                  <CustomText variant="Caption" color={theme.colors.success} style={styles.collectedText}>
                    수집 완료
                  </CustomText>
                </View>
              )}
            </View>

            {/* 지역 · 획득일 */}
            <View style={styles.metaRow}>
              <View style={styles.metaItem}>
                <Ionicons name="location" size={13} color={theme.colors.primary} />
                <CustomText variant="Body/Small" color={theme.colors.textSecondary}>
                  {obtained ? card.region : '???'}
                </CustomText>
              </View>
              {obtained && card.date && (
                <View style={styles.metaItem}>
                  <Ionicons name="checkmark-circle" size={13} color={theme.colors.success} />
                  <CustomText variant="Body/Small" color={theme.colors.textSecondary}>
                    {card.date} 획득
                  </CustomText>
                </View>
              )}
            </View>

            {/* 설명 */}
            <View style={styles.descBox}>
              <Ionicons name="sparkles" size={14} color={theme.colors.primary} style={styles.descIcon} />
              <CustomText variant="Body/Small" color={theme.colors.text} style={styles.descText}>
                {obtained ? card.description : '이 랜드마크를 방문하면 카드 정보가 공개됩니다.'}
              </CustomText>
            </View>

            {/* 등급 설명 */}
            <View style={[styles.rarityBox, { backgroundColor: grade.soft }]}>
              <CustomText variant="Body/Small" color={grade.color} style={styles.rarityText}>
                {grade.description}
              </CustomText>
            </View>

            {!obtained && (
              <TouchableOpacity style={styles.ctaBtn} onPress={onVerifyPress} activeOpacity={0.9}>
                <CustomText variant="UI/Button" color="#FFFFFF" style={styles.ctaText}>
                  방문 인증하러 가기
                </CustomText>
              </TouchableOpacity>
            )}
          </ScrollView>
        </Pressable>
      </Pressable>
    </Modal>
  );
}

const styles = StyleSheet.create({
  backdrop: {
    flex: 1,
    backgroundColor: 'rgba(0,0,0,0.5)',
    justifyContent: 'flex-end',
  },
  sheet: {
    backgroundColor: theme.colors.canvas,
    borderTopLeftRadius: theme.rounded.xl,
    borderTopRightRadius: theme.rounded.xl,
    overflow: 'hidden',
    maxHeight: '85%',
  },
  closeBtn: {
    position: 'absolute',
    top: 14,
    right: 14,
    zIndex: 2,
    width: 32,
    height: 32,
    borderRadius: 16,
    backgroundColor: 'rgba(0,0,0,0.28)',
    justifyContent: 'center',
    alignItems: 'center',
  },
  hero: {
    height: 200,
  },
  heroTextWrap: {
    position: 'absolute',
    left: 16,
    right: 56,
    bottom: 14,
    gap: 6,
  },
  heroName: {
    fontWeight: 'bold',
  },
  heroLocked: {
    flex: 1,
    backgroundColor: theme.colors.surfaceDim,
    justifyContent: 'center',
    alignItems: 'center',
    gap: 10,
  },
  lockedLabel: {
    fontWeight: '600',
  },
  gradePill: {
    alignSelf: 'flex-start',
    borderRadius: theme.rounded.pill,
    paddingHorizontal: 10,
    paddingVertical: 3,
  },
  gradePillText: {
    fontWeight: 'bold',
  },
  content: {
    padding: 20,
    gap: 12,
  },
  ratingRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  starRow: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 2,
  },
  rarityPill: {
    marginLeft: 6,
  },
  collectedRow: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 4,
  },
  collectedText: {
    fontWeight: '600',
  },
  metaRow: {
    flexDirection: 'row',
    gap: 14,
  },
  metaItem: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 5,
  },
  descBox: {
    flexDirection: 'row',
    backgroundColor: theme.colors.surface,
    borderRadius: theme.rounded.card,
    padding: 14,
    gap: 8,
  },
  descIcon: {
    marginTop: 2,
  },
  descText: {
    flex: 1,
  },
  rarityBox: {
    borderRadius: theme.rounded.card,
    paddingVertical: 12,
    paddingHorizontal: 16,
    alignItems: 'center',
  },
  rarityText: {
    fontWeight: '600',
  },
  ctaBtn: {
    height: 52,
    borderRadius: theme.rounded.cta,
    backgroundColor: theme.colors.primary,
    justifyContent: 'center',
    alignItems: 'center',
    marginTop: 4,
  },
  ctaText: {
    fontWeight: 'bold',
  },
});
