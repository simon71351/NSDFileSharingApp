package com.bignerdranch.android.newprojectdemo;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * Created by simon on 8/20/16.
 */
public class LogFragment extends Fragment {

    ArrayList<String> fullPathArray = new ArrayList<>();
    ArrayList<String> fileNameArray = new ArrayList<>();
    ArrayList<String> fileCategoryArray = new ArrayList<>();
    ArrayList<Boolean> upDownStatusArray = new ArrayList<>();
    LogAdapter logAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_gallery, container, false);

        ListView logList = (ListView) view.findViewById(R.id.PhoneMusicList);

        String[] fileNames = {"File name 1", "File name 2"};
        String[] fileTypes = {"image", "image"};
        boolean[] upDownStatus = {false, true};

        readFromLogFile();
        logAdapter = new LogAdapter(getContext());
        logList.setAdapter(logAdapter);

        //logList.setOnItemClickListener(musicgridlistener);

        return view;
    }

    public class LogAdapter extends BaseAdapter {
        private Context mContext;

        public LogAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
            return fileNameArray.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            //System.gc();
            View view;

            try {

                if (convertView == null) {
                    view = getLayoutInflater(null).inflate(R.layout.log_list_item, parent, false);
                } else {
                    view = convertView;
                }

                //0 - Download, 1 - upload
                ((ImageView)view.findViewById(R.id.upDownview)).setImageResource(upDownStatusArray.get(position).booleanValue()? R.drawable.ic_download: R.drawable.ic_upload );

                ((TextView)view.findViewById(R.id.imagePath)).setText(fileNameArray.get(position).toString());
                ((TextView)view.findViewById(R.id.fileType)).setText(fileCategoryArray.get(position).toString());

                return view;
            }catch (Exception e){
                e.printStackTrace();

            }

            return null;
        }
    }

    // 0 - download, 1 - upload
    public void writeToLogFile(String[] selectedPaths, boolean upDownStatus){
        String FILENAME = "file_log";
        int idx = -1;
        String fileNamePart = "";
        String fileType = "";
        String fileCategory = "";
        FileOutputStream fos = null;
        DataOutputStream dos = null;


        try{
            fos = getContext().openFileOutput(FILENAME, Context.MODE_PRIVATE | Context.MODE_APPEND);
            dos = new DataOutputStream(fos);
        }catch(Exception e){
            e.printStackTrace();
        }


        for(int i = 0; i < selectedPaths.length; i++){
            idx = selectedPaths[i].lastIndexOf("/");
            fileNamePart = selectedPaths[i].substring(idx);

            idx = fileNamePart.lastIndexOf(".");
            fileType = fileNamePart.substring(idx);

            if(fileType == "jpg" || fileType == "jpeg" || fileType == "png"){
                fileCategory = "image";
            }
            else if(fileType == "mp3" || fileType == "wma" || fileType == "m4a" || fileType == "wav"){
                fileCategory = "audio";
            }
            else if(fileType == "mp4" || fileType == "avi" || fileType == "flv" || fileType == "mov"){
                fileCategory = "video";
            }


            try{

                dos.writeUTF(fileNamePart);
                dos.writeUTF(selectedPaths[i]);
                dos.writeUTF(fileCategory);
                dos.writeBoolean(upDownStatus);

                dos.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        readFromLogFile();


    }

    public void readFromLogFile(){
        String FILENAME = "file_log";
        String fileNamePart = "";
        int size = 0;

        fullPathArray.clear();
        fileNameArray.clear();
        fileCategoryArray.clear();
        upDownStatusArray.clear();

        try{

            String[] files = getContext().fileList();
            boolean found = false;

            for(int i = 0; i < files.length; i++){
                if(files[i].equals(FILENAME)){
                    found = true;
                    return;
                }
            }
            if(!found) return;

            FileInputStream fis = getContext().openFileInput(FILENAME);
            DataInputStream dis = new DataInputStream(fis);

            while((fileNamePart = dis.readUTF()) != null){
                fileNameArray.add(fileNamePart);
                fullPathArray.add(dis.readUTF());
                fileCategoryArray.add(dis.readUTF());
                upDownStatusArray.add(dis.readBoolean());
            }
            outputFileArray(fileNameArray);

            dis.close();

        }catch (Exception e){
            e.printStackTrace();
        }
        logAdapter.notifyDataSetInvalidated();

        //Log.e("StringData", "String: " + string + " Size: " + size);
    }

    public void outputFileArray(ArrayList list){
        for(int i = 0; i < list.size(); i++){
            Log.e("FileNameArray", "File Name: "+list.get(i).toString());
        }
    }
}
