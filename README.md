# Datablit Analytics Library

A lightweight analytics library for Android applications to track user events and behavior.

## Features

- **Event Tracking**: Track custom events with properties
- **User Identification**: Identify users with traits
- **Automatic Batching**: Events are automatically batched and sent to the server
- **Offline Support**: Events are queued locally and sent when network is available
- **Lifecycle Tracking**: Optional automatic tracking of application lifecycle events
- **Rule Evaluation**: Evaluate feature flags and rules for users with context parameters
- **Experiment Variants**: Get experiment variants for users
- **Configurable**: Customize flush intervals, batch sizes, and endpoints

## Installation

Add the dependency to your `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.datablit:kotlin:1.0.0")
}
```

## Usage

### Basic Setup

```kotlin
val datablit = Datablit("your-api-key", context)
```

### Track Events

```kotlin
// Track a simple event
datablit.track("Button Click")

// Track an event with properties
datablit.track("Purchase", mapOf(
    "productId" to "123",
    "price" to 29.99,
    "currency" to "USD"
))
```

### Identify Users

```kotlin
// Identify a user with traits
datablit.identify("user123", mapOf(
    "name" to "John Doe",
    "email" to "john@example.com",
    "plan" to "premium"
))
```

### Force Flush

```kotlin
// Manually flush queued events
datablit.flush()
```

### Rule Evaluation

```kotlin
// Evaluate a rule for a user with context parameters
datablit.rule.evalRule(
    key = "feature_flag",
    userId = "user123",
    params = mapOf(
        "os_name" to "android",
        "app_version" to "1.0.0"
    )
) { result ->
    result.onSuccess { ruleResult ->
        if (ruleResult.result) {
            println("Rule evaluated to true")
        } else {
            println("Rule evaluated to false")
        }
    }.onFailure { error ->
        println("Rule evaluation failed: ${error.message}")
    }
}
```

### Experiment Variants

```kotlin
// Get experiment variant for a user
datablit.experiment.getVariant(
    expId = "01K2JKVXR0J0ZWPX40XY8CAWBS",
    entityId = "user123"
) { result ->
    result.onSuccess { response ->
        println("User is in variant: ${response.variant}") // "control", "variant_a", etc.
    }.onFailure { error ->
        println("Failed to get variant: ${error.message}")
    }
}
```

## Configuration

| Property                          | Default                                 | Description                                  |
| --------------------------------- | --------------------------------------- | -------------------------------------------- |
| `endpoint`                        | `"https://event.datablit.com/v1/batch"` | API endpoint for sending events              |
| `flushAt`                         | `20`                                    | Number of events to batch before sending     |
| `flushInterval`                   | `30000`                                 | Interval in milliseconds to flush events     |
| `trackApplicationLifecycleEvents` | `false`                                 | Automatically track app lifecycle events     |
| `trackDeepLinks`                  | `false`                                 | Track deep link events (not implemented yet) |
| `enableDebugLogs`                 | `false`                                 | Enable debug logging                         |
| `apiBaseURL`                      | `"https://console.datablit.com"`        | Internal Use - Base URL for API operations   |

## License

MIT
