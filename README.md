# ReelBlock — Android MVP

A tiny Android prototype that blocks Instagram Reels using an AccessibilityService.

## What it does

- Watches only Instagram (`com.instagram.android`).
- Detects a click labelled "Reels".
- Also looks for a conservative combination of "Reels" + common video actions.
- Sends a system Back action when a Reels surface is detected.
- No Google Play Services, HMS Core, account, server, or network connection required.

## Build

1. Open this folder in Android Studio.
2. Let Gradle sync.
3. Connect the Huawei phone with USB debugging enabled.
4. Run the app.
5. Open Accessibility settings from ReelBlock.
6. Enable ReelBlock.
7. Open Instagram and tap Reels.

## Important

Instagram changes its UI/accessibility labels over time. If the blocker misses Reels
or blocks normal Instagram screens, the detection rules need to be tuned against the
exact Instagram version on the phone.

The next MVP iteration should add:
- an in-app ON/OFF switch,
- a block counter,
- a "strict mode",
- better detection based on the actual node tree observed on the device,
- Huawei battery/background-operation guidance,
- optional blocking of other short-video feeds.

## Cloud build (recommended for low disk space)

The repository includes `.github/workflows/build-apk.yml` for GitHub Actions. It builds `app-debug.apk` in the cloud, so Android Studio and the Android SDK do not need to be installed locally.

See `CLOUD_BUILD.md` for the exact steps.
"# reelzblock" 
