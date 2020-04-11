package com.lumindtech.hypercapbattery.utils;

import android.text.format.DateFormat;
import android.widget.Toast;

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.lumindtech.hypercapbattery.utils.Constants.Default;

/**
 * Created by Carlos Zuluaga on 10/10/2016.
 */

public class Protocol {

    public static String[] decoderState(String data){

        int dataLength = data.length();
        String arr[]=new String[12];
        String Defaultarr[]=new String[1];
        Defaultarr[0]="0";
        String initarray[]=new String[1];
        initarray[0]="@";
        String finalarray[]=new String[1];
        finalarray[0]="@";
        System.out.println("String Length = " + String.valueOf(dataLength));

        if(data.length()>70){
            if((data.charAt(0)=='@')&&(data.charAt(dataLength-3)=='@')) {

                String datanew;

                Pattern patron = Pattern.compile("[^0-9A-Za-z^.,]"); //Dejar todos los numeros la ',' y el '.'---
                Matcher encaja = patron.matcher(data);
                datanew = encaja.replaceAll("");

                arr = datanew.split(",");
                String[] both = concatAll(initarray,arr,finalarray);

                int pos = 0;
                for (int i = 0; i < arr.length; i++) {
                    System.out.println("arr[" + i + "] = " + both[i].trim());
                    pos = pos + 1;
                }
                System.out.println("LENGTH: "+both.length);
                if(both.length==13){
                    if((both[0]=="@")&&(both[both.length-1]=="@")){

                        String s1=both[2];
                        String s2=both[3];
                        String s3=both[4];
                        String s4=both[5];
                        String s5=both[6];

                        both[3]= String.valueOf((Float.parseFloat(s2))-Float.parseFloat(s1));
                        both[4]= String.valueOf(Float.parseFloat(s3)-Float.parseFloat(s2));
                        both[5]= String.valueOf(Float.parseFloat(s4)-Float.parseFloat(s3));
                        both[6]= String.valueOf(Float.parseFloat(s5)-Float.parseFloat(s4));

                        System.out.println(both[2]);
                        System.out.println(both[3]);
                        System.out.println(both[4]);
                        System.out.println(both[5]);
                        System.out.println(both[6]);

                        return both;
                        //Trabajar con los datos o retornar el array
                    }
                }else {
                    return Defaultarr;
                }
            }
        }

        return Defaultarr;
    }

    public static String decoderResponse(String data){
        int dataLength = data.length();
        System.out.println("String Length = " + String.valueOf(dataLength));

        System.out.println("dato en: "+data.charAt(dataLength-3));

        if((data.charAt(0)=='@')&&(data.charAt(dataLength-3)=='@')) {

            String arr[];
            String datanew;

            Pattern patron = Pattern.compile("[^A-Za-z^.]"); //Dejar todos las letras y el '.'---
            Matcher encaja = patron.matcher(data);
            datanew=encaja.replaceAll("");
            System.out.println(datanew);

            arr = datanew.split(",");

            int pos = 0;
            for (int i = 0; i < arr.length; i++) {
                System.out.println("arr[" + i + "] = " + arr[i].trim());
                pos = pos + 1;
            }

            if(arr.length<=1){
                switch (arr[0]){
                    default:
                        return Constants.Default;
                    case "LVAL":
                        return Constants.LVAL;
                    case "ONRE":
                        return Constants.ON;
                    case "OFFR":
                        return Constants.OFF;
                    case"SeON":
                        return Constants.SecurON;
                    case"SOFF":
                        return Constants.SecurOFF;
                }
            }
        }
        return Constants.Default;
    }

    public static <T> T[] concatAll(T[] first, T[]... rest) {
        int totalLength = first.length;
        for (T[] array : rest) {
            totalLength += array.length;
        }
        T[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (T[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }

    public static String hexToBin(String s) {
        return new BigInteger(s, 16).toString(2);
    }
}

    //if (recDataString.charAt(0) == '@')                                //if it starts with # we know it is what we are looking for
    //{
        //String date = (DateFormat.format("dd-MM-yyyy", new java.util.Date()).toString());
        //System.out.println(date);

        //cont = cont + 1;
        //pause = "OFF " + cont;
        //logger.createlog("HyperCapBatt_Log-" + date);

        //String fullDate = (DateFormat.format("dd-MM-yyyy hh:mm:ss", new java.util.Date()).toString());
        //logger.addRecordToLog(fullDate, "HyperCapBatt_Log-" + date + ".txt");
        //logger.addRecordToLog(str, "HyperCapBatt_Log-" + date + ".txt");
        //logger.addRecordToLog(pause, "HyperCapBatt_Log-" + date + ".txt");


        //String arr[] = str.split(",");
        //int pos = 0;
        //for (int i = 0; i < arr.length; i++) {
          //  System.out.println("arr[" + i + "] = " + arr[i].trim());
           // pos = pos + 1;
        //}
        //System.out.println(arr.length);
        //System.out.println(arr[0]);
        /*if (pos < 2) {
            if (arr[0].equals("@LVAL@\r\n")) {
                Toast.makeText(getBaseContext(), "Nivel de Bateria Bajo", Toast.LENGTH_LONG).show();
            } else {
                //Mas tramas
            }
        } else {
            String sensor0 = arr[0].substring(1, 3);
            String sensor1 = arr[1];
            String sensor2 = arr[2];
            String sensor3 = arr[3];
            String sensor4 = arr[4];
            String sensor5 = arr[5];
            String sensor6 = arr[8];

            int i = Integer.parseInt(sensor6, 16);

            int numHex = Integer.parseInt(sensor0, 16);

            //int num = Integer.decode(sensor0);

            String binary = Integer.toBinaryString(numHex);

            DecimalFormat decimales = new DecimalFormat("0.00000");


            sensorView0.setText(" Control de Carga = " + binary);    //update the textviews with sensor values
            sensorView1.setText(" C1 = " + sensor1 + "V");
            sensorView2.setText(" C2 = " + decimales.format(Double.parseDouble(sensor2) - Double.parseDouble((sensor1))) + "V");
            sensorView3.setText(" C3 = " + decimales.format(Double.parseDouble(sensor3)-Double.parseDouble((sensor2))) + "V");
            sensorView4.setText(" C4 = " + decimales.format(Double.parseDouble(sensor4)-Double.parseDouble((sensor3))) + "V");
            sensorView5.setText(" C5 = " + decimales.format(Double.parseDouble(sensor5)-Double.parseDouble((sensor4))) + "V");
            sensorView6.setText(" Numero de Cargas = " + i);
        }

}*/
