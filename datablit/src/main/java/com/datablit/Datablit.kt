package com.datablit

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.time.Instant
import java.util.Timer
import java.util.TimerTask
import java.util.UUID
import kotlin.concurrent.thread

/**
 * Main Datablit analytics library class
 * 
 * Usage:
 * ```
 * val datablit = Datablit("your-api-key", context) {
 *     flushAt = 20
 *     flushInterval = 30000
 *     trackApplicationLifecycleEvents = true
 * }
 * 
 * datablit.identify("user123", mapOf("name" to "John Doe"))
 * datablit.track("Button Click", mapOf("buttonId" to "signup"))
 * ```
 */
class Datablit(
    val apiKey: String, 
    val context: Context, 
    val config: DatablitConfig = DatablitConfig()
) {
    private val gson: Gson = GsonBuilder().create()
    private val queue = mutableListOf<Event>()
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("DatablitPrefs", Context.MODE_PRIVATE)
    private var userId: String? = getUserId()
    private val anonymousId: String = getAnonymousId()
    private val queueFile = File(context.filesDir, "analytics_queue.json")
    private val timer = Timer()
    private val eventContext: Map<String, Any> = EventContext.generateContext(context)
    
    val rule: Rule = Rule(this)
    val experiment: Experiment = Experiment(this)

    init {
        if (apiKey.isEmpty()) throw IllegalArgumentException("API key cannot be empty")
        requireNotNull(context) { "Context must not be null." }
        
        restoreQueue()
        startFlushTimer()
        
        if (config.trackApplicationLifecycleEvents) {
            val lifecycle = ProcessLifecycleOwner.get().lifecycle
            lifecycle.addObserver(LifecycleObserver(this, context))
        }
    }

    /**
     * Identify a user with traits
     * @param userId The user identifier
     * @param traits User traits/properties
     */
    fun identify(userId: String, traits: Map<String, Any> = emptyMap()) {
        this.userId = userId
        saveUserId(userId)
        val event = getDefaultEvent().copy(type = EventType.identify, traits = traits)
        addInQueue(event)
    }

    /**
     * Track an event with properties
     * @param eventName The name of the event
     * @param properties Event properties
     */
    fun track(eventName: String, properties: Map<String, Any> = emptyMap()) {
        val event = getDefaultEvent().copy(
            type = EventType.track,
            event = eventName,
            properties = properties
        )
        addInQueue(event)
    }

    /**
     * Force flush all queued events to the server
     */
    fun flush() {
        if (queue.isEmpty()) return
        val batch = synchronized(queue) {
            val batchData = queue.toList()
            queue.clear()
            saveQueue()
            batchData
        }

        thread {
            try {
                val url = URL(config.endpoint)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.setRequestProperty("apiKey", apiKey)
                connection.doOutput = true
                val jsonBody =
                    gson.toJson(mapOf("batch" to batch, "sentAt" to Instant.now().toString()))
                connection.outputStream.use { it.write(jsonBody.toByteArray()) }

                if (connection.responseCode !in 200..299) {
                    if (connection.responseCode == 401 || connection.responseCode == 429 || connection.responseCode >= 500) {
                        throw Exception("Failed to send events: ${connection.responseCode}")
                    } else { // 4xx error. no need to retry
                        Log.e("Datablit", "Failed to send events: ${connection.responseCode}")
                    }
                }
                if (config.enableDebugLogs) Log.i("Datablit", "event send: $batch")
            } catch (e: Exception) {
                Log.e("Datablit", "Failed to send events: ${e.message}")
                synchronized(queue) {
                    queue.addAll(batch)
                    saveQueue()
                }
            }
        }
    }

    private fun getAnonymousId(): String {
        val storedId = sharedPreferences.getString("anonymous_id", null)
        return storedId ?: UUID.randomUUID().toString().also {
            sharedPreferences.edit { putString("anonymous_id", it) }
        }
    }

    private fun getUserId(): String? {
        return sharedPreferences.getString("user_id", null)
    }

    private fun saveUserId(userId: String) {
        sharedPreferences.edit { putString("user_id", userId) }
    }

    private fun getDefaultEvent(): Event {
        return Event(
            anonymousId = anonymousId,
            userId = userId,
            messageId = UUID.randomUUID().toString(),
            type = EventType.invalid,
            context = eventContext,
            originalTimestamp = Instant.now().toString(),
        )
    }

    private fun addInQueue(event: Event) {
        if (queue.size > 100) { // added max queue size check, otherwise backend failure will increase query size
            queue.clear()
            return
        }
        synchronized(queue) {
            queue.add(event)
            saveQueue()
            if (queue.size >= config.flushAt) {
                flush()
            }
        }
    }

    private fun saveQueue() {
        synchronized(queue) {
            queueFile.writeText(gson.toJson(queue))
        }
    }

    private fun restoreQueue() {
        if (queueFile.exists()) {
            val type = object : TypeToken<List<Event>>() {}.type
            queue.addAll(gson.fromJson(queueFile.readText(), type) ?: emptyList())
        }
    }

    private fun startFlushTimer() {
        timer.schedule(object : TimerTask() {
            override fun run() {
                flush()
            }
        }, config.flushInterval, config.flushInterval)
    }
}

/**
 * Convenience function to create a Datablit instance with configuration
 */
fun Datablit(
    apiKey: String, 
    context: Context, 
    block: DatablitConfig.() -> Unit
): Datablit {
    val config = DatablitConfig().apply(block)
    return Datablit(apiKey, context, config)
}
