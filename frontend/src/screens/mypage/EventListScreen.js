import React, { useCallback } from 'react';
import {
  StyleSheet, View, FlatList, TouchableOpacity, Image, ActivityIndicator, RefreshControl,
} from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { Ionicons } from '@expo/vector-icons';
import CustomText from '../../components/common/CustomText';
import theme from '../../theme/theme';
import { fetchEvents } from '../../api/events';
import usePagedList from './hooks/usePagedList';
import ListStateView from './components/ListStateView';
import { formatDate, getEventStatus, EVENT_STATUS_LABEL } from './utils/format';

const PAGE_SIZE = 10;

/** 이벤트 목록 — GET /events (이미지 + 기간 + 진행중/종료 배지) */
export default function EventListScreen({ navigation }) {
  const fetchPage = useCallback((page) => fetchEvents({ page, size: PAGE_SIZE }), []);
  const {
    items, loading, loadingMore, errorMessage, hasMore, loadMore, refresh,
  } = usePagedList(fetchPage, (res) => res?.items, [fetchPage]);

  return (
    <SafeAreaView style={styles.container} edges={['bottom']}>
      <FlatList
        data={items}
        keyExtractor={(item) => String(item.eventId)}
        contentContainerStyle={styles.list}
        showsVerticalScrollIndicator={false}
        onEndReached={loadMore}
        onEndReachedThreshold={0.4}
        refreshControl={(
          <RefreshControl
            refreshing={loading && items.length > 0}
            onRefresh={refresh}
            tintColor={theme.colors.primary}
          />
        )}
        ListEmptyComponent={(
          <ListStateView
            loading={loading && items.length === 0}
            errorMessage={items.length === 0 ? errorMessage : null}
            empty={!loading && !errorMessage && items.length === 0}
            emptyText="지금은 진행 중인 이벤트가 없어요."
            emptyHint="새 이벤트가 열리면 여기서 알려드릴게요."
            onRetry={refresh}
          />
        )}
        renderItem={({ item }) => (
          <EventCard
            event={item}
            onPress={() => navigation.navigate('EventDetail', { eventId: item.eventId, title: item.eventTitle })}
          />
        )}
        ListFooterComponent={
          hasMore || loadingMore
            ? <ActivityIndicator size="small" color={theme.colors.primary} style={styles.footer} />
            : null
        }
      />
    </SafeAreaView>
  );
}

function EventCard({ event, onPress }) {
  const status = getEventStatus(event.eventStart, event.eventEnd);
  const ended = status === 'ended';
  return (
    <TouchableOpacity style={[styles.card, ended && styles.cardEnded]} onPress={onPress} activeOpacity={0.85}>
      {event.eventImageUrl ? (
        <Image source={{ uri: event.eventImageUrl }} style={styles.image} resizeMode="cover" />
      ) : (
        <View style={[styles.image, styles.imagePlaceholder]}>
          <Ionicons name="megaphone-outline" size={28} color={theme.colors.textMuted} />
        </View>
      )}
      <View style={styles.body}>
        <View style={styles.titleRow}>
          <View style={[styles.statusPill, ended ? styles.statusEnded : styles.statusOngoing]}>
            <CustomText
              variant="Caption"
              color={ended ? theme.colors.textSecondary : theme.colors.white}
              style={styles.statusText}
            >
              {EVENT_STATUS_LABEL[status]}
            </CustomText>
          </View>
          <CustomText
            variant="Label/Medium"
            color={ended ? theme.colors.textSecondary : theme.colors.text}
            style={styles.title}
            numberOfLines={2}
          >
            {event.eventTitle}
          </CustomText>
        </View>
        <CustomText variant="Caption" color={theme.colors.textMuted}>
          {formatDate(event.eventStart)} ~ {formatDate(event.eventEnd)}
        </CustomText>
      </View>
    </TouchableOpacity>
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
    gap: theme.spacing.md,
  },
  card: {
    backgroundColor: theme.colors.canvas,
    borderRadius: theme.rounded.card,
    borderWidth: 1,
    borderColor: theme.colors.border,
    overflow: 'hidden',
  },
  cardEnded: {
    opacity: 0.7,
  },
  image: {
    width: '100%',
    aspectRatio: 2,
    backgroundColor: theme.colors.surfaceDim,
  },
  imagePlaceholder: {
    justifyContent: 'center',
    alignItems: 'center',
  },
  body: {
    padding: theme.spacing.base,
    gap: theme.spacing.sm,
  },
  titleRow: {
    flexDirection: 'row',
    alignItems: 'flex-start',
    gap: theme.spacing.sm,
  },
  title: {
    flex: 1,
    fontWeight: 'bold',
  },
  statusPill: {
    borderRadius: theme.rounded.sm,
    paddingHorizontal: theme.spacing.sm,
    paddingVertical: 2,
    marginTop: 1,
  },
  statusOngoing: {
    backgroundColor: theme.colors.primary,
  },
  statusEnded: {
    backgroundColor: theme.colors.surfaceDim,
  },
  statusText: {
    fontWeight: 'bold',
  },
  footer: {
    marginVertical: theme.spacing.base,
  },
});
