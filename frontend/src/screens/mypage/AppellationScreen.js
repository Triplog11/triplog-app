import React, { useState, useEffect, useCallback } from 'react';
import { StyleSheet, View, FlatList, TouchableOpacity, Image } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { Ionicons } from '@expo/vector-icons';
import CustomText from '../../components/common/CustomText';
import theme from '../../theme/theme';
import { fetchAppellations, setRepresentativeAppellation } from '../../api/appellations';
import { getAppellationFallback } from '../../utils/badgeAssets';
import ListStateView from './components/ListStateView';
import InlineToast from './components/InlineToast';

/** 칭호 목록 — GET /appellations, 탭하면 대표 칭호로 설정(PATCH) */
export default function AppellationScreen({ navigation }) {
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [errorMessage, setErrorMessage] = useState(null);
  const [updatingId, setUpdatingId] = useState(null);
  const [toast, setToast] = useState(null);

  const load = useCallback(async () => {
    setLoading(true);
    setErrorMessage(null);
    try {
      const result = await fetchAppellations();
      setItems(result?.items ?? []);
    } catch (error) {
      if (error?.status === 404) setItems([]);
      else setErrorMessage(error?.message ?? '칭호를 불러오지 못했어요.');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    load();
  }, [load]);

  const markRepresentative = (list, appellationId) => list.map((item) => ({
    ...item,
    representative: item.appellationId === appellationId,
  }));

  const handleSelect = async (item) => {
    if (item.representative || updatingId != null) return;
    const previous = items;
    setUpdatingId(item.appellationId);
    setItems(markRepresentative(items, item.appellationId)); // 낙관적 갱신
    try {
      await setRepresentativeAppellation(item.appellationId);
      setToast(`'${item.appellationName}' 칭호를 대표로 설정했어요`);
    } catch (error) {
      setItems(previous);
      setToast(error?.message ?? '대표 칭호 설정에 실패했어요. 다시 시도해 주세요.');
    } finally {
      setUpdatingId(null);
    }
  };

  const dismissToast = useCallback(() => setToast(null), []);

  return (
    <SafeAreaView style={styles.container} edges={['bottom']}>
      <FlatList
        data={items}
        keyExtractor={(item) => String(item.appellationId)}
        contentContainerStyle={styles.list}
        showsVerticalScrollIndicator={false}
        ListHeaderComponent={
          items.length > 0 ? (
            <CustomText variant="Caption" color={theme.colors.textMuted} style={styles.hint}>
              칭호를 누르면 닉네임 옆에 보이는 대표 칭호로 설정돼요.
            </CustomText>
          ) : null
        }
        ListEmptyComponent={(
          <ListStateView
            loading={loading}
            errorMessage={errorMessage}
            empty={!loading && !errorMessage}
            emptyText="아직 획득한 칭호가 없어요."
            emptyHint="랜드마크를 인증하고 뱃지를 모으면 칭호가 열려요."
            ctaLabel="탐험하러 가기"
            onCta={() => navigation.navigate('Home')}
            onRetry={load}
          />
        )}
        renderItem={({ item }) => (
          <TouchableOpacity
            style={[styles.row, item.representative && styles.rowActive]}
            onPress={() => handleSelect(item)}
            activeOpacity={0.8}
            disabled={updatingId != null}
          >
            <View style={[styles.iconWrap, item.representative && styles.iconWrapActive]}>
              <Image
                source={item.appellationUrl ? { uri: item.appellationUrl } : getAppellationFallback(item.appellationName)}
                style={styles.appellationIcon}
                resizeMode="contain"
              />
            </View>
            <CustomText variant="Label/Medium" color={theme.colors.text} style={styles.name}>
              {item.appellationName}
            </CustomText>
            {item.representative ? (
              <View style={styles.repPill}>
                <CustomText variant="Caption" color={theme.colors.white} style={styles.repText}>
                  대표
                </CustomText>
              </View>
            ) : (
              <Ionicons name="ellipse-outline" size={18} color={theme.colors.borderStrong} />
            )}
          </TouchableOpacity>
        )}
      />
      <InlineToast message={toast} onDismiss={dismissToast} />
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: theme.colors.surface,
  },
  list: {
    flexGrow: 1,
    padding: theme.spacing.lg,
    gap: theme.spacing.sm,
  },
  hint: {
    textAlign: 'center',
    marginBottom: theme.spacing.xs,
  },
  row: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: theme.spacing.md,
    backgroundColor: theme.colors.canvas,
    borderRadius: theme.rounded.card,
    borderWidth: 1,
    borderColor: theme.colors.border,
    padding: theme.spacing.base,
  },
  rowActive: {
    borderColor: theme.colors.primary,
    backgroundColor: theme.colors.primarySoft,
  },
  iconWrap: {
    width: 36,
    height: 36,
    borderRadius: 18,
    backgroundColor: theme.colors.surfaceDim,
    justifyContent: 'center',
    alignItems: 'center',
    overflow: 'hidden',
  },
  iconWrapActive: {
    backgroundColor: theme.colors.primarySoft,
  },
  appellationIcon: {
    width: 28,
    height: 28,
  },
  name: {
    flex: 1,
    fontWeight: 'bold',
  },
  repPill: {
    backgroundColor: theme.colors.primary,
    borderRadius: theme.rounded.sm,
    paddingHorizontal: theme.spacing.sm,
    paddingVertical: 2,
  },
  repText: {
    fontWeight: 'bold',
  },
});
