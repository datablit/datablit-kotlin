package com.datablit

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresPermission
import java.util.Locale
import java.util.TimeZone

/**
 * Handles generation of event context information
 */
internal object EventContext {
    
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    @SuppressLint("HardwareIds")
    fun generateContext(context: Context): Map<String, Any> {
        val packageManager = context.packageManager
        val packageName = context.packageName
        val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
        val appName = packageManager.getApplicationLabel(applicationInfo).toString()
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        val version = packageInfo.versionName
        val namespace = packageName
        val build = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            packageInfo.longVersionCode.toString()
        } else {
            @Suppress("DEPRECATION")
            packageInfo.versionCode.toString()
        }

        val deviceId = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        )
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        val name = Build.DEVICE
        val type = "android"

        val osName = "Android"
        val osVersion = Build.VERSION.RELEASE

        val displayMetrics = context.resources.displayMetrics
        val screenDensity = displayMetrics.density
        val screenHeight = displayMetrics.heightPixels
        val screenWidth = displayMetrics.widthPixels

        val locale = Locale.getDefault()
        val userAgent = System.getProperty("http.agent") ?: "Unknown User-Agent"
        val timeZone = TimeZone.getDefault().id

        return mapOf(
            "library" to mapOf(
                "name" to "com.datablit-kotlin",
                "version" to "1.0.0"
            ),
            "app" to mapOf(
                "name" to appName,
                "version" to version,
                "namespace" to namespace,
                "build" to build
            ),
            "device" to mapOf(
                "id" to deviceId,
                "manufacturer" to manufacturer,
                "model" to model,
                "name" to name,
                "type" to type
            ),
            "os" to mapOf(
                "name" to osName,
                "version" to osVersion
            ),
            "screen" to mapOf(
                "density" to screenDensity,
                "height" to screenHeight,
                "width" to screenWidth
            ),
            "locale" to locale,
            "userAgent" to userAgent,
            "timezone" to timeZone
        )
    }
}
