package com.xabi.simbase.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.ToggleOff
import androidx.compose.material.icons.filled.ToggleOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xabi.simbase.api.SimCard

@Composable
fun SimRow(
    sim: SimCard,
    isChanging: Boolean = false,
    onToggle: () -> Unit,
    onSimClick: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val stateLower = sim.state?.lowercase() ?: ""
    val isTransitioning = isChanging || stateLower in listOf("enabling", "disabling")

    // Colores con alto contraste adaptados para modo claro y modo oscuro
    val (cardBg, stateColor, stateLabel) = when (stateLower) {
        "enabled" -> Triple(
            if (isDark) Color(0xFF132A1C) else Color(0xFFE8F5E9),
            if (isDark) Color(0xFF81C784) else Color(0xFF2E7D32),
            "ACTIVA"
        )
        "disabled" -> Triple(
            if (isDark) Color(0xFF311516) else Color(0xFFFFEBEE),
            if (isDark) Color(0xFFE57373) else Color(0xFFC62828),
            "DESACTIVADA"
        )
        "enabling", "disabling" -> Triple(
            if (isDark) Color(0xFF2D2411) else Color(0xFFFFF8E1),
            if (isDark) Color(0xFFFFD54F) else Color(0xFFEF6C00),
            if (stateLower == "enabling") "ACTIVANDO..." else "DESACTIVANDO..."
        )
        else -> Triple(
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.onSurfaceVariant,
            sim.state?.uppercase() ?: "DESCONOCIDO"
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onSimClick() }
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = sim.name?.ifBlank { "Sin nombre" } ?: "Sin nombre",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ICCID:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(end = 4.dp)
                    )

                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .horizontalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = sim.iccid,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(Modifier.height(4.dp))

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = stateColor.copy(alpha = 0.18f),
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Text(
                        text = stateLabel,
                        color = stateColor,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .padding(start = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isTransitioning) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(26.dp),
                        strokeWidth = 2.5.dp,
                        color = stateColor
                    )
                } else {
                    IconButton(
                        onClick = onToggle
                    ) {
                        val (icon, tint) = when (stateLower) {
                            "enabled" -> Pair(Icons.Default.ToggleOn, stateColor)
                            "disabled" -> Pair(Icons.Default.ToggleOff, MaterialTheme.colorScheme.onSurfaceVariant)
                            else -> Pair(Icons.AutoMirrored.Filled.Help, MaterialTheme.colorScheme.outline)
                        }

                        Icon(
                            imageVector = icon,
                            contentDescription = "Cambiar estado SIM",
                            modifier = Modifier.size(36.dp),
                            tint = tint
                        )
                    }
                }
            }
        }
    }
}
