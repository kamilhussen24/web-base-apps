#!/usr/bin/env python3
"""
Android Icon Generator
======================
Give one image → Get all Android icons in correct sizes and names.

Usage:
    python3 generate_icons.py your_logo.png

Output:
    icons/ folder with all required files
"""

import sys
import os
from PIL import Image

# All required Android icon sizes
ICONS = {
    "mipmap-mdpi":    {"launcher": 48,  "round": 48},
    "mipmap-hdpi":    {"launcher": 72,  "round": 72},
    "mipmap-xhdpi":   {"launcher": 96,  "round": 96},
    "mipmap-xxhdpi":  {"launcher": 144, "round": 144},
    "mipmap-xxxhdpi": {"launcher": 192, "round": 192},
}

def generate_icons(input_path):
    if not os.path.exists(input_path):
        print(f"❌ File not found: {input_path}")
        sys.exit(1)

    print(f"📂 Loading: {input_path}")
    img = Image.open(input_path).convert("RGBA")

    output_base = "icons"
    count = 0

    for folder, sizes in ICONS.items():
        folder_path = os.path.join(output_base, folder)
        os.makedirs(folder_path, exist_ok=True)

        # ic_launcher (square)
        size = sizes["launcher"]
        square = img.resize((size, size), Image.LANCZOS)
        out_path = os.path.join(folder_path, "ic_launcher.png")
        square.save(out_path, "PNG")
        print(f"  ✅ {folder}/ic_launcher.png ({size}x{size})")
        count += 1

        # ic_launcher_round (circle crop)
        round_img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
        mask = Image.new("L", (size, size), 0)
        from PIL import ImageDraw
        draw = ImageDraw.Draw(mask)
        draw.ellipse([0, 0, size, size], fill=255)
        square_copy = img.resize((size, size), Image.LANCZOS)
        round_img.paste(square_copy, (0, 0), mask)
        out_path_round = os.path.join(folder_path, "ic_launcher_round.png")
        round_img.save(out_path_round, "PNG")
        print(f"  ✅ {folder}/ic_launcher_round.png ({size}x{size})")
        count += 1

    # Splash logo (512x512)
    splash_path = os.path.join(output_base, "drawable")
    os.makedirs(splash_path, exist_ok=True)
    splash = img.resize((512, 512), Image.LANCZOS)
    splash.save(os.path.join(splash_path, "splash_logo.png"), "PNG")
    print(f"  ✅ drawable/splash_logo.png (512x512)")
    count += 1

    print(f"\n🎉 Done! {count} files generated in '{output_base}/' folder")
    print(f"\n📋 Copy these folders to your Android project:")
    print(f"   icons/mipmap-*     →  app/src/main/res/")
    print(f"   icons/drawable/    →  app/src/main/res/drawable/")

if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("Usage: python3 generate_icons.py YOUR_IMAGE.png")
        print("Example: python3 generate_icons.py logo.png")
        sys.exit(1)

    generate_icons(sys.argv[1])
