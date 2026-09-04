package com.japcounter.jap_counter_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.japcounter.jap_counter_app.ui.navigation.JapNavGraph
import com.japcounter.jap_counter_app.ui.theme.JapCounterTheme
import com.japcounter.jap_counter_app.ui.viewmodel.JapViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: JapViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JapCounterTheme {
                JapNavGraph(viewModel = viewModel)
            }
        }
    }
}