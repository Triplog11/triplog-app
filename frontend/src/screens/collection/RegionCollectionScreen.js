import React, { useState } from 'react';
import { StyleSheet, View, ScrollView, TouchableOpacity } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { Ionicons } from '@expo/vector-icons';
import CustomText from '../../components/common/CustomText';
import theme from '../../theme/theme';
import { PROVINCE_LANDMARK_DATA, getCardById, getProvinceProgress } from '../../data/collection';
import LandmarkCardItem from './components/LandmarkCardItem';
import CardDetailModal from './components/CardDetailModal';
import PhotoPlaceholder from './components/PhotoPlaceholder';

/** 지역 도감 상세 — 히어로 이미지 + 수집 진행률 + 랜드마크 카드 그리드 */
export default function RegionCollectionScreen({ route, navigation }) {
  const { provinceKey } = route.params ?? {};
  const [selectedCard, setSelectedCard] = useState(null);
  const province = PROVINCE_LANDMARK_DATA[provinceKey];

  if (!province) {
    navigation.goBack();
    return null;
  }

  const cards = province.cardIds.map(getCardById).filter(Boolean);
  const { collected, total, percent } = getProvinceProgress(provinceKey);
  const complete = percent === 100;

  const openVerify = () => {
    setSelectedCard(null);
    navigation.navigate('Record');
  };

  return (
    <SafeAreaView style={styles.container} edges={['top']}>
      <ScrollView showsVerticalScrollIndicator={false} contentContainerStyle={styles.scrollContent}>
        {/* 히어로 */}
        <View style={styles.hero}>
          <PhotoPlaceholder icon="map-outline" size={40} />
          <View style={styles.heroOverlay} />
          <TouchableOpacity style={styles.backBtn} onPress={() => navigation.goBack()} hitSlop={8}>
            <Ionicons name="chevron-back" size={18} color="#FFFFFF" />
          </TouchableOpacity>
          <View style={styles.heroText}>
            <CustomText variant="Heading/H4" color="#FFFFFF" style={styles.bold}>
              {province.name}
            </CustomText>
            <CustomText variant="Caption" color="rgba(255,255,255,0.85)">
              {province.region}
            </CustomText>
          </View>
        </View>

        {/* 수집 진행률 */}
        <View style={styles.progressCard}>
          <View style={styles.progressHeader}>
            <View>
              <CustomText variant="Heading/H5" color={theme.colors.text} style={styles.bold}>
                수집 진행률
              </CustomText>
              <CustomText variant="Caption" color={theme.colors.textSecondary}>
                {collected}/{total}장 수집
              </CustomText>
            </View>
            <CustomText
              variant="Heading/H3"
              color={complete ? theme.colors.success : theme.colors.primary}
              style={styles.bold}
            >
              {percent}%
            </CustomText>
          </View>
          <View style={styles.progressTrack}>
            <View
              style={[
                styles.progressFill,
                complete && styles.progressFillComplete,
                { width: `${percent}%` },
              ]}
            />
          </View>
          {complete && (
            <View style={styles.completeRow}>
              <Ionicons name="trophy" size={13} color={theme.colors.success} />
              <CustomText variant="Caption" color={theme.colors.success} style={styles.bold}>
                {province.name} 완전 정복!
              </CustomText>
            </View>
          )}
        </View>

        <CustomText variant="Body/Small" color={theme.colors.textSecondary} style={styles.description}>
          {province.description}
        </CustomText>

        {/* 카드 그리드 */}
        <CustomText variant="Heading/H5" color={theme.colors.text} style={[styles.bold, styles.gridTitle]}>
          랜드마크 카드
        </CustomText>
        <View style={styles.grid}>
          {cards.map((card) => (
            <View key={card.id} style={styles.gridItem}>
              <LandmarkCardItem card={card} wishlisted={false} onPress={() => setSelectedCard(card)} />
            </View>
          ))}
        </View>
      </ScrollView>

      <CardDetailModal card={selectedCard} onClose={() => setSelectedCard(null)} onVerifyPress={openVerify} />
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: theme.colors.surface,
  },
  scrollContent: {
    paddingBottom: 40,
  },
  hero: {
    height: 150,
  },
  heroOverlay: {
    ...StyleSheet.absoluteFillObject,
    backgroundColor: 'rgba(0,0,0,0.3)',
  },
  backBtn: {
    position: 'absolute',
    top: 12,
    left: 16,
    width: 30,
    height: 30,
    borderRadius: 15,
    backgroundColor: 'rgba(0,0,0,0.4)',
    justifyContent: 'center',
    alignItems: 'center',
  },
  heroText: {
    position: 'absolute',
    left: 16,
    right: 16,
    bottom: 12,
  },
  bold: {
    fontWeight: 'bold',
  },
  progressCard: {
    margin: theme.spacing.lg,
    marginBottom: theme.spacing.sm,
    backgroundColor: theme.colors.canvas,
    borderRadius: theme.rounded.card,
    borderWidth: 1,
    borderColor: theme.colors.border,
    padding: theme.spacing.base,
  },
  progressHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: theme.spacing.sm,
  },
  progressTrack: {
    height: 9,
    borderRadius: 5,
    backgroundColor: theme.colors.surfaceDim,
    overflow: 'hidden',
  },
  progressFill: {
    height: '100%',
    borderRadius: 5,
    backgroundColor: theme.colors.primary,
  },
  progressFillComplete: {
    backgroundColor: theme.colors.success,
  },
  completeRow: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 5,
    marginTop: theme.spacing.sm,
  },
  description: {
    paddingHorizontal: theme.spacing.lg,
    marginBottom: theme.spacing.base,
  },
  gridTitle: {
    paddingHorizontal: theme.spacing.lg,
    marginBottom: theme.spacing.sm,
  },
  grid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    paddingHorizontal: theme.spacing.lg,
    gap: theme.spacing.sm,
  },
  gridItem: {
    width: '48%',
    flexGrow: 1,
  },
});
