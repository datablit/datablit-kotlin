package com.datablit

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.edit
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Lifecycle observer for tracking application lifecycle events
 */
class LifecycleObserver(val datablit: Datablit, val context: Context) : DefaultLifecycleObserver {
    private val firstLaunch = AtomicBoolean(false)
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(
            "DatablitPrefs",
            Context.MODE_PRIVATE
        ) // same as datablit class

    val packageManager: PackageManager? = context.packageManager
    val packageName: String? = context.packageName
    val packageInfo: PackageInfo? = packageManager?.getPackageInfo(packageName.toString(), 0)

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        firstLaunch.set(true)
        val curVersion = packageInfo?.versionName.toString()
        val curBuild = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            packageInfo?.longVersionCode.toString()
        } else {
            @Suppress("DEPRECATION")
            packageInfo?.versionCode.toString()
        }

        val pair = getVersionAndBuild() // previous version and build pair
        if (pair.first == null && pair.second == null) { // app installed
            datablit.track(
                "Application Installed", mapOf(
                    "version" to curVersion,
                    "build" to curBuild
                )
            )
            saveVersionAndBuild(curVersion, curBuild)
        } else if (curVersion != pair.first) { // app updated
            datablit.track(
                "Application Updated", mapOf(
                    "version" to curVersion,
                    "build" to curBuild,
                    "previous_version" to pair.first.toString(),
                    "previous_build" to pair.second.toString()
                )
            )
        }
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        val properties = mapOf(
            "from_background" to !firstLaunch.getAndSet(false)
        )
        datablit.track("Application Opened", properties)
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        datablit.track("Application Backgrounded")
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
    }

    private fun getVersionAndBuild(): Pair<String?, String?> {
        return Pair(
            sharedPreferences.getString("version", null),
            sharedPreferences.getString("build", null)
        )
    }

    private fun saveVersionAndBuild(version: String, build: String) {
        sharedPreferences.edit { putString("version", version) }
        sharedPreferences.edit { putString("build", build) }
    }
}
