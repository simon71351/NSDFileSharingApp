package com.bignerdranch.android.newprojectdemo;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ApConnActivity extends AppCompatActivity {


    private WifiManager wifiManager;
    private String TAG = "ApStatus";
    private Button buttonSetupAp;
    private Button buttonSearchWifi;
    private ListView listView;
    ArrayList<HashMap<String, String>> arraylist = new ArrayList<HashMap<String, String>>();
    SimpleAdapter adapter;
    String ITEM_KEY = "key";
    int size = 0;
    List<ScanResult> results;
    private ProgressBar progressBar;
    private String connectedAp = "";
    private boolean showWifiSearchResult = false;
    BroadcastReceiver mBroadcastReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apconn);

//        Button btnSetupAp = (Button) findViewById(R.id.btnSetupAp);
//        Button btnFindAp = (Button) findViewById(R.id.btnSearchNetwork);

//        btnSetupAp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
//
//        btnFindAp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Dialog dialog = new Dialog(ApConnActivity.this);
//                dialog.setContentView(R.layout.firstaidcategoryoutput_layout);
//                dialog.setTitle("Heart attack and shock");
//                dialog.setCancelable(true);
//                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,has);
//                setListAdapter(adapter);
//
//                dialog.show();
//            }
//        });

//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_apconn);

        /*wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiConfiguration wifiConfiguration = new WifiConfiguration();
        wifiConfiguration.SSID = "FileSharingAp";*/

        try {


            wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            disableAp();
            wifiManager.disconnect();
            wifiManager.setWifiEnabled(false);

            buttonSetupAp = (Button) findViewById(R.id.btnSetupAp);
            buttonSearchWifi = (Button) findViewById(R.id.btnSearchNetwork);


            buttonSetupAp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    createAp(ApConnActivity.this);
                    //buttonSearchWifi.setVisibility(View.GONE);
                    //startActivityForResult(new Intent(Settings.ACTION_WIRELESS_SETTINGS), 0);

                    Intent intent = new Intent(ApConnActivity.this, MainActivity.class);
                    intent.putExtra("ApStatus", true);
                    startActivity(intent);
                    finish();
                }
            });


            buttonSearchWifi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);


                    wifiManager.setWifiEnabled(true);

                   // startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS), 1);

                    WifiConfiguration netConfig = null;
                    try {
                        Method setWifiApMethod = wifi.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
                        boolean apStatus = (boolean) setWifiApMethod.invoke(wifi, netConfig, false);
                        //Toast.makeText(ApConnActivity.this, "Ap", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (wifi.isWifiEnabled() == false) {
                        Toast.makeText(getApplicationContext(), "wifi is disabled..making it enabled", Toast.LENGTH_LONG).show();
                        wifi.setWifiEnabled(true);
                    }

                    showWifiSearchResult = true;
                    Log.e(TAG, "showWifiSearchResult: " + showWifiSearchResult);

                    //buttonSetupAp.setVisibility(View.GONE);

                    disableAp();
                    LayoutInflater inflater = LayoutInflater.from(ApConnActivity.this);
                    View v = inflater.inflate(R.layout.ap_list, null, true);

                    listView = (ListView) v.findViewById(R.id.apinfo_list);
                    progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
                    progressBar.setVisibility(View.VISIBLE);

                    final Dialog dialog = new Dialog(ApConnActivity.this);
                    dialog.setContentView(v);
                    dialog.setTitle("Choose AP");
                    dialog.setCancelable(true);

                    dialog.show();

                    adapter = new SimpleAdapter(ApConnActivity.this, arraylist, R.layout.apinfo_row, new String[]{ITEM_KEY}, new int[]{R.id.wifiInfo});
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            String ssid = ((TextView) view).getText().toString();

                            WifiConfiguration conf = new WifiConfiguration();
                            conf.SSID = "\"" + ssid + "\"";
                            conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                            wifiManager.addNetwork(conf);

                            List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
                            for (WifiConfiguration wifiConfig : list) {
                                if (wifiConfig.SSID != null && wifiConfig.SSID.equals("\"" + ssid + "\"")) {
                                    wifiManager.disconnect();
                                    wifiManager.enableNetwork(wifiConfig.networkId, true);

                                    wifiManager.reconnect();

                                    connectedAp = ssid;
                                    writeToLogFile(ssid);
                                    Toast.makeText(ApConnActivity.this, "Connected ssid = "+connectedAp, Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();

                                    Intent intent = new Intent(ApConnActivity.this, MainActivity.class);
                                    //intent.putExtra("ApStatus", false);
                                    startActivity(intent);
                                    finish();

                                    break;
                                }
                            }
                        }
                    });



                }
            });


            //listView = (ListView) findViewById(R.id.list_view);



            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
            intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);


            mBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context c, Intent intent) {
                    String action = intent.getAction();
                    if (action == WifiManager.SCAN_RESULTS_AVAILABLE_ACTION) {
                        WifiManager wifi = (WifiManager) c.getSystemService(c.WIFI_SERVICE);
                        results = wifi.getScanResults();
                        size = results.size();
                        //Log.e("Network", "Num of networks: "+size);

                        if (showWifiSearchResult) updateDataInListView();
                    } else if (action == WifiManager.NETWORK_STATE_CHANGED_ACTION) {
                        NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                        boolean connected = info.isConnected();
                        if (connected)
                            Toast.makeText(ApConnActivity.this, "Connected to: " + connectedAp, Toast.LENGTH_SHORT).show();
                    }
                }
            };

            registerReceiver(mBroadcastReceiver, intentFilter);

        }catch(Exception e){
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Intent intent = new Intent(ApConnActivity.this, MainActivity.class);
        startActivity(intent);

    }

    private void updateDataInListView(){
        arraylist.clear();
        wifiManager.startScan();
        //i think this code will lock the following code before it has completed it's task

        //Toast.makeText(ApConnActivity.this, "Scanning...." + size, Toast.LENGTH_SHORT).show();
        //This code has little bit of error
        try
        {
            size = size - 1;
            while (size >= 0)
            {
                if(results.get(size).SSID.toString().contains("FileSharingAp")) {
                    HashMap<String, String> item = new HashMap<String, String>();
                    item.put(ITEM_KEY, results.get(size).SSID);
                    //+ "  " + results.get(size).capabilities);

                    arraylist.add(item);
                }
                    size--;
            }
            progressBar.setVisibility(View.GONE);
            adapter.notifyDataSetChanged();
        }
        catch (Exception e)
        { }
    }

    /*public static boolean isApOn(Context context){
        WifiManager wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);

        try{
            Method method = wifiManager.getClass().getDeclaredMethod("isWifiApEnabled");
            method.setAccessible(true);
            return (Boolean) method.invoke(wifiManager);
        }catch(Throwable ignored){}

        return false;
    }*/

    public void createAp(Context context){
        WifiManager wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);

//        if(wifiManager.isWifiEnabled()){
//            wifiManager.setWifiEnabled(false);
//        }

        WifiConfiguration netConfig = new WifiConfiguration();
        //netConfig.SSID = "FileSharingAp";
        netConfig.SSID = Build.MODEL+"-FileSharingAp";

        try{
            Method setWifiApMethod = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            boolean apStatus = (boolean) setWifiApMethod.invoke(wifiManager, netConfig, true);
            Log.e(TAG, "Ap get created");
        }catch(Throwable t){}

    }

//    public void disableAp(){
//        WifiConfiguration netConfig = new WifiConfiguration();
//        netConfig.SSID = "FileSharingAp";
//
//        try{
//            Method setWifiApMethod = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
//            boolean apStatus = (boolean) setWifiApMethod.invoke(wifiManager, netConfig, false);
//            Log.e(TAG, "Old Ap gets disabled");
//        }catch(Throwable t){}
//    }

    public void disableAp(){
        WifiConfiguration netConfig = new WifiConfiguration();
        //netConfig.SSID = "FileSharingAp";
        netConfig.SSID = readFromLogFile();

        int networkId = wifiManager.getConnectionInfo().getNetworkId();

        wifiManager.removeNetwork(networkId);
        wifiManager.saveConfiguration();

        try{
            Method setWifiApMethod = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            boolean apStatus = (boolean) setWifiApMethod.invoke(wifiManager, netConfig, false);
            Log.e(TAG, "Old Ap gets disabled");
        }catch(Throwable t){}
    }

    public void writeToLogFile(String apName){
        String FILENAME = "AP_log";

        try{
            FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
            DataOutputStream dos = new DataOutputStream(fos);

            dos.writeUTF(apName);

            dos.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public String  readFromLogFile(){
        String FILENAME = "AP_log";
        String apName = null;

        try{
            FileInputStream fis = openFileInput(FILENAME);
            DataInputStream dis = new DataInputStream(fis);

            apName = dis.readUTF();

            dis.close();

        }catch (Exception e){
            e.printStackTrace();
        }

        return apName;
    }
}
