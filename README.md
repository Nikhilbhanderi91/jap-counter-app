# 🕉️ Jap Counter App

A serene, spiritually-themed Android application built with modern **Jetpack Compose**, designed for mindful chanting, mantra counting, and spiritual habit tracking.

---

## ✨ Features

- **🌸 Serene Animated Splash Screen**:
  - Sacred glowing ॐ (Om) emblem with breathing pulse animations and soothing gradients.
- **📿 Mindful Jap Counter**:
  - Intuitive circular tap counter with real-time percentage progress ring.
  - Quick target counts (108, 1008) and custom count targets.
  - Dropdown & auto-suggestions for previously chanted mantra names.
- **📜 History & Task Tracking**:
  - Comprehensive history of completed and in-progress/pending chanting sessions.
  - Grouped by date (Today, Yesterday, etc.) with completion timestamps and progress indicators.
  - Resume in-progress chanting sessions anytime.
  - Delete individual records or clear history with confirmation dialogs.
- **🎉 Completion Celebration**:
  - Dedicated completion screen upon finishing target counts with one-tap restart or history view.
- **🎨 Spiritual Aesthetics**:
  - Warm Saffron, Sandalwood, and Cream palette tailored for tranquility and focus during meditation and prayers.

---

## 🛠️ Tech Stack & Architecture

- **Language**: Kotlin
- **UI Toolkit**: Jetpack Compose (Material 3)
- **Architecture**: MVVM (Model-View-ViewModel) + Repository Pattern
- **Local Persistence**:
  - **Room Database**: Chanting history logs and session tracking.
  - **DataStore (Preferences)**: Active session state, current counts, and user profile data.
- **Asynchronous**: Kotlin Coroutines & `Flow` / `StateFlow`
- **Navigation**: Jetpack Compose Navigation (`NavHost`)
- **Minimum SDK**: Android 7.0 (API Level 24)
- **Target SDK**: Android 15 (API Level 35)

---

## 📁 Project Structure

```text
app/src/main/java/com/example/jap_counter_app/
├── data/
│   ├── JapPreferences.kt          # Datastore preferences for active session
│   ├── JapSession.kt              # Session data model
│   ├── local/
│   │   ├── JapDatabase.kt         # Room database definition
│   │   ├── JapHistoryDao.kt       # Room DAO operations
│   │   └── JapHistoryEntity.kt    # History table entity
│   └── repository/
│       └── JapHistoryRepository.kt# Repository layer for data operations
├── ui/
│   ├── components/
│   │   └── NavComponents.kt       # Custom app bars, navigation headers
│   ├── history/
│   │   └── JapHistoryScreen.kt    # Jap history & past records screen
│   ├── navigation/
│   │   └── JapNavGraph.kt         # Compose navigation graph and route definitions
│   ├── screens/
│   │   ├── SplashScreen.kt        # Animated spiritual splash screen
│   │   ├── WelcomeScreen.kt       # User onboarding screen
│   │   ├── SetupScreen.kt         # Mantra & target configuration
│   │   ├── CounterScreen.kt       # Main interactive jap counter screen
│   │   └── CompletionScreen.kt    # Jap completion celebration screen
│   ├── theme/
│   │   ├── Color.kt               # Saffron & Sandalwood color palette
│   │   ├── Theme.kt               # Material 3 theme configuration
│   │   └── Type.kt                # Typography styles
│   └── viewmodel/
│       └── JapViewModel.kt        # Central ViewModel handling business logic
└── MainActivity.kt                # App entry point
```

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Ladybug (or newer)
- JDK 17
- Android SDK 35

### Installation & Run
1. Clone the repository:
   ```bash
   git clone https://github.com/Nikhilbhanderi91/jap-counter-app.git
   cd jap-counter-app
   ```
2. Open the project in Android Studio.
3. Sync Gradle dependencies.
4. Run on an Android Device or Emulator:
   ```bash
   ./gradlew installDebug
   ```

---

## 📄 License

This project is open-source and available under the [MIT License](LICENSE).
