package com.bignerdranch.android.newprojectdemo;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by simon on 7/24/16.
 */
class FileClient{
    String filepath = "/storage/emulated/0/DCIM/Camera/IMG20160102112128.jpg";
    //String filepath = "/storage/emulated/0/DCIM/Camera/C360_2015-06-29-10-24-32-130.jpg";
    Socket clientSocket = null;
    File sendFile = null;
    MainActivity mActivity;
    String[] paths;

    public FileClient(InetAddress address, int port, Context context, String[] selectedPaths){
        mActivity = (MainActivity) context;
        //sendFile = new File(filepath);

        this.paths = selectedPaths;


        for(int i = 0; i < paths.length; i++){
            Log.e("SelectedPath", "Selected Path: "+paths[i]);
        }
        new Thread(new ClientThread(address, port)).start();
    }

    class ClientThread implements Runnable{

        InetAddress address;
        int port;
        public ClientThread(InetAddress address, int port){
           this.address = address;
            this.port = port;
        }

        @Override
        public void run() {

            try{
                clientSocket = new Socket(address, 8080);
                PrintWriter output = new PrintWriter(
                        new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())), true);

                BufferedOutputStream bos = new BufferedOutputStream(clientSocket.getOutputStream());
                DataOutputStream dos = new DataOutputStream(bos);

                //String fileNamePart = "IMG20160102112128.jpg";
                //char[] fileNameArr = fileNamePart.toCharArray();

                //dos.writeUTF(fileNamePart);  //First Send = filename
                //output.flush();

                int byteRead = 0;
                char[] buffer = new char[1024];

                int idx = paths[0].lastIndexOf("/");
                String fileNamePart = paths[0].substring(idx);

                dos.writeUTF(fileNamePart);
                Log.e("NamePart", "Name part: "+fileNamePart);
                sendFile = new File(paths[0]);



                long size = sendFile.length();

                dos.writeLong(size);  //Second send = file size
                //output.flush();

//                BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(sendFile)));
//
//                while((byteRead = input.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1){
//                    Log.e("ByteRead", "ByteRead: "+byteRead);
//                    //char[] bufferChar = buffer.toString().toCharArray();
//                    //Log.e("BufferChar", "BufferChar: "+bufferChar.length);
//                    output.write(buffer, 0, byteRead);     //Thrid Send and following = data
//                    output.flush();
//                    size -= byteRead;
//                }


                FileInputStream fis = new FileInputStream(sendFile);
                BufferedInputStream bis = new BufferedInputStream(fis);

                int theByte = 0;
                while((theByte = bis.read()) != -1) bos.write(theByte);

                bis.close();
                dos.close();
                clientSocket.close();

                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mActivity, "File Sending Completed", Toast.LENGTH_SHORT).show();
                    }
                });


            }catch(Exception e){
                e.printStackTrace();
            }

        }
    }
}
