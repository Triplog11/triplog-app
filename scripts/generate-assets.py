#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Generate high-fidelity assets for TripLog application
"""

import math
import os
from PIL import Image, ImageDraw, ImageFilter, ImageFont

BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
FRONTEND_ASSETS = os.path.join(BASE_DIR, "frontend", "assets", "images")

# Ensure subdirectories exist
for subdir in ["cards", "badges", "appellations", "empty", "events"]:
    os.makedirs(os.path.join(FRONTEND_ASSETS, subdir), exist_ok=True)


def hex_to_rgb(hex_str):
    hex_str = hex_str.lstrip("#")
    return tuple(int(hex_str[i:i+2], 16) for i in (0, 2, 4))


def hex_to_rgba(hex_str, alpha=255):
    rgb = hex_to_rgb(hex_str)
    return (*rgb, alpha)


def create_linear_gradient(size, start_color, end_color, angle_deg=45):
    width, height = size
    base = Image.new("RGBA", (width, height), (0, 0, 0, 0))
    draw = ImageDraw.Draw(base)

    r1, g1, b1 = start_color[:3]
    r2, g2, b2 = end_color[:3]

    rad = math.radians(angle_deg)
    cos_a = math.cos(rad)
    sin_a = math.sin(rad)

    # Gradient line projection
    diag = math.sqrt(width**2 + height**2)
    for y in range(height):
        for x in range(width):
            # Project onto vector
            proj = (x * cos_a + y * sin_a) / diag
            proj = max(0.0, min(1.0, proj))
            r = int(r1 + (r2 - r1) * proj)
            g = int(g1 + (g2 - g1) * proj)
            b = int(b1 + (b2 - b1) * proj)
            base.putpixel((x, y), (r, g, b, 255))
    return base


def create_default_landmark_card():
    """A-1: 1080x1350 4:5 #F5F6F7 with mint->primary gradient Korean map silhouette and white pin."""
    w, h = 1080, 1350
    img = Image.new("RGBA", (w, h), hex_to_rgba("#F5F6F7"))
    
    # Create smooth abstract Korean peninsula map shape mask
    mask = Image.new("L", (w, h), 0)
    draw_mask = ImageDraw.Draw(mask)
    
    # Abstract geometric silhouette of Korea (Peninsula + Jeju + Ulleungdo/Dokdo)
    # Scaled to center (cx=540, cy=675)
    cx, cy = 540, 675
    peninsula_poly = [
        (cx - 160, cy - 350),  # North-west
        (cx + 80, cy - 380),   # North-east
        (cx + 200, cy - 280),  # East coast top
        (cx + 220, cy - 100),  # East sea border
        (cx + 170, cy + 100),  # Yeongdeok/Pohang
        (cx + 190, cy + 220),  # Busan / Ulsan
        (cx + 100, cy + 300),  # South coast (Masan/Tongyeong)
        (cx - 30, cy + 310),   # Yeosu/Suncheon
        (cx - 150, cy + 270),  # Haenam/Mokpo
        (cx - 180, cy + 140),  # Gunsan/Boryeong
        (cx - 130, cy + 50),   # Taean peninsula
        (cx - 170, cy - 40),   # Gyeonggi bay
        (cx - 190, cy - 180),  # Ongjin/Hwanghae
        (cx - 140, cy - 260),  # Pyeongyang bay
    ]
    draw_mask.polygon(peninsula_poly, fill=255)
    # Extra stylized islands: Jeju
    draw_mask.ellipse([cx - 130, cy + 350, cx - 40, cy + 390], fill=255)
    # Ulleungdo
    draw_mask.ellipse([cx + 280, cy - 80, cx + 310, cy - 50], fill=255)
    
    # Blur mask slightly for smooth edges
    mask = mask.filter(ImageFilter.GaussianBlur(18))
    
    # Create gradient layer: Mint #0ECEDB to Primary #368FFF (vertical/diagonal)
    grad = Image.new("RGBA", (w, h))
    c_start = hex_to_rgb("#0ECEDB")
    c_end = hex_to_rgb("#368FFF")
    for y in range(h):
        t = y / h
        r = int(c_start[0] + (c_end[0] - c_start[0]) * t)
        g = int(c_start[1] + (c_end[1] - c_start[1]) * t)
        b = int(c_start[2] + (c_end[2] - c_start[2]) * t)
        # 1px line
        line = Image.new("RGBA", (w, 1), (r, g, b, 255))
        grad.paste(line, (0, y))
        
    img.paste(grad, (0, 0), mask)
    
    # Draw White location pin in center
    pin_layer = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    draw_pin = ImageDraw.Draw(pin_layer)
    
    pin_cx, pin_cy = 540, 620
    pin_r = 64
    # Head circle
    draw_pin.ellipse([pin_cx - pin_r, pin_cy - pin_r, pin_cx + pin_r, pin_cy + pin_r], fill=(255, 255, 255, 255))
    # Triangle bottom
    triangle = [
        (pin_cx - pin_r + 8, pin_cy + 20),
        (pin_cx + pin_r - 8, pin_cy + 20),
        (pin_cx, pin_cy + pin_r + 60),
    ]
    draw_pin.polygon(triangle, fill=(255, 255, 255, 255))
    # Inner hole
    inner_r = 26
    draw_pin.ellipse([pin_cx - inner_r, pin_cy - inner_r, pin_cx + inner_r, pin_cy + inner_r], fill=hex_to_rgba("#368FFF", 255))
    
    img = Image.alpha_composite(img, pin_layer)
    out_path = os.path.join(FRONTEND_ASSETS, "cards", "default-landmark-card.png")
    img.save(out_path, "PNG")
    print(f"Created {out_path}")


def create_card_frames():
    """A-2: frame-{common,rare,epic,legendary}.png 1080x1350 transparent PNG, 24px border, radius 48px."""
    w, h = 1080, 1350
    tiers = {
        "common": hex_to_rgb("#6B7280"),
        "rare": hex_to_rgb("#2563EB"),
        "epic": hex_to_rgb("#7C3AED"),
        "legendary": hex_to_rgb("#D97706"),
    }
    border_w = 24
    radius = 48

    for name, color in tiers.items():
        img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
        draw = ImageDraw.Draw(img)
        
        # Outer rounded rect filled
        draw.rounded_rectangle([0, 0, w, h], radius=radius, fill=(*color, 255))
        
        # Inner clear mask
        inner_mask = Image.new("RGBA", (w, h), (0, 0, 0, 0))
        draw_inner = ImageDraw.Draw(inner_mask)
        draw_inner.rounded_rectangle(
            [border_w, border_w, w - border_w, h - border_w],
            radius=max(4, radius - border_w),
            fill=(0, 0, 0, 255)
        )
        
        # Cut inner part
        img_arr = img.copy()
        # Erase inner area
        for y in range(border_w, h - border_w):
            for x in range(border_w, w - border_w):
                # check if inside inner rounded rect
                if inner_mask.getpixel((x, y))[3] > 0:
                    img_arr.putpixel((x, y), (0, 0, 0, 0))
                    
        # If legendary, add subtle gold inner highlight line
        if name == "legendary":
            draw_extra = ImageDraw.Draw(img_arr)
            gold = hex_to_rgb("#FBBF24")
            draw_extra.rounded_rectangle(
                [border_w + 3, border_w + 3, w - border_w - 3, h - border_w - 3],
                radius=max(2, radius - border_w - 3),
                outline=(*gold, 230),
                width=3
            )
            # Corner accents
            c_len = 60
            for cx, cy in [(border_w, border_w), (w - border_w, border_w), (border_w, h - border_w), (w - border_w, h - border_w)]:
                draw_extra.ellipse([cx - 8, cy - 8, cx + 8, cy + 8], fill=(*gold, 255))

        out_path = os.path.join(FRONTEND_ASSETS, "cards", f"frame-{name}.png")
        img_arr.save(out_path, "PNG")
        print(f"Created {out_path}")


def draw_circle_badge_base(size=512):
    """Draw base white circle with #E8EAEC 1px border."""
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    margin = 8
    draw.ellipse([margin, margin, size - margin, size - margin], fill=(255, 255, 255, 255), outline=hex_to_rgb("#E8EAEC"), width=3)
    return img


def create_badges_and_appellations():
    """A-3: Badges (6) and Appellations (4) 512x512 flat 2-tone (#368FFF + #0ECEDB)."""
    p_blue = hex_to_rgb("#368FFF")
    mint = hex_to_rgb("#0ECEDB")
    
    # 1. first-visit (첫 방문)
    img = draw_circle_badge_base()
    draw = ImageDraw.Draw(img)
    # Flag + pin
    cx, cy = 256, 256
    # Flag pole
    draw.rectangle([cx - 40, cy - 110, cx - 32, cy + 120], fill=p_blue)
    # Flag wave
    draw.polygon([(cx - 32, cy - 110), (cx + 90, cy - 70), (cx - 32, cy - 30)], fill=mint)
    # Flag base
    draw.ellipse([cx - 70, cy + 110, cx - 2, cy + 130], fill=p_blue)
    # Sparkle
    draw.ellipse([cx + 60, cy + 20, cx + 76, cy + 36], fill=mint)
    img.save(os.path.join(FRONTEND_ASSETS, "badges", "first-visit.png"))

    # 2. seoul-conqueror (서울 정복자)
    img = draw_circle_badge_base()
    draw = ImageDraw.Draw(img)
    # Tower + Crown silhouette
    # Crown on top
    draw.polygon([(cx - 70, cy - 60), (cx - 40, cy - 120), (cx, cy - 80), (cx + 40, cy - 120), (cx + 70, cy - 60), (cx, cy - 40)], fill=mint)
    # Tower base
    draw.polygon([(cx - 35, cy - 40), (cx + 35, cy - 40), (cx + 55, cy + 110), (cx - 55, cy + 110)], fill=p_blue)
    draw.rectangle([cx - 70, cy + 110, cx + 70, cy + 126], fill=mint)
    img.save(os.path.join(FRONTEND_ASSETS, "badges", "seoul-conqueror.png"))

    # 3. card-collector-10 (카드 10장)
    img = draw_circle_badge_base()
    draw = ImageDraw.Draw(img)
    # 3 overlapping cards
    # Left card
    draw.rounded_rectangle([cx - 110, cy - 70, cx - 10, cy + 90], radius=12, fill=mint)
    # Right card
    draw.rounded_rectangle([cx + 10, cy - 70, cx + 110, cy + 90], radius=12, fill=mint)
    # Center card
    draw.rounded_rectangle([cx - 60, cy - 100, cx + 60, cy + 80], radius=16, fill=p_blue)
    # Star on center card
    draw.polygon([(cx, cy - 45), (cx + 12, cy - 15), (cx + 40, cy - 15), (cx + 18, cy + 5), (cx + 26, cy + 35), (cx, cy + 18), (cx - 26, cy + 35), (cx - 18, cy + 5), (cx - 40, cy - 15), (cx - 12, cy - 15)], fill=(255, 255, 255, 255))
    img.save(os.path.join(FRONTEND_ASSETS, "badges", "card-collector-10.png"))

    # 4. reviewer (기록가)
    img = draw_circle_badge_base()
    draw = ImageDraw.Draw(img)
    # Document notebook + pencil
    draw.rounded_rectangle([cx - 90, cy - 90, cx + 40, cy + 100], radius=16, fill=p_blue)
    draw.line([cx - 60, cy - 45, cx + 10, cy - 45], fill=(255, 255, 255, 255), width=6)
    draw.line([cx - 60, cy - 15, cx + 10, cy - 15], fill=(255, 255, 255, 255), width=6)
    draw.line([cx - 60, cy + 15, cx - 10, cy + 15], fill=(255, 255, 255, 255), width=6)
    # Diagonal pencil
    pencil_poly = [(cx + 20, cy + 90), (cx + 85, cy - 40), (cx + 105, cy - 30), (cx + 40, cy + 100), (cx + 10, cy + 110)]
    draw.polygon(pencil_poly, fill=mint)
    img.save(os.path.join(FRONTEND_ASSETS, "badges", "reviewer.png"))

    # 5. weekend-traveler (주말 여행자)
    img = draw_circle_badge_base()
    draw = ImageDraw.Draw(img)
    # Travel backpack / Compass
    draw.rounded_rectangle([cx - 75, cy - 60, cx + 75, cy + 90], radius=24, fill=p_blue)
    draw.rounded_rectangle([cx - 50, cy - 95, cx + 50, cy - 55], radius=12, fill=mint)
    draw.rounded_rectangle([cx - 50, cy - 10, cx + 50, cy + 60], radius=12, fill=(255, 255, 255, 255))
    draw.ellipse([cx - 15, cy + 10, cx + 15, cy + 40], fill=mint)
    img.save(os.path.join(FRONTEND_ASSETS, "badges", "weekend-traveler.png"))

    # 6. region-explorer (지역 탐험가)
    img = draw_circle_badge_base()
    draw = ImageDraw.Draw(img)
    # Compass / Globe
    draw.ellipse([cx - 90, cy - 90, cx + 90, cy + 90], outline=p_blue, width=12)
    draw.ellipse([cx - 70, cy - 70, cx + 70, cy + 70], fill=mint)
    # Compass needle
    draw.polygon([(cx, cy - 65), (cx + 18, cy), (cx, cy + 15), (cx - 18, cy)], fill=p_blue)
    draw.polygon([(cx, cy + 65), (cx + 18, cy), (cx, cy - 15), (cx - 18, cy)], fill=(255, 255, 255, 255))
    img.save(os.path.join(FRONTEND_ASSETS, "badges", "region-explorer.png"))

    # Appellations (칭호 4종)
    # 1. novice (초보 여행자)
    img = draw_circle_badge_base()
    draw = ImageDraw.Draw(img)
    # Sprout / Leaf
    draw.ellipse([cx - 65, cy - 60, cx + 10, cy + 40], fill=mint)
    draw.ellipse([cx - 10, cy - 80, cx + 65, cy + 20], fill=p_blue)
    draw.arc([cx - 40, cy - 20, cx + 40, cy + 110], start=45, end=135, fill=p_blue, width=10)
    draw.ellipse([cx - 40, cy + 90, cx + 40, cy + 120], fill=hex_to_rgb("#E8EAEC"))
    img.save(os.path.join(FRONTEND_ASSETS, "appellations", "novice.png"))

    # 2. explorer (탐험가)
    img = draw_circle_badge_base()
    draw = ImageDraw.Draw(img)
    # Mountains + Sun
    draw.ellipse([cx + 10, cy - 90, cx + 75, cy - 25], fill=mint)
    # Back mountain
    draw.polygon([(cx - 90, cy + 80), (cx - 15, cy - 40), (cx + 60, cy + 80)], fill=mint)
    # Front mountain
    draw.polygon([(cx - 30, cy + 80), (cx + 45, cy - 20), (cx + 105, cy + 80)], fill=p_blue)
    img.save(os.path.join(FRONTEND_ASSETS, "appellations", "explorer.png"))

    # 3. collector (수집가)
    img = draw_circle_badge_base()
    draw = ImageDraw.Draw(img)
    # Diamond gem
    poly_top = [(cx, cy - 95), (cx - 85, cy - 25), (cx + 85, cy - 25)]
    poly_bot = [(cx - 85, cy - 25), (cx + 85, cy - 25), (cx, cy + 95)]
    draw.polygon(poly_top, fill=mint)
    draw.polygon(poly_bot, fill=p_blue)
    draw.line([(cx, cy - 95), (cx, cy + 95)], fill=(255, 255, 255, 200), width=4)
    img.save(os.path.join(FRONTEND_ASSETS, "appellations", "collector.png"))

    # 4. master (마스터)
    img = draw_circle_badge_base()
    draw = ImageDraw.Draw(img)
    # Trophy + Crown / Laurel
    # Cup
    draw.rounded_rectangle([cx - 55, cy - 70, cx + 55, cy + 10], radius=16, fill=p_blue)
    draw.rectangle([cx - 15, cy + 10, cx + 15, cy + 65], fill=mint)
    draw.rounded_rectangle([cx - 60, cy + 65, cx + 60, cy + 90], radius=8, fill=p_blue)
    # Handles
    draw.arc([cx - 85, cy - 60, cx - 40, cy], start=90, end=270, fill=mint, width=8)
    draw.arc([cx + 40, cy - 60, cx + 85, cy], start=270, end=90, fill=mint, width=8)
    # Star inside
    draw.polygon([(cx, cy - 45), (cx + 8, cy - 25), (cx + 25, cy - 25), (cx + 12, cy - 10), (cx + 18, cy + 8), (cx, cy - 3), (cx - 18, cy + 8), (cx - 12, cy - 10), (cx - 25, cy - 25), (cx - 8, cy - 25)], fill=(255, 255, 255, 255))
    img.save(os.path.join(FRONTEND_ASSETS, "appellations", "master.png"))
    print("Created all Badges and Appellations.")


def create_empty_illustrations():
    """A-4: empty/{collection,travel-log,event}.png 360x240 line art #ADB1B5 + #0ECEDB point, transparent."""
    w, h = 360, 240
    line_c = hex_to_rgb("#ADB1B5")
    mint = hex_to_rgb("#0ECEDB")
    
    # 1. collection: 3 empty card slots
    img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = 180, 120
    # Left tilted card
    draw.rounded_rectangle([cx - 95, cy - 55, cx - 25, cy + 55], radius=8, outline=line_c, width=3)
    # Right tilted card
    draw.rounded_rectangle([cx + 25, cy - 55, cx + 95, cy + 55], radius=8, outline=line_c, width=3)
    # Center card (accented)
    draw.rounded_rectangle([cx - 45, cy - 65, cx + 45, cy + 65], radius=10, outline=line_c, width=4)
    # Plus sign in center card with mint accent
    draw.line([cx - 16, cy, cx + 16, cy], fill=mint, width=4)
    draw.line([cx, cy - 16, cx, cy + 16], fill=mint, width=4)
    img.save(os.path.join(FRONTEND_ASSETS, "empty", "collection.png"))

    # 2. travel-log: Open notebook and pen
    img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    # Left page
    draw.rounded_rectangle([cx - 90, cy - 55, cx - 5, cy + 55], radius=6, outline=line_c, width=3)
    # Right page
    draw.rounded_rectangle([cx + 5, cy - 55, cx + 90, cy + 55], radius=6, outline=line_c, width=3)
    # Lines on left page
    for ly in [cy - 30, cy - 10, cy + 10, cy + 30]:
        draw.line([cx - 75, ly, cx - 20, ly], fill=line_c, width=2)
    # Mint bookmark ribbon on right page
    draw.polygon([(cx + 40, cy - 55), (cx + 55, cy - 55), (cx + 55, cy + 10), (cx + 47, cy), (cx + 40, cy + 10)], fill=mint)
    # Pen
    draw.line([cx + 100, cy - 65, cx + 75, cy + 50], fill=line_c, width=4)
    draw.polygon([(cx + 75, cy + 50), (cx + 70, cy + 65), (cx + 85, cy + 58)], fill=mint)
    img.save(os.path.join(FRONTEND_ASSETS, "empty", "travel-log.png"))

    # 3. event: Calendar with flag
    img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    # Calendar body
    draw.rounded_rectangle([cx - 65, cy - 45, cx + 65, cy + 65], radius=10, outline=line_c, width=3)
    # Top bar
    draw.line([cx - 65, cy - 20, cx + 65, cy - 20], fill=line_c, width=3)
    # Spiral rings
    draw.ellipse([cx - 45, cy - 55, cx - 35, cy - 35], outline=line_c, width=3)
    draw.ellipse([cx + 35, cy - 55, cx + 45, cy - 35], outline=line_c, width=3)
    # Calendar grid dots
    for gx in [-35, 0, 35]:
        for gy in [0, 24, 46]:
            draw.ellipse([cx + gx - 3, cy + gy - 3, cx + gx + 3, cy + gy + 3], fill=line_c)
    # Mint Flag planted in calendar
    draw.line([cx, cy - 85, cx, cy - 20], fill=line_c, width=3)
    draw.polygon([(cx, cy - 85), (cx + 35, cy - 68), (cx, cy - 50)], fill=mint)
    img.save(os.path.join(FRONTEND_ASSETS, "empty", "event.png"))
    print("Created empty state illustrations.")


def create_event_banner_placeholders():
    """A-5: events/banner-placeholder-{1,2}.png 1600x900."""
    w, h = 1600, 900
    
    # 1. Mint gradient + map lines
    img1 = Image.new("RGBA", (w, h))
    c_start1 = hex_to_rgb("#0ECEDB")
    c_end1 = hex_to_rgb("#368FFF")
    for x in range(w):
        t = x / w
        r = int(c_start1[0] + (c_end1[0] - c_start1[0]) * t)
        g = int(c_start1[1] + (c_end1[1] - c_start1[1]) * t)
        b = int(c_start1[2] + (c_end1[2] - c_start1[2]) * t)
        draw_line = Image.new("RGBA", (1, h), (r, g, b, 255))
        img1.paste(draw_line, (x, 0))
        
    # Topographic / Grid lines (white with low opacity)
    overlay1 = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    d1 = ImageDraw.Draw(overlay1)
    for rad in range(150, 900, 90):
        d1.ellipse([w - 300 - rad, h - 150 - rad, w - 300 + rad, h - 150 + rad], outline=(255, 255, 255, 45), width=2)
    for rad in range(100, 600, 80):
        d1.ellipse([250 - rad, 200 - rad, 250 + rad, 200 + rad], outline=(255, 255, 255, 35), width=2)
    img1 = Image.alpha_composite(img1, overlay1)
    img1.save(os.path.join(FRONTEND_ASSETS, "events", "banner-placeholder-1.png"))

    # 2. Primary gradient + geometric cards
    img2 = Image.new("RGBA", (w, h))
    c_start2 = hex_to_rgb("#368FFF")
    c_end2 = hex_to_rgb("#1A85E8")
    for y in range(h):
        t = y / h
        r = int(c_start2[0] + (c_end2[0] - c_start2[0]) * t)
        g = int(c_start2[1] + (c_end2[1] - c_start2[1]) * t)
        b = int(c_start2[2] + (c_end2[2] - c_start2[2]) * t)
        draw_line = Image.new("RGBA", (w, 1), (r, g, b, 255))
        img2.paste(draw_line, (0, y))
        
    overlay2 = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    d2 = ImageDraw.Draw(overlay2)
    # Angled card outlines
    d2.rounded_rectangle([w - 450, 100, w - 100, 650], radius=32, outline=(255, 255, 255, 60), width=4)
    d2.rounded_rectangle([w - 320, 200, w + 30, 750], radius=32, outline=(255, 255, 255, 40), width=4)
    d2.rounded_rectangle([w - 580, 220, w - 230, 770], radius=32, outline=(255, 255, 255, 50), width=4)
    # Circle accents
    d2.ellipse([150, 250, 350, 450], outline=(255, 255, 255, 50), width=3)
    d2.ellipse([200, 300, 300, 400], fill=(255, 255, 255, 30))
    img2 = Image.alpha_composite(img2, overlay2)
    img2.save(os.path.join(FRONTEND_ASSETS, "events", "banner-placeholder-2.png"))
    print("Created event banner placeholders.")


def main():
    print("Generating assets...")
    create_default_landmark_card()
    create_card_frames()
    create_badges_and_appellations()
    create_empty_illustrations()
    create_event_banner_placeholders()
    print("All assets successfully generated!")


if __name__ == "__main__":
    main()
