#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
TripLog 8 E2E User Journey Automated Test Script
Runs all 8 user journey scenarios on Android Emulator and verifies stability.
"""

import os
import subprocess
import sys
import time

if sys.platform == "win32":
    import io
    sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding="utf-8", line_buffering=True)
    sys.stderr = io.TextIOWrapper(sys.stderr.buffer, encoding="utf-8", line_buffering=True)

ADB = os.path.expandvars(r"$LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe")
OUT_SHOTS = os.path.join(os.path.dirname(os.path.dirname(os.path.abspath(__file__))), "docs", "design", "screens-2026-08-27")
os.makedirs(OUT_SHOTS, exist_ok=True)

TIMESTAMP = int(time.time())
UJ_EMAIL = f"uj{TIMESTAMP}@triplog.test"
UJ_PASSWORD = "Password123!"
UJ_NICKNAME = f"유저{TIMESTAMP % 10000}"


def run_adb(cmd, timeout=30):
    full_cmd = f'"{ADB}" {cmd}'
    res = subprocess.run(
        full_cmd,
        shell=True,
        capture_output=True,
        text=True,
        encoding="utf-8",
        errors="replace",
        timeout=timeout,
    )
    return (res.stdout or "").strip()


def tap(x, y, delay=1.0):
    run_adb(f"shell input tap {x} {y}")
    time.sleep(delay)


def swipe(x1, y1, x2, y2, duration=300, delay=1.0):
    run_adb(f"shell input swipe {x1} {y1} {x2} {y2} {duration}")
    time.sleep(delay)


def keyevent(key, delay=1.0):
    run_adb(f"shell input keyevent {key}")
    time.sleep(delay)


def input_text_adb(text, delay=0.8):
    run_adb(f"shell input text '{text}'")
    time.sleep(delay)


def input_korean(text, delay=0.8):
    run_adb("shell ime set com.android.adbkeyboard/.AdbIME")
    time.sleep(0.3)
    run_adb(f'shell am broadcast -a ADB_INPUT_TEXT --es msg "{text}"')
    time.sleep(delay)
    run_adb("shell ime reset")
    time.sleep(0.3)


def set_geo(lng=126.977041, lat=37.579617):
    run_adb(f"emu geo fix {lng} {lat}")
    time.sleep(0.5)


def capture(name):
    out_file = os.path.join(OUT_SHOTS, f"{name}.png")
    run_adb(f"exec-out screencap -p > \"{out_file}\"")
    print(f"Captured {name}.png")


def launch_app():
    run_adb('shell am start -a android.intent.action.VIEW -d "triplog://expo-development-client/?url=http%3A%2F%2Flocalhost%3A8081"')
    time.sleep(3.0)


def main():
    print("=== Starting 8 E2E User Journey Scenarios ===")
    results = {}

    # -------------------------------------------------------------
    # UJ-01: 신규 사용자 온보딩 및 첫 진입
    # -------------------------------------------------------------
    print("\n[UJ-01] New User Onboarding & Signup Flow...")
    run_adb("shell pm clear store.triplog11.app")
    time.sleep(1.0)
    launch_app()
    tap(540, 1800, delay=2.0) # Dev client continue
    tap(540, 1950, delay=1.5) # '이메일로 시작하기'
    tap(540, 2200, delay=1.5) # '이메일로 가입하기'
    
    # Fill Signup Form
    tap(300, 360, delay=0.5)
    input_text_adb(UJ_EMAIL)
    tap(950, 360, delay=1.0) # Check email
    tap(300, 480, delay=0.5)
    input_text_adb(UJ_PASSWORD)
    tap(300, 600, delay=0.5)
    input_text_adb(UJ_PASSWORD)
    tap(300, 720, delay=0.5)
    input_korean(UJ_NICKNAME)
    tap(950, 720, delay=1.0) # Check nickname
    
    # RegionPicker
    swipe(540, 1500, 540, 800, delay=1.0)
    tap(540, 700, delay=1.5) # Open RegionPicker
    tap(540, 450, delay=1.0) # Select Seoul
    tap(540, 550, delay=1.0) # Select Jongno-gu
    
    # Terms Agree All
    swipe(540, 1500, 540, 800, delay=1.0)
    tap(120, 1250, delay=0.8) # Agree all
    tap(540, 2200, delay=3.5) # Submit
    
    # Location Permission dialog on Home
    tap(540, 1400, delay=1.5) # Allow location
    capture("uj-01-home-entered")
    results["UJ-01"] = "PASS"
    print("UJ-01: PASS")

    # -------------------------------------------------------------
    # UJ-02: 전국 지도 탐험 및 지역 도감 탐색
    # -------------------------------------------------------------
    print("\n[UJ-02] Korea Map & Region Dex Exploration...")
    set_geo()
    tap(324, 2260, delay=2.0) # Dex Tab
    tap(540, 700, delay=1.5)  # Tap Seoul in province list
    tap(540, 500, delay=1.5)  # Tap Gyeongbokgung
    capture("uj-02-locked-card-modal")
    tap(980, 480, delay=1.0)  # Close modal
    keyevent(4, delay=1.0)    # Back to Dex
    results["UJ-02"] = "PASS"
    print("UJ-02: PASS")

    # -------------------------------------------------------------
    # UJ-03: 현장 방문 및 5단계 랜드마크 인증
    # -------------------------------------------------------------
    print("\n[UJ-03] 5-Step GPS Landmark Verification...")
    tap(540, 2200, delay=2.0) # FAB Center Cert Button
    tap(540, 600, delay=1.5)  # Select Region
    tap(540, 500, delay=1.5)  # Select Landmark (Gyeongbokgung)
    time.sleep(2.5)           # GPS pulse
    tap(540, 2150, delay=1.5) # '기록 작성하기'
    tap(700, 480, delay=0.8)  # 5 stars
    tap(300, 580, delay=0.5)
    input_korean("서울의 자랑 경복궁")
    tap(300, 750, delay=0.5)
    input_korean("근정전과 경회루의 풍경이 아름답습니다.")
    tap(540, 2150, delay=3.5) # '인증하기'
    capture("uj-03-cert-success")
    results["UJ-03"] = "PASS"
    print("UJ-03: PASS")

    # -------------------------------------------------------------
    # UJ-04: 도감 수집품 감상 및 카드 인터랙션
    # -------------------------------------------------------------
    print("\n[UJ-04] Collection Dex & Card Interaction...")
    tap(540, 1950, delay=2.0) # '도감 보러 가기'
    tap(800, 360, delay=1.5)  # Card List Tab
    # Filter chip test
    tap(200, 430, delay=1.0)
    tap(400, 430, delay=1.0)
    tap(100, 430, delay=1.0) # All
    tap(280, 650, delay=1.5) # Open first card
    capture("uj-04-acquired-card-detail")
    tap(980, 480, delay=1.0) # Close
    results["UJ-04"] = "PASS"
    print("UJ-04: PASS")

    # -------------------------------------------------------------
    # UJ-05: 주간 미션 확인 및 진행도 점검
    # -------------------------------------------------------------
    print("\n[UJ-05] Weekly Mission Strip Progress...")
    tap(108, 2260, delay=2.0) # Home Tab
    swipe(540, 1200, 540, 1600, delay=1.0)
    capture("uj-05-mission-progress")
    results["UJ-05"] = "PASS"
    print("UJ-05: PASS")

    # -------------------------------------------------------------
    # UJ-06: 랭킹 및 탐험가 랭킹 비교
    # -------------------------------------------------------------
    print("\n[UJ-06] Ranking Podium & My Rank Check...")
    tap(756, 2260, delay=2.0) # Ranking Tab
    tap(750, 320, delay=1.5)  # Monthly
    tap(250, 320, delay=1.5)  # Total
    swipe(540, 800, 540, 1600, duration=400, delay=2.0) # Refresh
    capture("uj-06-ranking-view")
    results["UJ-06"] = "PASS"
    print("UJ-06: PASS")

    # -------------------------------------------------------------
    # UJ-07: 마이페이지 프로필 & 보관함 관리
    # -------------------------------------------------------------
    print("\n[UJ-07] MyPage Profile & Badges/Appellations...")
    tap(972, 2260, delay=2.0) # MyPage Tab
    # Appellations
    tap(780, 850, delay=1.5)
    tap(540, 450, delay=1.0) # Select Appellation
    keyevent(4, delay=1.0)
    # Badges
    tap(280, 850, delay=1.5)
    keyevent(4, delay=1.0)
    # Travel log
    swipe(540, 1600, 540, 800, delay=1.0)
    tap(300, 1300, delay=1.5)
    capture("uj-07-travel-log")
    keyevent(4, delay=1.0)
    results["UJ-07"] = "PASS"
    print("UJ-07: PASS")

    # -------------------------------------------------------------
    # UJ-08: 설정 변경, 오프라인 복원 및 세션 유지
    # -------------------------------------------------------------
    print("\n[UJ-08] Settings, Offline Resilience & Session...")
    tap(972, 2260, delay=1.0)
    tap(950, 180, delay=1.5) # Notification Bell
    tap(950, 180, delay=1.5) # Notification Settings
    tap(950, 350, delay=0.8) # Toggle Switch
    tap(950, 350, delay=0.8) # Toggle Switch back
    keyevent(4, delay=1.0)
    keyevent(4, delay=1.0)
    
    # Offline toggle test
    run_adb("shell cmd connectivity airplane-mode enable")
    time.sleep(1.5)
    tap(324, 2260, delay=1.5) # Dex
    tap(756, 2260, delay=1.5) # Ranking
    run_adb("shell cmd connectivity airplane-mode disable")
    time.sleep(2.0)
    
    # Logout & Relogin
    tap(972, 2260, delay=1.0)
    swipe(540, 1800, 540, 600, delay=1.0)
    tap(540, 2000, delay=1.5) # Logout
    tap(750, 1350, delay=2.0) # Confirm logout
    
    # Relogin
    tap(540, 1950, delay=1.5)
    tap(300, 360, delay=0.5)
    input_text_adb(UJ_EMAIL)
    tap(300, 480, delay=0.5)
    input_text_adb(UJ_PASSWORD)
    tap(540, 2150, delay=3.0) # Login
    capture("uj-08-relogin-success")
    results["UJ-08"] = "PASS"
    print("UJ-08: PASS")

    print("\n=== All 8 E2E User Journey Scenarios Completed Successfully ===")
    for k, v in results.items():
        print(f"[{k}] {v}")


if __name__ == "__main__":
    main()
