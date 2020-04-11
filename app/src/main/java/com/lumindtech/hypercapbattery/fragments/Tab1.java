package com.lumindtech.hypercapbattery.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.lumindtech.hypercapbattery.R;
import com.lumindtech.hypercapbattery.connection.BluetoothManager;
import com.lumindtech.hypercapbattery.utils.Protocol;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;

import java.util.ArrayList;

/**
 * Created by Carlos Zuluaga on 2/3/2016.
 */

//Our class extending fragment
public class Tab1 extends Fragment implements BluetoothManager.BluetoothManagerListener {

    //BluetoothManager
    BluetoothManager btManager=new BluetoothManager();
    String LOG_TAG="TAB1";
    public boolean FirstAnimationMChart=true;
    private BarChart mChart;
    Description description;
    private Button detailbtn;
    View customView;
    TextView tvCelda1result;
    TextView tvCelda2result;
    TextView tvCelda3result;
    TextView tvCelda4result;
    TextView tvCelda5result;
    TextView tvVoltajeresult;
    TextView tvControlresult;
    TextView tvPpal;

    //Overriden method onCreateView
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Returning the layout file after inflating
        //Change R.layout.tab1 in you classes

        btManager.setBluetoothManagerObjectListenerTab1(this);

        customView = inflater.inflate(R.layout.custom_view,null);
        tvCelda1result = (TextView) customView.findViewById(R.id.tvCelda1result);
        tvCelda2result = (TextView) customView.findViewById(R.id.tvCelda2result);
        tvCelda3result = (TextView) customView.findViewById(R.id.tvCelda3result);
        tvCelda4result = (TextView) customView.findViewById(R.id.tvCelda4result);
        tvCelda5result = (TextView) customView.findViewById(R.id.tvCelda5Result);
        tvVoltajeresult = (TextView) customView.findViewById(R.id.tvVoltaje);
        tvControlresult = (TextView) customView.findViewById(R.id.tvControlResult);


        Button dismissButton = (Button)customView.findViewById(R.id.custom_button);

        View view = inflater.inflate(R.layout.tab1, container, false);

        tvPpal = (TextView) view.findViewById(R.id.tvVoltajeppal);

        detailbtn = (Button) view.findViewById(R.id.detailedbtn);

        mChart = (BarChart) view.findViewById(R.id.chart1);

        description = new Description();
        description.setText("Actualizando Estado");



        //mChart.setNoDataText("Esperando Datos de Estado...");

        mChart.getDescription().setEnabled(true);

        final MaterialStyledDialog dialogHeader = new MaterialStyledDialog.Builder(getActivity())
                .setIcon(new IconicsDrawable(getActivity()).icon(MaterialDesignIconic.Icon.gmi_comment_alt).color(Color.WHITE))
                .withDialogAnimation(true)
                .setHeaderColor(R.color.dialog_2)
                .setCustomView(customView,20 , 20, 20, 0)
                .build();

        detailbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogHeader.show();
            }
        });

        dismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogHeader.dismiss();
            }
        });




        // scaling can now only be done on x- and y-axis separately
        mChart.setPinchZoom(false);
        mChart.setDoubleTapToZoomEnabled(false);
        mChart.setScaleEnabled(false);
        mChart.setDrawBarShadow(false);
        mChart.setDrawGridBackground(false);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);


        mChart.getAxisLeft().setDrawGridLines(false);

        mChart.getLegend().setEnabled(false);
        //Valor de array por defecto para construir la grafica
        //setData(6);
        mChart.setFitBars(true);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.removeAllLimitLines();
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setAxisMaximum(3f);
        leftAxis.setAxisMinimum(2.2f);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawLimitLinesBehindData(true);
        leftAxis.setDrawGridLines(true);
        mChart.getAxisRight().setEnabled(false);

        xAxis.setAvoidFirstLastClipping(true);


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        btManager.Send("RE");

    }

    @Override
    public void onPause () {
        super.onPause();
        Log.v(LOG_TAG, "onPause");
    }

    @Override
    public void onDetach () {
        super.onDetach();
        btManager.UnsuscribeBluetoothManagerObjectListenerTab1();
        this.btManager=null;
        Log.v(LOG_TAG, "onDetach");
    }

    @Override
    public void OnDataReceived(final String msgReceived) {
        System.out.println("Reciviendo desde TAB1");
        /*final String msg=msgReceived;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(),"DATA: "+msg,Toast.LENGTH_LONG).show();
            }
        });*/

        //protocol
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String[] arr;
                String control="Esperando...";
                arr=Protocol.decoderState(msgReceived);

                if(arr[0]!="0"){
                    String a = Protocol.hexToBin(arr[1]);
                    if(a.length()<8){
                        int cantidad=8-a.length();
                        StringBuffer sb = new StringBuffer(18);
                        for ( int i=0;i < cantidad;i++) {
                            sb.append("0");
                        }
                        control = sb.toString()+a;
                    }

                    tvCelda1result.setText(arr[2].substring(0,3).toString()+"volts");
                    tvCelda2result.setText(arr[3].substring(0,3).toString()+"volts");
                    tvCelda3result.setText(arr[4].substring(0,3).toString()+"volts");
                    tvCelda4result.setText(arr[5].substring(0,3).toString()+"volts");
                    tvCelda5result.setText(arr[6].substring(0,3).toString()+"volts");
                    tvVoltajeresult.setText(arr[7].substring(0,4).toString()+"volts");
                    tvPpal.setText(arr[7].substring(0,4).toString()+"Volts");
                    tvControlresult.setText(control);
                    mChart.setDescription(description);
                    setData(arr);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mChart.setDescription(null);
                }
            }
        });
    }


    public void setData(String[] arr) {

        ArrayList<BarEntry> yVals = new ArrayList<BarEntry>();

        for (int i = 2; i < arr.length-6; i++) {
            float val = Float.parseFloat(arr[i]);
            yVals.add(new BarEntry(i-1, val));
        }

        BarDataSet set = new BarDataSet(yVals, "Data Set");
        set.setColors(ColorTemplate.MATERIAL_COLORS);
        set.setDrawValues(true);

        int warningColor=R.color.red;
        int colored1=R.color.light_blue;
        int colored2=R.color.orange2;
        int colored3=R.color.blue_grey;
        int colored4=R.color.green;
        int colored5=R.color.lime;

        int[] colors= new int[]{colored1,colored2,colored3,colored4,colored5};


        if((Float.parseFloat(arr[2])>2.9)||(Float.parseFloat(arr[2])<2.2)){
           colors[0] = warningColor;
        }
        if((Float.parseFloat(arr[3])>2.9)||(Float.parseFloat(arr[3])<2.2)){
            colors[1]=warningColor;
        }
        if((Float.parseFloat(arr[4])>2.9)||(Float.parseFloat(arr[4])<2.2)){
            colors[2]=warningColor;
        }
        if((Float.parseFloat(arr[5])>2.9)||(Float.parseFloat(arr[5])<2.2)){
            colors[3]=warningColor;
        }
        if((Float.parseFloat(arr[6])>2.9)||(Float.parseFloat(arr[6])<2.2)){
            colors[4]=warningColor;
        }

        set.setColors(colors , getActivity());



        BarData data = new BarData(set);

        mChart.setData(data);

        mChart.invalidate();


        if(FirstAnimationMChart){
            mChart.animateY(800);
            FirstAnimationMChart=false;
        }
    }

}
