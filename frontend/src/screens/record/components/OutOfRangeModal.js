import React from 'react';
import { StyleSheet, View, TouchableOpacity, Modal, Pressable } from 'react-native';
import CustomText from '../../../components/common/CustomText';
import theme from '../../../theme/theme';
import { formatDistance } from '../../../utils/geo';

/**
 * 반경 이탈 — 토스트가 아닌 모달 (DESIGN.md §14). 원인 + 해결 방법만 담담하게.
 * @param remainingM 반경까지 더 가야 하는 거리(m)
 */
export default function OutOfRangeModal({ visible, radiusM, remainingM, onRetry, onClose }) {
  return (
    <Modal visible={visible} transparent animationType="fade" onRequestClose={onClose}>
      <Pressable style={styles.backdrop} onPress={onClose}>
        <Pressable style={styles.card} onPress={() => {}}>
          <CustomText variant="Heading/H5" color={theme.colors.text} style={styles.bold}>
            현재 위치가 랜드마크 반경 밖에 있어요
          </CustomText>
          <CustomText variant="Body/Small" color={theme.colors.textSecondary} style={styles.body}>
            {radiusM}m 안으로 다가가서 다시 시도해 주세요.
            {remainingM ? ` 지금은 ${formatDistance(remainingM)} 더 가야 해요.` : ''}
          </CustomText>
          <View style={styles.actions}>
            <TouchableOpacity style={styles.ghostBtn} onPress={onClose} activeOpacity={0.8}>
              <CustomText variant="UI/Button/Small" color={theme.colors.textSecondary} style={styles.bold}>
                닫기
              </CustomText>
            </TouchableOpacity>
            <TouchableOpacity style={styles.primaryBtn} onPress={onRetry} activeOpacity={0.9}>
              <CustomText variant="UI/Button/Small" color="#FFFFFF" style={styles.bold}>
                다시 시도
              </CustomText>
            </TouchableOpacity>
          </View>
        </Pressable>
      </Pressable>
    </Modal>
  );
}

const styles = StyleSheet.create({
  backdrop: {
    flex: 1,
    backgroundColor: 'rgba(0,0,0,0.5)',
    justifyContent: 'center',
    paddingHorizontal: theme.spacing.xl,
  },
  card: {
    backgroundColor: theme.colors.canvas,
    borderRadius: theme.rounded.card,
    padding: theme.spacing.lg,
  },
  bold: {
    fontWeight: 'bold',
  },
  body: {
    marginTop: theme.spacing.sm,
  },
  actions: {
    flexDirection: 'row',
    gap: theme.spacing.sm,
    marginTop: theme.spacing.lg,
  },
  ghostBtn: {
    flex: 1,
    height: 46,
    borderRadius: theme.rounded.md,
    backgroundColor: theme.colors.surfaceDim,
    justifyContent: 'center',
    alignItems: 'center',
  },
  primaryBtn: {
    flex: 1,
    height: 46,
    borderRadius: theme.rounded.md,
    backgroundColor: theme.colors.primary,
    justifyContent: 'center',
    alignItems: 'center',
  },
});
