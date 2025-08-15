package com.datablit

/**
 * Configuration class for Datablit analytics library
 */
data class DatablitConfig(
    var endpoint: String = "https://event.datablit.com/v1/batch",
    var flushAt: Int = 20,
    var flushInterval: Long = 30000,
    var trackApplicationLifecycleEvents: Boolean = false,
    var trackDeepLinks: Boolean = false,
    var enableDebugLogs: Boolean = false,
    var apiBaseURL: String = "https://console.datablit.com"
) {
    init {
        require(flushAt > 0) { "flushAt must be greater than 0" }
        require(flushInterval > 0) { "flushInterval must be greater than 0" }
    }
}
