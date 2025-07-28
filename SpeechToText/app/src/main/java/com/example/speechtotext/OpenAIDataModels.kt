package com.example.speechtotext

// --- Data classes for OpenAI API ---
data class OpenAIChatRequest(
    val model: String = "gpt-3.5-turbo", // Or other models like "gpt-4" if you have access
    val messages: List<ChatMessageOAI>,
    val temperature: Float = 0.7f
)

data class ChatMessageOAI(
    val role: String, // "system", "user", "assistant"
    val content: String
)

data class OpenAIChatResponse(
    val choices: List<ChoiceOAI>?,
    val error: OpenAIError?
)

data class ChoiceOAI(
    val message: ChatMessageOAI?
)

data class OpenAIError(
    val message: String?,
    val type: String?
)
