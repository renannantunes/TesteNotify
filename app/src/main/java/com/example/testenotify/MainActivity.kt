package com.example.testenotify

import android.os.Bundle
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.testenotify.ui.theme.TesteNotifyTheme
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : ComponentActivity() {
    private val TAG = "MyFirebaseMsgService"
    private val permissionRequestCode = 123

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val channel = NotificationChannel(
            "CHANNEL_ID",
            "High priority notifications",
            NotificationManager.IMPORTANCE_HIGH
        )

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

        getToken()

        setContent {
            TesteNotifyTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Greeting("Android")
//                    PermissionRequestScreen()
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
            text = "Hello $name!",
            modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TesteNotifyTheme {
        Greeting("Android")
    }
}

@Composable
fun PermissionRequestScreen() {
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { hasPermission ->
            if (hasPermission) {
                // A permissão foi concedida
                // Adicione aqui qualquer lógica adicional que você deseja executar
            }
        }
    )

    var hasNotificationPermission by remember { mutableStateOf(false) }

    Button(
        onClick = {
            if (!hasNotificationPermission) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    ) {
        Text(text = "Request permission")
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