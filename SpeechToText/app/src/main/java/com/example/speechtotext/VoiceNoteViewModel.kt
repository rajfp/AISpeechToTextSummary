package com.example.speechtotext

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class VoiceNoteUiEvent {
    object RequestRecordAudioPermission : VoiceNoteUiEvent()
    object LaunchSpeechToText : VoiceNoteUiEvent()
}

class VoiceNoteViewModel : ViewModel() {

    private val repository = OpenAIRepository()

    private val _transcribedText = MutableStateFlow("")
    val transcribedText: StateFlow<String> = _transcribedText.asStateFlow()

    private val _summaryText = MutableStateFlow("")
    val summaryText: StateFlow<String> = _summaryText.asStateFlow()

    private val _isSummarizing = MutableStateFlow(false)
    val isSummarizing: StateFlow<Boolean> = _isSummarizing.asStateFlow()

    private val _userMessage = MutableStateFlow("") // For errors or info messages
    val userMessage: StateFlow<String> = _userMessage.asStateFlow()

    private val _uiEvent = MutableSharedFlow<VoiceNoteUiEvent>()
    val uiEvent: SharedFlow<VoiceNoteUiEvent> = _uiEvent.asSharedFlow()

    fun updateTranscribedText(text: String) {
        _transcribedText.value = transcribedText.value + text
        _summaryText.value = "" // Clear previous summary
    }

    fun clearAllText() {
        _transcribedText.value = ""
        _summaryText.value = ""
        _userMessage.value = ""
    }

    fun requestRecordAudioPermission() {
        viewModelScope.launch {
            _uiEvent.emit(VoiceNoteUiEvent.RequestRecordAudioPermission)
        }
    }

    fun onAudioPermissionResult(isGranted: Boolean) {
        viewModelScope.launch {
            if (isGranted) {
                _uiEvent.emit(VoiceNoteUiEvent.LaunchSpeechToText)
            } else {
                _userMessage.value = "Audio permission denied. Cannot record."
            }
        }
    }

    fun summarizeText() {
        if (_transcribedText.value.isBlank()) {
            _userMessage.value = "Nothing to summarize."
            return
        }
        if (BuildConfig.OPENAI_API_KEY.startsWith("DEFAULT_") || BuildConfig.OPENAI_API_KEY.isBlank()) {
            _userMessage.value = "Error: OpenAI API Key not configured in local.properties."
            return
        }

        viewModelScope.launch {
            _isSummarizing.value = true
            _summaryText.value = "" // Clear previous summary
            val (summary, error) = repository.getSummary(
                BuildConfig.OPENAI_API_KEY,
                _transcribedText.value
            )
            if (summary != null) {
                _summaryText.value = summary
            } else {
                _summaryText.value = ""
                _userMessage.value = error ?: "An unknown error occurred during summarization."
            }
            _isSummarizing.value = false
        }
    }

    fun clearUserMessage() {
        _userMessage.value = ""
    }
}
