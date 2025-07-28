package com.example.speechtotext

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

// --- Retrofit Service Interface for OpenAI ---
interface OpenAIService {
    @POST("v1/chat/completions")
    suspend fun getChatCompletions(
        @Header("Authorization") apiKey: String,
        @Body request: OpenAIChatRequest
    ): Response<OpenAIChatResponse>
}
