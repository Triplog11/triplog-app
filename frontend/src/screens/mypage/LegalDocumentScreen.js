import React from 'react';
import { StyleSheet, View, ScrollView } from 'react-native';
import CustomText from '../../components/common/CustomText';
import theme from '../../theme/theme';
import { PRIVACY_POLICY } from '../../constants/legal/privacyPolicy';
import { TERMS_OF_SERVICE } from '../../constants/legal/termsOfService';

/** 라우트 파라미터 document 값에 대응하는 문서 */
const DOCUMENTS = {
  privacy: PRIVACY_POLICY,
  terms: TERMS_OF_SERVICE,
};

/** 문단 목록을 렌더링합니다. */
function Paragraphs({ items }) {
  if (!items?.length) return null;
  return items.map((text, index) => (
    <CustomText
      key={`p-${index}`}
      variant="Body/Small"
      color={theme.colors.textBody}
      style={styles.paragraph}
    >
      {text}
    </CustomText>
  ));
}

/** 가운뎃점으로 구분한 항목 목록을 렌더링합니다. */
function Bullets({ items }) {
  if (!items?.length) return null;
  return (
    <View style={styles.bulletGroup}>
      {items.map((text, index) => (
        <View key={`b-${index}`} style={styles.bulletRow}>
          <CustomText variant="Body/Small" color={theme.colors.textMuted} style={styles.bulletMark}>
            ·
          </CustomText>
          <CustomText variant="Body/Small" color={theme.colors.textBody} style={styles.bulletText}>
            {text}
          </CustomText>
        </View>
      ))}
    </View>
  );
}

/** 하나의 조항을 렌더링합니다. */
function Section({ section }) {
  return (
    <View style={styles.section}>
      <CustomText variant="Label/Large" color={theme.colors.text} style={styles.heading}>
        {section.heading}
      </CustomText>
      <Paragraphs items={section.body} />
      <Bullets items={section.bullets} />
      <Paragraphs items={section.after} />
    </View>
  );
}

/**
 * 이용약관과 개인정보 처리방침을 보여 주는 공용 화면입니다.
 * 인터넷 연결 없이도 열람할 수 있도록 본문을 앱에 내장해 두었습니다.
 *
 * @param route.params.document 'privacy' 또는 'terms'
 */
export default function LegalDocumentScreen({ route }) {
  const document = DOCUMENTS[route?.params?.document] ?? PRIVACY_POLICY;

  return (
    <ScrollView
      style={styles.container}
      contentContainerStyle={styles.content}
      showsVerticalScrollIndicator={false}
    >
      <CustomText variant="Heading/H4" color={theme.colors.text} style={styles.title}>
        {document.title}
      </CustomText>
      <CustomText variant="Body/Small" color={theme.colors.textMuted} style={styles.date}>
        {document.effectiveDate}
      </CustomText>

      {document.intro ? (
        <View style={styles.introBox}>
          <CustomText variant="Body/Small" color={theme.colors.textBody}>
            {document.intro}
          </CustomText>
        </View>
      ) : null}

      {document.sections.map((section) => (
        <Section key={section.heading} section={section} />
      ))}

      <View style={styles.webNotice}>
        <CustomText variant="Body/Small" color={theme.colors.textMuted}>
          {`같은 내용을 웹에서도 확인하실 수 있습니다.
${document.webUrl}`}
        </CustomText>
      </View>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: theme.colors.white,
  },
  content: {
    paddingHorizontal: theme.spacing.lg,
    paddingTop: theme.spacing.lg,
    // 탭 바에 마지막 문단이 가려지지 않도록 여백을 둡니다.
    paddingBottom: 104,
  },
  title: {
    fontWeight: 'bold',
  },
  date: {
    marginTop: 4,
  },
  introBox: {
    marginTop: theme.spacing.base,
    padding: theme.spacing.base,
    backgroundColor: theme.colors.surface,
    borderRadius: theme.rounded.card,
  },
  section: {
    marginTop: theme.spacing.lg,
  },
  heading: {
    fontWeight: 'bold',
    marginBottom: theme.spacing.sm,
  },
  paragraph: {
    marginBottom: theme.spacing.sm,
    lineHeight: 21,
  },
  bulletGroup: {
    marginBottom: theme.spacing.sm,
  },
  bulletRow: {
    flexDirection: 'row',
    marginBottom: 6,
  },
  bulletMark: {
    width: 12,
    lineHeight: 21,
  },
  bulletText: {
    flex: 1,
    lineHeight: 21,
  },
  webNotice: {
    marginTop: theme.spacing.xl,
    padding: theme.spacing.base,
    backgroundColor: theme.colors.surface,
    borderRadius: theme.rounded.card,
  },
});
