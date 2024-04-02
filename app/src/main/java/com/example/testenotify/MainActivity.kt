package com.example.testenotify

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationManagerCompat
import com.example.testenotify.ui.theme.TesteNotifyTheme
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging


class MainActivity : ComponentActivity() {
    private val TAG = "MyFirebaseMsgService"
    private val permissionRequestCode = 123

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Verifica se a ação ANSWER_CALL está presente nos extras da Intent
        if (intent != null && intent.action != null && intent.action == "ACTION_ANSWER_CALL") {
            // Ação a ser executada quando o usuário atender à chamada
            stopService(Intent(this, AlertService::class.java)) // Para o serviço AlertService
        }

        getToken()

        setContent {
            MyApp()
        }
    }
}

@Composable
fun MyApp() {
    var isPermissionRequested by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val notificationManager = NotificationManagerCompat.from(context)
    val areNotificationsEnabled = notificationManager.areNotificationsEnabled()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Bem-vindo Usuário",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (!areNotificationsEnabled && !isPermissionRequested) {
            RequestPermissionButton(onPermissionGranted = { isPermissionRequested = true })
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
            text = "Bem-vindo $name!",
            modifier = modifier
                .fillMaxSize() // Preenche toda a largura e altura disponível
                .wrapContentSize(Alignment.Center) // Alinha ao centro vertical e horizontalmente
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewMyApp() {
    MyApp()
}
@Composable
fun RequestPermissionButton(onPermissionGranted: () -> Unit) {
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { hasPermission ->
            if (hasPermission) {
                // A permissão foi concedida
                onPermissionGranted()
            }
        }
    )

    Button(
        onClick = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        },
        modifier = Modifier.width(200.dp), // Define o tamanho do botão
        shape = RoundedCornerShape(8.dp), // Define o formato do botão
        colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray) // Define a cor de fundo do botão
    ) {
        Text(
            text = "Solicitar Permissão de Notificação",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
    }
}

fun getToken() {
    val TAG = "MyFirebaseMsgService";

    FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
        if (!task.isSuccessful) {
            Log.w(TAG, "Fetching FCM registration token failed", task.exception)
            return@OnCompleteListener
        }

        // Get new FCM registration token
        val token = task.result

        // Log and toast
        Log.d(TAG, token)
    })
}