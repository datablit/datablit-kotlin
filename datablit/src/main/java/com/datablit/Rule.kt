package com.datablit

import com.google.gson.Gson
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

/**
 * Request data class for rule evaluation
 */
data class EvalRuleRequest(
    val key: String,
    val userId: String,
    val params: Map<String, Any>? = null
)

/**
 * Response data class for rule evaluation
 */
data class EvalRuleResponse(
    val key: String,
    val userId: String,
    val result: Boolean
)

/**
 * Rule evaluation functionality for Datablit
 * Provides methods to evaluate rules for users based on context parameters
 */
class Rule(private val datablit: Datablit) {
    private val gson: Gson = Gson()

    /**
     * Evaluate a rule for a given user and context
     * Returns the evaluation result with key, userId, and result
     * 
     * @param key The rule key to evaluate
     * @param userId The user identifier
     * @param params Optional parameters for rule evaluation
     * @param callback Callback to handle the result or error
     */
    fun evalRule(key: String, userId: String, params: Map<String, Any>? = null, callback: (Result<EvalRuleResponse>) -> Unit) {
        if (datablit.apiKey.isEmpty()) {
            callback(Result.failure(IllegalStateException("API key is not set. Please initialize the SDK first.")))
            return
        }

        thread {
            try {
                val request = EvalRuleRequest(key, userId, params)
                val url = URL("${datablit.config.apiBaseURL}/api/1.0/rule/eval")
                val connection = url.openConnection() as HttpURLConnection
                
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.setRequestProperty("apiKey", datablit.apiKey)
                connection.doOutput = true

                val jsonBody = gson.toJson(request)
                connection.outputStream.use { it.write(jsonBody.toByteArray()) }

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

                val response = gson.fromJson(responseText, EvalRuleResponse::class.java)
                callback(Result.success(response))
            } catch (e: Exception) {
                callback(Result.failure(e))
            }
        }
    }
}
