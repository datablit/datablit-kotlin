package com.example.test

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.test.ui.theme.TestTheme
//import com.segment.analytics.kotlin.android.Analytics
//import com.segment.analytics.kotlin.core.*

import com.datablit.Datablit

class MainActivity : ComponentActivity() {
    private lateinit var datablit: Datablit

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
        datablit = Datablit("dL01K2NXKB74QVFY8XJVMCJ9ACM0", applicationContext) {
            endpoint =
                "https://staging-event.datablit.com/v1/batch"
            flushAt = 1
            flushInterval = 10000
            trackApplicationLifecycleEvents = true
            enableDebugLogs = true
            apiBaseURL = "https://staging-console.datablit.com"
        }
        setContent {
            TestTheme {
                MainScreen(datablit)
            }
        }
    }
}

@Composable
fun MainScreen(datablit: Datablit) {
    var showSnackbar by remember { mutableStateOf(false) }
    var ruleResult by remember { mutableStateOf<String?>(null) }
    var variantResult by remember { mutableStateOf<String?>(null) }
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
                datablit.track("View Product", productDetails)
            }) {
                Text(text = "Send event")
            }
            Button(onClick = {
                showSnackbar = true
                val traits: Map<String, Any> = mapOf(
                    "email" to "deepak@gmail.com"
                )
                datablit.identify("android_1234", traits)
            }) {
                Text(text = "identify")
            }
            
            Button(onClick = {
                datablit.rule.evalRule(
                    key = "test_rule",
                    userId = "android_1234",
                    params = mapOf("os_name" to "android")
                ) { result ->
                    result.onSuccess { response ->
                        ruleResult = "Rule result: ${response.result}"
                    }.onFailure { error ->
                        ruleResult = "Error: ${error.message}"
                    }
                }
            }) {
                Text(text = "Evaluate Rule")
            }
            
            ruleResult?.let { result ->
                Text(text = result, modifier = Modifier.padding(top = 16.dp))
            }
            
            Button(onClick = {
                datablit.experiment.getVariant(
                    expId = "01K2QM64F1141FFJNACB6WY132",
                    entityId = "android_12"
                ) { result ->
                    result.onSuccess { response ->
                        variantResult = "Variant: ${response.variant}"
                    }.onFailure { error ->
                        variantResult = "Error: ${error.message}"
                    }
                }
            }) {
                Text(text = "Get Experiment Variant")
            }
            
            variantResult?.let { result ->
                Text(text = result, modifier = Modifier.padding(top = 16.dp))
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
