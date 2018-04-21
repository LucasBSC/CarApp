package com.example.lucas.appdotcc;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.Set;


/**
 * Created by lucas on 20/04/2018.
 */

public class devicesList extends ListActivity {

    // Variável que representa o módulo Bluetooth do celular.
    private BluetoothAdapter BluetoothAdapter2 = null;

    // Variavel para ser passado o endereço de identificação do Bluetooth.
    static String MAC_ADDRESS = null;

    // Atividade iniciada ao abrir iniciar execução da classe.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Define layout padrão do Android para lista (Array) de dispositivos pareados.
        ArrayAdapter<String> ArrayBluetooth = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

        // Variável que guarda o módulo Bluetooth do aparelho.
        BluetoothAdapter2 = BluetoothAdapter.getDefaultAdapter();

        // Retorna os dispositivos específicos pareados com o aparelho em um Array
        Set<BluetoothDevice> dispositivosPareados = BluetoothAdapter2.getBondedDevices();


        // Verifica se há dispositivos pareados, caso haja coloca tudo em um Array explicitando Nome e MacAddress de cada.
        if (dispositivosPareados.size() > 0) {
            for (BluetoothDevice dispositivo : dispositivosPareados) {
                String nomeDoDevice = dispositivo.getName();
                String macAddress = dispositivo.getAddress();
                ArrayBluetooth.add(nomeDoDevice + "\n" + macAddress);
            }
        }
        setListAdapter(ArrayBluetooth);
    }

    // Atividade executada ao clicar em um item da lista,
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        // Variável para pegar informação do item clicado.
        String info = ((TextView) v).getText().toString();

        // Gera uma substring para mostrar apenas o endereço MAC.
        String enderecoMac = info.substring(info.length() - 17);

        // Intent que serve como ponte para levar esse MAC-Address até a Main Activity do App.
        Intent retornaMAC = new Intent();
        retornaMAC.putExtra(MAC_ADDRESS, enderecoMac);

        // Força resultado da atividade para OK.
        setResult(RESULT_OK, retornaMAC);

        // Fecha a tela de Lista dos Dispositivos.
        finish();
    }
}
