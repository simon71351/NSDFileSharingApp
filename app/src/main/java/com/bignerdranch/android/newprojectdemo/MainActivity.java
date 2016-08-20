package com.bignerdranch.android.newprojectdemo;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    //Declaring All The Variables Needed

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private Button btnSend;
    private boolean isAp;
    private String TAG = "FileChooser";
    public static  String ITEM_KEY = "key";
    private ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
    SimpleAdapter adapter;
    private int selectedTabPos = 0;

    NsdHelper mNsdHelper;
    FileTransferService transferService;
    int selectedTab = 0;
    boolean startDiscover = true;

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//
//        if(selectedTab == 0) {
////            viewPagerAdapter.getImageGalleryFragment().getGridView().clearChoices();
////
//            final GridView gridView = viewPagerAdapter.getImageGalleryFragment().getGridView();
//            Object[] objArr = viewPagerAdapter.getImageGalleryFragment().getSelectedPos();
////            for(int i = 0; i < gridView.getCount(); i++){
////                gridView.setItemChecked(i, false);
////            }
////
////            gridView.post(new Runnable() {
////                @Override
////                public void run() {
////                    gridView.setChoiceMode(ListView.CHOICE_MODE_NONE);
////                }
////            });
//
//            Log.e("ToastValue", "Toast value: "+selectedTab);
//
//            ArrayList<ImageGalleryFragment.ImageModel> list = viewPagerAdapter.getImageGalleryFragment().getModelList();
//            for(int i = 0; i < list.size(); i++){
//                list.get(i).setChecked(false);
//            }
//
//            for(int i = 0; i < objArr.length; i++ )
//                ((ImageGalleryFragment.MarkableImageView)gridView.getChildAt((int)objArr[i])).setChecked(false);
//
//            viewPagerAdapter.getImageGalleryFragment().getAdapter().notifyDataSetChanged();
//        }
//        else if(selectedTab == 1) viewPagerAdapter.getAudioGalleryFragment();
//        else if (selectedTab == 2) viewPagerAdapter.getVideoGalleryFragment();
//
//
//
//    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    class DelayTask extends AsyncTask<FileClient, Integer, String> {
        int count = 0;
        ProgressDialog progressDialog;


        @Override
        protected void onPreExecute() {
            //pb.setVisibility(ProgressBar.VISIBLE);
            progressDialog = new ProgressDialog(MainActivity.this);

            progressDialog.setMessage("Sending File");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

            runOnUiThread(new Runnable(){
                @Override
                public void run() {
                    progressDialog.show();
                }
            });

        }

        @Override
        protected String doInBackground(FileClient... params) {

            FileClient fileClient = params[0];

            int transferStatus = (int)(( (fileClient.getSendTotalFilesSize() * 1.0) / fileClient.getTotalFilesSize()) * 100);

            while (true) {
                SystemClock.sleep(200);

                publishProgress(transferStatus);
                if(transferStatus == 100) break;

                //Log.e("TransferStatus", "Transfer Status: " + transferStatus);
                transferStatus = (int)(( (fileClient.getSendTotalFilesSize() * 1.0) / fileClient.getTotalFilesSize()) * 100);
            }

            SystemClock.sleep(1000);
            if(transferStatus == 100) progressDialog.dismiss();

            return "Complete";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressDialog.setProgress(values[0]);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        isAp = getIntent().getBooleanExtra("ApStatus", false);
        Log.e("ApStatus", "isAP: "+((isAp)? "1":"0"));

//        if(isAp) {
//         android.provider.Settings.System.putString(getContentResolver(), android.provider.Settings.System.WIFI_USE_STATIC_IP, "192.168.43.1");
//        }

        try{
            WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            Method isWifiApMethod = wifi.getClass().getMethod("isWifiApEnabled");
            boolean isEnabled = (boolean) isWifiApMethod.invoke(wifi);

            //if(isEnabled && isAp) android.provider.Settings.System.putString(getContentResolver(), android.provider.Settings.System.WIFI_USE_STATIC_IP, "192.168.43.1");


        }catch(Exception e){
            e.printStackTrace();
        }



//        WifiConfiguration wifiConf = null;
//        WifiManager wifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
//        WifiInfo connectionInfo = wifiManager.getConnectionInfo();
//        List<WifiConfiguration> configuredNetworks = wifiManager.getConfiguredNetworks();
//        for (WifiConfiguration conf : configuredNetworks){
//            if (conf.networkId == connectionInfo.getNetworkId()){
//                wifiConf = conf;
//                break;
//            }
//        }
//
//        try{
//            final ContentResolver cr = getContentResolver();
//            if(isAp){
//                android.provider.Settings.System.putString(getContentResolver(), android.provider.Settings.System.WIFI_USE_STATIC_IP, "192.168.1.1");
////                try{
////                    setIpAssignment("STATIC", wifiConf); //or "DHCP" for dynamic setting
////                    setIpAddress(InetAddress.getByName("192.168.1.100"), 24, wifiConf);
////                    setGateway(InetAddress.getByName("4.4.4.4"), wifiConf);
////                    setDNS(InetAddress.getByName("4.4.4.4"), wifiConf);
////                    wifiManager.updateNetwork(wifiConf); //apply the setting
////                    wifiManager.saveConfiguration(); //Save it
////                }catch(Exception e){
////                    e.printStackTrace();
////                }
//            }
//            else{
//                //android.provider.Settings.System.putString(getContentResolver(), android.provider.Settings.System.WIFI_USE_STATIC_IP, "192.168.43.2");
//                setIpAssignment("STATIC", wifiConf); //or "DHCP" for dynamic setting
//                setIpAddress(InetAddress.getByName("192.168.1.101"), 24, wifiConf);
//                setGateway(InetAddress.getByName("4.4.4.4"), wifiConf);
//                setDNS(InetAddress.getByName("4.4.4.4"), wifiConf);
//                wifiManager.updateNetwork(wifiConf); //apply the setting
//                wifiManager.saveConfiguration(); //Save it
//            }
//        }catch(Exception e){
//            e.printStackTrace();
//        }
//






        ListView listView = (ListView) findViewById(R.id.device_list);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setSelector(android.R.color.darker_gray);



        adapter = new SimpleAdapter(this, arrayList, R.layout.clientinfolist, new String[]{ITEM_KEY}, new int[]{R.id.serviceInfo});
        listView.setAdapter(adapter);
        /*
        Assigning view variables to thier respective view in xml
        by findViewByID method
         */

//        toolbar = (Toolbar) findViewById(R.id.tool_bar);

        try {
            transferService = new FileTransferService(this);
        }catch (Exception e){
            e.printStackTrace();
        }

        File appImagesDir = new File(Environment.getExternalStorageDirectory()+"/Pictures/FileShare");
        File appMusicDir = new File(Environment.getExternalStorageDirectory()+"/Music/FileShare");
        //File appMovieDir = new File(Environment.getExternalStorageDirectory()+"/Movie/FileShare");


        appImagesDir.mkdirs();
        appMusicDir.mkdirs();

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        btnSend = (Button) findViewById(R.id.btnSend);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                     String[] selectedPaths = null;
      //              Toast.makeText(MainActivity.this, "Button gets clicked", Toast.LENGTH_SHORT).show();
                    if(selectedTabPos == 1){
                        selectedPaths = viewPagerAdapter.getImageGalleryFragment().getSelectedImagePaths();

                        Log.e("SelectedPath", "SelectedPath: "+selectedPaths.length);
                    }
                    else if(selectedTabPos == 2){
                        selectedPaths = viewPagerAdapter.getAudioGalleryFragment().getSelectedSongs();
                        Log.e("SelectedPath", "SelectedPath: "+selectedPaths.length);
                    }
                    else if(selectedTabPos == 3){
                        selectedPaths = viewPagerAdapter.getVideoGalleryFragment().getSelectedSongs();
                        Log.e("SelectedPath", "SelectedPath: "+selectedPaths.length);
                    }

                    if(selectedPaths.length == 0){
                        Toast.makeText(getApplication(), "No file is selected", Toast.LENGTH_SHORT).show();
                        return;
                    }


                    String ipAddress = mNsdHelper.getFoundServices().get(0).get("ServiceInfo").ipAddress;
                    int port = mNsdHelper.getFoundServices().get(0).get("ServiceInfo").port;
                    Toast.makeText(MainActivity.this, "Sending to: "+ipAddress, Toast.LENGTH_SHORT).show();

                    InetAddress inetAddress = null;
                    try {
                        inetAddress = InetAddress.getByName(ipAddress);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }




                    FileClient fileClient = new FileClient(inetAddress, port, MainActivity.this, selectedPaths);

                    try{
                        new DelayTask().execute(fileClient);
                    }catch(Exception e){
                        e.printStackTrace();
                    }

//                    try{
//                        final String[] finalSelectedPaths = selectedPaths;
//                        new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                viewPagerAdapter.getLogFragment().writeToLogFile(finalSelectedPaths, true);
//                            }
//                        }).start();
//                    }catch(Exception e){
//                        e.printStackTrace();
//                    }

                    viewPagerAdapter.getLogFragment().writeToLogFile(selectedPaths, false);




                    if(selectedTabPos == 1){
                        viewPagerAdapter.getImageGalleryFragment().removeImageSelection();
                    }else if(selectedTabPos == 2){
                        viewPagerAdapter.getAudioGalleryFragment().removeAudioSelection();
                    }else if(selectedTabPos == 3){
                        viewPagerAdapter.getVideoGalleryFragment().removeAudioSelection();
                    }

                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });

        /*
        Creating Adapter and setting that adapter to the viewPager
        setSupportActionBar method takes the toolbar and sets it as
        the default action bar thus making the toolbar work like a normal
        action bar.
         */
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        //setSupportActionBar(toolbar);
        transferService.setViewPagerAdapter(viewPagerAdapter);


        /*
        TabLayout.newTab() method creates a tab view, Now a Tab view is not the view
        which is below the tabs, its the tab itself.
         */

        final TabLayout.Tab image = tabLayout.newTab();
        final TabLayout.Tab music = tabLayout.newTab();
        final TabLayout.Tab video = tabLayout.newTab();
        final TabLayout.Tab log = tabLayout.newTab();

        /*
        Setting Title text for our tabs respectively
         */

        //home.setText("Home");
        image.setIcon(R.drawable.ic_image_dark);
        music.setIcon(R.drawable.ic_music_dark);
        video.setIcon(R.drawable.ic_video_dark);
        log.setIcon(R.drawable.ic_log);

        /*
        Adding the tab view to our tablayout at appropriate positions
        As I want home at first position I am passing home and 0 as argument to
        the tablayout and like wise for other tabs as well
         */
        tabLayout.addTab(log, 0);
        tabLayout.addTab(image, 1);
        tabLayout.addTab(music, 2);
        tabLayout.addTab(video, 3);


        /*
        TabTextColor sets the color for the title of the tabs, passing a ColorStateList here makes
        tab change colors in different situations such as selected, active, inactive etc

        TabIndicatorColor sets the color for the indiactor below the tabs
         */

        tabLayout.setTabTextColors(ContextCompat.getColorStateList(this, R.color.tab_selector));
        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.PrimaryColor));


        /*
        Adding a onPageChangeListener to the viewPager
        1st we add the PageChangeListener and pass a TabLayoutPageChangeListener so that Tabs Selection
        changes when a viewpager page changes.
         */



        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.e("TabPos", "TabPosition: "+tab.getPosition());
                viewPager.setCurrentItem(tab.getPosition());

                selectedTab = tab.getPosition();

                //Toast.makeText(getApplication(), "SelectedTab: "+selectedTab, Toast.LENGTH_SHORT).show();
                selectedTabPos = selectedTab;

                if(selectedTabPos == 0) {
                    if(viewPagerAdapter.getImageGalleryFragment() != null) viewPagerAdapter.getImageGalleryFragment().removeImageSelection();
                    if(viewPagerAdapter.getAudioGalleryFragment() != null) viewPagerAdapter.getAudioGalleryFragment().removeAudioSelection();
                    if(viewPagerAdapter.getVideoGalleryFragment() != null) viewPagerAdapter.getVideoGalleryFragment().removeAudioSelection();
                }else if(selectedTabPos == 1){
                    if(viewPagerAdapter.getAudioGalleryFragment() != null) viewPagerAdapter.getAudioGalleryFragment().removeAudioSelection();
                    if(viewPagerAdapter.getVideoGalleryFragment() != null) viewPagerAdapter.getVideoGalleryFragment().removeAudioSelection();
                }else if(selectedTabPos == 2){
                    if(viewPagerAdapter.getImageGalleryFragment() != null) viewPagerAdapter.getImageGalleryFragment().removeImageSelection();
                    if(viewPagerAdapter.getVideoGalleryFragment() != null) viewPagerAdapter.getVideoGalleryFragment().removeAudioSelection();
                }else if(selectedTabPos == 3){
                    if(viewPagerAdapter.getImageGalleryFragment() != null) viewPagerAdapter.getImageGalleryFragment().removeImageSelection();
                    if(viewPagerAdapter.getAudioGalleryFragment() != null) viewPagerAdapter.getAudioGalleryFragment().removeAudioSelection();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                selectedTabPos = position;
                //Toast.makeText(getApplication(), "SelectedTab: "+position, Toast.LENGTH_SHORT).show();

                switch (position){
                    case 0:
                        image.setIcon(R.drawable.ic_image_dark);
                        music.setIcon(R.drawable.ic_music_dark);
                        video.setIcon(R.drawable.ic_video_dark);
                        log.setIcon(R.drawable.ic_log);
                        break;
                    case 1:
                        /*
                        setting Home as White and rest grey
                        and like wise for all other positions
                         */
                        image.setIcon(R.drawable.ic_image);
                        music.setIcon(R.drawable.ic_music_dark);
                        video.setIcon(R.drawable.ic_video_dark);
                        log.setIcon(R.drawable.ic_log_dark);


                        break;
                    case 2:
                        image.setIcon(R.drawable.ic_image_dark);
                        music.setIcon(R.drawable.ic_music);
                        video.setIcon(R.drawable.ic_video_dark);
                        log.setIcon(R.drawable.ic_log_dark);

                        break;
                    case 3:
                        image.setIcon(R.drawable.ic_image_dark);
                        music.setIcon(R.drawable.ic_music_dark);
                        video.setIcon(R.drawable.ic_video);
                        log.setIcon(R.drawable.ic_log_dark);

                        break;
                }


                try {

                    if (selectedTabPos == 0) {
                        if(viewPagerAdapter.getImageGalleryFragment() != null) viewPagerAdapter.getImageGalleryFragment().removeImageSelection();
                        if(viewPagerAdapter.getAudioGalleryFragment() != null) viewPagerAdapter.getAudioGalleryFragment().removeAudioSelection();
                        if(viewPagerAdapter.getVideoGalleryFragment() != null) viewPagerAdapter.getVideoGalleryFragment().removeAudioSelection();
                    }
                    else if (selectedTabPos == 1) {
                        if(viewPagerAdapter.getAudioGalleryFragment() != null) viewPagerAdapter.getAudioGalleryFragment().removeAudioSelection();
                        if(viewPagerAdapter.getVideoGalleryFragment() != null) viewPagerAdapter.getVideoGalleryFragment().removeAudioSelection();
                    } else if (selectedTabPos == 2) {
                        if(viewPagerAdapter.getImageGalleryFragment() != null) viewPagerAdapter.getImageGalleryFragment().removeImageSelection();
                        if(viewPagerAdapter.getVideoGalleryFragment() != null) viewPagerAdapter.getVideoGalleryFragment().removeAudioSelection();
                    } else if (selectedTabPos == 3) {
                        if(viewPagerAdapter.getImageGalleryFragment() != null) viewPagerAdapter.getImageGalleryFragment().removeImageSelection();
                        if(viewPagerAdapter.getAudioGalleryFragment() != null) viewPagerAdapter.getAudioGalleryFragment().removeAudioSelection();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public ArrayList<HashMap<String, String>> getListData(){
        return arrayList;
    }

    @Override
    protected void onPause() {
        if (mNsdHelper != null) {

            mNsdHelper.tearDown();

        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.e("MainActivity", "onResume gets called");
        super.onResume();
        mNsdHelper = new NsdHelper(this);
        mNsdHelper.setApStatus(isAp);
        mNsdHelper.initializeNsd();

        //if(startDiscover){
            mNsdHelper.registerService(transferService.getLocalPort());
            Log.e("ListeningPort", Build.MODEL+"(port): "+transferService.getLocalPort());

            mNsdHelper.discoverServices();
        //}

        //startDiscover = false;

        /*new Thread(new Runnable() {
            @Override
            public void run() {
                mNsdHelper.discoverServices();
                try {

                    Thread.sleep(7000);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }).start();*/

        Log.d(TAG, "Local Port: "+transferService.getLocalPort());
    }

    @Override
    protected void onDestroy() {
        try {
            mNsdHelper.tearDown();
        }catch(Exception e){
            e.printStackTrace();
        }
        //transferService.tearDown();
        super.onDestroy();
    }

    public void updateListData(){
        Log.e(TAG, "Update ListData gets called");
        adapter.notifyDataSetChanged();
    }
//
//    public static void setIpAssignment(String assign , WifiConfiguration wifiConf)
//            throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException{
//        setEnumField(wifiConf, assign, "ipAssignment");
//    }
//
//    public static void setIpAddress(InetAddress addr, int prefixLength, WifiConfiguration wifiConf)
//            throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException,
//            NoSuchMethodException, ClassNotFoundException, InstantiationException, InvocationTargetException {
//        Object linkProperties = getField(wifiConf, "linkProperties");
//        if(linkProperties == null)return;
//        Class laClass = Class.forName("android.net.LinkAddress");
//        Constructor laConstructor = laClass.getConstructor(new Class[]{InetAddress.class, int.class});
//        Object linkAddress = laConstructor.newInstance(addr, prefixLength);
//
//        ArrayList mLinkAddresses = (ArrayList)getDeclaredField(linkProperties, "mLinkAddresses");
//        mLinkAddresses.clear();
//        mLinkAddresses.add(linkAddress);
//    }
//
//    public static void setGateway(InetAddress gateway, WifiConfiguration wifiConf)
//            throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException,
//            ClassNotFoundException, NoSuchMethodException, InstantiationException, InvocationTargetException{
//        Object linkProperties = getField(wifiConf, "linkProperties");
//        if(linkProperties == null)return;
//        Class routeInfoClass = Class.forName("android.net.RouteInfo");
//        Constructor routeInfoConstructor = routeInfoClass.getConstructor(new Class[]{InetAddress.class});
//        Object routeInfo = routeInfoConstructor.newInstance(gateway);
//
//        ArrayList mRoutes = (ArrayList)getDeclaredField(linkProperties, "mRoutes");
//        mRoutes.clear();
//        mRoutes.add(routeInfo);
//    }
//
//    public static void setDNS(InetAddress dns, WifiConfiguration wifiConf)
//            throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException{
//        Object linkProperties = getField(wifiConf, "linkProperties");
//        if(linkProperties == null)return;
//
//        ArrayList<InetAddress> mDnses = (ArrayList<InetAddress>)getDeclaredField(linkProperties, "mDnses");
//        mDnses.clear(); //or add a new dns address , here I just want to replace DNS1
//        mDnses.add(dns);
//    }

    public static Object getField(Object obj, String name)
            throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException{
        Field f = obj.getClass().getField(name);
        Object out = f.get(obj);
        return out;
    }

    public static Object getDeclaredField(Object obj, String name)
            throws SecurityException, NoSuchFieldException,
            IllegalArgumentException, IllegalAccessException {
        Field f = obj.getClass().getDeclaredField(name);
        f.setAccessible(true);
        Object out = f.get(obj);
        return out;
    }

    public static void setEnumField(Object obj, String value, String name)
            throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException{
        Field f = obj.getClass().getField(name);
        f.set(obj, Enum.valueOf((Class<Enum>) f.getType(), value));
    }


}

