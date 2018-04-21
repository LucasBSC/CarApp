package com.example.lucas.appdotcc;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    //  Botões
    Button btnConectar, btnAbrir, btnLigar;

    // Variável a ser passada para o método "startActivityForResult" de ativação do Bluetooth ser chamado.
    private static final int Solicitar_Bluetooth = 1;

    // Variável a ser passada para o método "startActivityForResult" de conexão do Bluetooth ser chamado.
    private static final int Conectar_Bluetooth = 2;

    // Variável que recebe presença de Bluetooth
    BluetoothAdapter BluetoothAdapter = null;

    boolean conexao = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Link dos botões da interface com a lógica de programação.
        btnConectar = (Button)findViewById(R.id.btnConectar);
        btnAbrir = (Button)findViewById(R.id.btnAbrir);
        btnLigar = (Button)findViewById(R.id.btnLigar);

        // Aplicação inicia e tenta checar a existência de módulo Bluetooth.
        BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Caso não haja Bluetooth, printa mensagem.
        if (BluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "Seu dispositivo não possui Bluetooth", Toast.LENGTH_LONG).show();

         // Caso haja Bluetooth, irá ativá-lo.
        } else if (!BluetoothAdapter.isEnabled()) {
            Intent ativaBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(ativaBluetooth, Solicitar_Bluetooth);
        }

        // Cria um Listener de click para o botão CONECTAR
        btnConectar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(conexao) {
                    // Caso a conexão já esteja feita
                } else {
                    // Caso não esteja feita, abre lista de dispositivos pareados.
                    Intent abrirLista = new Intent(MainActivity.this, devicesList.class);
                    startActivityForResult(abrirLista, Conectar_Bluetooth);
                }

            }
        });
    }

    //Nova atividade aberta com base no resultado da presença de Bluetooth no dispositivo da atividade de abertura do App.

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case Solicitar_Bluetooth:
                //Caso a solicitação seja efetuada com sucesso, printa que o Bluetooth foi ativado.
                if(resultCode == Activity.RESULT_OK) {
                    Toast.makeText(getApplicationContext(), "O bluetooth foi ativado com sucesso", Toast.LENGTH_LONG).show();
                //Caso contrário, printa mensagem de erro e encerra aplicativo.
                } else {
                    Toast.makeText(getApplicationContext(), "Ocorreu um problema com a ativação do Bluetooth. App será encerrado", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
        }

    }
}
