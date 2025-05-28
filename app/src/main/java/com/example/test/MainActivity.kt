package com.example.test

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.test.ui.theme.TestTheme
//import com.segment.analytics.kotlin.android.Analytics
//import com.segment.analytics.kotlin.core.*

import com.datablit.analytics.Analytics

class MainActivity : ComponentActivity() {
    private lateinit var analytics: Analytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

//        analytics = Analytics("pnGhP77ELs1GLSuhnYGWQ7WKSlw9eI12", applicationContext) {
//            trackApplicationLifecycleEvents = true
//            flushAt = 3
//            flushInterval = 10
//            collectDeviceId = true
//            useLifecycleObserver = true
//        }
        analytics = Analytics("01JSWDEZYM0E19ZF3RMHS75PB1L287079", applicationContext) {
            endpoint =
                "https://4ce5-2401-4900-1c5d-50a2-94f6-82e4-5886-c3b1.ngrok-free.app/v1/batch"
            flushAt = 1
            flushInterval = 10000
            trackApplicationLifecycleEvents = true
            enableDebugLogs = true
        }
        setContent {
            TestTheme {
                MainScreen(analytics)
            }
        }
    }
}

@Composable
fun MainScreen(analytics: Analytics) {
    var showSnackbar by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center
        ) {
            Button(onClick = {
                showSnackbar = true
                val productDetails: Map<String, Any> = mapOf(
                    "productId" to 123,
                    "productName" to "Striped trousers"
                )
                analytics.track("View Product", productDetails)
            }) {
                Text(text = "Send event")
            }
            Button(onClick = {
                showSnackbar = true
                val traits: Map<String, Any> = mapOf(
                    "email" to "deepak@gmail.com"
                )
                analytics.identify("1234", traits)
            }) {
                Text(text = "identify")
            }
        }
    }

    if (showSnackbar) {
        LaunchedEffect(snackbarHostState) {
            snackbarHostState.showSnackbar("Button clicked!")
            showSnackbar = false
        }
    }
}
