package com.xabi.simbase.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.xabi.simbase.ui.SettingsScreen
import com.xabi.simbase.ui.SettingsViewModel
import com.xabi.simbase.ui.SimbaseApp
import com.xabi.simbase.data.TokenStore
import androidx.compose.runtime.collectAsState
import androidx.compose.material3.CircularProgressIndicator
import com.xabi.simbase.SimViewModel
import com.xabi.simbase.SimViewModelFactory
import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
fun AppNavigation(viewModel: SimViewModel) {
    val navController = rememberNavController()
    val context = LocalContext.current.applicationContext

    NavHost(navController = navController, startDestination = "loading") {

        composable("loading") {

            // Leer tokens desde DataStore
            val tokens = TokenStore.readTokens(context).collectAsState(initial = null).value

            if (tokens == null) {
                CircularProgressIndicator()
            } else {
                val read = tokens.first
                val write = tokens.second

                println("TOKENS -> read='${read}', write='${write}'")

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