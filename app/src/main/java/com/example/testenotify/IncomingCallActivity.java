package com.example.testenotify;

// IncomingCallActivity.java
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;

public class IncomingCallActivity extends Activity {

    private MediaPlayer mediaPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_call);
//        startAlarmSound(this);

        // Encontre os botões no layout
        Button buttonAnswer = findViewById(R.id.buttonAnswer);
        Button buttonReject = findViewById(R.id.buttonReject);

        // Configure os listeners de clique para os botões
        buttonAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ação ao clicar em "Atender"
                // Por exemplo, iniciar uma nova Activity para atender à chamada
                Toast.makeText(IncomingCallActivity.this, "Chamada atendida", Toast.LENGTH_SHORT).show();
                // Aqui você pode iniciar uma nova Activity ou serviço para atender à chamada
                stopAlertService();
                goToMain();
            }
        });

        buttonReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ação ao clicar em "Recusar"
                // Por exemplo, encerrar a Activity ou realizar outra ação adequada
                Toast.makeText(IncomingCallActivity.this, "Chamada recusada", Toast.LENGTH_SHORT).show();
                stopAlertService();
//                goToMain();
                finish(); // Encerra a Activity
            }
        });
    }

    private void stopAlertService() {
        Intent serviceIntent = new Intent(this, AlertService.class);
        stopService(serviceIntent);
    }

    private void goToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}

