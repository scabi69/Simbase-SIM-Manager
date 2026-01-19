package com.xabi.simbase.ui


import android.app.Activity
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.compose.ui.platform.LocalContext
import com.xabi.simbase.SimViewModel

@Composable
fun SettingsScreen(
    viewModel: SimViewModel,
    onBack: () -> Unit
) {
        val readToken by viewModel.readToken.collectAsState()
        val writeToken by viewModel.writeToken.collectAsState()

    Log.d("SETTINGS", "SettingsScreen recomposed")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {

            Text(
                text = "Token de lectura",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium
            )

            OutlinedTextField(
                value = readToken,
                onValueChange = { viewModel.updateReadToken(it) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),


                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    cursorColor = MaterialTheme.colorScheme.primary
                )
            )



            Spacer(Modifier.height(16.dp))

            Text(
                text = "Token de escritura",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium
            )

            OutlinedTextField(
                value = writeToken,
                onValueChange = { viewModel.updateWriteToken(it) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),


                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    cursorColor = MaterialTheme.colorScheme.primary
                )
            )


            Spacer(Modifier.height(24.dp))

            if (readToken.isBlank() || writeToken.isBlank()) {
                Text(
                    text = "Debes introducir ambos tokens para continuar",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }


            Button(
                onClick = {
                Log.d("SETTINGS", "Guardando tokens desde SettingsScreen")

                    viewModel.saveTokens()
                    onBack()   // ← vuelve a "loading"
                },
                enabled = readToken.isNotBlank() && writeToken.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar")
            }


            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    if (readToken.isNotBlank() && writeToken.isNotBlank()) {
                        onBack()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Volver")
            }

        val activity = LocalContext.current as Activity

        Button(
            onClick = {
                viewModel.clearTokens {
                    activity.finishAffinity()   // ← cierre limpio

                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text("Borrar tokens y salir")
        }


        }
    }
