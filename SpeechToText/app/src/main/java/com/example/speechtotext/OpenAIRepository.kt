package com.example.speechtotext

import android.util.Log
import com.google.gson.Gson

class OpenAIRepository {

    private val openAIService: OpenAIService = RetrofitClient.openAIService

    /**
     * Fetches a summary from the OpenAI API.
     * @return Pair<Summary String?, Error Message String?>
     */
    suspend fun getSummary(apiKey: String, textToSummarize: String): Pair<String?, String?> {
        if (apiKey.startsWith("DEFAULT_") || apiKey.isBlank()) {
            return Pair(null, "Error: OpenAI API Key not configured in local.properties.")
        }
        if (textToSummarize.isBlank()) {
            return Pair(null, "Nothing to summarize.")
        }

        return try {
            val request = OpenAIChatRequest(
                messages = listOf(
                    ChatMessageOAI("system", "You are a helpful assistant that summarizes text concisely."),
                    ChatMessageOAI("user", "Summarize the following text: $textToSummarize")
                )
            )
            val response = openAIService.getChatCompletions("Bearer $apiKey", request)

            if (response.isSuccessful) {
                val content = response.body()?.choices?.firstOrNull()?.message?.content
                Pair(content?.trim() ?: "No summary received.", null)
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("OpenAIRepository", "API Error: ${response.code()} $errorBody")
                try {
                    val errorResponse = Gson().fromJson(errorBody, OpenAIChatResponse::class.java)
                    Pair(null, "API Error: ${errorResponse?.error?.message ?: response.message()}")
                } catch (e: Exception) {
                    Pair(null, "API Error: ${response.code()} - ${response.message()}. Unable to parse error response.")
                }
            }
        } catch (e: Exception) {
            Log.e("OpenAIRepository", "Exception: ${e.message}", e)
            Pair(null, "Exception during summarization: ${e.message}")
        }
    }
}
