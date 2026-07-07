import React from 'react';
import { StyleSheet, View, Text, Modal, Pressable } from 'react-native';
import { Feather } from '@expo/vector-icons';
import PressableScale from '../../../components/common/PressableScale';
import theme from '../../../theme/theme';

/**
 * 위치 권한 사전 안내 모달 — 시스템 팝업 전에 왜 필요한지 한국어로 설명하는
 * 일반적인 국내 앱 패턴. (DESIGN.md §4 Overlays / §10 Voice)
 */
export default function LocationPermissionModal({ visible, onAllow, onLater }) {
  return (
    <Modal visible={visible} transparent animationType="fade" onRequestClose={onLater}>
      <Pressable style={styles.backdrop} onPress={onLater}>
        <Pressable style={styles.card} onPress={() => {}}>
          <View style={styles.iconCircle}>
            <Feather name="map-pin" size={26} color={theme.colors.primary} />
          </View>
          <Text style={styles.title}>위치를 알려주시겠어요?</Text>
          <Text style={styles.body}>
            현재 위치로 지도를 움직이고,{'\n'}랜드마크 방문을 인증하는 데 사용해요.{'\n'}위치는
            지도와 인증 순간에만 확인해요.
          </Text>
          <PressableScale style={styles.allowBtn} onPress={onAllow} accessibilityLabel="위치 허용">
            <Text style={styles.allowBtnText}>좋아요</Text>
          </PressableScale>
          <Pressable style={styles.laterBtn} onPress={onLater} accessibilityRole="button">
            <Text style={styles.laterBtnText}>다음에 할게요</Text>
          </Pressable>
        </Pressable>
      </Pressable>
    </Modal>
  );
}

const styles = StyleSheet.create({
  backdrop: {
    flex: 1,
    backgroundColor: 'rgba(0,0,0,0.5)',
    alignItems: 'center',
    justifyContent: 'center',
    padding: 24,
  },
  card: {
    width: '100%',
    maxWidth: 320,
    backgroundColor: theme.colors.white,
    borderRadius: theme.rounded.lg,
    paddingVertical: 28,
    paddingHorizontal: 24,
    alignItems: 'center',
    ...theme.shadow.outlined,
  },
  iconCircle: {
    width: 56,
    height: 56,
    borderRadius: 28,
    backgroundColor: theme.colors.primarySoft,
    alignItems: 'center',
    justifyContent: 'center',
    marginBottom: 16,
  },
  title: {
    fontFamily: theme.typography.fontFamily.bold,
    fontSize: theme.typography.size.title,
    fontWeight: '700',
    color: theme.colors.text,
    marginBottom: 10,
  },
  body: {
    fontFamily: theme.typography.fontFamily.regular,
    fontSize: theme.typography.size.body,
    lineHeight: Math.round(theme.typography.size.body * 1.57),
    color: theme.colors.textBody,
    textAlign: 'center',
    marginBottom: 22,
  },
  allowBtn: {
    width: '100%',
    height: 48,
    borderRadius: theme.rounded.md,
    backgroundColor: theme.colors.primary,
    alignItems: 'center',
    justifyContent: 'center',
  },
  allowBtnText: {
    fontFamily: theme.typography.fontFamily.bold,
    fontSize: theme.typography.size.bodyLarge,
    fontWeight: '700',
    color: theme.colors.white,
  },
  laterBtn: {
    marginTop: 12,
    paddingVertical: 8,
    paddingHorizontal: 16,
  },
  laterBtnText: {
    fontFamily: theme.typography.fontFamily.regular,
    fontSize: theme.typography.size.body,
    fontWeight: '600',
    color: theme.colors.textMuted,
  },
});
