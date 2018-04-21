package com.example.lucas.appdotcc;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ArrayAdapter;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
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
}
