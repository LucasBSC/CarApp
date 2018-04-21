package com.example.lucas.appdotcc;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    // Variável que recebe presença de Bluetooth
    BluetoothAdapter BluetoothAdapter = null;

    // Variável que recebe o suposto dispositivo Bluetooth Remoto
    BluetoothDevice Device = null;

    // Variável que serve como canal de comunicação de dados (Socket)
    BluetoothSocket Socket = null;

    // Variável a ser passada para o método "startActivityForResult" de ATIVAÇÃO do Bluetooth ser chamado.
    private static final int Solicitar_Bluetooth = 1;

    // Variável a ser passada para o método "startActivityForResult" de CONEXÃO do Bluetooth ser chamado.
    private static final int Conectar_Bluetooth = 2;

    // Booleana para indicar status da conexão Bluetooth
    boolean conexao = false;

    // Variável para endereço MAC
    private static String MAC = null;

    // Identificação do módulo Bluetooth remoto
    UUID meuUUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");




    // Botões
    Button btnConectar, btnAbrir, btnLigar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Link dos botões da interface com a lógica de programação.
        btnConectar = (Button)findViewById(R.id.btnConectar);
        btnAbrir = (Button)findViewById(R.id.btnAbrir);
        btnLigar = (Button)findViewById(R.id.btnLigar);

        // Aplicação inicia e tenta checar a existência efetiva de módulo Bluetooth.
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
                    // Caso a conexão já esteja feita ou dê problema, fecha Socket, altera botão ou printa erro.
                    try {
                        Socket.close();
                        conexao = false;
                        btnConectar.setText("CONECTAR");
                        Toast.makeText(getApplicationContext(), "Bluetooth foi desconectado", Toast.LENGTH_LONG).show();
                    } catch (IOException erro) {
                        Toast.makeText(getApplicationContext(), "Ocorreu um erro: " + erro, Toast.LENGTH_LONG).show();
                    }
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

            case Conectar_Bluetooth:
                // Busca MAC-Address especificado na atividade devicesList
                if (resultCode == Activity.RESULT_OK) {
                    MAC = data.getExtras().getString(devicesList.MAC_ADDRESS);
                    //Toast.makeText(getApplicationContext(), "MAC: " + MAC, Toast.LENGTH_LONG).show();

                    // Associa o Bluetooth do celular ao dispositivo Bluetooth remoto escolhido.
                    Device = BluetoothAdapter.getRemoteDevice(MAC);

                    // Cria o canal de comunicação e altera o botão.
                    try {
                        Socket = Device.createRfcommSocketToServiceRecord(meuUUID);
                        Socket.connect();
                        conexao = true;
                        btnConectar.setText("DESCONECTAR");
                        Toast.makeText(getApplicationContext(), "Você foi conectado com " + MAC, Toast.LENGTH_LONG).show();
                    // Print erro.
                    } catch (IOException erro) {
                        conexao = false;
                        Toast.makeText(getApplicationContext(), "Ocorreu um erro: " + erro, Toast.LENGTH_LONG).show();
                    }
                // Print erro caso não obtenha o MAC-Address
                } else {
                    Toast.makeText(getApplicationContext(), "Falha ao obter MAC-ADDRESS", Toast.LENGTH_LONG).show();
                }
        }

    }
}
