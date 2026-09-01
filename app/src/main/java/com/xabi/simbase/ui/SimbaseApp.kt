package com.xabi.simbase.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.xabi.simbase.SimViewModel
import com.xabi.simbase.ui.components.SimDetailDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimbaseApp(
    viewModel: SimViewModel = viewModel(),
    onOpenSettings: () -> Unit
) {
    val sims by viewModel.sims.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val changingSimIccid by viewModel.changingSimIccid.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val readToken by viewModel.readToken.collectAsState()
    val selectedSim by viewModel.selectedSim.collectAsState()

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    // Carga inicial cuando el token esté disponible
    LaunchedEffect(readToken) {
        if (readToken.isNotBlank() && sims.isEmpty()) {
            viewModel.loadSims()
        }
    }

    // Notificación en Snackbar ante errores de red o autenticación
    LaunchedEffect(errorMessage) {
        errorMessage?.let { msg ->
            snackbarHostState.showSnackbar(
                message = msg,
                duration = SnackbarDuration.Long
            )
            viewModel.clearErrorMessage()
        }
    }

    // Diálogo de detalles
    selectedSim?.let { sim ->
        SimDetailDialog(
            sim = sim,
            onDismiss = { viewModel.clearSelectedSim() }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Simbase Manager",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.loadSims() },
                        enabled = !isLoading
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refrescar")
                    }
                    IconButton(onClick = onOpenSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Ajustes")
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Barra de progreso de carga en la parte superior
            if (isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Lista o estado vacío
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    if (sims.isEmpty() && !isLoading) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "No se encontraron tarjetas SIM",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "Comprueba que tus tokens en Ajustes tengan los permisos adecuados o pulsa el botón para recargar.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            Spacer(Modifier.height(16.dp))
                            Button(onClick = { viewModel.loadSims() }) {
                                Icon(Icons.Default.Refresh, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("Reintentar")
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 8.dp)
                        ) {
                            items(
                                items = sims,
                                key = { it.iccid }
                            ) { sim ->
                                SimRow(
                                    sim = sim,
                                    isChanging = (changingSimIccid == sim.iccid),
                                    onToggle = { viewModel.toggleSim(sim) },
                                    onSimClick = { viewModel.selectSim(sim) }
                                )
                            }
                        }
                    }
                }

                // Botón Salir
                OutlinedButton(
                    onClick = { context.findActivity()?.finish() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text("Salir de la app")
                }
            }
        }
    }
}