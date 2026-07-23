import React, { useState, useMemo } from 'react';
import { StyleSheet, View, TextInput, TouchableOpacity, FlatList } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import CustomText from '../../../components/common/CustomText';
import theme from '../../../theme/theme';
import { GRADE_CONFIG } from '../../../data/collection';
import { formatDistance } from '../../../data/nearbyLandmarks';

const FILTERS = [
  { key: 'all', label: '전체' },
  { key: 'canVerify', label: '인증 가능' },
  { key: 'visited', label: '방문 완료' },
];

/** 주변 랜드마크 목록 — 검색 + 필터 + 거리순 정렬 */
export default function NearbyLandmarkList({ landmarks, locationState, onSelect, onRetryLocation }) {
  const [search, setSearch] = useState('');
  const [filter, setFilter] = useState('all');

  const filtered = useMemo(() => {
    const keyword = search.trim();
    return landmarks.filter((l) => {
      if (keyword && !l.name.includes(keyword) && !l.region.includes(keyword)) return false;
      if (filter === 'canVerify') return l.canVerify && !l.visited;
      if (filter === 'visited') return l.visited;
      return true;
    });
  }, [landmarks, search, filter]);

  const verifiableCount = landmarks.filter((l) => l.canVerify && !l.visited).length;

  return (
    <View style={styles.container}>
      {/* 위치 상태 배너 */}
      <View style={styles.locationBanner}>
        <Ionicons
          name={locationState === 'ready' ? 'location' : 'location-outline'}
          size={16}
          color={locationState === 'ready' ? theme.colors.locationBlue : theme.colors.textMuted}
        />
        <CustomText variant="Body/Small" color={theme.colors.textSecondary} style={styles.bannerText}>
          {locationState === 'loading' && '현재 위치를 확인하고 있어요'}
          {locationState === 'ready' && `근처에서 인증할 수 있는 곳이 ${verifiableCount}곳 있어요`}
          {locationState === 'denied' && '위치 권한이 없어 거리를 계산할 수 없어요'}
          {locationState === 'error' && '현재 위치를 확인하지 못했어요'}
        </CustomText>
        {(locationState === 'denied' || locationState === 'error') && (
          <TouchableOpacity onPress={onRetryLocation} hitSlop={8}>
            <CustomText variant="Label/Medium" color={theme.colors.primary} style={styles.retryText}>
              다시 시도
            </CustomText>
          </TouchableOpacity>
        )}
      </View>

      {/* 검색 */}
      <View style={styles.searchBar}>
        <Ionicons name="search" size={16} color={theme.colors.textMuted} />
        <TextInput
          style={styles.searchInput}
          placeholder="랜드마크나 지역을 검색해 보세요"
          placeholderTextColor={theme.colors.textMuted}
          value={search}
          onChangeText={setSearch}
        />
      </View>

      {/* 필터 */}
      <View style={styles.filterRow}>
        {FILTERS.map((f) => {
          const active = filter === f.key;
          return (
            <TouchableOpacity
              key={f.key}
              style={[styles.filterChip, active && styles.filterChipActive]}
              onPress={() => setFilter(f.key)}
              activeOpacity={0.8}
            >
              <CustomText
                variant="Label/Medium"
                color={active ? '#FFFFFF' : theme.colors.textSecondary}
                style={styles.bold}
              >
                {f.label}
              </CustomText>
            </TouchableOpacity>
          );
        })}
      </View>

      <FlatList
        data={filtered}
        keyExtractor={(item) => String(item.id)}
        contentContainerStyle={styles.listContent}
        showsVerticalScrollIndicator={false}
        ListEmptyComponent={
          <View style={styles.emptyBox}>
            <CustomText variant="Body/Medium" color={theme.colors.textSecondary} style={styles.emptyText}>
              {search
                ? `'${search}' 검색 결과가 없어요. 다른 이름으로 찾아보시겠어요?`
                : '조건에 맞는 랜드마크가 없어요.'}
            </CustomText>
          </View>
        }
        renderItem={({ item }) => {
          const grade = GRADE_CONFIG[item.grade];
          return (
            <TouchableOpacity style={styles.row} onPress={() => onSelect(item)} activeOpacity={0.85}>
              <View style={[styles.thumb, { backgroundColor: grade.soft }]}>
                <Ionicons name="location" size={20} color={grade.color} />
              </View>
              <View style={styles.rowInfo}>
                <View style={styles.rowTitleLine}>
                  <CustomText variant="Label/Medium" color={theme.colors.text} style={styles.bold} numberOfLines={1}>
                    {item.name}
                  </CustomText>
                  <View style={[styles.gradePill, { backgroundColor: grade.soft }]}>
                    <CustomText variant="Caption" color={grade.color} style={styles.gradePillText}>
                      {item.grade}
                    </CustomText>
                  </View>
                </View>
                <CustomText variant="Caption" color={theme.colors.textSecondary}>
                  {item.region} · {formatDistance(item.distanceM)}
                </CustomText>
                <View style={styles.rewardRow}>
                  <CustomText variant="Caption" color={theme.colors.textMuted}>
                    +{item.xp} XP · {item.point}점
                  </CustomText>
                </View>
              </View>
              <View style={styles.rowRight}>
                {item.visited ? (
                  <View style={[styles.statusPill, styles.visitedPill]}>
                    <CustomText variant="Caption" color={theme.colors.success} style={styles.bold}>
                      인증 완료
                    </CustomText>
                  </View>
                ) : item.canVerify ? (
                  <View style={[styles.statusPill, styles.verifyPill]}>
                    <CustomText variant="Caption" color="#FFFFFF" style={styles.bold}>
                      인증 가능
                    </CustomText>
                  </View>
                ) : (
                  <View style={[styles.statusPill, styles.farPill]}>
                    <CustomText variant="Caption" color={theme.colors.textMuted} style={styles.bold}>
                      반경 밖
                    </CustomText>
                  </View>
                )}
                <Ionicons name="chevron-forward" size={14} color={theme.colors.textMuted} />
              </View>
            </TouchableOpacity>
          );
        }}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  bold: {
    fontWeight: 'bold',
  },
  locationBanner: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 6,
    marginHorizontal: theme.spacing.lg,
    marginTop: theme.spacing.sm,
    paddingHorizontal: theme.spacing.base,
    paddingVertical: 10,
    borderRadius: theme.rounded.card,
    backgroundColor: theme.colors.primarySoft,
  },
  bannerText: {
    flex: 1,
  },
  retryText: {
    fontWeight: 'bold',
  },
  searchBar: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 8,
    height: 44,
    marginHorizontal: theme.spacing.lg,
    marginTop: theme.spacing.sm,
    paddingHorizontal: theme.spacing.base,
    borderRadius: theme.rounded.pill,
    backgroundColor: theme.colors.surfaceDim,
  },
  searchInput: {
    flex: 1,
    color: theme.colors.text,
    fontSize: 14,
    fontFamily: 'Pretendard-Regular',
    padding: 0,
  },
  filterRow: {
    flexDirection: 'row',
    gap: 6,
    marginHorizontal: theme.spacing.lg,
    marginTop: theme.spacing.sm,
  },
  filterChip: {
    height: 32,
    paddingHorizontal: 14,
    borderRadius: theme.rounded.pill,
    backgroundColor: theme.colors.surfaceDim,
    justifyContent: 'center',
  },
  filterChipActive: {
    backgroundColor: theme.colors.primary,
  },
  listContent: {
    padding: theme.spacing.lg,
    gap: theme.spacing.sm,
  },
  row: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: theme.spacing.sm,
    backgroundColor: theme.colors.canvas,
    borderRadius: theme.rounded.card,
    borderWidth: 1,
    borderColor: theme.colors.border,
    padding: theme.spacing.sm,
  },
  thumb: {
    width: 48,
    height: 48,
    borderRadius: theme.rounded.md,
    justifyContent: 'center',
    alignItems: 'center',
  },
  rowInfo: {
    flex: 1,
    gap: 2,
  },
  rowTitleLine: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 6,
  },
  gradePill: {
    borderRadius: theme.rounded.pill,
    paddingHorizontal: 7,
    paddingVertical: 1,
  },
  gradePillText: {
    fontWeight: 'bold',
    fontSize: 10,
  },
  rewardRow: {
    marginTop: 2,
  },
  rowRight: {
    alignItems: 'flex-end',
    gap: 5,
  },
  statusPill: {
    borderRadius: theme.rounded.sm,
    paddingHorizontal: 8,
    paddingVertical: 3,
  },
  verifyPill: {
    backgroundColor: theme.colors.primary,
  },
  visitedPill: {
    backgroundColor: theme.colors.primarySoft,
  },
  farPill: {
    backgroundColor: theme.colors.surfaceDim,
  },
  emptyBox: {
    paddingTop: 60,
    paddingHorizontal: 30,
  },
  emptyText: {
    textAlign: 'center',
  },
});
