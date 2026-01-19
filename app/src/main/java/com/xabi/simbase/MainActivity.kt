package com.xabi.simbase

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalContext
import com.xabi.simbase.navigation.AppNavigation
import androidx.lifecycle.viewmodel.compose.viewModel
import com.xabi.simbase.data.dataStore

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            // AHORA SÍ: estamos dentro de un @Composable
            val context = LocalContext.current.applicationContext

            val viewModel: SimViewModel = viewModel(
                factory = SimViewModelFactory(context.dataStore)
            )

            AppNavigation(viewModel)
        }
    }
}





