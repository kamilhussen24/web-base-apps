# Android WebView App Template

Complete ready-to-use Android WebView app template.  
Change 10 things → Push → APK/AAB ready!

---

## ✏️ What to Change

| # | What | File |
|---|------|------|
| 1 | Package name | `app/build.gradle` → `namespace` & `applicationId` |
| 2 | Package name | `AndroidManifest.xml` → all 3 activity names |
| 3 | Package name | All 3 `.kt` files → first line |
| 4 | App name | `res/values/strings.xml` |
| 5 | Version | `app/build.gradle` → `versionCode` & `versionName` |
| 6 | Website URL | `MainActivity.kt` → `webUrl` |
| 7 | Website host | `MainActivity.kt` → `webHost` |
| 8 | Share message | `MainActivity.kt` → `shareMessage` |
| 9 | Brand color | `res/values/colors.xml` → `primary` |
| 10 | Project name | `settings.gradle` → `rootProject.name` |

---

## 🖼️ Replace These Files

| File | Size |
|------|------|
| `res/drawable/splash_logo.png` | 512×512 recommended |
| `res/mipmap-mdpi/ic_launcher.png` | 48×48 |
| `res/mipmap-hdpi/ic_launcher.png` | 72×72 |
| `res/mipmap-xhdpi/ic_launcher.png` | 96×96 |
| `res/mipmap-xxhdpi/ic_launcher.png` | 144×144 |
| `res/mipmap-xxxhdpi/ic_launcher.png` | 192×192 |

> Placeholder files already exist — just replace with your images.

---

## 🌐 Website Integration

Add this code to **every page** of your website:

### 1. External Link Handler
Opens external links in Chrome Custom Tab instead of WebView.

```html
<!-- App Function -->
<script>
document.addEventListener('DOMContentLoaded', function () {
  document.querySelectorAll('a').forEach(function (link) {
    link.addEventListener('click', function (e) {
      var href = link.getAttribute('href');
      // External links — not your own website
      if (href && href.startsWith('http') && !href.includes('your-website.com')) {
        if (typeof Android !== 'undefined') {
          e.preventDefault();
          Android.openUrl(href);
        }
      }
    });
  });
});
</script>
```

> ✏️ Replace `your-website.com` with your actual domain.

---

### 2. Share Button (optional)
Only add if your website has a share button.

**HTML button:**
```html
<button onclick="shareContent(event)">Share</button>
```

**JavaScript:**
```html
<script>
  function shareContent(event) {
    if (event) { event.stopPropagation(); event.preventDefault(); }

    const shareText = 'Check out this app!\nhttps://your-website.com';

    if (typeof Android !== "undefined") {
      // Inside app — native Android share dialog
      Android.share();

    } else if (navigator.share) {
      // Mobile browser — system share
      navigator.share({ text: shareText })
        .catch(() => copyToClipboard(shareText));

    } else {
      // Desktop — clipboard copy
      copyToClipboard(shareText);
    }
  }

  function copyToClipboard(text) {
    navigator.clipboard.writeText(text)
      .then(() => alert("Link copied!"))
      .catch(() => alert("Could not copy link."));
  }
</script>
```

> ✏️ Replace `shareText` with your own message and URL.

---

### 3. How it works

| Situation | What happens |
|-----------|-------------|
| User inside app clicks external link | Opens in Chrome Custom Tab |
| User inside app clicks share button | Native Android share dialog |
| User in mobile browser clicks share | System share sheet |
| User on desktop clicks share | Link copied to clipboard |

---

## 🚀 How to Build

**Actions** tab → **Build Android App** → **Run workflow** → select:

| Option | Needs | Output |
|--------|-------|--------|
| `debug` | Nothing | Debug APK only |
| `release` | Keystore secrets | Release APK + AAB |
| `both` | Keystore secrets | All 3 files |

Download from **Actions** → latest run → **Artifacts**.

---

## 🔑 GitHub Secrets (for release only)

`Settings → Secrets → Actions → New secret`:

| Secret | Value |
|--------|-------|
| `KEYSTORE_BASE64` | `base64 -w 0 release.keystore` |
| `STORE_PASSWORD` | Keystore password |
| `KEY_ALIAS` | Key alias |
| `KEY_PASSWORD` | Key password |

---

## 📋 Checklist

**App:**
- [ ] Package name (3 places)
- [ ] App name
- [ ] Website URL + host
- [ ] Share message
- [ ] Brand color
- [ ] Logo (`splash_logo.png`)
- [ ] App icons (all 6 sizes)
- [ ] Splash tagline (`activity_splash.xml`)
- [ ] Project name (`settings.gradle`)
- [ ] Version (`versionCode` + `versionName`)

**Website:**
- [ ] External link handler added to all pages
- [ ] Share button code added (if needed)
- [ ] `your-website.com` replaced with actual domain

---

## License
MIT — Free to use and modify.
