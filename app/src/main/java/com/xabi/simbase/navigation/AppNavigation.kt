package com.xabi.simbase.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.xabi.simbase.SimViewModel
import com.xabi.simbase.data.TokenStore
import com.xabi.simbase.ui.SettingsScreen
import com.xabi.simbase.ui.SimbaseApp

@Composable
fun AppNavigation(viewModel: SimViewModel) {
    val navController = rememberNavController()
    val context = LocalContext.current.applicationContext

    NavHost(navController = navController, startDestination = "loading") {

        composable("loading") {
            val tokens by TokenStore.readTokens(context).collectAsState(initial = null)

            LaunchedEffect(tokens) {
                val currentTokens = tokens ?: return@LaunchedEffect
                val read = currentTokens.first
                val write = currentTokens.second

                if (read.isBlank() || write.isBlank()) {
                    navController.navigate("settings") {
                        popUpTo("loading") { inclusive = true }
                    }
                } else {
                    navController.navigate("main") {
                        popUpTo("loading") { inclusive = true }
                    }
                }
            }

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        composable("main") {
            SimbaseApp(
                viewModel = viewModel,
                onOpenSettings = { navController.navigate("settings") }
            )
        }

        composable("settings") {
            SettingsScreen(
                viewModel = viewModel,
                onBack = {
                    navController.navigate("loading") {
                        popUpTo("settings") { inclusive = true }
                    }
                }
            )
        }
    }
}