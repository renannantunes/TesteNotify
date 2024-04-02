package com.example.testenotify;

// RejectReceiver.java
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class RejectReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Ação a ser executada quando o botão "Recusar" for clicado
        Toast.makeText(context, "Chamada recusada", Toast.LENGTH_SHORT).show();

        // Envia uma mensagem para o serviço AlertService indicando que a chamada foi recusada
        Intent serviceIntent = new Intent(context, AlertService.class);
        serviceIntent.setAction(AlertService.ACTION_STOP_ALERT);
        context.startService(serviceIntent);
    }
}

