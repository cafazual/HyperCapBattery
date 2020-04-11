package com.lumindtech.hypercapbattery.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.lumindtech.hypercapbattery.R;
import com.lumindtech.hypercapbattery.connection.BluetoothManager;
import com.lumindtech.hypercapbattery.fragments.Pager;

public class MainActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener {

    //This is our tablayout
    private TabLayout tabLayout;

    //This is our viewPager
    private ViewPager viewPager;

    private static int DURATION=5000;

    BluetoothManager bluetoothManager= new BluetoothManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //Adding toolbar to the activity
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Initializing the tablayout
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        //tabLayout.setTabMode(TabLayout.MODE_FIXED);

        //Adding the tabs using addTab() method
        tabLayout.addTab(tabLayout.newTab().setText("Tab1"));//TAB1
        tabLayout.addTab(tabLayout.newTab().setText("Tab2"));//TAB2
        tabLayout.addTab(tabLayout.newTab().setText("Tab3"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        //Initializing viewPager
        viewPager = (ViewPager) findViewById(R.id.pager);

        //Creating our pager adapter
        Pager adapter = new Pager(getSupportFragmentManager(), tabLayout.getTabCount());

        //Adding adapter to pager
        viewPager.setAdapter(adapter);

        //Adding onTabSelectedListener to swipe views
        tabLayout.setOnTabSelectedListener(this);
        tabLayout.setupWithViewPager(viewPager);
    }


    @Override
    public void onResume() {
        super.onResume();
        //Get MAC address from DeviceListActivity via intent
        //Intent intent = getIntent();

        AsyncKeepAlive keepAlive = new AsyncKeepAlive();
        keepAlive.execute();
        //Get the MAC address from the DeviceListActivty via EXTRA
        //address = intent.getStringExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS);

        //create device and set the MAC address
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        bluetoothManager.Disconnect();
        bluetoothManager.Close();
        this.finish();
    }


    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }
    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    private class AsyncKeepAlive extends AsyncTask<String, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(String... address) {
            boolean result=true;
            while (result){
                try {
                    Thread.sleep(DURATION);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //result=bluetoothManager.KeepAlive();
                result=bluetoothManager.isKeepAlive();
            }
            return result;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }

        @Override
        protected void onPreExecute() {
            //progressDialog.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Boolean result) {

            if(result){
                this.execute();
            }else{
                Toast.makeText(MainActivity.this,"Se perdió la conexión.", Toast.LENGTH_LONG).show();
                bluetoothManager.Close();
                Intent connectIntent = new Intent(MainActivity.this, ConnectActivity.class);
                startActivity(connectIntent);
                finish();
            }
        }
    }

}