import React, { useState, useEffect, useCallback } from 'react';
import { StyleSheet, View, ScrollView, Image } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import CustomText from '../../components/common/CustomText';
import theme from '../../theme/theme';
import { fetchEventDetail } from '../../api/events';
import { EventAssets } from '../../assets';
import ListStateView from './components/ListStateView';
import { formatDate, getEventStatus, EVENT_STATUS_LABEL } from './utils/format';

/** 이벤트 상세 — GET /events/{id} (이미지 1·2 + 본문 + 기간) */
export default function EventDetailScreen({ route }) {
  const eventId = route?.params?.eventId;
  const [event, setEvent] = useState(null);
  const [loading, setLoading] = useState(true);
  const [errorMessage, setErrorMessage] = useState(null);
  const [imgError0, setImgError0] = useState(false);
  const [imgError1, setImgError1] = useState(false);

  const load = useCallback(async () => {
    if (eventId == null) {
      setErrorMessage('이벤트 정보를 찾을 수 없어요.');
      setLoading(false);
      return;
    }
    setLoading(true);
    setErrorMessage(null);
    try {
      const result = await fetchEventDetail(eventId);
      setEvent(result ?? null);
    } catch (error) {
      setErrorMessage(error?.message ?? '이벤트를 불러오지 못했어요.');
    } finally {
      setLoading(false);
    }
  }, [eventId]);

  useEffect(() => {
    load();
  }, [load]);

  if (loading || errorMessage || !event) {
    return (
      <SafeAreaView style={styles.container} edges={['bottom']}>
        <ListStateView
          loading={loading}
          errorMessage={errorMessage}
          empty={!loading && !errorMessage && !event}
          emptyText="이벤트 정보를 찾을 수 없어요."
          onRetry={load}
        />
      </SafeAreaView>
    );
  }

  const status = getEventStatus(event.eventStart, event.eventEnd);
  const ended = status === 'ended';
  const fallbackBanner = EventAssets.banners[Math.abs(Number(eventId) || 0) % 2];
  const heroSource = event.eventImageUrl1 && !imgError0 ? { uri: event.eventImageUrl1 } : fallbackBanner;

  return (
    <SafeAreaView style={styles.container} edges={['bottom']}>
      <ScrollView contentContainerStyle={styles.content} showsVerticalScrollIndicator={false}>
        <Image
          source={heroSource}
          style={styles.hero}
          resizeMode="cover"
          onError={() => setImgError0(true)}
        />

        <View style={styles.card}>
          <View style={styles.titleRow}>
            <View style={[styles.statusPill, ended ? styles.statusEnded : styles.statusOngoing]}>
              <CustomText
                variant="Caption"
                color={ended ? theme.colors.textSecondary : theme.colors.white}
                style={styles.bold}
              >
                {EVENT_STATUS_LABEL[status]}
              </CustomText>
            </View>
            <CustomText variant="Heading/H3" color={theme.colors.text} style={[styles.bold, styles.title]}>
              {event.eventTitle}
            </CustomText>
          </View>
          <CustomText variant="Caption" color={theme.colors.textMuted}>
            {formatDate(event.eventStart)} ~ {formatDate(event.eventEnd)}
          </CustomText>
          {event.eventContent ? (
            <CustomText variant="Body/Medium" color={theme.colors.textBody} style={styles.body}>
              {event.eventContent}
            </CustomText>
          ) : null}
        </View>

        {event.eventImageUrl2 && !imgError1 ? (
          <Image
            source={{ uri: event.eventImageUrl2 }}
            style={styles.secondary}
            resizeMode="cover"
            onError={() => setImgError1(true)}
          />
        ) : null}
      </ScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: theme.colors.surface,
  },
  content: {
    padding: theme.spacing.lg,
    gap: theme.spacing.base,
    paddingBottom: theme.spacing.section,
  },
  hero: {
    width: '100%',
    aspectRatio: 16 / 9,
    borderRadius: theme.rounded.lg,
    backgroundColor: theme.colors.surfaceDim,
  },
  secondary: {
    width: '100%',
    aspectRatio: 4 / 5,
    borderRadius: theme.rounded.lg,
    backgroundColor: theme.colors.surfaceDim,
  },
  card: {
    backgroundColor: theme.colors.canvas,
    borderRadius: theme.rounded.card,
    borderWidth: 1,
    borderColor: theme.colors.border,
    padding: theme.spacing.lg,
    gap: theme.spacing.sm,
  },
  titleRow: {
    flexDirection: 'row',
    alignItems: 'flex-start',
    gap: theme.spacing.sm,
  },
  title: {
    flex: 1,
  },
  bold: {
    fontWeight: 'bold',
  },
  statusPill: {
    borderRadius: theme.rounded.sm,
    paddingHorizontal: theme.spacing.sm,
    paddingVertical: 2,
    marginTop: 4,
  },
  statusOngoing: {
    backgroundColor: theme.colors.primary,
  },
  statusEnded: {
    backgroundColor: theme.colors.surfaceDim,
  },
  body: {
    lineHeight: 22,
    marginTop: theme.spacing.xs,
  },
});
