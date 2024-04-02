package com.example.testenotify;

// AnswerReceiver.java
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class AnswerReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Ação a ser executada quando o botão "Atender" for clicado
        Toast.makeText(context, "Chamada atendida", Toast.LENGTH_SHORT).show();

        // Envia uma mensagem para o serviço AlertService indicando que a chamada foi recusada
        Intent serviceIntent = new Intent(context, AlertService.class);
        serviceIntent.setAction(AlertService.ACTION_STOP_ALERT);
        context.startService(serviceIntent);

        // Abrir o aplicativo (MainActivity)
        Intent mainIntent = new Intent(context, MainActivity.class);
        mainIntent.setAction(Intent.ACTION_MAIN);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(mainIntent);
    }
}

