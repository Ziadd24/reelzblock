# Cloud APK build

This project is configured for GitHub Actions. No Android Studio is required.

## Build

1. Create a GitHub repository and upload this project.
2. Push the files to the `main` branch.
3. Open the repository's **Actions** tab.
4. Select **Build ReelBlock APK**.
5. Open the completed workflow run.
6. Under **Artifacts**, download `ReelBlock-debug-apk`.
7. Extract it to get `app-debug.apk`.
8. Copy the APK to the Huawei phone and install it.

The workflow provisions JDK 17, Gradle 8.9, Android SDK platform 35, and Build Tools 35.0.0 on the cloud runner.
