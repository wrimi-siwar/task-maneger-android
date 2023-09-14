package com.selmi.myapp;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;



public class bluetoothActivity extends AppCompatActivity {

    private static final String TAG = "BluetoothActivity";
    private static final int REQUEST_ENABLE_BLUETOOTH = 1;

    private BluetoothAdapter mBluetoothAdapter;
    private ArrayAdapter<String> mDevicesArrayAdapter;
    private ListView mDevicesListView;
    private Button mScanButton;
    private static final int REQUEST_BLUETOOTH_PERMISSION = 1;
    private static final int REQUEST_ACCESS_COARSE_LOCATION = 1;

    int requestCode = 1001;

    BroadcastReceiver mReceiver;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
                != PackageManager.PERMISSION_GRANTED) {
            // Demander la permission BLUETOOTH_CONNECT
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.BLUETOOTH_CONNECT},
                    REQUEST_BLUETOOTH_PERMISSION);
        } else {
            // Récupération de l'objet BluetoothAdapter
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            // Vérification si le périphérique prend en charge le Bluetooth
            if (mBluetoothAdapter == null) {
                Toast.makeText(this, "Le Bluetooth n'est pas pris en charge par votre périphérique", Toast.LENGTH_SHORT).show();
                finish();
            }

            // Vérification si le Bluetooth est activé, sinon demande à l'utilisateur de l'activer
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BLUETOOTH);
            }

            // Récupération de la ListView et du bouton Scan
            mDevicesListView = findViewById(R.id.deviceListView);
            mScanButton = findViewById(R.id.scanButton);

            // Initialisation de l'ArrayAdapter pour la ListView
            mDevicesArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
            mDevicesListView.setAdapter(mDevicesArrayAdapter);
        }
        // Définition du OnClickListener pour le bouton Scan
        mScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(bluetoothActivity.this, Manifest.permission.BLUETOOTH_SCAN)
                        != PackageManager.PERMISSION_GRANTED) {
                    // La permission n'est pas accordée
                    // Demander la permission
                    ActivityCompat.requestPermissions(bluetoothActivity.this, new String[]{Manifest.permission.BLUETOOTH_SCAN}, requestCode);
                } else {
                    // La permission est déjà accordée
                    // Vérification si le Bluetooth est en train de scanner
                    if (mBluetoothAdapter.isDiscovering()) {
                        mBluetoothAdapter.cancelDiscovery();
                        mScanButton.setText("Scan");
                        Toast.makeText(bluetoothActivity.this, "Scan annulé", Toast.LENGTH_SHORT).show();
                    } else {
                        mDevicesArrayAdapter.clear();
                        mBluetoothAdapter.startDiscovery();
                        mScanButton.setText("Annuler");
                        Toast.makeText(bluetoothActivity.this, "Scan en cours...", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });


        // Définition du BroadcastReceiver pour les appareils Bluetooth découverts
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Vérification de la permission
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // La permission n'a pas été accordée
                    Log.e(TAG, "Permission not granted");
                    return;
                }

                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    // Récupération de l'objet BluetoothDevice
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                    // Ajout du périphérique à l'ArrayAdapter
                    mDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                }
            }
        };


        // Enregistrement du BroadcastReceiver pour les appareils Bluetooth découverts
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Annulation de la découverte des appareils Bluetooth
            if (mBluetoothAdapter != null) {
                mBluetoothAdapter.cancelDiscovery();
            }
        } else {
            // Demande de la permission ACCESS_COARSE_LOCATION
            requestPermissions(new String[] {Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_ACCESS_COARSE_LOCATION);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_BLUETOOTH_PERMISSION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // La permission est accordée, vous pouvez continuer votre logique ici
                // Par exemple, initialiser le BluetoothAdapter ici
            } else {
                // La permission est refusée, vous pouvez afficher un message à l'utilisateur ou
                // désactiver les fonctionnalités qui nécessitent la permission BLUETOOTH_CONNECT
            }
        }
    }
}
