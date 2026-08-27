import React, { useState, useEffect } from 'react';
import {
  StyleSheet, View, Modal, Pressable, TouchableOpacity, Image, ActivityIndicator,
} from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import CustomText from '../../../components/common/CustomText';
import theme from '../../../theme/theme';
import { fetchReviewDetail } from '../../../api/reviews';
import { formatDate, formatReward } from '../utils/format';

/**
 * 여행 기록 상세 바텀시트 — GET /reviews/{id}/detail.
 * 목록 아이템(review)을 즉시 보여주고, 상세 응답으로 보강한다.
 */
export default function ReviewDetailModal({ review, onClose }) {
  const [detail, setDetail] = useState(null);
  const [loading, setLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState(null);

  useEffect(() => {
    setDetail(null);
    setErrorMessage(null);
    if (!review?.reviewId) return undefined;
    let mounted = true;
    setLoading(true);
    fetchReviewDetail(review.reviewId)
      .then((result) => mounted && setDetail(result ?? null))
      .catch((error) => mounted && setErrorMessage(error?.message ?? '기록을 불러오지 못했어요.'))
      .finally(() => mounted && setLoading(false));
    return () => {
      mounted = false;
    };
  }, [review?.reviewId]);

  if (!review) return null;

  const title = detail?.landmarkName ?? review.reviewTitle ?? review.contentTitle ?? '여행 기록';
  const subtitle = review.reviewTitle && review.contentTitle && review.reviewTitle !== review.contentTitle
    ? review.contentTitle
    : null;
  const regionName = detail?.regionName ?? review.regionName;
  const imageUrl = detail?.imageUrl ?? review.imageUrl;
  const reward = formatReward(detail?.acquiredXp ?? review.acquiredXp, detail?.acquiredScore ?? review.acquiredScore);
  const date = formatDate(detail?.createdAt ?? review.createdAt);

  return (
    <Modal visible transparent animationType="slide" onRequestClose={onClose}>
      <Pressable style={styles.backdrop} onPress={onClose} />
      <View style={styles.sheet}>
        <View style={styles.handle} />
        <TouchableOpacity style={styles.closeBtn} onPress={onClose} hitSlop={8} activeOpacity={0.7}>
          <Ionicons name="close" size={22} color={theme.colors.textSecondary} />
        </TouchableOpacity>

        {imageUrl ? (
          <Image source={{ uri: imageUrl }} style={styles.image} resizeMode="cover" />
        ) : (
          <View style={[styles.image, styles.imagePlaceholder]}>
            <Ionicons name="image-outline" size={32} color={theme.colors.textMuted} />
          </View>
        )}

        <CustomText variant="Heading/H3" color={theme.colors.text} style={styles.title}>
          {title}
        </CustomText>
        {subtitle ? (
          <CustomText variant="Body/Small" color={theme.colors.textSecondary}>
            {subtitle}
          </CustomText>
        ) : null}

        <View style={styles.metaRow}>
          {regionName ? (
            <View style={styles.tag}>
              <Ionicons name="location-outline" size={12} color={theme.colors.textSecondary} />
              <CustomText variant="Caption" color={theme.colors.textSecondary}>{regionName}</CustomText>
            </View>
          ) : null}
          {date ? (
            <CustomText variant="Caption" color={theme.colors.textMuted}>{date}</CustomText>
          ) : null}
        </View>

        {reward ? (
          <View style={styles.rewardBox}>
            <Ionicons name="sparkles-outline" size={16} color={theme.colors.primary} />
            <CustomText variant="Label/Medium" color={theme.colors.primary} style={styles.rewardText}>
              {reward}
            </CustomText>
          </View>
        ) : null}

        {loading && <ActivityIndicator size="small" color={theme.colors.primary} style={styles.loader} />}
        {errorMessage ? (
          <CustomText variant="Caption" color={theme.colors.textMuted} style={styles.errorText}>
            {errorMessage}
          </CustomText>
        ) : null}
      </View>
    </Modal>
  );
}

const styles = StyleSheet.create({
  backdrop: {
    flex: 1,
    backgroundColor: 'rgba(0,0,0,0.5)',
  },
  sheet: {
    backgroundColor: theme.colors.canvas,
    borderTopLeftRadius: theme.rounded.xl,
    borderTopRightRadius: theme.rounded.xl,
    padding: theme.spacing.lg,
    paddingBottom: theme.spacing.section,
    gap: theme.spacing.sm,
  },
  handle: {
    alignSelf: 'center',
    width: 40,
    height: 4,
    borderRadius: 2,
    backgroundColor: theme.colors.borderStrong,
    marginBottom: theme.spacing.sm,
  },
  closeBtn: {
    position: 'absolute',
    top: theme.spacing.base,
    right: theme.spacing.base,
    zIndex: 1,
  },
  image: {
    width: '100%',
    aspectRatio: 4 / 3,
    borderRadius: theme.rounded.lg,
    backgroundColor: theme.colors.surfaceDim,
  },
  imagePlaceholder: {
    justifyContent: 'center',
    alignItems: 'center',
  },
  title: {
    fontWeight: 'bold',
    marginTop: theme.spacing.sm,
  },
  metaRow: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: theme.spacing.sm,
  },
  tag: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 3,
    backgroundColor: theme.colors.surface,
    borderRadius: theme.rounded.sm,
    paddingHorizontal: theme.spacing.sm,
    paddingVertical: 2,
  },
  rewardBox: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: theme.spacing.sm,
    backgroundColor: theme.colors.primarySoft,
    borderRadius: theme.rounded.md,
    padding: theme.spacing.md,
    marginTop: theme.spacing.xs,
  },
  rewardText: {
    fontWeight: 'bold',
  },
  loader: {
    marginTop: theme.spacing.xs,
  },
  errorText: {
    textAlign: 'center',
  },
});
