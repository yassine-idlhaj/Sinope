<div align="center">

<img src="playstore/sinope_playstore_512.png" alt="Sinope" width="96" height="96">

# Sinope

**An offline two-factor authentication app for Android.**

Your codes are generated on your device. There is no account, no server,
and no internet permission.

[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-7C3AED.svg)](LICENSE)
[![Platform](https://img.shields.io/badge/Platform-Android%2011%2B-00D4FF.svg)](#requirements)
[![Privacy](https://img.shields.io/badge/Data%20collected-none-00E5A0.svg)](https://yassine-idlhaj.github.io/Sinope/privacy.html)

</div>

---

## Why

Most authenticator apps ask you to trust a company with the one thing that
protects every account you own. Sinope asks you to trust an operating system
guarantee instead: **the app does not declare the `INTERNET` permission**, so
Android will not let it open a network connection. That is not a promise in a
privacy policy — it is enforced by the platform, and you can verify it in one
line of [`AndroidManifest.xml`](app/src/main/AndroidManifest.xml).

## Features

- **TOTP codes** (RFC 6238) — 6 or 8 digits, 30s or 60s periods, SHA-1/256/512
- **Add by QR scan or by hand** — camera is optional
- **Encrypted at rest** — each secret is sealed with AES-256-GCM under a key held
  in the Android Keystore
- **Encrypted backups** — export to a `.sinope` file protected by Argon2id + AES-256-GCM
- **Biometric app lock** and **screenshot protection**, both optional
- **Search, favourites, custom icons and colours**
- **Eight languages** — English, العربية, Français, Español, Deutsch, Português, Türkçe, 日本語
- **No ads, no analytics, no crash reporting, no tracking**

## Security design

| Concern | Approach |
|---|---|
| Secrets at rest | AES-256-GCM; key generated in the Android Keystore, non-exportable |
| Backup files | Argon2id (64 MiB, 3 iterations) derives the key; AES-256-GCM seals the payload |
| Cloud backup | The account database is excluded from Android auto-backup and device transfer |
| Biometrics | Handled by `BiometricPrompt`; the app receives a pass/fail result only |
| Network | No `INTERNET` permission — exfiltration is impossible, not merely unimplemented |

Backup passwords are never stored and cannot be recovered. That is deliberate;
it also means a lost password means a lost backup.

## Permissions

| Permission | Purpose | Required |
|---|---|---|
| `CAMERA` | Scanning setup QR codes | No — manual entry works without it |
| `USE_BIOMETRIC` | The optional app lock | No — off by default |

Nothing else is requested.

## Requirements

- **Android 11 (API 30)** or newer
- Built against the Android 37 SDK

## Building

```bash
git clone https://github.com/yassine-idlhaj/Sinope.git
cd Sinope
./gradlew assembleDebug          # debug APK
./gradlew testDebugUnitTest      # unit tests
./gradlew lintDebug              # static analysis
```

Release builds are signed with a key that is not in this repository. A
`./gradlew assembleRelease` without it produces an unsigned artifact.

## Architecture

Clean-architecture layering with Jetpack Compose throughout.

```
presentation/   Compose screens, ViewModels, UI state and events
domain/         Use cases, models, repository interfaces — no Android deps
data/           Room database, DataStore, crypto, backup serialisation
core/           Design tokens, shared composables, utilities
di/             Hilt modules
```

**Stack** — Kotlin · Jetpack Compose · Hilt · Room · DataStore · CameraX ·
ML Kit (on-device barcode) · BouncyCastle (Argon2id) · kotlinx.serialization

## Privacy

Sinope collects nothing. The full policy is at
**[yassine-idlhaj.github.io/Sinope/privacy.html](https://yassine-idlhaj.github.io/Sinope/privacy.html)**.

## Contributing

Issues and pull requests are welcome. Please keep changes scoped, and run
`./gradlew testDebugUnitTest lintDebug` before opening a PR.

## Licence

[GNU General Public License v3.0](LICENSE).

You may use, study, share and modify this software. If you distribute a
modified version, it must also be released under the GPL-3.0 with its source
available — so a closed-source fork of an authenticator app is not permitted.

<div align="center">
<sub>Sinope is a moon of Jupiter.</sub>
</div>
