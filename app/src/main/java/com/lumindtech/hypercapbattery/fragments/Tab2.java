package com.lumindtech.hypercapbattery.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.lumindtech.hypercapbattery.R;
import com.lumindtech.hypercapbattery.activities.ConnectActivity;
import com.lumindtech.hypercapbattery.activities.MainActivity;
import com.lumindtech.hypercapbattery.connection.BluetoothManager;
import com.lumindtech.hypercapbattery.utils.Constants;
import com.lumindtech.hypercapbattery.utils.NotifyUser;
import com.lumindtech.hypercapbattery.utils.Protocol;

/**
 * Created by Carlos Zuluaga on 2/3/2016.
 */

public class Tab2 extends Fragment implements BluetoothManager.BluetoothManagerListener, CompoundButton.OnCheckedChangeListener {
    BluetoothManager btManager=new BluetoothManager();
    String LOG_TAG="Tab2";
    String response = Constants.Default;
    private ProgressBar progressDialog;
    ToggleButton tbtnProtection;
    ToggleButton tbtnDigitalOut;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        btManager.setBluetoothManagerObjectListenerTab2(this);
        View view = inflater.inflate(R.layout.tab2, container, false);

        tbtnProtection=(ToggleButton) view.findViewById(R.id.toggleButton2);
        tbtnDigitalOut=(ToggleButton) view.findViewById(R.id.toggleButton4);

        tbtnProtection.setOnCheckedChangeListener(this);
        tbtnDigitalOut.setOnCheckedChangeListener(this);

        //Progress Dialog
        progressDialog = (ProgressBar) view.findViewById(R.id.progressbar_downloading);
        progressDialog.setVisibility(ProgressBar.INVISIBLE);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        btManager.Send("RE");
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        System.out.println("Fragment2 vista destruida");
    }

    @Override
    public void onDetach () {
        super.onDetach();
        btManager.UnsuscribeBluetoothManagerObjectListenerTab2();
        this.btManager=null;
        Log.v(LOG_TAG, "onDetach");
    }

    @Override
    public void OnDataReceived(String msgReceived) {
        System.out.println("Recibo desde TAB2");
        response=Protocol.decoderResponse(msgReceived);
        if(!response.equals(Constants.Default)){
            NotifyUser.toastMessage(getActivity().getApplicationContext(),response);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {
            case R.id.toggleButton2:
                if(tbtnProtection.isChecked()){
                    btManager.Send("RP");
                    //tbtnProtection.setEnabled(false);
                    //AsyncTask para cambiar el checked
                    //AsyncCommand command = new AsyncCommand();
                    //command.execute();
                }else {
                    btManager.Send("RO");
                    //tbtnProtection.setEnabled(false);
                    //AsyncCommand command = new AsyncCommand();
                    //command.execute();
                }

                break;
            case R.id.toggleButton4:
                if(tbtnDigitalOut.isChecked()){
                    btManager.Send("R1");
                    //AsyncCommand command = new AsyncCommand();
                    //command.execute();
                }else{
                    btManager.Send("R0");
                    //AsyncCommand command = new AsyncCommand();
                    //command.execute();
                }
                break;
        }
    }

    private class AsyncCommand extends AsyncTask<Void, Integer, Integer> {
        @Override
        protected Integer doInBackground(Void... params) {
            int cont=0;

            System.out.println("PASE POR EL ASYNC");

            /*while ((response.equals(Constants.Default))&&(cont<=3)){

                System.out.println("CONTADOR: "+cont);
                cont++;
            }*/

            switch (response){
                default:
                    return 0;
                case Constants.Default:
                    return 0;
                case Constants.ON:
                    return 1;
                case Constants.SecurON:
                    return 2;
                case Constants.OFF:
                    return 3;
                case Constants.SecurOFF:
                    return 4;
            }

        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }

        @Override
        protected void onPreExecute() {
            progressDialog.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Integer result) {

            switch (result){
                case 0:
                    progressDialog.setVisibility(View.INVISIBLE);
                case 1:
                    Toast.makeText(getActivity(), "Salida Digital Activa.",
                            Toast.LENGTH_SHORT).show();
                    progressDialog.setVisibility(View.INVISIBLE);
                    tbtnDigitalOut.setEnabled(true);
                    tbtnDigitalOut.setChecked(true);
                    break;
                case 2:
                    Toast.makeText(getActivity(), "Salida Digital Inactiva.",
                            Toast.LENGTH_SHORT).show();
                    progressDialog.setVisibility(View.INVISIBLE);
                    tbtnDigitalOut.setEnabled(true);
                    tbtnDigitalOut.setChecked(false);
                    break;
                case 3:
                    Toast.makeText(getActivity(), "Sistema de Protección Activo.",
                            Toast.LENGTH_SHORT).show();
                    progressDialog.setVisibility(View.INVISIBLE);
                    tbtnProtection.setEnabled(true);
                    tbtnProtection.setChecked(true);
                    break;
                case 4:
                    Toast.makeText(getActivity(), "Sistema de Protección Inactivo.",
                            Toast.LENGTH_SHORT).show();
                    progressDialog.setVisibility(View.INVISIBLE);
                    tbtnProtection.setEnabled(true);
                    tbtnProtection.setChecked(false);
                    break;

            }
        }
    }
}
