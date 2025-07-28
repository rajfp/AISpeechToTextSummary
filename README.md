# AISpeechToTextSummary
# SpeechToText Summary Android App

## Overview

SpeechToText Summary is an Android application that allows users to record their voice, convert the speech to text, and then generate a concise summary of the transcribed text using the OpenAI API. This app demonstrates a practical use case of integrating speech recognition and advanced AI-powered text summarization in a mobile environment. It's built with modern Android development practices using Kotlin and Jetpack Compose.

## Features

*   **Voice Recording:** Captures audio input from the user.
*   **Speech-to-Text Transcription:** Converts spoken audio into text using Android's built-in speech recognition capabilities.
*   **Text Summarization:** Utilizes the OpenAI GPT model (via API) to generate a summary of the transcribed text.
*   **Clear Interface:** Simple UI to record, view transcribed text, and see the generated summary.
*   **Error Handling:** Provides user feedback for common issues (e.g., missing API key, permission denial).
*   **Clean Architecture:** Organized into View, ViewModel, Repository, and Service layers for better maintainability and testability.

## Architecture

The application follows an MVVM (Model-View-ViewModel)-like architecture:

*   **View (`MainActivity.kt` containing `VoiceNoteScreen.kt`):** Jetpack Compose UI that observes state from the ViewModel and forwards user actions.
*   **ViewModel (`VoiceNoteViewModel.kt`):** Holds UI state, handles user interactions, and communicates with the `OpenAIRepository`. It uses Kotlin Flows and `viewModelScope` for managing asynchronous operations.
*   **Repository (`OpenAIRepository.kt`):** Acts as a single source of truth for data. It encapsulates the logic for fetching summaries from the OpenAI API.
*   **Service (`OpenAIService.kt` & `RetrofitClient.kt`):** Defines the Retrofit interface for OpenAI API calls and manages the Retrofit client setup.
*   **Data Models (`OpenAIDataModels.kt`):** Kotlin data classes representing the request and response structures for the OpenAI API.
