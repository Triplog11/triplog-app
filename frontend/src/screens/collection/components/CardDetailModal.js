import React, { useState, useEffect } from 'react';
import { StyleSheet, View, Modal, Pressable, TouchableOpacity, ScrollView, ActivityIndicator } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import CustomText from '../../../components/common/CustomText';
import theme from '../../../theme/theme';
import { GRADE_CONFIG, tierToGrade, formatAcquiredDate } from '../../../data/collection';
import { fetchLandmarkDetail } from '../../../api/landmarks';
import PhotoPlaceholder from './PhotoPlaceholder';

const MAX_STARS = 4;

/**
 * 카드 상세 바텀시트.
 * - card: {landmarkId?, name, region?, grade?, imageUrl?, obtained, date?}
 * - landmarkId가 있으면 GET /landmarks/{id}로 카드명·등급·이미지·획득일·방문횟수를 보강한다.
 * - 미획득 카드는 정보를 가리고 "방문 인증하러 가기" CTA를 노출한다.
 */
export default function CardDetailModal({ card, onClose, onVerifyPress }) {
  const [detail, setDetail] = useState(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    setDetail(null);
    if (!card?.landmarkId) return;
    let mounted = true;
    setLoading(true);
    fetchLandmarkDetail(card.landmarkId)
      .then((result) => mounted && setDetail(result ?? null))
      .catch(() => {})
      .finally(() => mounted && setLoading(false));
    return () => {
      mounted = false;
    };
  }, [card?.landmarkId]);

  if (!card) return null;

  const gradeKey = tierToGrade(detail?.cardTier) ?? card.grade ?? null;
  const grade = gradeKey ? GRADE_CONFIG[gradeKey] : null;
  const obtained = detail?.acquired ?? card.obtained;
  const name = detail?.cardName ?? card.name;
  const imageUrl = detail?.cardUrl ?? card.imageUrl ?? null;
  const region = detail?.regionName ?? card.region;
  const date = formatAcquiredDate(detail?.acquiredAt) ?? card.date ?? null;
  const visitCount = detail?.visitCount;

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
                <PhotoPlaceholder
                  uri={imageUrl}
                  tint={grade?.soft ?? theme.colors.primarySoft}
                  icon="camera-outline"
                  size={44}
                />
                {imageUrl ? <View style={styles.heroShade} /> : null}
                <View style={styles.heroTextWrap}>
                  {grade && (
                    <View style={[styles.gradePill, { backgroundColor: theme.colors.canvas }]}>
                      <CustomText variant="Caption" color={grade.color} style={styles.gradePillText}>
                        {grade.label}
                      </CustomText>
                    </View>
                  )}
                  <CustomText
                    variant="Heading/H3"
                    color={imageUrl ? '#FFFFFF' : theme.colors.text}
                    style={styles.heroName}
                  >
                    {name}
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
            {loading && <ActivityIndicator size="small" color={theme.colors.primary} />}
            {/* 별점 + 희귀도 (등급 있을 때) + 수집 상태 */}
            <View style={styles.ratingRow}>
              {grade ? (
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
              ) : (
                <View />
              )}
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
                  {obtained ? region ?? '위치 정보 없음' : '???'}
                </CustomText>
              </View>
              {obtained && date && (
                <View style={styles.metaItem}>
                  <Ionicons name="checkmark-circle" size={13} color={theme.colors.success} />
                  <CustomText variant="Body/Small" color={theme.colors.textSecondary}>
                    {date} 획득
                  </CustomText>
                </View>
              )}
              {obtained && visitCount != null && (
                <View style={styles.metaItem}>
                  <Ionicons name="footsteps" size={13} color={theme.colors.primary} />
                  <CustomText variant="Body/Small" color={theme.colors.textSecondary}>
                    {visitCount}회 방문
                  </CustomText>
                </View>
              )}
            </View>

            {/* 설명 */}
            <View style={styles.descBox}>
              <Ionicons name="sparkles" size={14} color={theme.colors.primary} style={styles.descIcon} />
              <CustomText variant="Body/Small" color={theme.colors.text} style={styles.descText}>
                {obtained
                  ? `${detail?.landmarkName ?? card.landmarkName ?? name}에서 방문 인증을 완료한 랜드마크 카드예요.`
                  : '이 랜드마크를 방문하면 카드 정보가 공개돼요.'}
              </CustomText>
            </View>

            {/* 등급 설명 (등급 있을 때) */}
            {grade && (
              <View style={[styles.rarityBox, { backgroundColor: grade.soft }]}>
                <CustomText variant="Body/Small" color={grade.color} style={styles.rarityText}>
                  {grade.description}
                </CustomText>
              </View>
            )}

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
  heroShade: {
    ...StyleSheet.absoluteFillObject,
    backgroundColor: 'rgba(0,0,0,0.3)',
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
    flexWrap: 'wrap',
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
