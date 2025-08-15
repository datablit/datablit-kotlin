package com.datablit

/**
 * Represents the type of analytics event
 */
enum class EventType {
    invalid, identify, track
}

/**
 * Represents an analytics event with all necessary metadata
 */
data class Event(
    val anonymousId: String,
    val userId: String?,
    val messageId: String,
    val type: EventType,
    val context: Map<String, Any> = emptyMap(),
    val originalTimestamp: String,
    val event: String? = null,
    val properties: Map<String, Any>? = null,
    val traits: Map<String, Any>? = null
)
