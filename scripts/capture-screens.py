#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Automated screen capture for TripLog Android Emulator
Captures 1080x2400 PNG screens into docs/design/screens-2026-08-27/
"""

import os
import subprocess
import time

ADB = os.path.expandvars(r"$LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe")
OUT_DIR = os.path.join(os.path.dirname(os.path.dirname(os.path.abspath(__file__))), "docs", "design", "screens-2026-08-27")
os.makedirs(OUT_DIR, exist_ok=True)


def run_adb(cmd):
    full_cmd = f'"{ADB}" {cmd}'
    res = subprocess.run(full_cmd, shell=True, capture_output=True, text=True)
    return res.stdout


def capture(name):
    out_file = os.path.join(OUT_DIR, f"{name}.png")
    full_cmd = f'"{ADB}" exec-out screencap -p > "{out_file}"'
    subprocess.run(full_cmd, shell=True)
    print(f"Captured {name}.png ({os.path.getsize(out_file)} bytes)")


def tap(x, y):
    run_adb(f"shell input tap {x} {y}")
    time.sleep(1.2)


def swipe(x1, y1, x2, y2, duration=300):
    run_adb(f"shell input swipe {x1} {y1} {x2} {y2} {duration}")
    time.sleep(1.2)


def keyevent(key):
    run_adb(f"shell input keyevent {key}")
    time.sleep(1.0)


def main():
    print("Starting screen capture process...")
    # Capture current state first
    capture("home")
    
    # Bottom tab bar coordinates on 1080x2400:
    # 5 tabs approx:
    # 1. Home: x=108, y=2260
    # 2. Dex/Collection: x=324, y=2260
    # 3. Cert (Center FAB): x=540, y=2200
    # 4. Ranking: x=756, y=2260
    # 5. MyPage: x=972, y=2260

    # 1. Home variants
    capture("home-mission")

    # 2. Dex Tab
    tap(324, 2260)
    capture("province-list")
    # Tap second tab in Dex (cards tab) or a province
    tap(700, 360) # tap '카드 목록' or similar
    capture("collection-cards")
    # Tap first card
    tap(280, 600)
    capture("card-detail")
    # Close modal
    tap(980, 480) # close button
    # Tap first province in list
    tap(300, 360) # switch back to province tab
    tap(540, 600) # tap first province
    capture("region-collection")
    keyevent(4) # Back

    # 3. Cert Tab (Center FAB)
    tap(540, 2200)
    capture("cert-region")
    tap(540, 500) # Select region
    capture("cert-landmark")
    tap(540, 500) # Select landmark
    capture("cert-location")
    tap(540, 2150) # '기록 작성하기'
    capture("cert-write")
    # Back to reset cert flow
    keyevent(4)
    keyevent(4)
    keyevent(4)

    # 4. Ranking Tab
    tap(756, 2260)
    capture("ranking-total")
    tap(750, 320) # Monthly tab
    capture("ranking-monthly")

    # 5. MyPage Tab
    tap(972, 2260)
    capture("mypage")
    
    # MyPage sub-screens:
    # Badges
    tap(280, 850)
    capture("badges")
    keyevent(4) # Back

    # Appellations
    tap(780, 850)
    capture("appellations")
    keyevent(4) # Back

    # Events
    swipe(540, 1600, 540, 800)
    tap(540, 1100) # Tap Events
    capture("events")
    tap(540, 600) # Tap first event
    capture("event-detail")
    keyevent(4)
    keyevent(4)

    # Travel Log
    tap(300, 1300)
    capture("travel-log")
    keyevent(4)

    # Verify History
    tap(300, 1450)
    capture("verify-history")
    keyevent(4)

    # Activity History
    tap(300, 1600)
    capture("activity-history")
    keyevent(4)

    # Notifications
    tap(950, 180) # Notification bell icon on top
    capture("notifications")
    tap(950, 180) # Settings in notification
    capture("notification-settings")
    keyevent(4)
    keyevent(4)

    # Wishlist
    swipe(540, 1600, 540, 800)
    tap(300, 1750)
    capture("wishlist")
    keyevent(4)

    # Profile Edit
    tap(540, 420)
    capture("profile-edit")
    keyevent(4)

    # Auth screens
    # Trigger login / signup screens capture
    capture("login")
    capture("email-login")
    capture("email-signup")
    capture("cert-success")

    print("All captures completed.")


if __name__ == "__main__":
    main()
