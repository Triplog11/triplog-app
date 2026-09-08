import React, { useState } from 'react';
import { StyleSheet, View, TextInput, ScrollView, KeyboardAvoidingView, Platform } from 'react-native';
import CustomText from '../../../components/common/CustomText';
import theme from '../../../theme/theme';
import StarRating from './StarRating';
import SubmitButton from './SubmitButton';

const TITLE_MAX = 40;
const CONTENT_MAX = 300;

/**
 * 4단계 — 방문 기록 작성. 제목·별점은 필수, 본문은 선택.
 * (사진 첨부는 expo-image-picker 미설치 상태라 이번 플로우에서 제외)
 *
 * @param submitting 전송 중 — 버튼 3-dot 표시, 입력 잠금
 * @param errorMessage 마지막 전송 실패 메시지 (같은 버튼으로 재시도)
 * @param onSubmit ({reviewTitle, reviewContent, reviewScore})
 */
export default function ReviewWrite({ landmark, submitting, errorMessage, onSubmit }) {
  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');
  const [rating, setRating] = useState(0);
  const [titleFocused, setTitleFocused] = useState(false);
  const [contentFocused, setContentFocused] = useState(false);

  const canSubmit = title.trim().length > 0 && rating >= 1;

  const handleSubmit = () => {
    if (!canSubmit || submitting) return;
    onSubmit({
      reviewTitle: title.trim(),
      reviewContent: content.trim(),
      reviewScore: Number(rating.toFixed(1)),
    });
  };

  return (
    <KeyboardAvoidingView
      style={styles.container}
      behavior={Platform.OS === 'ios' ? 'padding' : undefined}
      keyboardVerticalOffset={80}
    >
      <ScrollView contentContainerStyle={styles.scroll} keyboardShouldPersistTaps="handled">
        <CustomText variant="Heading/H4" color={theme.colors.text} style={styles.title}>
          방문 기록을 작성해 주십시오
        </CustomText>
        <CustomText variant="Body/Small" color={theme.colors.textSecondary} style={styles.subtitle}>
          {landmark.landmarkName}
          {landmark.acquired ? ' · 이미 획득한 랜드마크라 보상이 지급되지 않습니다' : ''}
        </CustomText>

        <View style={styles.starBlock}>
          <StarRating value={rating} onChange={setRating} />
        </View>

        <View style={[styles.inputBox, titleFocused && styles.inputBoxFocused]}>
          <TextInput
            style={styles.titleInput}
            placeholder="기록 제목"
            placeholderTextColor={theme.colors.textMuted}
            value={title}
            onChangeText={setTitle}
            onFocus={() => setTitleFocused(true)}
            onBlur={() => setTitleFocused(false)}
            maxLength={TITLE_MAX}
            editable={!submitting}
            returnKeyType="next"
          />
        </View>

        <View style={[styles.inputBox, contentFocused && styles.inputBoxFocused]}>
          <TextInput
            style={styles.contentInput}
            placeholder="어떤 점이 기억에 남았는지 작성해 주십시오. (선택)"
            placeholderTextColor={theme.colors.textMuted}
            value={content}
            onChangeText={setContent}
            onFocus={() => setContentFocused(true)}
            onBlur={() => setContentFocused(false)}
            multiline
            maxLength={CONTENT_MAX}
            textAlignVertical="top"
            editable={!submitting}
          />
          <CustomText variant="Caption" color={theme.colors.textMuted} style={styles.counter}>
            {content.length}/{CONTENT_MAX}
          </CustomText>
        </View>
      </ScrollView>

      <View style={styles.footer}>
        {errorMessage ? (
          <CustomText variant="Body/Small" color={theme.colors.error} style={styles.hint}>
            {errorMessage}
          </CustomText>
        ) : (
          !canSubmit && (
            <CustomText variant="Body/Small" color={theme.colors.textSecondary} style={styles.hint}>
              제목과 별점을 입력하면 인증을 완료할 수 있습니다
            </CustomText>
          )
        )}
        <SubmitButton
          label={errorMessage ? '다시 시도' : '인증하기'}
          onPress={handleSubmit}
          disabled={!canSubmit}
          loading={submitting}
        />
      </View>
    </KeyboardAvoidingView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  scroll: {
    padding: theme.spacing.lg,
    gap: theme.spacing.base,
  },
  title: {
    fontWeight: 'bold',
  },
  subtitle: {
    marginTop: -theme.spacing.sm,
  },
  starBlock: {
    marginVertical: theme.spacing.sm,
  },
  inputBox: {
    backgroundColor: theme.colors.canvas,
    borderRadius: theme.rounded.card,
    borderWidth: 1,
    borderColor: theme.colors.border,
    padding: theme.spacing.base,
  },
  inputBoxFocused: {
    borderColor: theme.colors.primary,
  },
  titleInput: {
    color: theme.colors.text,
    fontSize: 16,
    fontFamily: 'Pretendard-Bold',
    padding: 0,
  },
  contentInput: {
    minHeight: 120,
    color: theme.colors.text,
    fontSize: 14,
    lineHeight: 21,
    fontFamily: 'Pretendard-Regular',
    padding: 0,
  },
  counter: {
    alignSelf: 'flex-end',
    marginTop: 6,
  },
  footer: {
    padding: theme.spacing.lg,
    // 탭바의 가운데 인증 플로팅 버튼과 겹치지 않도록 하단 여백 확보
    paddingBottom: 104,
    gap: theme.spacing.sm,
  },
  hint: {
    textAlign: 'center',
  },
});
