/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bignerdranch.android.newprojectdemo;

import android.app.Activity;
import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.text.format.Formatter;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class NsdHelper {

    Activity mContext;

    NsdManager mNsdManager;
    NsdManager.ResolveListener mResolveListener;
    NsdManager.DiscoveryListener mDiscoveryListener;
    NsdManager.RegistrationListener mRegistrationListener;

    public static final String SERVICE_TYPE = "_http._tcp.";

    public static final String TAG = "NsdHelper";
    public String mServiceName = "NsdChat";
    private boolean discoveryStarted = false;
    private String deviceName = null;
    private String foundServiceName = null;
    private boolean isAP = false;
    private ArrayList<HashMap<String, NetworkServiceInfo>> foundServices = new ArrayList<>();

    NsdServiceInfo mService;

    public class NetworkServiceInfo{
        public String ipAddress;
        public int port;
        public String deviceName;
    }

    public NsdHelper(Activity context) {
        mContext = context;
        mNsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
    }

    public void initializeNsd() {
        initializeResolveListener();
        initializeDiscoveryListener();
        initializeRegistrationListener();

        //mNsdManager.init(mContext.getMainLooper(), this);

    }

    public void setApStatus(boolean status){
        isAP = status;

    }

    public ArrayList<HashMap<String, NetworkServiceInfo>> getFoundServices(){
        return foundServices;
    }

    public void initializeDiscoveryListener() {
        mDiscoveryListener = new NsdManager.DiscoveryListener() {

            @Override
            public void onDiscoveryStarted(String regType) {
                Log.e(TAG, "Service discovery started");
            }

            @Override
            public void onServiceFound(NsdServiceInfo service) {
                Log.e(TAG, "Service discovery success" + service);

                if (!service.getServiceType().equals(SERVICE_TYPE)) {

                    Log.e(TAG, "Unknown Service Type: " + service.getServiceType());
                }
//                else if (service.getServiceName().equals(mServiceName)) {
//                    Log.d(TAG, "Same machine: " + mServiceName);
//                } else if (service.getServiceName().contains(mServiceName)){
//                    mResolveListener = null;
//                    initializeResolveListener();
//                    mNsdManager.resolveService(service, mResolveListener);
//                }

                else{
//                    int index = service.getServiceName().indexOf("--")+2;
//                    String deviceName = service.getServiceName().substring(index);

                    String[] splitInfo = service.getServiceName().split("--");

                    if(!splitInfo[1].equals(Build.MODEL)){
                        foundServiceName = service.getServiceName();



                        if(isAP && !(splitInfo[2].equals("192.168.43.1") || splitInfo[2].equals("0.0.0.0"))){
                            updateDeviceList(service, false);

                        }
                    }


                    mResolveListener = null;
                    initializeResolveListener();
                    if(mNsdManager != null) {
                        mNsdManager.resolveService(service, mResolveListener);


                        //Log.e(TAG, "mNsdManager is not null");
                    }
                    else Log.e(TAG, "mNsdManger is null");
                }
            }

            @Override
            public void onServiceLost(NsdServiceInfo service) {
                Log.e(TAG, "service lost" + service);
                if (mService == service) {
                    mService = null;
                }
            }
            
            @Override
            public void onDiscoveryStopped(String serviceType) {
                Log.e(TAG, "Discovery stopped: " + serviceType);
            }

            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Discovery failed: Error code:" + errorCode);
                mNsdManager.stopServiceDiscovery(this);
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Discovery failed: Error code:" + errorCode);
                mNsdManager.stopServiceDiscovery(this);
            }
        };
    }

    public void initializeResolveListener() {
        mResolveListener = new NsdManager.ResolveListener() {

            @Override
            public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                Log.e(TAG, "Resolve failed" + errorCode);
                Log.e(TAG, serviceInfo.toString());
            }

            @Override
            public void onServiceResolved(final NsdServiceInfo serviceInfo) {
                Log.e(TAG, "Resolve Succeeded. " + serviceInfo);


                if(foundServiceName == null) return;
//                if (serviceInfo.getServiceName().equals(mServiceName)) {
//                    Log.d(TAG, "Same IP.");
//                    return;
//                }


//                mContext.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
                        WifiManager wifiManager = (WifiManager) mContext.getSystemService(mContext.WIFI_SERVICE);
                        if((serviceInfo.getHost().getHostAddress().equals(Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress())))
                             ||(isAP && (serviceInfo.getHost().getHostAddress().equals("0.0.0.0") ||
                                serviceInfo.getHost().getHostAddress().equals("192.168.43.1")))){

                            return;
                        }else{
                            Log.e(TAG, "Current Device IP("+Build.MODEL+"): "+Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress()));
                            Log.e(TAG, "Found Service IP("+Build.MODEL+"): "+serviceInfo.getHost().getHostAddress());
                        }
                        mService = serviceInfo;
                        Log.e("Resolved IP Log", Build.MODEL+": "+serviceInfo.getHost().getHostAddress());
                        Log.e("Resolved Port Log", Build.MODEL+": "+serviceInfo.getPort());

                        //item.put(NsdChatActivity.ITEM_KEY, "Host name: "+serviceInfo.getHost().getHostName()+"\n"+"IP Address: "+serviceInfo.getHost().getHostAddress());
                        //item.put(NsdChatActivity.ITEM_KEY, serviceInfo.getAttributes().get("DeviceName").toString());
                //serviceInfo.getHost().


                    updateDeviceList(serviceInfo);


//                    }
//                });

            }
        };
    }

    public void updateDeviceList(NsdServiceInfo serviceInfo) {
        updateDeviceList(serviceInfo, true);
    }

    public void updateDeviceList(NsdServiceInfo serviceInfo, boolean solved){
        try{
            HashMap<String, String> item = new HashMap<>();
            //InetAddress inetAddress = InetAddress.getByName(serviceInfo.getHost().getHostAddress());


//            int index = foundServiceName.indexOf("--");
//            deviceName = foundServiceName.substring(index + 2);

            String[] splitInfo = serviceInfo.getServiceName().split("--");


            //deviceName = inetAddress.getCanonicalHostName();
            boolean addNew = false;
            Log.e(TAG, splitInfo[1]);
            if(foundServices.size() == 0 && (!splitInfo[1].equals(Build.MODEL))){
                NetworkServiceInfo networkServiceInfo = new NetworkServiceInfo();
                networkServiceInfo.deviceName = splitInfo[1];
                networkServiceInfo.ipAddress = (solved)? serviceInfo.getHost().getHostAddress() : splitInfo[2];
                networkServiceInfo.port = (solved)? serviceInfo.getPort() : Integer.parseInt(splitInfo[3]);

                HashMap<String, NetworkServiceInfo> networkInfo = new HashMap<>();
                networkInfo.put("ServiceInfo", networkServiceInfo);
                foundServices.add(networkInfo);
                addNew = true;
            }
            else {


                for (HashMap<String, NetworkServiceInfo> itemInfo : foundServices) {
                    Log.e("itemInfo", itemInfo.get("ServiceInfo").deviceName);
                    if (!itemInfo.get("ServiceInfo").deviceName.equals(splitInfo[1]) && (!splitInfo[1].equals(Build.MODEL))) {
                        NetworkServiceInfo networkServiceInfo = new NetworkServiceInfo();
                        networkServiceInfo.deviceName = splitInfo[1];
                        networkServiceInfo.ipAddress = (solved)? serviceInfo.getHost().getHostAddress() : splitInfo[2];
                        networkServiceInfo.port = (solved)? serviceInfo.getPort() : Integer.parseInt(splitInfo[3]);

                        HashMap<String, NetworkServiceInfo> networkInfo = new HashMap<>();
                        networkInfo.put("ServiceInfo", networkServiceInfo);
                        foundServices.add(networkInfo);
                        addNew = true;

                    }
                }
            }

            if (addNew) {
                item.put(MainActivity.ITEM_KEY, splitInfo[1]);
                ((MainActivity) mContext).getListData().add(item);
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((MainActivity) mContext).updateListData();
                    }
                });
            }


        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void initializeRegistrationListener() {
        mRegistrationListener = new NsdManager.RegistrationListener() {

            @Override
            public void onServiceRegistered(NsdServiceInfo NsdServiceInfo) {
                mServiceName = NsdServiceInfo.getServiceName();
            }
            
            @Override
            public void onRegistrationFailed(NsdServiceInfo arg0, int arg1) {
            }

            @Override
            public void onServiceUnregistered(NsdServiceInfo arg0) {
            }
            
            @Override
            public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
            }
            
        };
    }

    public void registerService(int port) {
        Log.e("Registering", "registerService method gets called");
        NsdServiceInfo serviceInfo  = new NsdServiceInfo();
        //serviceInfo.setHost();

        Log.e("DEVICENAME",  Build.MODEL);

        String deviceName = Build.MODEL;
        WifiManager wm = (WifiManager) mContext.getSystemService(mContext.WIFI_SERVICE);
        //String ip = (isAP)? "192.168.43.1":Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        //String ip = (isAP)? "192.168.43.1":"192.168.43.84";

        //serviceInfo.setHost(inetAddress);

        String serviceName = mServiceName+"--"+deviceName+"--"+ip+"--"+port;
        serviceInfo.setPort(port);
        serviceInfo.setServiceName(serviceName);
        serviceInfo.setServiceType(SERVICE_TYPE);
        //serviceInfo.setAttribute("DeviceName", "DeviceName");
        Log.e("RegisterPort", "Port("+Build.MODEL+"): "+serviceInfo.getPort());

        mNsdManager.registerService(
                serviceInfo, NsdManager.PROTOCOL_DNS_SD, mRegistrationListener);

    }

    public void discoverServices() {
        stopDiscovery();
        initializeDiscoveryListener();
        mNsdManager.discoverServices(
                SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);
        discoveryStarted = true;
    }
    
    public void stopDiscovery() {
        if(discoveryStarted && mDiscoveryListener != null){
            mNsdManager.stopServiceDiscovery(mDiscoveryListener);
            mDiscoveryListener = null;
        }
    }

    public NsdServiceInfo getChosenServiceInfo() {
        return mService;
    }
    
    public void tearDown() {
        mNsdManager.unregisterService(mRegistrationListener);
    }
}
