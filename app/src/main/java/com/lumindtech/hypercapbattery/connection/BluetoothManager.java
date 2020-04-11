package com.lumindtech.hypercapbattery.connection;

import android.content.Context;

/**
 * Created by Carlos Zuluaga on 07/10/2016.
 */

public class BluetoothManager implements Bluetooth.BluetoothListener{

    private static Bluetooth BT;
    private static BluetoothManagerListener listenerTab1;
    private static BluetoothManagerListener listenerTab2;

    public  interface BluetoothManagerListener{
        void OnDataReceived(String msgReceived);
    }

    public BluetoothManager(){
    }

    public void setBluetoothManagerObjectListenerTab1(BluetoothManagerListener listener){
        this.listenerTab1=listener;

    }

    public void setBluetoothManagerObjectListenerTab2(BluetoothManagerListener listener){
        this.listenerTab2=listener;
    }

    public void  UnsuscribeBluetoothManagerObjectListenerTab1(){
        this.listenerTab1=null;
    }

    public  void UnsuscribeBluetoothManagerObjectListenerTab2(){
        this.listenerTab2=null;
    }

    public boolean start(String address, Context context) {
        boolean response=false;
        if(BT==null){
            BT = new Bluetooth(context);
            response=BT.startBT(address);
            BT.setBluetoothObjectListener(this);
        }
        return response;
    }

    public  boolean isKeepAlive(){
        if(BT!=null){
            return BT.isKeepAlive();
        }
        return false;
    }

    public void Close(){
        if(BT!=null){
            BT.disconnect();
        }
        BT=null;
    }

    public void Disconnect(){
        if(BT!=null){
            BT.disconnect();
        }
    }

    public void Send(String msg){
        BT.Send(msg);
    }

    @Override
    public void OnDataLoaded(StringBuilder msgReceived) {
        if(listenerTab1!=null){
            listenerTab1.OnDataReceived(msgReceived.toString()); //Disparo el evento del bluetoothManager
        }
        if(listenerTab2!=null){
            listenerTab2.OnDataReceived(msgReceived.toString());
        }
    }
}
