package com.xabi.simbase.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import com.xabi.simbase.api.SimCard
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.ui.unit.dp
import androidx.compose.material3.*
import androidx.compose.ui.window.DialogProperties


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimDetailDialog(
    sim: SimCard,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false // ocupa casi toda la pantalla
        ),
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        },
        title = {
            Text("Detalles de la SIM")
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Aquí van TODOS los detalles sin scroll
                Text("Nombre: ${sim.name ?: "-"}")
                Text("Estado: ${sim.state ?: "-"}")
                Text("ICCID: ${sim.iccid}")
                Text("Tags: ${sim.tags?.joinToString(", ") ?: "-"}")
                Text("Plan ID: ${sim.plan_id ?: "-"}")
                Text("Cobertura: ${sim.coverage ?: "-"}")
                Text("Perfil SIM: ${sim.sim_profile ?: "-"}")
                Text("MSISDN: ${sim.msisdn ?: "-"}")
                Text("Hardware: ${sim.hardware ?: "-"}")
                Text("IMEI: ${sim.imei ?: "-"}")
                Text("IMEI Lock: ${sim.imei_lock ?: "-"}")
                Text("IP Pública: ${sim.public_ip ?: "-"}")
                Text("IP Privada: ${sim.private_network_ip ?: "-"}")

                Spacer(Modifier.height(12.dp))

                Text("Uso del mes:", style = MaterialTheme.typography.titleMedium)
                Text("  Datos: ${sim.current_month_usage?.data ?: "-"}")
                Text("  Voz: ${sim.current_month_usage?.voice ?: "-"}")

                Spacer(Modifier.height(12.dp))

                Text("Costes del mes:", style = MaterialTheme.typography.titleMedium)
                Text("  Total: ${sim.current_month_costs?.total ?: "-"}")
                Text("  Datos: ${sim.current_month_costs?.data ?: "-"}")
                Text("  Voz: ${sim.current_month_costs?.voice ?: "-"}")

                Spacer(Modifier.height(12.dp))

                Text("Autodisable: ${sim.autodisable ?: "-"}")
            }
        }
    )
}