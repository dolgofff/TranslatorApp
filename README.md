# TranslatorApp

An Android translation application built with Kotlin and Jetpack Compose.

The project explores the implementation of a production-oriented Android application architecture while combining text translation, camera-based text recognition, authentication, persistent user preferences, and asynchronous data processing.

> **Project status:** Work in progress. Core application flows and architecture are implemented, while some features and UI details are still being refined.

## Features

* Text translation between supported languages
* Source and target language selection with language swapping
* Camera-based text recognition
* Translation of text recognized from images
* Google authentication
* Persistent application preferences
* Audio playback for translated content
* Material 3 UI built entirely with Jetpack Compose
* Navigation between application flows using Navigation 3

## Camera Translation

One of the main technical parts of the project is the image translation flow.

The application uses **CameraX** for camera integration and **ML Kit Text Recognition** for OCR. Recognized text is passed through the application's translation pipeline and exposed to the UI as state.

The implementation handles asynchronous recognition and translation separately, including cancellation of outdated work when the input image or selected languages change.

This flow is designed to keep camera-specific, recognition, translation, and UI responsibilities separated rather than implementing the entire pipeline directly inside the UI layer.

## Architecture

The application follows a layered, multi-module architecture:

```text
TranslatorApp
├── app
│   ├── UI
│   ├── ViewModels
│   ├── Navigation
│   └── Dependency Injection
│
├── domain
│   ├── Models
│   ├── Repository contracts
│   └── Use cases
│
└── data
    ├── Repository implementations
    ├── Remote data sources
    ├── Local persistence
    ├── Camera / ML Kit integration
    └── External service integrations
```

### `app`

Contains the presentation layer of the application:

* Jetpack Compose UI
* screen state and ViewModels
* navigation
* lifecycle-aware interaction with application features
* dependency injection setup

### `domain`

Contains application-level abstractions and business operations.

The module defines repository interfaces and use cases independently from concrete Android or network implementations.

Examples include:

* translation operations
* text recognition
* authentication
* application preferences
* audio-related operations

### `data`

Contains concrete implementations of the interfaces defined in the domain layer.

This includes integrations with:

* translation services
* CameraX
* ML Kit
* Firebase
* DataStore
* network APIs
* media playback

The dependency direction keeps the domain layer independent from implementation details:

```text
app ──────► domain
 │
 └────────► data ──────► domain
```

## State and asynchronous processing

The project relies on **Kotlin Coroutines** and **Flow-based state management** for asynchronous operations.

ViewModels coordinate UI state while longer-running work such as text recognition and translation is executed asynchronously.

Where appropriate, running jobs are explicitly cancelled when newer user input makes their result obsolete. This is particularly important for image recognition and translation flows, where language changes or a newly selected image can invalidate an operation that is still running.

## Tech Stack

### Language & UI

* **Kotlin**
* **Jetpack Compose**
* **Material 3**

### Architecture & Android

* **Multi-module architecture**
* **ViewModel**
* **Kotlin Coroutines**
* **Flow / StateFlow**
* **Hilt**
* **KSP**
* **Navigation 3**

### Camera & Machine Learning

* **CameraX**
* **ML Kit Text Recognition**
* **ML Kit Language Identification**
* **CameraX ML Kit Vision integration**

### Networking

* **Retrofit**
* **OkHttp**
* **Kotlin Serialization**

### Authentication & Backend

* **Firebase Authentication**
* **Cloud Firestore**
* **Android Credential Manager**
* **Google Sign-In**

### Persistence & Media

* **DataStore**
* **Media3 / ExoPlayer**

### Image loading

* **Coil**

## Screenshots

> Screenshots and demonstration media will be added as the UI is finalized.

<!--
Example layout:

<p align="center">
  <img src="docs/screenshots/translation.png" width="30%" />
  <img src="docs/screenshots/camera.png" width="30%" />
  <img src="docs/screenshots/account.png" width="30%" />
</p>
-->

## Project Structure

The project deliberately separates business abstractions from framework-specific implementations.

For example, the domain module exposes repository contracts such as:

```text
AuthRepository
GlobalRepository
RecognitionRepository
TranslationRepository
```

Concrete implementations remain in the data layer, while the presentation layer communicates with application functionality primarily through use cases.

This makes individual parts of the application easier to evolve without tightly coupling the UI to networking, Firebase, camera APIs, or ML Kit.

## Development Goals

This project is primarily being developed to practice and demonstrate modern Android engineering approaches in a non-trivial application.

The focus is on:

* separation of concerns
* explicit state management
* lifecycle-aware asynchronous operations
* dependency inversion between modules
* integrating Android platform APIs behind abstractions
* handling real camera and ML processing pipelines
* keeping UI logic separate from data and infrastructure code

## Roadmap

The application is still under active development.

Planned work includes:

* further UI/UX polish
* completing remaining translation flows
* improving error handling
* expanding automated test coverage
* refining camera translation behavior
* general architecture and code-quality improvements

## Getting Started

Clone the repository:

```bash
git clone https://github.com/dolgofff/TranslatorApp.git
```

Open the project in Android Studio and let Gradle synchronize the dependencies.

### Requirements

* Android Studio with support for the current Android Gradle Plugin
* JDK 11+
* Android SDK
* Device or emulator running Android 8.0 (API 26) or newer

Some functionality may require additional service configuration, including Firebase and external API credentials.

## License

This project is currently developed as a personal portfolio / pet project.
