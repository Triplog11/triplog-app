import React, { useState } from 'react';
import { StyleSheet, View, Text, ScrollView, Linking } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { LinearGradient } from 'expo-linear-gradient';
import { Feather } from '@expo/vector-icons';
import Animated, { FadeIn, FadeInDown } from 'react-native-reanimated';
import PressableScale from '../../components/common/PressableScale';
import RegionStatCards from './components/RegionStatCards';
import RegionCardGrid from './components/RegionCardGrid';
import RegionCardCarousel from './components/RegionCardCarousel';
import theme from '../../theme/theme';
import { findRegion, getRegionDetail } from '../../data/regions';

const FACT_ICONS = ['map', 'users', 'grid'];

function ViewToggle({ view, onChange }) {
  return (
    <View style={styles.toggle} accessibilityRole="tablist">
      {[
        { key: 'grid', icon: 'grid' },
        { key: 'carousel', icon: 'credit-card' },
      ].map((item) => {
        const active = view === item.key;
        return (
          <PressableScale
            key={item.key}
            pressScale={0.92}
            onPress={() => onChange(item.key)}
            style={[styles.toggleBtn, active && styles.toggleBtnActive]}
            accessibilityLabel={item.key === 'grid' ? '그리드 보기' : '카드 보기'}
          >
            <Feather
              name={item.icon}
              size={16}
              color={active ? theme.colors.white : theme.colors.textMuted}
            />
          </PressableScale>
        );
      })}
    </View>
  );
}

/**
 * 지역 상세 (EXPLORE-02) — 지역 소개 헤더 + 통계 + 랜드마크 카드 그리드/캐러셀
 */
export default function RegionDetailScreen({ navigation, route }) {
  const regionName = route.params?.regionName;
  const region = findRegion(regionName) || { name: regionName, collected: 0, total: 1 };
  const detail = getRegionDetail(region);
  const percent = Math.min(100, Math.round((region.collected / region.total) * 100));

  const [view, setView] = useState('grid');

  const facts = [detail.area, detail.population, detail.districts].filter(
    (label) => label && label !== '—'
  );

  return (
    <SafeAreaView style={styles.container} edges={['top']}>
      <LinearGradient
        colors={[theme.colors.primarySoft, theme.colors.white]}
        locations={[0, 0.72]}
        style={styles.header}
      >
        <View style={styles.headerTop}>
          <PressableScale
            pressScale={0.92}
            onPress={() => navigation.goBack()}
            style={styles.backBtn}
            accessibilityLabel="뒤로"
          >
            <Feather name="chevron-left" size={22} color={theme.colors.text} />
          </PressableScale>
          <ViewToggle view={view} onChange={setView} />
        </View>

        <Animated.View entering={FadeInDown.duration(theme.motion.normal)}>
          <View style={styles.titleRow}>
            <Text style={styles.title}>{region.name}</Text>
            {detail.homepage && (
              <PressableScale
                pressScale={0.94}
                onPress={() => Linking.openURL(detail.homepage)}
                style={styles.homeBtn}
                accessibilityLabel="공식 홈페이지"
              >
                <Feather name="globe" size={20} color={theme.colors.primary} />
              </PressableScale>
            )}
          </View>

          {facts.length > 0 && (
            <View style={styles.facts}>
              {facts.map((label, i) => (
                <View key={label} style={styles.fact}>
                  <Feather
                    name={FACT_ICONS[i] || 'info'}
                    size={13}
                    color={theme.colors.primary}
                  />
                  <Text style={styles.factText}>{label}</Text>
                </View>
              ))}
            </View>
          )}

          <Text style={styles.summary}>{detail.summary}</Text>
        </Animated.View>
      </LinearGradient>

      <ScrollView
        style={styles.body}
        contentContainerStyle={styles.bodyContent}
        showsVerticalScrollIndicator={false}
      >
        {view === 'grid' ? (
          <Animated.View key="grid" entering={FadeIn.duration(theme.motion.normal)}>
            <RegionStatCards percent={percent} style={styles.stats} />
            <Text style={styles.cardsTitle}>카드</Text>
            <RegionCardGrid landmarks={detail.landmarks} regionName={region.name} />
          </Animated.View>
        ) : (
          <Animated.View key="carousel" entering={FadeIn.duration(theme.motion.normal)}>
            <RegionCardCarousel
              landmarks={detail.landmarks}
              collected={region.collected}
              total={region.total}
              date={detail.date}
              meta={detail.meta}
            />
            <RegionStatCards percent={percent} />
          </Animated.View>
        )}
      </ScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: theme.colors.white,
  },
  header: {
    paddingHorizontal: 16,
    paddingBottom: 18,
    borderBottomWidth: 1,
    borderBottomColor: theme.colors.border,
  },
  headerTop: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingTop: 4,
    paddingBottom: 10,
  },
  backBtn: {
    width: 32,
    height: 32,
    alignItems: 'center',
    justifyContent: 'center',
    borderRadius: theme.rounded.sm,
  },
  toggle: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 4,
    padding: 4,
    backgroundColor: theme.colors.surface,
    borderRadius: theme.rounded.full,
    borderWidth: 1,
    borderColor: theme.colors.border,
  },
  toggleBtn: {
    width: 32,
    height: 32,
    borderRadius: 16,
    alignItems: 'center',
    justifyContent: 'center',
  },
  toggleBtnActive: {
    backgroundColor: theme.colors.primary,
  },
  titleRow: {
    flexDirection: 'row',
    alignItems: 'flex-start',
    justifyContent: 'space-between',
    gap: 12,
    marginBottom: 10,
    paddingHorizontal: 2,
  },
  title: {
    fontFamily: theme.typography.fontFamily.bold,
    fontSize: theme.typography.size.xl,
    fontWeight: '700',
    lineHeight: Math.round(theme.typography.size.xl * 1.3),
    color: theme.colors.text,
  },
  homeBtn: {
    width: 40,
    height: 40,
    borderRadius: 20,
    borderWidth: 1,
    borderColor: theme.colors.border,
    backgroundColor: theme.colors.white,
    alignItems: 'center',
    justifyContent: 'center',
  },
  facts: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 6,
    marginBottom: 10,
    paddingHorizontal: 2,
  },
  fact: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 4,
    paddingVertical: 5,
    paddingHorizontal: 10,
    backgroundColor: theme.colors.white,
    borderWidth: 1,
    borderColor: theme.colors.border,
    borderRadius: theme.rounded.full,
  },
  factText: {
    fontFamily: theme.typography.fontFamily.bold,
    fontSize: 11,
    fontWeight: '600',
    color: theme.colors.textSecondary,
  },
  summary: {
    fontFamily: theme.typography.fontFamily.regular,
    fontSize: theme.typography.size.sm,
    lineHeight: Math.round(theme.typography.size.sm * 1.55),
    color: theme.colors.text,
    paddingHorizontal: 2,
  },
  body: {
    flex: 1,
    backgroundColor: theme.colors.white,
  },
  bodyContent: {
    paddingHorizontal: 16,
    paddingTop: 16,
    paddingBottom: 40,
  },
  stats: {
    marginBottom: 20,
  },
  cardsTitle: {
    fontFamily: theme.typography.fontFamily.bold,
    fontSize: theme.typography.size.md,
    fontWeight: '700',
    color: theme.colors.text,
    marginBottom: 14,
  },
});
