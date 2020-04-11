package com.lumindtech.hypercapbattery.connection;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;

import com.lumindtech.hypercapbattery.activities.ConnectActivity;
import com.lumindtech.hypercapbattery.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import static com.lumindtech.hypercapbattery.utils.Preferences.PREF_USER_MAXRECONNECT;

/**
 * Created by Carlos Zuluaga on 07/10/2016.
 */

public class Bluetooth {

    public interface  BluetoothListener{
        void OnDataLoaded(StringBuilder msgReceived);
    }

    public BluetoothListener listener;
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private ConnectedThread mConnectedThread;
    private static String address;
    private  static int MAXRECONNECT;
    private static final UUID BTMODULEHC06UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public StringBuilder recDataString = new StringBuilder();


    public Bluetooth(Context context){

        MAXRECONNECT= Integer.parseInt(Utils.readSharedSetting(context,PREF_USER_MAXRECONNECT,"2"));
        System.out.println(MAXRECONNECT);
        this.listener=null;
    }

    public void setBluetoothObjectListener(BluetoothListener listener) {
        this.listener = listener;
    }

    public boolean startBT(String MacAddress){

        btAdapter = BluetoothAdapter.getDefaultAdapter();       // get Bluetooth adapter
        address = MacAddress;
        //create device and set the MAC address
        BluetoothDevice device = btAdapter.getRemoteDevice(address);


        if (btSocket == null) {
            try {
                btSocket = createBluetoothSocket(device);
            } catch (IOException e) {
                System.out.println("Falla al crear el socket");
                return  false;
            }
            // Establish the Bluetooth socket connection.
            int count=0;
            boolean band;
            do{
                try {
                    System.out.println("Intentando Conectar");
                    btSocket.connect();
                    band=true;
                } catch (IOException e) {
                    try {
                        btSocket.close();
                        band=false;
                    } catch (IOException e2) {
                        //insert code to deal with this
                        band=false;
                    }
                }
                count++;
            }while((count<=MAXRECONNECT)&!(band));
            if(band){
                mConnectedThread = new ConnectedThread(btSocket);
                mConnectedThread.start();

                //I send a character when resuming.beginning transmission to check device is connected
                //If it is not an exception will be thrown in the write method and finish() will be called
                mConnectedThread.write("Conectado");
                return band;
            }
        }
        return false;
    }


    public boolean isKeepAlive() {

        if(mConnectedThread.write("RE")){
            return true;
        }else{
            int count=0;
            boolean band;
            do{
                try {
                    System.out.println("Reintentando conexión");
                    btSocket.connect();
                    band=true;
                }catch (Exception e){
                    System.out.println("No se pudo conectar");
                    band=false;
                }
                count++;
            }while ((count<=MAXRECONNECT)&!(band));
            if(band){
                mConnectedThread = new ConnectedThread(btSocket);
                mConnectedThread.start();
            }
            return band;
        }
    }


    public void Send(String msg){
        mConnectedThread.write(msg);
    }


    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        return device.createRfcommSocketToServiceRecord(BTMODULEHC06UUID);
        //creates secure outgoing connecetion with BT device using UUID
    }

    public void disconnect(){
        if(mConnectedThread!=null){
            mConnectedThread.disconnect();
        }
    }

    public class ConnectedThread extends Thread {
        private InputStream mmInStream;
        private OutputStream mmOutStream;


        //creation of the connect thread
        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                //Create I/O streams for connection
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[256];
            int bytes;

            // Keep looping to listen for received messages
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);            //read bytes from input buffer
                    String readMessage = new String(buffer, 0, bytes);

                    // Send the obtained bytes to the UI Activity via Method
                    recDataString.append(readMessage);                                    //keep appending to string until ~
                    int endOfLineIndex = recDataString.indexOf("@\r\n");                   // determine the end-of-line

                    if (endOfLineIndex > 0) {                                           // make sure there data before ~
                        //Metodo para enviar la trama que llegó a otro activity o fragment (evento)
                        System.out.println(recDataString);
                        if(listener!=null){
                            listener.OnDataLoaded(recDataString);
                        }
                        //BluetoothManager.bluetoothIn.obtainMessage(BluetoothManager.handlerState, bytes, -1, readMessage).sendToTarget();
                        recDataString.delete(0, recDataString.length());

                        //Disparar el evento aquí
                    }
                } catch (IOException e) {
                    break;
                }
            }
        }

        //write method
        public boolean write(String input) {
            byte[] msgBuffer = input.getBytes();           //converts entered String into bytes
            try {
                mmOutStream.write(msgBuffer);                //write bytes over BT connection via outstream
            } catch (IOException e) {
                System.out.println("Conexion Fallida");
                //e.printStackTrace();
                return false;
            }
            return true;
        }

        public void disconnect(){
            if (mmInStream != null) {
                try {mmInStream.close();} catch (Exception e) {}
                mmInStream = null;
            }

            if (mmOutStream != null) {
                try {mmOutStream.close();} catch (Exception e) {}
                mmOutStream = null;
            }

            if (btSocket != null) {
                try {btSocket.close();} catch (Exception e) {}
                btSocket = null;
            }
        }
    }
}



