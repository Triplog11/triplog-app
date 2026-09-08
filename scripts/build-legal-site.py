"""docs/legal 의 마크다운 문서를 정적 HTML 페이지로 변환한다.

원스토어 심사에 제출할 공개 URL 을 만들기 위한 스크립트다. 외부 의존성 없이
표준 라이브러리만 쓰며, 문서가 쓰는 문법(제목, 문단, 목록, 표, 구분선,
굵게, 인라인 코드, 링크)만 처리한다.
"""
import html
import os
import re

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
SRC = os.path.join(ROOT, 'docs', 'legal')
# 결과물을 Triplog11/triplog-legal 저장소에 복사하면 GitHub Pages 로 배포된다.
OUT = os.environ.get('LEGAL_SITE_OUT', os.path.join(ROOT, 'legal-site'))

STYLE = """
:root { color-scheme: light; }
* { box-sizing: border-box; }
body {
  margin: 0; padding: 0;
  font-family: -apple-system, BlinkMacSystemFont, 'Apple SD Gothic Neo',
    'Malgun Gothic', 'Noto Sans KR', sans-serif;
  color: #1B1D1F; background: #FFFFFF; line-height: 1.7;
  -webkit-text-size-adjust: 100%;
}
header {
  background: #00ADBA; color: #fff; padding: 28px 20px;
}
header .brand { font-size: 20px; font-weight: 700; letter-spacing: -0.02em; }
header .sub { font-size: 14px; opacity: 0.9; margin-top: 4px; }
main { max-width: 760px; margin: 0 auto; padding: 32px 20px 80px; }
h1 { font-size: 26px; font-weight: 700; margin: 0 0 6px; letter-spacing: -0.02em; }
h2 { font-size: 19px; font-weight: 700; margin: 40px 0 12px; letter-spacing: -0.01em; }
h3 { font-size: 16px; font-weight: 700; margin: 28px 0 10px; }
p { margin: 0 0 14px; }
ul, ol { margin: 0 0 16px; padding-left: 22px; }
li { margin-bottom: 8px; }
hr { border: 0; border-top: 1px solid #E8EAEC; margin: 36px 0; }
code {
  background: #F5F6F7; padding: 2px 6px; border-radius: 4px;
  font-family: Consolas, Menlo, monospace; font-size: 0.9em;
}
a { color: #368FFF; }
.meta { color: #7E848A; font-size: 14px; margin-bottom: 28px; }
.table-wrap { overflow-x: auto; margin: 0 0 20px; }
table { border-collapse: collapse; width: 100%; font-size: 14px; min-width: 420px; }
th, td { border: 1px solid #E8EAEC; padding: 10px 12px; text-align: left; vertical-align: top; }
th { background: #F5F6F7; font-weight: 700; }
nav.docs { margin: 0 0 8px; }
nav.docs a {
  display: inline-block; margin-right: 14px; font-size: 14px;
  text-decoration: none; color: #368FFF; font-weight: 600;
}
footer {
  border-top: 1px solid #E8EAEC; padding: 24px 20px 40px;
  color: #7E848A; font-size: 13px; text-align: center;
}
@media (max-width: 480px) {
  h1 { font-size: 22px; } h2 { font-size: 17px; }
  main { padding: 24px 16px 60px; }
}
"""

PAGE = """<!doctype html>
<html lang="ko">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>{title} · 트립로그</title>
<meta name="description" content="트립로그(TripLog) {title}">
<style>{style}</style>
</head>
<body>
<header>
  <div class="brand">트립로그 TripLog</div>
  <div class="sub">전국의 관광지를 방문하고 기록하는 여행 수집 앱</div>
</header>
<main>
<nav class="docs">
  <a href="./">홈</a><a href="./privacy.html">개인정보 처리방침</a><a href="./terms.html">서비스 이용약관</a>
</nav>
{body}
</main>
<footer>트립로그 운영팀 · 최종 수정 2026년 9월 8일</footer>
</body>
</html>
"""


def inline(text):
    """굵게, 인라인 코드, 링크 같은 인라인 문법을 변환한다."""
    text = html.escape(text)
    text = re.sub(r'`([^`]+)`', r'<code>\1</code>', text)
    text = re.sub(r'\*\*([^*]+)\*\*', r'<strong>\1</strong>', text)
    text = re.sub(r'\[([^\]]+)\]\((https?://[^)]+)\)',
                  r'<a href="\2" target="_blank" rel="noopener">\1</a>', text)
    text = re.sub(r'(?<![">])(https?://[^\s<),]+)',
                  r'<a href="\1" target="_blank" rel="noopener">\1</a>', text)
    return text


def convert(md):
    """마크다운 본문을 HTML 조각으로 바꾼다."""
    out, lines, i = [], md.split('\n'), 0
    while i < len(lines):
        line = lines[i].rstrip()

        if not line.strip():
            i += 1
            continue

        if line.startswith('---') and set(line.strip()) == {'-'}:
            out.append('<hr>')
            i += 1
            continue

        m = re.match(r'^(#{1,4})\s+(.*)$', line)
        if m:
            level = len(m.group(1))
            out.append(f'<h{level}>{inline(m.group(2))}</h{level}>')
            i += 1
            continue

        # 표: 헤더 줄과 구분 줄이 이어지는 형태만 인정한다.
        if line.startswith('|') and i + 1 < len(lines) and re.match(r'^\|[\s:\-|]+\|$', lines[i + 1].strip()):
            headers = [c.strip() for c in line.strip('|').split('|')]
            rows, i = [], i + 2
            while i < len(lines) and lines[i].strip().startswith('|'):
                rows.append([c.strip() for c in lines[i].strip().strip('|').split('|')])
                i += 1
            th = ''.join(f'<th>{inline(h)}</th>' for h in headers)
            tb = ''.join('<tr>' + ''.join(f'<td>{inline(c)}</td>' for c in r) + '</tr>' for r in rows)
            out.append(f'<div class="table-wrap"><table><thead><tr>{th}</tr></thead><tbody>{tb}</tbody></table></div>')
            continue

        if re.match(r'^\d+\.\s', line):
            items = []
            while i < len(lines) and re.match(r'^\d+\.\s', lines[i].strip()):
                items.append(re.sub(r'^\d+\.\s+', '', lines[i].strip()))
                i += 1
            out.append('<ol>' + ''.join(f'<li>{inline(x)}</li>' for x in items) + '</ol>')
            continue

        if line.lstrip().startswith(('- ', '* ')):
            items = []
            while i < len(lines) and lines[i].lstrip().startswith(('- ', '* ')):
                items.append(lines[i].lstrip()[2:])
                i += 1
            out.append('<ul>' + ''.join(f'<li>{inline(x)}</li>' for x in items) + '</ul>')
            continue

        para = [line]
        i += 1
        while i < len(lines) and lines[i].strip() and not re.match(
                r'^(#{1,4}\s|\||\d+\.\s|[-*]\s|---)', lines[i].strip()):
            para.append(lines[i].strip())
            i += 1
        out.append(f'<p>{inline(" ".join(para))}</p>')

    return '\n'.join(out)


def build(src_name, out_name, title):
    md = open(os.path.join(SRC, src_name), encoding='utf-8').read()
    body = convert(md)
    page = PAGE.format(title=title, style=STYLE, body=body)
    open(os.path.join(OUT, out_name), 'w', encoding='utf-8').write(page)
    return len(page)


os.makedirs(OUT, exist_ok=True)
n1 = build('privacy-policy.md', 'privacy.html', '개인정보 처리방침')
n2 = build('terms-of-service.md', 'terms.html', '서비스 이용약관')

index_body = """<h1>트립로그 약관 안내</h1>
<p class="meta">트립로그(TripLog)의 개인정보 처리방침과 서비스 이용약관을 확인하실 수 있습니다.</p>
<ul>
<li><a href="./privacy.html">개인정보 처리방침</a> — 어떤 정보를 어떤 목적으로 수집하고 얼마나 보관하는지 안내합니다.</li>
<li><a href="./terms.html">서비스 이용약관</a> — 서비스 이용 조건과 절차, 이용자와 운영자의 권리와 의무를 정합니다.</li>
</ul>
<hr>
<h2>트립로그는 어떤 앱입니까</h2>
<p>전국 시·군·구의 관광지를 직접 방문해 인증하면 그곳의 랜드마크 카드가 도감에 담기는 여행 기록 앱입니다.
방문한 지역이 늘어날수록 전국 지도가 점점 채워집니다.</p>
<h2>위치 정보는 어떻게 처리합니까</h2>
<p>트립로그는 이용자의 위도와 경도 좌표를 서버로 전송하거나 저장하지 않습니다.
방문 인증 과정에서 단말기의 위치를 확인하지만, 이 좌표는 단말기 안에서 현재 행정구역(시·군·구)을
판별하는 용도로만 잠시 사용되고 곧바로 폐기됩니다.</p>
<h2>문의</h2>
<p>서비스 이용과 개인정보 처리에 관한 문의는 트립로그 운영팀으로 연락해 주십시오.</p>
"""
open(os.path.join(OUT, 'index.html'), 'w', encoding='utf-8').write(
    PAGE.format(title='약관 안내', style=STYLE, body=index_body))
open(os.path.join(OUT, '.nojekyll'), 'w').write('')

print(f'생성 완료: privacy.html({n1:,}자) terms.html({n2:,}자) index.html')
print('출력 위치:', OUT)
