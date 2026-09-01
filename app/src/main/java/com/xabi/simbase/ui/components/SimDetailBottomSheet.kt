package com.xabi.simbase.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.xabi.simbase.api.SimCard

@Composable
fun SimDetailDialog(
    sim: SimCard,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Cerrar")
            }
        },
        title = {
            Text(
                text = sim.name?.ifBlank { "Detalles de la SIM" } ?: "Detalles de la SIM",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 8.dp)
            ) {
                DetailSectionTitle("Información General")
                DetailRow("Estado", sim.state ?: "-")
                DetailRow("ICCID", sim.iccid)
                DetailRow("Plan ID", sim.plan_id ?: "-")
                DetailRow("Cobertura", sim.coverage ?: "-")
                DetailRow("Perfil SIM", sim.sim_profile ?: "-")
                if (!sim.tags.isNullOrEmpty()) {
                    DetailRow("Tags", sim.tags.joinToString(", "))
                }
                DetailRow("Autodisable", sim.autodisable ?: "-")

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                DetailSectionTitle("Red y Dispositivo")
                DetailRow("MSISDN", sim.msisdn ?: "-")
                DetailRow("Hardware", sim.hardware ?: "-")
                DetailRow("IMEI", sim.imei ?: "-")
                DetailRow("IMEI Lock", sim.imei_lock ?: "-")
                DetailRow("IP Pública", sim.public_ip ?: "-")
                DetailRow("IP Privada", sim.private_network_ip ?: "-")

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                DetailSectionTitle("Consumo del Mes")
                val usage = sim.current_month_usage
                DetailRow("Datos", formatDataUsage(usage?.data))
                DetailRow("Voz", "${usage?.voice ?: 0} min")
                DetailRow("SMS Salientes (MO)", "${usage?.sms_mo ?: 0}")
                DetailRow("SMS Entrantes (MT)", "${usage?.sms_mt ?: 0}")
                DetailRow("Sesiones totales", "${usage?.total_sessions ?: 0}")

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                DetailSectionTitle("Costes del Mes")
                val costs = sim.current_month_costs
                DetailRow("Total", costs?.total ?: "-")
                DetailRow("Datos", costs?.data ?: "-")
                DetailRow("Voz", costs?.voice ?: "-")
                DetailRow("SMS", costs?.sms ?: "-")
                DetailRow("Línea", costs?.line_rental ?: "-")
            }
        }
    )
}

@Composable
private fun DetailSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(bottom = 6.dp)
    )
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1.4f)
        )
    }
}

private fun formatDataUsage(bytes: Int?): String {
    if (bytes == null || bytes == 0) return "0 MB"
    val mb = bytes.toDouble() / (1024 * 1024)
    return if (mb >= 1024) {
        String.format("%.2f GB", mb / 1024)
    } else {
        String.format("%.2f MB", mb)
    }
}