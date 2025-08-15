package com.datablit

import com.google.gson.Gson
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

/**
 * Response data class for experiment variant
 */
data class GetVariantResponse(
    val expId: String,
    val entityId: String,
    val variant: String
)

/**
 * Experiment functionality for Datablit
 * Provides methods to get experiment variants for users
 */
class Experiment(private val datablit: Datablit) {
    private val gson: Gson = Gson()

    /**
     * Get experiment variant for a user
     * 
     * @param expId The experiment identifier
     * @param entityId The user/entity identifier
     * @param callback Callback to handle the result or error
     */
    fun getVariant(expId: String, entityId: String, callback: (Result<GetVariantResponse>) -> Unit) {
        if (datablit.apiKey.isEmpty()) {
            callback(Result.failure(IllegalStateException("API key is not set. Please initialize the SDK first.")))
            return
        }

        thread {
            try {
                val queryParams = "expId=$expId&entityId=$entityId"
                val url = URL("${datablit.config.apiBaseURL}/api/1.0/experiment/variant?$queryParams")
                val connection = url.openConnection() as HttpURLConnection
                
                connection.requestMethod = "GET"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.setRequestProperty("apiKey", datablit.apiKey)

                if (connection.responseCode != 200) {
                    val errorData = try {
                        connection.errorStream?.use { stream ->
                            gson.fromJson(stream.bufferedReader().readText(), Map::class.java)
                        } ?: emptyMap<String, Any>()
                    } catch (e: Exception) {
                        emptyMap<String, Any>()
                    }
                    
                    val errorMessage = errorData["message"] as? String ?: "Unknown error"
                    throw Exception("API request failed: ${connection.responseCode} ${connection.responseMessage} - $errorMessage")
                }

                val responseText = connection.inputStream.use { stream ->
                    stream.bufferedReader().readText()
                }

                val response = gson.fromJson(responseText, GetVariantResponse::class.java)
                callback(Result.success(response))
            } catch (e: Exception) {
                callback(Result.failure(e))
            }
        }
    }
}
