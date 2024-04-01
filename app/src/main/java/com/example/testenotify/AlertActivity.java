package com.example.testenotify;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AlertActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);

        Button liberarButton = findViewById(R.id.liberar_button);
        liberarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implemente o que deseja fazer ao clicar no botão "Liberar"
                // Por exemplo, parar o serviço e fechar a atividade
                stopAlertService();
                finish();
            }
        });
    }

    private void stopAlertService() {
        Intent serviceIntent = new Intent(this, AlertService.class);
        stopService(serviceIntent);
    }
}

