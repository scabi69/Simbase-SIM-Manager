package com.xabi.simbase.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xabi.simbase.api.SimCard
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ToggleOn
import androidx.compose.material.icons.filled.ToggleOff
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Help

@Composable
fun SimRow(sim: SimCard, onToggle: () -> Unit) {

    // Color según estado
    val backgroundColor = when (sim.state?.lowercase()) {
        "enabled" -> Color(0xFFDFF6DD)     // Verde suave
        "disabled" -> Color(0xFFF8D7DA)    // Rojo suave
        "enabling", "disabling" -> Color(0xFFFFF3CD) // Amarillo suave (opcional)
        else -> Color.White
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text("Nombre: ${sim.name ?: "Sin nombre"}")

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ICCID:",
                        fontSize = 14.sp,
                        modifier = Modifier.padding(end = 6.dp)
                    )

                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .horizontalScroll(rememberScrollState())
                    ) {
                        Text(sim.iccid)
                    }
                }

                Text("Estado: ${sim.state}")
            }

            val isEnabled = sim.state?.lowercase() == "enabled"
            val buttonText = if (isEnabled) "Desactivar" else "Activar"

            IconButton(
                onClick = onToggle
            ) {
                val icon = when (sim.state) {
                    "enabled" -> Icons.Default.ToggleOn
                    "disabled" -> Icons.Default.ToggleOff
                    "enabling" -> Icons.Default.Sync
                    "disabling" -> Icons.Default.Sync
                    else -> Icons.Default.Help
                }

                Icon(
                    imageVector = icon,
                    contentDescription = "Cambiar estado SIM"
                )
            }


        }
    }
}
