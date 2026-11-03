# QR Chuyển Khoản

Android app (sideloaded APK, never on the Play Store) that makes VietQR bank-transfer QR codes offline. UI text is
Vietnamese; code, names, comments, commits and docs are English. The owner is a Rails developer new to Android:
keep code simple, explain Android concepts briefly by comparison with Rails.

## Build, test, install

Gradle runs on Windows (from WSL call it through PowerShell). JDK: `D:\dev\android-sdk\jbr`; SDK path in
`local.properties` (not committed).

```powershell
$env:JAVA_HOME='D:\dev\android-sdk\jbr'
.\gradlew.bat testDebugUnitTest      # JVM tests: payload, CRC, formatting, bank search, form rules
.\gradlew.bat assembleRelease        # app\build\outputs\apk\release\app-release.apk
```

Ship the **release** APK (R8-shrunk, signed with the local debug key so it installs over earlier APKs).
The owner tests on their own phone: send the APK, do not start the emulator (the laptop is too weak).

## How it works

- `qr/VietQr.kt` builds the QR text: EMVCo TLV fields (`00` format, `01` 11 static / 12 with amount, `38` NAPAS
  GUID `A000000727` + BIN + account + service `QRIBFTTA`, `53` 704, `54` amount, `58` VN, `62/08` note) and a
  CRC-16/CCITT-FALSE in `63`. The holder name is **not** encoded (the payer's bank looks it up); it is only printed
  on the card. Notes are reduced to accent-free `A-Za-z0-9 ` and at most 25 characters.
  `VietQrTest` compares against two codes decoded from img.vietqr.io (2026-11-03): keep those passing.
- `qr/Banks.kt` is the bank list (BIN, code, names) copied from `https://api.vietqr.io/v2/banks`; refresh by hand.
- `qr/QrCard.kt` draws the whole card (header, QR, name, account, amount, note) into one Bitmap with Android
  Canvas; the screen shows that same bitmap, saves it and shares it.
- `data/AccountStore.kt` (DataStore) remembers the last bank/account/name. `data/ImageExport.kt` saves through
  MediaStore to `Pictures/QR Chuyển Khoản` (no permission on Android 10+) and shares via FileProvider
  (`cache/shared`).
- `ui/`: `MainViewModel` (form state + result), `FormScreen`, `BankPicker` (bottom sheet, accent-free search),
  `ResultScreen` (full screen brightness while shown). Two screens switched in `MainActivity`, no navigation library.

## Working rules

- Ask before adding features outside the agreed list. Ideas offered but not built yet: several saved accounts,
  home-screen widget with the main account's QR, amount written in words.
