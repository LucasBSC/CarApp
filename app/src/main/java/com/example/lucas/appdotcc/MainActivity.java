package com.example.lucas.appdotcc;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

    // Variável para indicar ação de receber dados do Bluetooth.
    private static final int MESSAGE_READ = 3;

    // Booleana para indicar status da conexão Bluetooth
    boolean conexao = false;

    // Thread de comunicação entre celular e dispositivo remoto
    ConnectedThread connectedThread;

    // Handler para receber dados do Bluetooth.
    Handler mHandler;

    // String builder para acumular dados que provém do Bluetooth.
    StringBuilder dadosBluetooth = new StringBuilder();


    // Variável para endereço MAC
    private static String MAC = null;

    // Identificação do módulo Bluetooth remoto
    UUID meuUUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");


    // Botões
    Button btnConectar, btnAbrir, btnLigar, btnPorta;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Link dos botões da interface com a lógica de programação.
        btnConectar = (Button)findViewById(R.id.btnConectar);
        btnAbrir = (Button)findViewById(R.id.btnAbrir);
        btnLigar = (Button)findViewById(R.id.btnLigar);
        btnPorta = (Button)findViewById(R.id.btnPorta);

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

        // Cria um Listener de click para o botão CONECTAR.
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

        // Cria um Listener de click para o Botão ABRIR.
        btnAbrir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Caso tenha conexão, envia string.
                if (conexao) {
                    connectedThread.enviar("led1");
                // Caso não tenha conexão, printa erro.
                } else {
                    Toast.makeText(getApplicationContext(), "Bluetooth está desconectado", Toast.LENGTH_LONG).show();
                }

            }
        });

        // Handler para tratar toda informação que chega através do Bluetooth.
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {

                // Se tem mensagem acumula em uma variável.
                if (msg.what == MESSAGE_READ) {
                    String dadosRecebidos = (String) msg.obj;
                    dadosBluetooth.append(dadosRecebidos);
                    // Considera '}' o último elemento da informação
                    int fimDaInformacao = dadosBluetooth.indexOf("}");
                    // Se esse elemento for maior que zero, então temos informação disponivel e a passamos a uma variável.
                    if (fimDaInformacao > 0) {
                        String dadosCompletos = dadosBluetooth.substring(0, fimDaInformacao);
                        // Guardamos o tamanho dessa informação.
                        int tamInformacao = dadosCompletos.length();


                        // Se começar com '{' é uma informação completa.
                        if (dadosBluetooth.charAt(0) == '{') {
                            String dadosFinais = dadosBluetooth.substring(1, tamInformacao);

                            // Se recebe 'l1on', botão fica verde
                            if (dadosFinais.contains("l1on")) {
                                btnAbrir.setText("FECHAR");
                                btnPorta.setBackgroundColor(Color.rgb(65, 134, 12));
                            }

                            if (dadosFinais.contains("l1off")) {
                                btnAbrir.setText("ABRIR");
                                btnPorta.setBackgroundColor(Color.rgb(191, 25, 25));
                            }

                        }

                        // Deleta os dados da variável ao final do processo.
                        dadosBluetooth.delete(0, dadosBluetooth.length());

                    }
                }
            }
        };

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

                        connectedThread = new ConnectedThread(Socket);
                        connectedThread.start();

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

    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private byte[] mmBuffer; // mmBuffer store for the stream

        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams; using temp objects because
            // member streams are final.
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {  }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024]; // Buffer para o canal de comunicação.
            int bytes; // Bytes que retornam do read().

            // Escuta a InputStream até ocorrer erro de Exception.

           while (true) {
                try {
                    // Lê os dados da InputStream.
                    bytes = mmInStream.read(buffer);


                    // Variável que recebe o buffer de Bytes transformando-os em String.
                    String dadosBt = new String(buffer, 0, bytes);


                    // Envia os bytes obtidos para atividade na Interface.
                   mHandler.obtainMessage(MESSAGE_READ, bytes, -1, dadosBt).sendToTarget();

                } catch (IOException e) {
                    break;
                }
            }
        }

        // Método para enviar dados ao dispositivo Bluetooth.
        public void enviar(String dadosEnviados) {
            byte[] msgBuffer = dadosEnviados.getBytes();
            try {
                mmOutStream.write(msgBuffer);
            } catch (IOException e) {  }
        }
    }
}

