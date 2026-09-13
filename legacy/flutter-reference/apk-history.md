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

## Shared Android identity in this 2025 set

Verified across all five APKs:

- App label: `check2go`
- Package: `co.check2go.check2go`
- Main activity: `co.check2go.check2go.MainActivity`
- Flutter embedding: present
- Version name: `1.0.0`
- Version code: `1`
- ABIs bundled: arm64-v8a, armeabi-v7a, x86_64

Because `versionName` and `versionCode` were not incremented across these archived builds, the filename date and file hash are important identifiers for recovery work.

## Asset evolution observed

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

## Recovery interpretation

The most important transitions in this set are:

1. **2025-07-30 → 2025-08-06** — incremental UI/checklist work.
2. **2025-08-06 → 2025-08-24** — major Android/runtime transition: SDK baseline changed and native SQLite was introduced.
3. **2025-08-24 → 2025-08-31** — ObjectBox JNI appears alongside SQLite, plus a new checkmark asset.
4. **2025-08-31 → 2025-09-11** — same packaged file layout, but rebuilt application and native persistence binaries.

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
