# Historical APK Build Registry

This file tracks historical Check2GO Android APKs recovered from archived builds.

The APK filename date is treated as the build/reference date unless stronger evidence is available. APK binaries themselves are not stored here; this registry records verified metadata and notable structural changes.

## Build timeline

| Build date | Source artifact | Size | SHA-256 | Package | Version | SDK | Persistence/native clues | Notable delta |
|---|---|---:|---|---|---|---|---|---|
| 2025-07-30 | `30.07.2025(1).apk` | 23.58 MiB | `3a031fee591bc342b1a6d12987332217756f87ceee107268fe770f0bd10c87b2` | `co.check2go.check2go` | `1.0.0` (`versionCode 1`) | min 21 / target 35 / compile 35 | no bundled `libsqlite3.so` or ObjectBox JNI observed | Earliest build currently in this historical set. Flutter assets include trip creation, travel dates/travelers, empty travelers state, home/events/documents/checklists icons. |
| 2025-08-06 | `06.08.2025(1).apk` | 23.82 MiB | `2b236a7fbfaec40a1aa6d66063d380ed854988d9016bd471c7023777d0cf2045` | `co.check2go.check2go` | `1.0.0` (`versionCode 1`) | min 21 / target 35 / compile 35 | no bundled `libsqlite3.so` or ObjectBox JNI observed | Adds `ic_edit.svg` and `checklist_empty.png`; application AOT binary changed. |
| 2025-08-24 | `24.08.2025(1).apk` | 57.35 MiB | `989e1216613dda7b97b9328249933d63f20c0279ccd68399f071a024c3603571` | `co.check2go.check2go` | `1.0.0` (`versionCode 1`) | min 24 / target 36 / compile 36 | bundled SQLite native library appears for arm64-v8a, armeabi-v7a and x86_64 | Major packaging/runtime transition. Minimum SDK rises 21→24; target/compile SDK rises 35→36; APK size more than doubles; `libsqlite3.so` is introduced. |
| 2025-08-31 | `31.08.2025(1).apk` | 62.98 MiB | `5e2b9bd3a6430295242ab29815e79ef4219a4c7e8f42db37d2bec6f468a70553` | `co.check2go.check2go` | `1.0.0` (`versionCode 1`) | min 24 / target 36 / compile 36 | SQLite retained; ObjectBox JNI introduced for all three bundled ABIs | Adds `libobjectbox-jni.so` and `ic_checkmark.svg`. This is strong evidence that ObjectBox was introduced into the app runtime at this point, alongside SQLite. |
| 2025-09-11 | `11.09.2025(1).apk` | 63.41 MiB | `c696b59afb382452b6282189be3eb4baa09a7192ab87e068395951d3724c4ff5` | `co.check2go.check2go` | `1.0.0` (`versionCode 1`) | min 24 / target 36 / compile 36 | SQLite + ObjectBox JNI retained | Archive entry set is unchanged from 2025-08-31, but `libapp.so`, ObjectBox JNI, `classes.dex` and other compiled artifacts changed, confirming a new build rather than a duplicate APK. |
| 2025-09-23 | `23.09.2025(1).apk` | 66.94 MiB | `ad20041c9aff9b1ec1b84303e9cee1398315f3aee6d76f0dfd974fca374c5d8e` | `co.check2go.check2go` | `1.0.0` (`versionCode 1`) | min 24 / target 36 / compile 36 | SQLite retained; ObjectBox JNI removed; Amplify Cognito/secure-storage assets and Google credentials/sign-in components appear | Major authentication/runtime transition. APK grows from 95 to 640 entries, gains `classes2.dex`, AWS Amplify Cognito activities/assets, Google sign-in / AndroidX Credentials support and a much larger Android resource set. |
| 2025-10-11 | `11.10.2025(1).apk` | 68.14 MiB | `3e54e99650529c4a7a828cfa3172c5e4120d63e56a17149fed4664369316788f` | `co.check2go.check2go` | `1.0.0` (`versionCode 1`) | min 24 / target 36 / compile 36 | SQLite + Amplify/Cognito stack retained | Adds `ic_document_copy.svg`, `ic_more_action_vertical.svg` and `ic_remove.svg`; application AOT binaries grow and change, consistent with new document/action UI behavior. |
| 2025-10-26 | `26.10.2025(1).apk` | 74.84 MiB | `3d760eb6d3c7755ef625a1b0683410ccd11afa63b452a33cefe0ad42797ec703` | `co.check2go.check2go` | `1.0.0` (`versionCode 1`) | min 24 / target 36 / compile 36 | SQLite retained; AndroidX DataStore native shared-counter libraries added | Adds `ic_save.svg`, Amplify Authenticator social-login assets, AndroidX DataStore components, Apache Tika resources and URL-launcher WebView activity. This is another major dependency/feature expansion. |
| 2025-10-27 | `27.10.2025(1).apk` | 74.87 MiB | `a19e046ba872d85cff459d8145ba398d2bb1fcbd642c65cea8aa97f540e7e928` | `co.check2go.check2go` | `1.0.0` (`versionCode 1`) | min 24 / target 36 / compile 36 | Same persistence/native library set as 2025-10-26 | No archive paths added or removed. `libapp.so`, `classes.dex` and baseline profile content changed, confirming an application-code revision one day later. |
| 2025-10-27 | `27.10.2025 (dark theme)(1).apk` | 74.87 MiB | `f54673a2d6c189a41c26ee797ae9888f0d8524e1802f30b0fd3a818dcad006e3` | `co.check2go.check2go` | `1.0.0` (`versionCode 1`) | min 24 / target 36 / compile 36 | Same native/dependency set as standard 2025-10-27 build | Dark-theme variant. Entry names, manifest, DEX/resources/assets and file sizes match the standard 2025-10-27 APK; only the three ABI-specific `libapp.so` payloads differ, strongly locating the theme change in compiled Dart/AOT application code. |

## Shared Android identity in this 2025 set

Verified across all ten APK artifacts currently registered:

- App label: `check2go`
- Package: `co.check2go.check2go`
- Main activity: `co.check2go.check2go.MainActivity`
- Flutter embedding: present
- Version name: `1.0.0`
- Version code: `1`
- ABIs bundled: arm64-v8a, armeabi-v7a, x86_64

From 2025-08-24 onward the Android SDK baseline is min SDK 24 / target SDK 36 / compile SDK 36. Because `versionName` and `versionCode` were not incremented across these archived builds, the filename date, variant label and file hash are important identifiers for recovery work.

## Asset and dependency evolution observed

### 2025-07-30 baseline

Observed Flutter assets include:

- `background_create_trip.jpg`
- `travel_dates_step.jpg`
- `travel_travelers_step.jpg`
- `travelers_list_empty.png`
- `mock_background_abudhabi.jpg`
- `mock_background_telaviv.jpg`
- navigation/action icons for home, events, documents, checklists, calendar, settings, notifications and related flows

### 2025-08-06

Added:

- `assets/icons/ic_edit.svg`
- `assets/images/checklist_empty.png`

### 2025-08-24

No new product-facing Flutter asset was identified relative to 2025-08-06, but native/runtime packaging changed substantially and SQLite became bundled.

### 2025-08-31

Added:

- `assets/icons/ic_checkmark.svg`
- ObjectBox JNI native libraries

### 2025-09-11

No asset-path additions or removals were observed relative to 2025-08-31. Compiled application/runtime content changed.

### 2025-09-23

No new product-facing app asset path was added, but the Android/runtime layer changed sharply:

- ObjectBox JNI disappears from all three ABIs;
- `classes2.dex` appears;
- AWS Amplify Cognito and secure-storage worker assets are packaged;
- manifest entries for Amplify Cognito custom tabs/dev menu appear;
- Google Sign-In and AndroidX Credentials components appear;
- Android resource and dependency metadata expands substantially.

This is the strongest evidence in the current archive for the introduction of an Amplify/Cognito-based authentication stack.

### 2025-10-11

Added:

- `assets/icons/ic_document_copy.svg`
- `assets/icons/ic_more_action_vertical.svg`
- `assets/icons/ic_remove.svg`

No archive paths were removed.

### 2025-10-26

Added or newly visible:

- `assets/icons/ic_save.svg`
- Amplify Authenticator social-button font and Google icon assets;
- AndroidX DataStore libraries and `libdatastore_shared_counter.so` for all three ABIs;
- Apache Tika MIME/detection/parser resources;
- URL launcher WebView activity in the manifest.

Apache Tika presence demonstrates a bundled file/metadata parsing dependency, but APK inspection alone does not prove exactly which user flow invokes it.

### 2025-10-27

The standard build has the same archive-entry layout as 2025-10-26. Application AOT code, `classes.dex` and dex baseline-profile content changed.

The dark-theme variant has exactly the same archive-entry paths as the standard 2025-10-27 build. Manifest, DEX, Android resources and Flutter assets are byte-identical; only `libapp.so` differs for arm64-v8a, armeabi-v7a and x86_64. This makes it a particularly useful reference for isolating the historical theme implementation.

## Recovery interpretation

The most important transitions in this set are:

1. **2025-07-30 → 2025-08-06** — incremental UI/checklist work.
2. **2025-08-06 → 2025-08-24** — major Android/runtime transition: SDK baseline changed and native SQLite was introduced.
3. **2025-08-24 → 2025-08-31** — ObjectBox JNI appears alongside SQLite, plus a new checkmark asset.
4. **2025-08-31 → 2025-09-11** — same packaged file layout, but rebuilt application and native persistence binaries.
5. **2025-09-11 → 2025-09-23** — major auth/dependency transition: ObjectBox disappears; Amplify Cognito, secure storage, Google sign-in/credentials support and multidex appear.
6. **2025-09-23 → 2025-10-11** — document/action UI assets are added without another major packaging shift.
7. **2025-10-11 → 2025-10-26** — another substantial expansion: save action, Amplify Authenticator social assets, DataStore, Apache Tika and URL-launcher WebView support.
8. **2025-10-26 → 2025-10-27** — application-code revision with stable packaged layout.
9. **2025-10-27 standard → dark-theme variant** — only Dart AOT `libapp.so` payloads differ; all other packaged content matches.

These observations describe artifacts actually present in the APKs. They should not be interpreted as complete source-level change logs: Dart application code is AOT-compiled and exact source cannot be losslessly reconstructed from the APK alone.

## Update procedure for future historical APKs

For every newly supplied historical APK:

1. record filename/date and byte size;
2. calculate SHA-256;
3. extract package, app label, version name/code and SDK levels;
4. inventory Flutter assets and native libraries;
5. compare archive entries and compiled binaries with the nearest previous build;
6. add the build chronologically to this registry;
7. create a dedicated APK recovery note when the build contains materially new recoverable behavior or architecture.

## Related recovery reference

See also `apk-2026-02-02.md` for the later recovered APK reference and the richer feature/module inventory extracted from that build.
