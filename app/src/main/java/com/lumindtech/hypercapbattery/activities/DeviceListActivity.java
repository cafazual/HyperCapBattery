package com.lumindtech.hypercapbattery.activities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lumindtech.hypercapbattery.connection.BluetoothManager;
import com.lumindtech.hypercapbattery.R;
import com.lumindtech.hypercapbattery.utils.Utils;

import static  com.lumindtech.hypercapbattery.utils.Preferences.PREF_USER_BT_ADDRESS;
import static com.lumindtech.hypercapbattery.utils.Preferences.PREF_USER_FIRST_CONNECT;


import java.util.Set;

/**
 * This Activity appears as a dialog. It lists any paired devices and
 * devices detected in the area after discovery. When a device is chosen
 * by the user, the MAC address of the device is sent back to the parent
 * Activity in the result Intent.
 */
public class DeviceListActivity extends Activity {

    /**
     * Tag for Log
     */
    private static final String TAG = "DeviceListActivity";

    /**
     * Member fields
     */
    private BluetoothAdapter mBtAdapter;
    BluetoothManager bluetoothManager;
    private ProgressBar progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bluetoothManager= new BluetoothManager();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.blue_bluetooth));
        }

        // Setup the window
        //requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_device_list);

        //Progress Dialog
        progressDialog = (ProgressBar) findViewById(R.id.progressbar_downloadingDevice);
        progressDialog.setVisibility(ProgressBar.INVISIBLE);

        // Set result CANCELED in case the user backs out
        setResult(Activity.RESULT_CANCELED);

        // Initialize array adapters. One for already paired devices and
        // one for newly discovered devices
        ArrayAdapter<String> pairedDevicesArrayAdapter =
                new ArrayAdapter<String>(this, R.layout.device_name);

        // Find and set up the ListView for paired devices
        ListView pairedListView = (ListView) findViewById(R.id.paired_devices);
        pairedListView.setAdapter(pairedDevicesArrayAdapter);
        pairedListView.setOnItemClickListener(mDeviceClickListener);

        // Get the local Bluetooth adapter
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        // Get a set of currently paired devices
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

        // If there are paired devices, add each one to the ArrayAdapter
        if (pairedDevices.size() > 0) {
            findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
            for (BluetoothDevice device : pairedDevices) {
                pairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        } else {
            String noDevices = getResources().getText(R.string.none_paired).toString();
            pairedDevicesArrayAdapter.add(noDevices);
        }
    }

    /**
     * The on-click listenerTab1 for all devices in the ListViews
     */
    private AdapterView.OnItemClickListener mDeviceClickListener
            = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            // Cancel discovery because it's costly and we're about to connect
            mBtAdapter.cancelDiscovery();
            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            String[] address = new String[1];
            address[0]=info.substring(info.length() - 17);
            Utils.saveSharedSetting(DeviceListActivity.this, PREF_USER_BT_ADDRESS, address[0]);
            // Create the result Intent and include the MAC address
            AsyncConnect connect = new AsyncConnect();
            connect.execute(address);
        }
    };

    private class AsyncConnect extends AsyncTask<String, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(String... address) {
            boolean result;

            result=bluetoothManager.start(address[0], DeviceListActivity.this);

            return result;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }

        @Override
        protected void onPreExecute() {
            progressDialog.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Boolean result) {

            if(result){
                Intent mainIntent = new Intent(DeviceListActivity.this, MainActivity.class);
                startActivity(mainIntent);
                setResult(Activity.RESULT_OK, mainIntent);
                Utils.saveSharedSetting(DeviceListActivity.this, PREF_USER_FIRST_CONNECT,"false");
                finish();
                Toast.makeText(DeviceListActivity.this, "Conectado.",
                        Toast.LENGTH_SHORT).show();
                progressDialog.setVisibility(View.INVISIBLE);
            }else{
                Toast.makeText(DeviceListActivity.this,"No Logramos Conectarnos.", Toast.LENGTH_LONG).show();
                progressDialog.setVisibility(View.INVISIBLE);
                bluetoothManager.Close();
            }

        }
    }
}
