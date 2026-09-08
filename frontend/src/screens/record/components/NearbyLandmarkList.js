import React from 'react';
import { StyleSheet, View, FlatList, TouchableOpacity } from 'react-native';
import CustomText from '../../../components/common/CustomText';
import theme from '../../../theme/theme';
import { formatDistance } from '../../../utils/geo';
import SelectRow, { listContentStyle } from './SelectRow';

/**
 * 2단계 — 현재 위치가 속한 시·군·구의 랜드마크 중에서 인증할 대상을 고른다.
 * 목록 순서는 utils/geo.js의 sortLandmarksByProximity가 정한다.
 *
 * @param placeLabel 확인된 현재 위치 라벨 (예: '대전 유성구')
 * @param onRetry 위치를 다시 탐색한다
 */
export default function NearbyLandmarkList({ landmarks, placeLabel, onSelect, onRetry }) {
  return (
    <FlatList
      data={landmarks}
      keyExtractor={(item) => String(item.landmarkId)}
      contentContainerStyle={listContentStyle}
      showsVerticalScrollIndicator={false}
      ListHeaderComponent={
        <View style={styles.header}>
          <CustomText variant="Heading/H4" color={theme.colors.text} style={styles.bold}>
            인증하려는 랜드마크를 선택해 주십시오
          </CustomText>
          {placeLabel ? (
            <CustomText variant="Body/Small" color={theme.colors.textSecondary}>
              현재 위치는 {placeLabel}입니다.
            </CustomText>
          ) : null}
        </View>
      }
      ListFooterComponent={
        <TouchableOpacity style={styles.outlineBtn} onPress={onRetry} activeOpacity={0.85}>
          <CustomText variant="UI/Button" color={theme.colors.primary} style={styles.bold}>
            다시 시도하기
          </CustomText>
        </TouchableOpacity>
      }
      renderItem={({ item }) => (
        <SelectRow
          icon="location"
          title={item.landmarkName}
          subtitle={buildSubtitle(item)}
          highlighted={item.acquired}
          badge={item.acquired ? '획득' : null}
          onPress={() => onSelect(item)}
        />
      )}
    />
  );
}

/** 소속 지역을 표시하고, 좌표를 아는 랜드마크는 거리도 함께 보여 준다 */
function buildSubtitle(landmark) {
  const region = landmark.regionName ?? '';
  if (landmark.distanceM == null) return region;
  return `${region} ${formatDistance(landmark.distanceM)}`.trim();
}

const styles = StyleSheet.create({
  bold: {
    fontWeight: 'bold',
  },
  header: {
    gap: 6,
    marginBottom: theme.spacing.xs,
  },
  outlineBtn: {
    height: 56,
    marginTop: theme.spacing.md,
    borderRadius: theme.rounded.cta,
    backgroundColor: theme.colors.canvas,
    borderWidth: 1,
    borderColor: theme.colors.primary,
    justifyContent: 'center',
    alignItems: 'center',
  },
});
