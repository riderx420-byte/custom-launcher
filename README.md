# Custom Android Launcher

A simple, customizable Android home launcher.

## Features

- 📱 App drawer with all installed apps
- 🎨 Customizable colors and theme
- 🔒 4-slot quick access dock
- 🔍 Search bar (UI ready)
- 📐 4-column grid layout

## Customization

### Change Colors

Edit `app/src/main/res/values/colors.xml`:

```xml
<color name="background">#1a1a2e</color>      <!-- Main background -->
<color name="dock_background">#16213e</color> <!-- Dock bar -->
<color name="accent">#e94560</color>          <!-- Accent color -->
```

### Change Dock Apps

Edit `MainActivity.java`, find `setupDock()` method:

```java
String[] dockPackages = {
    "com.android.phone",      // Phone
    "com.android.messaging",  // Messages
    "com.android.browser",    // Browser
    "com.android.settings"    // Settings
};
```

Replace with your preferred app package names.

### Change Grid Columns

Edit `MainActivity.java`, find `onCreate()`:

```java
GridLayoutManager layoutManager = new GridLayoutManager(this, 4); // Change 4 to desired columns
```

## Build APK

### Option 1: GitHub Actions (Recommended)

1. Push this code to a GitHub repository
2. Go to Actions tab
3. Run "Build APK" workflow
4. Download APK from artifacts

### Option 2: Android Studio

1. Open project in Android Studio
2. Build → Build Bundle(s) / APK(s) → Build APK(s)
3. Find APK in `app/build/outputs/apk/debug/`

### Option 3: Command Line

```bash
./gradlew assembleDebug
# APK will be at: app/build/outputs/apk/debug/app-debug.apk
```

## Install

1. Download the APK
2. Enable "Install from Unknown Sources" on your device
3. Install the APK
4. Press home button and select "Custom Launcher"
5. Set as default launcher

## Project Structure

```
custom-launcher/
├── app/
│   ├── src/main/
│   │   ├── java/com/customlauncher/
│   │   │   ├── MainActivity.java
│   │   │   ├── AppAdapter.java
│   │   │   └── AppInfo.java
│   │   ├── res/
│   │   │   ├── layout/
│   │   │   ├── values/
│   │   │   └── drawable/
│   │   └── AndroidManifest.xml
│   └── build.gradle
├── .github/workflows/
│   └── build.yml
└── build.gradle
```

## Requirements

- Android 7.0 (API 24) or higher
- Minimum 100MB free space

## License

MIT License - Feel free to modify and distribute!
