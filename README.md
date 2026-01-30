# Word of the Day App

A feature-rich Android application that helps users expand their vocabulary by providing a new word every day, complete with definitions and real-world news context.

---

## Features

### 📖 Word Discovery
- **Random Word Generation**: Fetches interesting words using a random word API.
- **Detailed Definitions**: Provides comprehensive definitions from multiple sources including Dictionary API and Merriam-Webster.
- **News Context**: Integrates with News API to show how the word is used in contemporary journalism, helping users understand practical application.

### ⚙️ User Experience & Personalization
- **Multi-language Support**: Fully localized in **English**, **Mandarin**, and **Korean**.
- **Theming**: Supports both Light and Dark modes with seamless transitions.
- **Accessibility**: Adjustable font sizes to suit user preferences.
- **Selection Strategies**: Choose how new words are selected (e.g., Strict mode to avoid repeats).
- **Daily Limits**: Intelligent refresh management with daily quotas.

### 🎵 Multimedia
- **Background Music**: Ambient background music with multiple themes.
- **Sound Effects**: Interactive sound feedback for navigation and UI actions.

---

## Tech Stack

| Component | Technology |
|-----------|------------|
| **UI Framework** | Jetpack Compose (Material Design 3) |
| **Dependency Injection** | Hilt (Dagger) |
| **Networking** | Retrofit 2 & OkHttp |
| **Image Loading** | Coil |
| **State Management** | ViewModel & Kotlin Coroutines (Flow) |
| **Persistence** | Jetpack DataStore |
| **Architecture** | MVVM (Model-View-ViewModel) |

---

## Getting Started

### Prerequisites
- Android Studio Ladybug or newer
- API 26+ (Android 8.0) recommended
- API Keys for:
  - NewsAPI.org
  - Merriam-Webster Dictionary API

### Installation
1. Clone the repository.
2. Open the project in Android Studio.
3. (Optional) Add your API keys to the configuration.
4. Build and run on an emulator or physical device.

---

## 📚 License
This project is developed for educational purposes as part of the CP3406/CP5307 Assessment.
