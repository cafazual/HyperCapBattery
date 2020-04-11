package com.lumindtech.hypercapbattery.activities;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.lumindtech.hypercapbattery.R;
import com.lumindtech.hypercapbattery.connection.BluetoothManager;
import com.lumindtech.hypercapbattery.utils.Utils;
import static com.lumindtech.hypercapbattery.utils.Preferences.PREF_USER_BT_ADDRESS;
import static com.lumindtech.hypercapbattery.utils.Preferences.PREF_USER_FIRST_CONNECT;
import static com.lumindtech.hypercapbattery.utils.Preferences.PREF_USER_FIRST_TIME;

public class ConnectActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "ConnectActivity";
    boolean isUserFirstTime;
    boolean isUserFirstConnect;
    private static final int CHILD_REQUEST = 1;
    private BluetoothAdapter mBtAdapter;
    private BluetoothManager bluetoothManager=new BluetoothManager();
    private ProgressBar progressDialog;
    private Button connectBtn;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_connect);

        if(bluetoothManager!=null){
            System.out.println("bluetootManager está activo");
        }

        //Progress Dialog
        progressDialog = (ProgressBar) findViewById(R.id.progressbar_downloading);
        progressDialog.setVisibility(ProgressBar.INVISIBLE);
        connectBtn = (Button)this.findViewById(R.id.connectbtn);
        connectBtn.setOnClickListener(this);
        EvaluatePresentation();
    }

    public void EvaluatePresentation() {
        this.isUserFirstTime = Boolean.valueOf(Utils.readSharedSetting(this,PREF_USER_FIRST_TIME,"true"));
        Intent introIntent = new Intent(this, PagerIntroActivity.class);
        introIntent.putExtra(PREF_USER_FIRST_TIME, this.isUserFirstTime);
        if (this.isUserFirstTime) {
            startActivity(introIntent);
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.connectbtn: {
                connectBtn.setEnabled(false);
                checkBTState();
            }
        }
    }

    private void checkBTState() {
        this.mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        if (this.mBtAdapter == null) {
            Toast.makeText(this.getBaseContext(), "El Dispositivo no soporta Bluetooth", Toast.LENGTH_LONG).show();
        } else if (this.mBtAdapter.isEnabled()) {
            Log.d(TAG, "...Bluetooth Encendido...");
            EvaluateConnection();
        } else {
            Intent enableBtIntent = new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE");
            this.startActivityForResult(enableBtIntent, CHILD_REQUEST);
            this.setResult(RESULT_OK);
        }
    }

    public void EvaluateConnection() {
        this.isUserFirstConnect = Boolean.valueOf(Utils.readSharedSetting(this, PREF_USER_FIRST_CONNECT, "true"));
        Intent connectIntent = new Intent(this, DeviceListActivity.class);
        connectIntent.putExtra(PREF_USER_FIRST_CONNECT, this.isUserFirstConnect);
        if (this.isUserFirstConnect) {
            this.startActivity(connectIntent);
        } else {
            String address = String.valueOf(Utils.readSharedSetting(this,PREF_USER_BT_ADDRESS, "null"));
            AsyncConnect connect = new AsyncConnect();
            connect.execute(address);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHILD_REQUEST) {
            switch (resultCode) {
                case RESULT_OK: {
                    System.out.println("CONECTADO");
                    this.EvaluateConnection();
                    break;
                }
                case RESULT_CANCELED: {
                    System.out.println("CANCELADO");
                }
            }
        }
    }

    private class AsyncConnect extends AsyncTask<String, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(String... address) {
            boolean result;

            result=bluetoothManager.start(address[0],ConnectActivity.this);

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
                Intent mainIntent = new Intent(ConnectActivity.this, MainActivity.class);
                startActivity(mainIntent);
                setResult(Activity.RESULT_OK, mainIntent);
                finish();
                Toast.makeText(ConnectActivity.this, "Conectado.",
                        Toast.LENGTH_SHORT).show();
                progressDialog.setVisibility(View.INVISIBLE);
                connectBtn.setEnabled(true);
            }else{
                Toast.makeText(ConnectActivity.this,"No Logramos Conectarnos.", Toast.LENGTH_LONG).show();
                progressDialog.setVisibility(View.INVISIBLE);
                bluetoothManager.Close();
                connectBtn.setEnabled(true);
            }
        }
    }
}
