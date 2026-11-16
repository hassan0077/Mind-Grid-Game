# 🧠 MindGrid — Memorize • Repeat • Conquer

[![Android](https://img.shields.io/badge/Platform-Android-3DDC84?style=flat-square&logo=android&logoColor=white)](https://developer.android.com/)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-7F52FF?style=flat-square&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?style=flat-square&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Material 3](https://img.shields.io/badge/Design-Material%203-757575?style=flat-square&logo=materialdesign&logoColor=white)](https://m3.material.io/)

**MindGrid** is a vibrant, modern brain-training memory game for Android. Test your focus and spatial memory by observing sequences of flashing neon tiles across growing grid sizes, then repeat them accurately to advance through levels and beat your high scores!

---

## ✨ Features

- 🎮 **3 Difficulty Modes**:
  - **Easy (2x2 Grid)**: 4 tiles, relaxed flashing pace (600ms delay) — Perfect for beginners.
  - **Normal (3x3 Grid)**: 9 tiles, standard pace (450ms delay) — Balanced challenge.
  - **Hard (4x4 Grid)**: 16 tiles, fast & intense pace (300ms delay) — Ultimate test of focus.
- 🎨 **Cyber-Neon Themes**: Easily toggle between **Cyber Dark Mode** and **Modern Light Mode**.
- 🔊 **Audio & Haptic Feedback**: Built-in tone generator with pitch variations per tile, success/error sound effects, and smooth haptic tactile vibrations.
- 🏆 **Local High Score System**: High scores are persisted independently for each difficulty mode using `SharedPreferences`.
- 🔒 **100% Offline & Private**: Zero data collection, no account creation, and no internet connection required.
- ⚡ **Smooth Animations**: Built using Compose `Crossfade` transitions, spring scale physics, and pulsing glow effects.

---

## 🛠️ Tech Stack & Architecture

- **Language**: 100% [Kotlin](https://kotlinlang.org/)
- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) & [Material 3](https://m3.material.io/)
- **Architecture**: Single Activity architecture using declarative state management (`State` & `remember`)
- **Audio & System Interop**: `ToneGenerator` for low-latency audio tones and `LocalHapticFeedback` for tactile feedback
- **Build System**: Gradle Kotlin DSL (`build.gradle.kts`) with Version Catalogs (`libs.versions.toml`)
- **Target SDK**: Android 14+ (API 34/37) | **Min SDK**: Android 7.0 (API 24)

---

## 📂 Project Structure

```text
com.hassanaj.mindgrid
 ├── MainActivity.kt        # App entry point, single-activity host & theme management
 ├── MainMenuScreen.kt      # Main menu UI (Difficulty selector, theme toggle, rules dialog)
 ├── MemoryGridGame.kt      # Game logic, state machine, dynamic grid rendering & animations
 ├── GameSettings.kt        # Difficulty configurations (Grid dimensions, delays, descriptions)
 ├── SplashScreen.kt        # Animated splash screen with spring scale & pulsing glow
 └── SoundManager.kt        # ToneGenerator wrapper for sound effects
```

---

## 🚀 Getting Started

### Prerequisites

- **Android Studio** (Ladybug / 2024.2+ recommended)
- **JDK 11** or higher
- Android Device or Emulator running **API 24 (Android 7.0)** or higher

### Build & Run

1. **Clone the repository**:
   ```bash
   git clone https://github.com/YOUR_USERNAME/MindGrid.git
   cd MindGrid
   ```

2. **Open in Android Studio** and sync Gradle.

3. **Run on Device / Emulator**:
   ```bash
   ./gradlew installDebug
   ```

---

## 📜 License

```text
Copyright (c) 2025 MindGrid Studio / Hassan
```
