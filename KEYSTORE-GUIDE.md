# Keystore Guide — Android App Signing

A complete guide to create, manage, and use an Android keystore for signing your Android app.

---

## What is a Keystore?

A keystore is a digital signature file required to publish your app on the Google Play Store.
Every update you release must be signed with the **same keystore file**.

> **Warning:** If you lose your keystore, you will never be able to update your app on Play Store again. Always keep a secure backup.

---

## Requirements

Make sure Java is installed on your machine:

```bash
java -version
```

If not installed:

```bash
# Ubuntu / Debian / Linux
sudo apt install default-jdk

# Check again
keytool -help
```

---

## Step 1 — Create a New Keystore

```bash
keytool -genkey -v \
  -keystore release.keystore \
  -alias mykey \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000
```

You will be prompted:

```
Enter keystore password:               → Set a strong password
Re-enter new password:                 → Same password again
What is your first and last name?      → Your name
What is your organizational unit?      → Press Enter to skip
What is your organization?             → Your brand or company name
What is your city or locality?         → Your city
What is your state or province?        → Your country
What is your two-letter country code?  → BD
Is CN=... correct? [no]:               → yes
Enter key password for mykey:          → Same password (or set a different one)
```

A file named `release.keystore` will be created in your current directory.

---

## Step 2 — View Keystore Information

```bash
keytool -list -v \
  -keystore release.keystore \
  -storepass YOUR_PASSWORD
```

Example output:

```
Keystore type: PKCS12
Keystore provider: SUN

Your keystore contains 1 entry

Alias name: mykey
Creation date: ...
Certificate fingerprints:
  SHA1:   1B:04:23:E0:...
  SHA256: 79:C2:43:3D:...
```

**Save these values:**

| Value | Used for |
|-------|----------|
| Alias name | `KEY_ALIAS` in GitHub Secrets |
| SHA256 fingerprint | `assetlinks.json` for deep links |

---

## Step 3 — Generate Base64 String

GitHub Secrets cannot store binary files, so we convert the keystore to a Base64 string.

### Linux:
```bash
base64 -w 0 release.keystore > keystore_base64.txt
```

### Mac:
```bash
base64 -i release.keystore > keystore_base64.txt
```

View the output:
```bash
cat keystore_base64.txt
```

The output will look like this:
```
MIIKGQIBAzCCCd8GCSqGSIb3DQEHAaCCCdAEggnMMIIJyDCCBW8GCS...
```

Copy the **entire content** — this is what goes into GitHub Secrets.

---

## Step 4 — Add to GitHub Secrets

**GitHub repo → Settings → Secrets and variables → Actions → New repository secret**

| Secret Name | Value |
|-------------|-------|
| `KEYSTORE_BASE64` | Full content of `keystore_base64.txt` |
| `STORE_PASSWORD` | Password you set when creating the keystore |
| `KEY_ALIAS` | Alias you used (e.g. `mykey`) |
| `KEY_PASSWORD` | Key password (same as store password if not set separately) |

---

## Step 5 — Verify Everything Works

```bash
keytool -list \
  -keystore release.keystore \
  -storepass YOUR_PASSWORD
```

Expected output:
```
Keystore type: PKCS12
Your keystore contains 1 entry
mykey, ...
```

If you see your alias name — everything is correct.

---

## Change Passwords

### Change store password:
```bash
keytool -storepasswd \
  -keystore release.keystore \
  -storepass OLD_PASSWORD \
  -new NEW_PASSWORD
```

### Change key password:
```bash
keytool -keypasswd \
  -keystore release.keystore \
  -alias mykey \
  -keypass OLD_PASSWORD \
  -new NEW_PASSWORD
```

> After changing passwords, update your GitHub Secrets accordingly.

---

## Backup Your Keystore

Keep all three of these safe:

1. `release.keystore` file
2. Store password
3. Key alias and key password

Recommended backup locations:
- Google Drive (private folder)
- Telegram Saved Messages
- External hard drive

```bash
# Copy to a backup location
cp release.keystore /path/to/backup/release.keystore
```

> Never commit `release.keystore` to GitHub. It is already added to `.gitignore` in both templates.

---

## Quick Reference

```bash
# 1. Create keystore
keytool -genkey -v \
  -keystore release.keystore \
  -alias mykey \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000

# 2. View keystore info
keytool -list -v \
  -keystore release.keystore \
  -storepass YOUR_PASSWORD

# 3. Generate base64
base64 -w 0 release.keystore > keystore_base64.txt

# 4. Add to GitHub Secrets:
#    KEYSTORE_BASE64 → content of keystore_base64.txt
#    STORE_PASSWORD  → your store password
#    KEY_ALIAS       → mykey
#    KEY_PASSWORD    → your key password
```

---

## Troubleshooting

| Problem | Solution |
|---------|----------|
| `keytool: command not found` | Install Java: `sudo apt install default-jdk` |
| Wrong password error | Double-check your store and key passwords |
| Play Store update rejected | Make sure you are using the exact same keystore |
| `Alias does not exist` | Run `keytool -list` to see the correct alias name |
| Keystore file lost | You cannot update the app on Play Store anymore — start a new app |

---

## Summary

| Item | Where to Use |
|------|-------------|
| `release.keystore` | Keep safe — never share or commit |
| Store password | `STORE_PASSWORD` GitHub Secret |
| Key alias | `KEY_ALIAS` GitHub Secret |
| Key password | `KEY_PASSWORD` GitHub Secret |
| Base64 output | `KEYSTORE_BASE64` GitHub Secret |
| SHA256 fingerprint | `assetlinks.json` for Android deep links |
