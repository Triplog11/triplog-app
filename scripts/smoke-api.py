#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
TripLog Backend API Smoke Test Script
Python standard library only.
"""

import json
import os
import sys
import urllib.error
import urllib.parse
import urllib.request

# Ensure UTF-8 output on Windows
if sys.platform == "win32":
    import io
    sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding="utf-8", line_buffering=True)
    sys.stderr = io.TextIOWrapper(sys.stderr.buffer, encoding="utf-8", line_buffering=True)

BASE_URL = os.environ.get("TRIPLOG_API_BASE", "https://triplog11.store").rstrip("/")
QA_EMAIL = os.environ.get("TRIPLOG_QA_EMAIL", "")
QA_PASSWORD = os.environ.get("TRIPLOG_QA_PASSWORD", "")

ENDPOINTS = [
    "/home",
    "/users/mypage",
    "/mypage/activityhistory",
    "/stats/me",
    "/stats/rankings/me",
    "/stats/rankings?rankingType=TOTAL",
    "/stats/rankings?rankingType=MONTHLY",
    "/regions/nationwide/map",
    "/regions?page=0&size=5",
    "/regions/provinces/11/map",
    "/missions/me",
    "/missions",
    "/notifications",
    "/notifications/settings",
    "/landmarks/me",
    "/reviews",
    "/appellations",
    "/badges",
    "/events",
    "/bookmarks",
]


def login(email: str, password: str) -> str:
    url = f"{BASE_URL}/auth/oauth"
    payload = json.dumps({
        "provider": "LOCAL",
        "email": email,
        "password": password
    }).encode("utf-8")

    req = urllib.request.Request(
        url,
        data=payload,
        headers={"Content-Type": "application/json", "Accept": "application/json"},
        method="POST",
    )

    try:
        with urllib.request.urlopen(req, timeout=10) as resp:
            data = json.loads(resp.read().decode("utf-8"))
            # Expecting accessToken in data (direct or data.data or data.accessToken)
            token = data.get("accessToken") or (data.get("data") or {}).get("accessToken")
            if not token:
                print(f"[ERROR] Failed to extract accessToken: {data}", file=sys.stderr)
                sys.exit(1)
            return token
    except Exception as e:
        print(f"[ERROR] Login request failed: {e}", file=sys.stderr)
        sys.exit(1)


def test_endpoint(path: str, token: str) -> tuple[str, int, str]:
    url = f"{BASE_URL}{path}"
    headers = {
        "Accept": "application/json",
        "Authorization": f"Bearer {token}",
    }
    req = urllib.request.Request(url, headers=headers, method="GET")

    status_code = 0
    body_snippet = ""
    try:
        with urllib.request.urlopen(req, timeout=10) as resp:
            status_code = resp.getcode()
            body = resp.read().decode("utf-8", errors="replace")
            clean_body = " ".join(body.split())
            body_snippet = clean_body[:120]
    except urllib.error.HTTPError as e:
        status_code = e.code
        body = e.read().decode("utf-8", errors="replace")
        clean_body = " ".join(body.split())
        body_snippet = clean_body[:120]
    except Exception as e:
        status_code = 0
        body_snippet = f"Error: {str(e)[:110]}"

    return path, status_code, body_snippet


def main():
    if not QA_EMAIL or not QA_PASSWORD:
        print("[WARNING] TRIPLOG_QA_EMAIL or TRIPLOG_QA_PASSWORD not set. Checking anonymous/local access if possible.", file=sys.stderr)

    token = ""
    if QA_EMAIL and QA_PASSWORD:
        token = login(QA_EMAIL, QA_PASSWORD)

    results = []
    has_critical_error = False

    for path in ENDPOINTS:
        p, status, snippet = test_endpoint(path, token)
        results.append((p, status, snippet))
        if status == 401 or (500 <= status < 600) or status == 0:
            has_critical_error = True

    print("| 경로 | 상태코드 | 응답 첫 120자 |")
    print("| --- | :---: | --- |")
    for p, status, snippet in results:
        # Escape pipe symbols in snippet for markdown table
        safe_snippet = snippet.replace("|", "\\|")
        print(f"| `{p}` | {status} | {safe_snippet} |")

    if has_critical_error:
        print(f"\n[FAILURE] Smoke test detected 401, 5xx, or network failure.", file=sys.stderr)
        sys.exit(1)
    else:
        print(f"\n[SUCCESS] All endpoints responded without 401 or 5xx.")
        sys.exit(0)


if __name__ == "__main__":
    main()
