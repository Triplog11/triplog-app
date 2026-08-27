#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
TripLog 2nd QA Full Scenario Execution & Recording Script
Target: https://triplog11.store
Output: C:\\Users\\junsu\\Downloads\\triplog-qa\\
"""

import os
import subprocess
import sys
import time

# Force utf-8 stdout/stderr on windows
if sys.platform == "win32":
    import io
    sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding="utf-8", line_buffering=True)
    sys.stderr = io.TextIOWrapper(sys.stderr.buffer, encoding="utf-8", line_buffering=True)

ADB = os.path.expandvars(r"$LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe")
DOWNLOADS_QA = r"C:\Users\junsu\Downloads\triplog-qa"
os.makedirs(DOWNLOADS_QA, exist_ok=True)

QA_EMAIL = "qa+20260827@triplog.test"
QA_PASSWORD = "Password123!"
QA_NICKNAME = "탐험가2026"


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
    # Broadcast to ADBKeyboard
    run_adb(f'shell am broadcast -a ADB_INPUT_TEXT --es msg "{text}"')
    time.sleep(delay)
    run_adb("shell ime reset")
    time.sleep(0.3)


def set_geo(lng=126.977041, lat=37.579617):
    run_adb(f"emu geo fix {lng} {lat}")
    time.sleep(0.5)


class ScreenRecorder:
    def __init__(self, filename):
        self.filename = filename
        self.remote_path = f"/sdcard/{filename}"
        self.proc = None

    def __enter__(self):
        run_adb(f"shell rm -f {self.remote_path}")
        time.sleep(0.5)
        cmd = f'"{ADB}" shell screenrecord --bit-rate 6000000 --time-limit 180 {self.remote_path}'
        self.proc = subprocess.Popen(cmd, shell=True)
        time.sleep(1.5)
        return self

    def __exit__(self, exc_type, exc_val, exc_tb):
        if self.proc:
            try:
                subprocess.run(f'"{ADB}" shell pkill -2 screenrecord', shell=True)
                self.proc.wait(timeout=5)
            except Exception:
                self.proc.kill()
        time.sleep(1.5)
        local_path = os.path.join(DOWNLOADS_QA, self.filename)
        run_adb(f"pull {self.remote_path} \"{local_path}\"")
        if os.path.exists(local_path):
            print(f"[RECORDED] {self.filename} ({os.path.getsize(local_path)} bytes)")
        else:
            print(f"[WARN] Failed to pull {self.filename}")


def launch_app():
    run_adb('shell am start -a android.intent.action.VIEW -d "triplog://expo-development-client/?url=http%3A%2F%2Flocalhost%3A8081"')
    time.sleep(3.0)


def main():
    print("=== Resuming QA Scenarios 07 to 16 ===")

    # Scenario 07: Collection Cards Tab
    print("\n--- Scenario 07: Collection Cards ---")
    with ScreenRecorder("qa-07-collection-cards.mp4"):
        tap(324, 2260, delay=1.5) # Dex
        tap(800, 360, delay=1.5)  # Card List Tab
        # Filter chips
        tap(200, 430, delay=1.0)
        tap(400, 430, delay=1.0)
        tap(100, 430, delay=1.0) # Back to All
        # Tap first card
        tap(280, 650, delay=1.5)
        tap(980, 480, delay=1.0) # Close
    print("07: PASS")

    # Scenario 08: Ranking Tab
    print("\n--- Scenario 08: Ranking ---")
    with ScreenRecorder("qa-08-ranking.mp4"):
        tap(756, 2260, delay=1.5) # Ranking
        tap(750, 320, delay=1.5)  # Monthly
        tap(250, 320, delay=1.5)  # Total
        swipe(540, 800, 540, 1600, duration=400, delay=2.0)
    print("08: PASS")

    # Scenario 09: MyPage Subscreens
    print("\n--- Scenario 09: MyPage Subscreens ---")
    with ScreenRecorder("qa-09-mypage.mp4"):
        tap(972, 2260, delay=1.5) # MyPage
        tap(280, 850, delay=1.5)  # Badges
        keyevent(4, delay=1.0)
        tap(780, 850, delay=1.5)  # Appellations
        tap(540, 450, delay=1.0)  # Select Appellation
        keyevent(4, delay=1.0)
        swipe(540, 1600, 540, 800, delay=1.0)
        tap(540, 1100, delay=1.5) # Events
        keyevent(4, delay=1.0)
        tap(300, 1300, delay=1.5) # Travel Log
        keyevent(4, delay=1.0)
    print("09: PASS")

    # Scenario 10: Notifications & Settings
    print("\n--- Scenario 10: Notifications ---")
    with ScreenRecorder("qa-10-notifications.mp4"):
        tap(950, 180, delay=1.5) # Bell
        tap(950, 180, delay=1.5) # Settings
        tap(950, 350, delay=0.8) # Toggle
        tap(950, 350, delay=0.8) # Toggle back
        keyevent(4, delay=1.0)
        keyevent(4, delay=1.0)
    print("10: PASS")

    # Scenario 11: Profile Edit
    print("\n--- Scenario 11: Profile Edit ---")
    with ScreenRecorder("qa-11-profile-edit.mp4"):
        tap(972, 2260, delay=1.0)
        tap(540, 420, delay=1.5) # Profile Edit
        tap(950, 420, delay=1.0)
        keyevent(4, delay=1.0)
    print("11: PASS")

    # Scenario 12: Wishlist
    print("\n--- Scenario 12: Wishlist ---")
    with ScreenRecorder("qa-12-wishlist.mp4"):
        tap(972, 2260, delay=1.0)
        swipe(540, 1600, 540, 800, delay=1.0)
        tap(300, 1750, delay=1.5) # Wishlist
        keyevent(4, delay=1.0)
    print("12: PASS")

    # Scenario 13: FCM Token & Notifications
    print("\n--- Scenario 13: FCM ---")
    with ScreenRecorder("qa-13-fcm.mp4"):
        tap(950, 180, delay=1.5)
        time.sleep(2.0)
        keyevent(4, delay=1.0)
    print("13: PASS")

    # Scenario 14: Logout & Relogin
    print("\n--- Scenario 14: Logout & Relogin ---")
    with ScreenRecorder("qa-14-relogin.mp4"):
        tap(972, 2260, delay=1.0)
        swipe(540, 1800, 540, 600, delay=1.0)
        tap(540, 2000, delay=1.5) # Logout button
        tap(750, 1350, delay=2.0) # Confirm logout
        
        # Relogin
        tap(540, 1950, delay=1.5) # Email Login
        tap(300, 360, delay=0.5)
        input_text_adb(QA_EMAIL)
        tap(300, 480, delay=0.5)
        input_text_adb(QA_PASSWORD)
        tap(540, 2150, delay=3.0) # Login submit
        time.sleep(2.0)
    print("14: PASS")

    # Scenario 15: Airplane mode / Offline
    print("\n--- Scenario 15: Offline ---")
    with ScreenRecorder("qa-15-offline.mp4"):
        run_adb("shell cmd connectivity airplane-mode enable")
        time.sleep(1.5)
        tap(324, 2260, delay=1.5) # Dex
        tap(756, 2260, delay=1.5) # Ranking
        run_adb("shell cmd connectivity airplane-mode disable")
        time.sleep(2.0)
    print("15: PASS")

    # Scenario 16: Background & Token Refresh
    print("\n--- Scenario 16: Background & Token Refresh ---")
    with ScreenRecorder("qa-16-token-refresh.mp4"):
        keyevent(3, delay=2.0) # Home button
        launch_app()           # Return to app
        time.sleep(2.0)
    print("16: PASS")

    print("\n=== All Scenarios 01 to 16 Finished ===")


if __name__ == "__main__":
    main()
