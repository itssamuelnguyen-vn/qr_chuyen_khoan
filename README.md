# QR Chuyển Khoản

A small Android app that makes a bank-transfer QR code (VietQR / NAPAS 247) for any Vietnamese bank account.
Pick the bank, type the account number and holder name, optionally an amount and a transfer note, press
**Tạo mã QR**. The card can be saved to the gallery or shared to Zalo, Messenger and so on. Any Vietnamese
banking or e-wallet app can scan it.

Everything happens on the phone: no internet, no API key, no permissions.

- Android 10+ (minSdk 29), sideloaded APK (`app-release.apk`), not on the Play Store.
- Kotlin, Jetpack Compose + Material 3, ZXing core (QR drawing), DataStore (remembers the last account).

See `CLAUDE.md` for how the code is organised and how to build it.
