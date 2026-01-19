package com.xabi.simbase.ui


import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import android.app.Activity

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding

import androidx.compose.material3.Button

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.xabi.simbase.SimViewModel
import androidx.compose.material3.ExperimentalMaterial3Api
import com.xabi.simbase.ui.SimRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimbaseApp(
    viewModel: SimViewModel = viewModel(),
    onOpenSettings: () -> Unit
) {


    val sims by viewModel.sims.collectAsState()
    val activity = LocalContext.current as Activity

    LaunchedEffect(viewModel.readToken.value) {
        if (viewModel.readToken.value.isNotBlank()) {
            viewModel.loadSims()
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Simbase Manager") },
                actions = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Ajustes")
                    }

                }
            )
        }

    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            // --- PARTE SUPERIOR (con weight) ---
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Button(onClick = { viewModel.loadSims() }) {
                    Text("Refrescar")
                }

                Spacer(Modifier.height(16.dp))

                LazyColumn {
                    items(sims) { sim ->
                        SimRow(
                            sim = sim,
                            onToggle = { viewModel.toggleSim(sim) }
                        )
                    }
                }
            }

            // --- BOTÓN SALIR ABAJO ---
            Button(
                onClick = { activity.finish() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text("Salir de la app")
            }
        }
    }
}