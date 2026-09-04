# Habit Stakes 🎯

A high-stakes habit accountability app built with modern Android (Kotlin + Jetpack Compose + Room + Hilt). 

**Pledge real stakes (money, social, time) on your habits. Submit proof. Win or forfeit.**

---

## 🌟 Key Features

| Feature | Description |
|---------|-------------|
| **Multi-Type Stakes** | Money ($), Social, Time-Lock, or Combo stakes |
| **Proof Verification** | Submit photo, video (15s), timelapse, screenshot, or text notes |
| **Fight Mode (PVP)** | Join challenge pools and compete against others for the prize pool |
| **Auto / Manual Verify** | AI auto-verification with confidence scoring or community vote |
| **Biometric Security** | Protect your stakes with fingerprint / face unlock |
| **Offline-First** | Complete Room database backing — works without connectivity |
| **Auto APK Builds** | GitHub Actions workflow builds signed/debug APKs on push |

---

## 🏗️ Architecture & Tech Stack

- **UI Layer:** 100% Jetpack Compose + Material 3 Theme + Dynamic Colors
- **Architecture:** MVVM + Repository pattern
- **Database:** Room with TypeConverters and Flow reactivity
- **Dependency Injection:** Hilt
- **Async/Flows:** Kotlin Coroutines & StateFlow
- **Image Loading:** Coil
- **Serialization:** Kotlinx Serialization
- **CI/CD:** GitHub Actions (Linux Runner, JDK 17, Android SDK 34)

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Jellyfish (2023.3.1) or newer
- JDK 17
- Android SDK 34 (Android 14)

### Local Build

```bash
# Clone the repository
git clone https://github.com/YOUR_USERNAME/habit-stakes.git
cd habit-stakes

# Make gradlew executable
chmod +x gradlew

# Build debug APK
./gradlew assembleDebug

# Output APK location:
# app/build/outputs/apk/debug/app-debug.apk
```

### GitHub Actions (Automatic APK)

1. Push this repo to your GitHub account
2. Go to the **Actions** tab in your repository
3. Every `push` automatically triggers a build
4. Download the `app-debug.apk` or `app-release-unsigned.apk` from the **Artifacts** section!

---

## 📱 Screen Map

```
HomeScreen
 ├── TopBar (Profile & Settings)
 ├── Scorecard (Total Staked, Won, Forfeited)
 ├── Active Habits List (Cards with streak, success %, stake chip)
 └── FAB -> CreateHabitScreen
      ├── Title & Description
      ├── Stake Type Selector (Money, Social, Time, Combo)
      ├── Stake Amount Field
      ├── Frequency & Reminder Time Pickers
      ├── Proof Type Selector (Camera, Video, Text, GPS)
      └── Advanced Options (Grace Period, Auto-Verify)

HabitDetailScreen (Tap habit card)
 ├── Habit Header Stats
 ├── Stakes History
 └── Completion History (Verified vs Forfeit logs)
```

---

## 📄 License

MIT License — free to use, modify, publish, or distribute.
