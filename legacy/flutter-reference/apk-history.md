# Historical APK Build Registry

This file tracks historical Check2GO Android APKs recovered from archived builds.

The APK filename date is treated as the build/reference date unless stronger evidence is available. APK binaries themselves are not stored here; this registry records verified metadata and notable structural changes.

## Build timeline

| Build date | Source artifact | Size | SHA-256 | Package | Version | SDK | Notable delta |
|---|---|---:|---|---|---|---|---|
| 2025-07-30 | `30.07.2025(1).apk` | 23.58 MiB | `3a031fee591bc342b1a6d12987332217756f87ceee107268fe770f0bd10c87b2` | `co.check2go.check2go` | `1.0.0` (`versionCode 1`) | min 21 / target 35 / compile 35 | Earliest recovered build; baseline trip/checklist/document/navigation assets. |
| 2025-08-06 | `06.08.2025(1).apk` | 23.82 MiB | `2b236a7fbfaec40a1aa6d66063d380ed854988d9016bd471c7023777d0cf2045` | `co.check2go.check2go` | `1.0.0` (`versionCode 1`) | min 21 / target 35 / compile 35 | Adds `ic_edit.svg` and `checklist_empty.png`; AOT app binary changes. |
| 2025-08-24 | `24.08.2025(1).apk` | 57.35 MiB | `989e1216613dda7b97b9328249933d63f20c0279ccd68399f071a024c3603571` | `co.check2go.check2go` | `1.0.0` (`versionCode 1`) | min 24 / target 36 / compile 36 | Major runtime shift; SQLite native library appears; SDK baseline rises. |
| 2025-08-31 | `31.08.2025(1).apk` | 62.98 MiB | `5e2b9bd3a6430295242ab29815e79ef4219a4c7e8f42db37d2bec6f468a70553` | `co.check2go.check2go` | `1.0.0` (`versionCode 1`) | min 24 / target 36 / compile 36 | ObjectBox JNI added alongside SQLite; adds `ic_checkmark.svg`. |
| 2025-09-11 | `11.09.2025(1).apk` | 63.41 MiB | `c696b59afb382452b6282189be3eb4baa09a7192ab87e068395951d3724c4ff5` | `co.check2go.check2go` | `1.0.0` (`versionCode 1`) | min 24 / target 36 / compile 36 | Same packaged layout as 2025-08-31 but rebuilt application/native persistence binaries. |
| 2025-09-23 | `23.09.2025(1).apk` | 66.94 MiB | `ad20041c9aff9b1ec1b84303e9cee1398315f3aee6d76f0dfd974fca374c5d8e` | `co.check2go.check2go` | `1.0.0` (`versionCode 1`) | min 24 / target 36 / compile 36 | ObjectBox removed; Amplify Cognito/secure storage + Google credentials/sign-in components and multidex appear. |
| 2025-10-11 | `11.10.2025(1).apk` | 68.14 MiB | `3e54e99650529c4a7a828cfa3172c5e4120d63e56a17149fed4664369316788f` | `co.check2go.check2go` | `1.0.0` (`versionCode 1`) | min 24 / target 36 / compile 36 | Adds document/action assets: copy, overflow action and remove. |
| 2025-10-26 | `26.10.2025(1).apk` | 74.84 MiB | `3d760eb6d3c7755ef625a1b0683410ccd11afa63b452a33cefe0ad42797ec703` | `co.check2go.check2go` | `1.0.0` (`versionCode 1`) | min 24 / target 36 / compile 36 | Adds save action, Amplify Authenticator assets, AndroidX DataStore, Apache Tika and URL-launcher WebView support. |
| 2025-10-27 | `27.10.2025(1).apk` | 74.87 MiB | `a19e046ba872d85cff459d8145ba398d2bb1fcbd642c65cea8aa97f540e7e928` | `co.check2go.check2go` | `1.0.0` (`versionCode 1`) | min 24 / target 36 / compile 36 | Application-code revision with unchanged archive layout. |
| 2025-10-27 | `27.10.2025 (dark theme)(1).apk` | 74.87 MiB | `f54673a2d6c189a41c26ee797ae9888f0d8524e1802f30b0fd3a818dcad006e3` | `co.check2go.check2go` | `1.0.0` (`versionCode 1`) | min 24 / target 36 / compile 36 | Dark-theme variant; only ABI-specific Dart AOT `libapp.so` payloads differ from standard 27.10. |
| 2025-10-28 | `28.10.2025_.apk` | 75.26 MiB | `73740a6353180a817afb98cbe99d1e15656464377537d6f31a4611ab034a1ee2` | `co.check2go.check2go` | `1.0.0` (`versionCode 1`) | min 24 / target 36 / compile 36 | Another AOT-code-only revision; packaged paths unchanged. |
| 2025-10-30 | `30.10.2025.apk` | 72.28 MiB | `fe17f6181f8a212f8f6f875f4d44e2d3bea5d8c79ed257f31182557b2a6aed83` | `co.check2go.check2go` | `1.0.0` (`versionCode 1`) | min 24 / target 36 / compile 36 | Android auth footprint simplified: Google Sign-In / AndroidX Credentials/FIDO components removed while Amplify assets remain. |
| 2025-12-30 | `30.12.2025.apk` | 103.74 MiB | `13e176212bba26d022c044814e779f53882c210db7dd2f78ed9240e95ff59888` | `co.check2go.check2go` | `1.0.0` (`versionCode 1`) | min 24 / target 36 / compile 36 | Major OCR expansion: bundled Google ML Kit models + `libmlkit_google_ocr_pipeline.so`; new people/travel assets; label becomes `Check2go`. |
| 2026-01-12 | `12.01.2026.apk` | 104.18 MiB | `24b77ae351300eb58b04befe5c4130a7f415a5ee79a8b6132c37222b24beeb85` | `co.check2go.check2go` | `1.0.0` (`versionCode 1`) | min 24 / target 36 / compile 36 | Camera/gallery input added: `CAMERA`, ImagePicker provider, `ic_camera.svg`, `ic_image.svg`. |
| 2026-01-21 | `21.01.2026.apk` | 108.35 MiB | `e65540cbf91dc5c1d24c4bf59791e48b157f19fdb5791fbda40a4870828c2301` | `co.check2go` | `0.5.0` (`versionCode 1`) | min 24 / target 36 / compile 36 | **Android identity reset:** package changes to `co.check2go`, main activity to `co.check2go.MainActivity`, versionName to `0.5.0`. Local notifications + WorkManager/Room scheduling stack added. |
| 2026-01-28 | `28.01.2026.apk` | 108.57 MiB | `df360f93684612cf97b6f2263a763863be92a48d6fdcf6a0a4f537d430e0bd57` | `co.check2go` | `0.5.0` (`versionCode 1`) | min 24 / target 36 / compile 36 | UI/navigation expansion: PDF, category/filter, language, location, refresh, share and theme assets; old mock destination backgrounds removed. |
| 2026-02-02 | `02.02.2026(1).apk` | 110.99 MiB | `f1c0423c4796943eae55152a8763a1b95eca30da481fd8ce1f3f538d5ffbfe8f` | `co.check2go` | `0.5.0` (`versionCode 1`) | min 24 / target 36 / compile 36 | Contact/people expansion: `READ_CONTACTS`, `flutter_contacts`, import/search/link/mail/person assets, social-provider icons and `intl_phone_field` flag assets. Matches the existing 02.02.2026 recovery reference hash. |

## Duplicate artifact aliases

- `12.01.2026(1).apk` is byte-for-byte identical to `12.01.2026.apk`: same size and SHA-256 `24b77ae351300eb58b04befe5c4130a7f415a5ee79a8b6132c37222b24beeb85`. It is not counted as a separate build.

## Android identity timeline

### Through 2026-01-12

- Package: `co.check2go.check2go`
- Main activity: `co.check2go.check2go.MainActivity`
- Version name: `1.0.0`
- Version code: `1`

### From 2026-01-21

- Package: `co.check2go`
- Main activity: `co.check2go.MainActivity`
- Version name: `0.5.0`
- Version code: `1`

The package/application ID change means Android treats the 2026-01-21 line as a different application identity from earlier `co.check2go.check2go` builds rather than a normal in-place APK update.

Common baseline from 2025-08-24 onward remains min SDK 24 / target SDK 36 / compile SDK 36. All inspected builds bundle arm64-v8a, armeabi-v7a and x86_64.

## Asset and dependency evolution observed

### 2025-07-30 → 2025-10-30

Key transitions:
- 2025-08-06: edit/checklist-empty UI assets.
- 2025-08-24: SQLite introduced; SDK baseline raised.
- 2025-08-31: ObjectBox JNI introduced.
- 2025-09-23: ObjectBox removed; Amplify Cognito/secure storage, Google credentials/sign-in support and multidex introduced.
- 2025-10-11: document/action UI assets added.
- 2025-10-26: DataStore, Amplify Authenticator assets, Apache Tika, save action and WebView support added.
- 2025-10-27/28: AOT application revisions, including a 27.10 dark-theme variant.
- 2025-10-30: native Google Sign-In / AndroidX Credentials/FIDO footprint removed while Amplify remains.

### 2025-12-30 — OCR expansion

Major additions:
- bundled Google ML Kit text-recognition models;
- `libmlkit_google_ocr_pipeline.so` for all three ABIs;
- ML Kit init/service/provider components and Google DataTransport components;
- `ic_beach.svg`, `ic_person_add.svg`, `ic_person_square.svg`;
- app label capitalization changes to `Check2go`.

### 2026-01-12 — camera/gallery input

Added:
- `android.permission.CAMERA`;
- `io.flutter.plugins.imagepicker.ImagePickerFileProvider`;
- Google module-dependency service;
- `ic_camera.svg`;
- `ic_image.svg`.

ML Kit OCR remains present, strongly supporting a camera/gallery → OCR/document-processing path.

### 2026-01-21 — app identity + notifications/scheduling

This is one of the largest architectural transitions in the archive:

- package changes `co.check2go.check2go` → `co.check2go`;
- main activity changes accordingly;
- versionName changes `1.0.0` → `0.5.0`;
- new permissions include `RECEIVE_BOOT_COMPLETED`, `VIBRATE`, `WAKE_LOCK`, `POST_NOTIFICATIONS`, `SCHEDULE_EXACT_ALARM`, `USE_EXACT_ALARM`, and `FOREGROUND_SERVICE`;
- `flutter_local_notifications` receivers appear;
- AndroidX WorkManager services/receivers and Room invalidation service appear;
- new product assets include alert/notification states, empty states, people/profile/trip/sign-out and `logo.svg`;
- old `ic_beach.svg`, `checklist_empty.png` and `travelers_list_empty.png` are removed.

This strongly supports introduction of persistent local reminder/notification scheduling plus a broader UI/navigation redesign.

### 2026-01-28 — navigation/settings/document polish

Added product assets:
- `ic_arrow_left.svg`;
- category/filter controls;
- chevrons and close;
- `ic_document_pdf.svg`;
- `ic_language.svg`;
- `ic_location.svg`;
- `ic_refresh.svg`;
- `ic_share.svg`;
- `ic_theme.svg`.

Removed:
- `mock_background_abudhabi.jpg`;
- `mock_background_telaviv.jpg`.

Package/version/notification permissions remain stable from 2026-01-21.

### 2026-02-02 — contacts + phone-field/auth UI expansion

Added:
- `android.permission.READ_CONTACTS`;
- verified `flutter_contacts` Android plugin code;
- `ic_import_contacts.svg`;
- `ic_link.svg`, `ic_mail.svg`, `ic_person_circle.svg`, `ic_replace.svg`, `ic_search.svg`;
- `ic_apple.svg`, `ic_facebook.svg`, `ic_google.svg`;
- `logo_checkmark.svg`, `logo_static.svg`, `logo_static.png`;
- 256 country/region flag assets from `intl_phone_field`.

The Apple/Facebook/Google icons demonstrate provider-facing UI assets; icon presence alone does not prove all three providers were fully functional. The existing deeper `apk-2026-02-02.md` recovery note should remain the source for behavioral/module-level interpretation.

## Recovery interpretation

The most important recovered transitions are:

1. **2025-07-30 → 2025-08-06** — incremental UI/checklist work.
2. **2025-08-06 → 2025-08-24** — SDK/runtime transition and SQLite introduction.
3. **2025-08-24 → 2025-08-31** — ObjectBox introduced alongside SQLite.
4. **2025-08-31 → 2025-09-23** — later rebuild followed by major auth/dependency migration to Amplify/Cognito.
5. **2025-09-23 → 2025-10-26** — document/actions, DataStore, Tika and Authenticator expansion.
6. **2025-10-26 → 2025-10-30** — rapid AOT/theme iterations, then simplification of native Google credential/sign-in footprint.
7. **2025-10-30 → 2025-12-30** — major OCR/document-processing expansion.
8. **2025-12-30 → 2026-01-12** — camera/gallery input added on top of OCR.
9. **2026-01-12 → 2026-01-21** — **package/application identity changes**, versionName resets to `0.5.0`, and local notifications/WorkManager scheduling arrive.
10. **2026-01-21 → 2026-01-28** — broad navigation/settings/document UI polish.
11. **2026-01-28 → 2026-02-02** — contacts import, phone-field assets and expanded identity/auth UI.

These observations describe artifacts actually present in the APKs. They are not complete source-level changelogs: Dart application code is AOT-compiled and exact source cannot be losslessly reconstructed from an APK alone.

## Update procedure for future historical APKs

For every newly supplied historical APK:

1. record filename/date and byte size;
2. calculate SHA-256;
3. extract package, app label, version name/code and SDK levels;
4. inventory Flutter assets, Android manifest components and native libraries;
5. compare archive entries and compiled binaries with the nearest previous build;
6. add the build chronologically to this registry;
7. mark exact duplicate uploads as aliases rather than separate builds;
8. create/update a dedicated recovery note when the build contains materially new recoverable behavior or architecture.

## Related recovery reference

See `apk-2026-02-02.md` for the richer feature/module inventory extracted from the 02.02.2026 build. The uploaded `02.02.2026(1).apk` has the same SHA-256 as that reference.
