package com.japcounter.jap_counter_app.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.japcounter.jap_counter_app.ui.history.JapHistoryScreen
import com.japcounter.jap_counter_app.ui.screens.CompletionScreen
import com.japcounter.jap_counter_app.ui.screens.CounterScreen
import com.japcounter.jap_counter_app.ui.screens.SetupScreen
import com.japcounter.jap_counter_app.ui.screens.SplashScreen
import com.japcounter.jap_counter_app.ui.screens.WelcomeScreen
import com.japcounter.jap_counter_app.ui.theme.SaffronDark
import com.japcounter.jap_counter_app.ui.viewmodel.JapViewModel

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Welcome : Screen("welcome")
    object Setup : Screen("setup")
    object Counter : Screen("counter")
    object Completion : Screen("completion")
    object History : Screen("history")
}

@Composable
fun JapNavGraph(viewModel: JapViewModel) {
    val navController = rememberNavController()
    val sessionState by viewModel.sessionState.collectAsState()
    val historyList by viewModel.historyList.collectAsState(initial = emptyList())
    val previousJapNames = remember(historyList) {
        historyList.map { it.japName }.distinct()
    }
    
    var isInitialized by remember { mutableStateOf(false) }

    // Mark initialization ready without creating fake demo history or session records
    LaunchedEffect(Unit) {
        viewModel.sessionState.collect {
            if (!isInitialized) {
                isInitialized = true
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                isReady = isInitialized,
                onSplashFinished = {
                    navController.navigate(Screen.Counter.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Welcome.route) {
                WelcomeScreen(
                    onContinue = { name ->
                        viewModel.saveUserName(name)
                        navController.navigate(Screen.Setup.route) {
                            popUpTo(Screen.Welcome.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Setup.route) {
                SetupScreen(
                    onStartJap = { mantra, limit ->
                        viewModel.startNewSession(mantra, limit)
                        navController.navigate(Screen.Counter.route) {
                            popUpTo(Screen.Setup.route) { inclusive = true }
                        }
                    },
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Screen.Counter.route) {
                CounterScreen(
                    userName = sessionState.userName ?: "",
                    mantraName = sessionState.mantraName,
                    currentCount = sessionState.currentCount,
                    targetCount = sessionState.targetCount,
                    previousJapNames = previousJapNames,
                    onTap = {
                        viewModel.incrementCounter()
                    },
                    onReset = {
                        viewModel.resetSession()
                    },
                    onNavigateToCompletion = {
                        navController.navigate(Screen.Completion.route) {
                            popUpTo(Screen.Counter.route) { inclusive = true }
                        }
                    },
                    onUpdateSessionSettings = { name, target ->
                        viewModel.updateMantraAndTarget(name, target)
                    },
                    onHistoryTab = {
                        navController.navigate(Screen.History.route)
                    }
                )
            }

            composable(Screen.Completion.route) {
                CompletionScreen(
                    mantraName = sessionState.mantraName,
                    targetCount = sessionState.targetCount,
                    onStartAgain = {
                        viewModel.resetSession()
                        navController.navigate(Screen.Counter.route) {
                            popUpTo(Screen.Completion.route) { inclusive = true }
                        }
                    },
                    onViewHistory = {
                        navController.navigate(Screen.History.route)
                    }
                )
            }

            composable(Screen.History.route) {
                JapHistoryScreen(
                    viewModel = viewModel,
                    onBack = {
                        navController.popBackStack()
                    },
                    onSelectPendingTask = { record ->
                        navController.navigate(Screen.Counter.route) {
                            popUpTo(Screen.History.route) { inclusive = true }
                        }
                    },
                    onSelectCompletedTask = { record ->
                        navController.navigate(Screen.Completion.route)
                    }
                )
            }
        }
}
